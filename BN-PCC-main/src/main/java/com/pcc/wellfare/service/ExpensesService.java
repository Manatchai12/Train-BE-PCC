package com.pcc.wellfare.service;

import com.pcc.wellfare.model.Budget;
import com.pcc.wellfare.model.Employee;
import com.pcc.wellfare.model.Expenses;
import com.pcc.wellfare.repository.BudgetRepository;
import com.pcc.wellfare.repository.EmployeeRepository;
import com.pcc.wellfare.repository.ExpensesRepository;

import com.pcc.wellfare.requests.ExpensesRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpensesService {

    private final ExpensesRepository expensesRepository;
    private final BudgetRepository budgetRepository;
    private final EntityManager entityManager;
    private final EmployeeRepository employeeRepository;

    public ExpensesService(ExpensesRepository expensesRepository, BudgetRepository budgetRepository,
            EmployeeRepository employeeRepository, EntityManager entityManager) {
        this.expensesRepository = expensesRepository;
        this.budgetRepository = budgetRepository;
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
    }

    public List<Expenses> findAllExpensesWithDateOfAdmissionNotNull() {
        return expensesRepository.findByDateOfAdmissionIsNotNull();
    }

    public Optional<Expenses> findbyid(Long id) {
        return expensesRepository.findById(id);
    }

    private double parseDoubleWithComma(String value) {
        value = value.replaceAll(",", "");
        return Double.parseDouble(value);
    }

    public Map<String, Double> getTotal(Long userId) {
        List<Object[]> useBudget = expensesRepository.getUse(userId);
        List<Object[]> canUseBudget = budgetRepository.getCanUse(userId);
        // const RoomService = emp.file
        if (canUseBudget.isEmpty()) {
            throw new RuntimeException("Can Use Budget is empty");
        } else {
            if (useBudget.isEmpty()) {
                Map<String, Double> responseData = new HashMap<>();
                Object[] can = canUseBudget.get(0);
                double canOpd = parseDoubleWithComma(can[0].toString());
                double canIpd = parseDoubleWithComma(can[1].toString());
                double roomService = budgetRepository.getPerDay(userId);
                responseData.put("opd", canOpd);
                responseData.put("ipd", canIpd);
                responseData.put("room", roomService);
                return responseData;
            } else {
                Object[] use = useBudget.get(0);
                Object[] can = canUseBudget.get(0);

                double useOpd = parseDoubleWithComma(use[0].toString());
                double useIpd = parseDoubleWithComma(use[1].toString());
                double canOpd = parseDoubleWithComma(can[0].toString());
                double canIpd = parseDoubleWithComma(can[1].toString());

                double totalOpd = canOpd - useOpd;
                double totalIpd = canIpd - useIpd;
                double roomService = budgetRepository.getPerDay(userId);

                Map<String, Double> responseData = new HashMap<>();
                responseData.put("opd", totalOpd);
                responseData.put("ipd", totalIpd);
                responseData.put("room", roomService);

                return responseData;
            }
        }
    }

    public Object getExpenses(Long userId) {
        float totalOpd = (expensesRepository.getUseOpd(userId) != null) ? expensesRepository.getUseOpd(userId) : 0.0f;
        float totalIpd = (expensesRepository.getUseIpd(userId) != null) ? expensesRepository.getUseIpd(userId) : 0.0f;
        if (totalOpd<=0 && totalIpd<=0) {
            return "ยังไม่มีการเบิกในระบบ";
        } else {
           

            Map<String, Float> result = new HashMap<>();
            result.put("opd", totalOpd);
            result.put("ipd", totalIpd);
            return result;
        }
    }

    public Object create(ExpensesRequest expensesRequest, Long userId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(userId);
        Employee employee = employeeOptional.orElseThrow(() -> new RuntimeException("Employee not found"));

        float perDay = budgetRepository.getPerDay(userId);
        float opdExpenses = (expensesRepository.getUseOpd(userId) != null) ? expensesRepository.getUseOpd(userId)
                : 0.0f;
        float ipdExpenses = (expensesRepository.getUseIpd(userId) != null) ? expensesRepository.getUseIpd(userId)
                : 0.0f;

        float totalOpd = budgetRepository.getOpd(userId) - opdExpenses;
        float totalIpd = budgetRepository.getIpd(userId) - ipdExpenses;

        float withdrawOpd = expensesRequest.getOpd();
        float withdrawIpd = expensesRequest.getIpd();
        int days = expensesRequest.getDays();
        float roomService = expensesRequest.getRoomService();
        String types = expensesRequest.getTypes();
        float calPerDay = roomService / days;
        float deduct = perDay * days;
        float canWithdraw = 0;

        if (types.equals("opd")) {
            if (totalOpd == 0) {
                return "เบิกได้ 0 บาท";
            } else if (totalOpd >= withdrawOpd) {
                totalOpd = totalOpd - withdrawOpd;
                canWithdraw = withdrawOpd;
                if (totalOpd == 0) {
                    return canWithdraw;
                } else {
                    if (calPerDay > perDay) {
                        if (deduct > totalOpd) {
                            canWithdraw = withdrawOpd + totalOpd;
                        } else {
                            canWithdraw = withdrawOpd + deduct;
                        }
                    } else {
                        if (totalOpd - roomService < 0) {
                            canWithdraw = canWithdraw + totalOpd;
                        } else {
                            canWithdraw = withdrawOpd + roomService;
                        }
                    }
                }
            } else {
                canWithdraw = totalOpd;
            }
        } else if (types.equals("ipd")) {
            if (totalIpd == 0) {
                return "เบิกได้ 0 บาท";
            } else if (totalIpd >= withdrawIpd) {
                totalIpd = totalIpd - withdrawIpd;
                canWithdraw = withdrawIpd;
                if (totalIpd == 0) {
                    return canWithdraw;
                } else {
                    if (calPerDay > perDay) {
                        if (deduct > totalIpd) {
                            canWithdraw = withdrawIpd + totalIpd;
                        } else {
                            canWithdraw = withdrawIpd + deduct;
                        }
                    } else {
                        if (totalIpd - roomService < 0) {
                            canWithdraw = canWithdraw + totalIpd;
                        } else {
                            canWithdraw = withdrawIpd + roomService;
                        }
                    }
                }
            } else {
                canWithdraw = totalIpd;
            }
        } else {
            return "Type ผิดเว้ย";
        } if (canWithdraw == 0)  {
            return "ไม่มียอด";
         } else {
   

        // สร้างวัตถุ Expenses และบันทึกลงในฐานข้อมูล
        Expenses expenses = Expenses
                .builder()
                .employee(employee)
                .ipd(withdrawIpd)
                .opd(withdrawOpd)
                .roomService(roomService)
                .canWithdraw(canWithdraw)
                .days(days)
                .startDate(expensesRequest.getStartDate())
                .endDate(expensesRequest.getEndDate())
                .dateOfAdmission(new Date())
                .description(expensesRequest.getDescription())
                .remark(expensesRequest.getRemark())
                .build();
        return expensesRepository.save(expenses);
     }
    }

    public Page<Expenses> getAllExpenseByPage(Pageable pageeble) {
        return expensesRepository.findAll(pageeble);
    }

    public Page<Expenses> getExpensesByName(Pageable pageable, String name) {
        Long uid = 0l;
        String[] names = name.split("\\s+", 2);
        String fname = names[0];
        String surname = names[1];
        Optional<Long> userIdOptional = employeeRepository.findUserIdByTnameAndTsurname(fname, surname);
        uid = userIdOptional.orElse(0L);
        if (uid != 0L) {
            return expensesRepository.findAllByEmployeeUserId(uid, pageable);
        } else {
            return Page.empty();
        }
    }

    public Page<Expenses> getExpenseByEmpCode(Pageable pageable, String code) {
        Long uid = 0l;
        Optional<Long> userIdOptional = employeeRepository.findUserIdByEmpid(code);
        uid = userIdOptional.orElse(0L);
        if (uid != 0L) {
            return expensesRepository.findAllByEmployeeUserId(uid, pageable);
        } else {
            return Page.empty();
        }
    }

    public Page<Expenses> getAllExpenseByFilter(Pageable pageeble, String type, String value) {
        Long userId = 0l;
        if (type == "name") {
            String[] names = value.split("\\s+", 2);
            String name = names[0];
            String surname = names[1];
            System.out.println(name);
            System.out.println(surname);
            Optional<Long> userIdOptional = employeeRepository.findUserIdByTnameAndTsurname(name, surname);
            userId = userIdOptional.orElse(0L);
        } else if (type == "code") {
            String code = value;
            Optional<Long> userIdOptional = employeeRepository.findUserIdByEmpid(code);
            userId = userIdOptional.orElse(0L);
        }
        System.out.println(userId);
        System.out.println(value);
        System.out.println(type);
        if (userId != 0L) {
            return expensesRepository.findAllByEmployeeUserId(userId, pageeble);
        } else {
            return Page.empty();
        }

    }

    // public Object update(ExpensesRequest expensesRequest, Long userId, Long
    // expensesId) {
    // Optional<Expenses> expensesOptional =
    // expensesRepository.findById(expensesId);
    // Expenses expenses = expensesOptional.orElseThrow(() -> new
    // RuntimeException("Expenses not found"));

    // Optional<Employee> employeeOptional = employeeRepository.findById(userId);
    // Employee employee = employeeOptional.orElseThrow(() -> new
    // RuntimeException("Employee not found"));

    // float perDay = budgetRepository.getPerDay(userId);
    // float opdExpenses = (expensesRepository.getUseOpd(userId) != null) ?
    // expensesRepository.getUseOpd(userId) : 0.0f;
    // float ipdExpenses = (expensesRepository.getUseIpd(userId) != null) ?
    // expensesRepository.getUseIpd(userId) : 0.0f;

    // float totalOpd = budgetRepository.getOpd(userId) - opdExpenses;
    // float totalIpd = budgetRepository.getIpd(userId) - ipdExpenses;

    // float withdrawOpd = expensesRequest.getOpd();
    // float withdrawIpd = expensesRequest.getIpd();
    // int days = expensesRequest.getDays();
    // float roomService = expensesRequest.getRoomService();
    // String types = expensesRequest.getTypes();
    // float calPerDay = roomService / days;
    // float deduct = perDay * days;
    // float canWithdraw = 0;

    // if (types.equals("opd")) {
    // if (totalOpd == 0) {
    // return "เบิกได้ 0 บาท";
    // } else if (totalOpd >= withdrawOpd) {
    // totalOpd = totalOpd - withdrawOpd;
    // canWithdraw = withdrawOpd;
    // if (totalOpd == 0) {
    // return canWithdraw;
    // } else {
    // if (calPerDay > perDay) {
    // if (deduct > totalOpd) {
    // canWithdraw = withdrawOpd + totalOpd;
    // totalOpd = 0;
    // } else {
    // totalOpd = totalOpd - deduct;
    // canWithdraw = withdrawOpd + deduct;
    // }
    // } else {
    // if (totalOpd - roomService < 0) {
    // canWithdraw = canWithdraw + totalOpd;
    // totalOpd = 0;
    // } else {
    // totalOpd = totalOpd - roomService;
    // canWithdraw = withdrawOpd + roomService;
    // }
    // }
    // }
    // } else {
    // canWithdraw = totalOpd;
    // }
    // } else if (types.equals("ipd")) {
    // if (totalIpd == 0) {
    // return "เบิกได้ 0 บาท";
    // } else if (totalIpd >= withdrawIpd) {
    // totalIpd = totalIpd - withdrawIpd;
    // canWithdraw = withdrawIpd;
    // if (totalIpd == 0) {
    // return canWithdraw;
    // } else {
    // if (calPerDay > perDay) {
    // if (deduct > totalIpd) {
    // canWithdraw = withdrawIpd + totalIpd;
    // totalIpd = 0;
    // } else {
    // totalIpd = totalIpd - deduct;
    // canWithdraw = withdrawIpd + deduct;
    // }
    // } else {
    // if (totalIpd - roomService < 0) {
    // canWithdraw = canWithdraw + totalIpd;
    // totalIpd = 0;
    // } else {
    // totalIpd = totalIpd - roomService;
    // canWithdraw = withdrawIpd + roomService;
    // }
    // }
    // }
    // } else {
    // canWithdraw = totalIpd;
    // }
    // } else {
    // System.out.println("Type ผิดเว้ย");
    // }

    // // อัพเดทค่าใน object Expenses ตามที่ต้องการ
    // expenses.setEmployee(employee);
    // expenses.setIpd(withdrawIpd);
    // expenses.setOpd(withdrawOpd);
    // expenses.setRoomService(roomService);
    // expenses.setCanWithdraw(canWithdraw);
    // expenses.setDays(days);
    // expenses.setStartDate(expensesRequest.getStartDate());
    // expenses.setEndDate(expensesRequest.getEndDate());
    // expenses.setDateOfAdmission(expensesRequest.getAdMission());
    // expenses.setDescription(expensesRequest.getDescription());
    // expenses.setRemark(expensesRequest.getRemark());

    // return expensesRepository.save(expenses);
    // }

    public List<Expenses> getExpensesByUserId(Long userId) {
        return expensesRepository.findByEmployeeUserId(userId);
    }

    public List<Expenses> getByUserId(Long userId) {
        return expensesRepository.findByEmployeeUserId(userId);
    }

    public Map<String, Object> getExpensesById(Long userId) {
        String jpql = "SELECT e.user_id, e.\"e-mail\", e.empid, e.remark, e.status, e.tname, e.tposition, " +
                "e.tprefix, e.tsurname, e.budget_id, e.dept_code, e2.id, e2.can_withdraw, e2.date_of_admission, e2.days, "
                +
                "e2.description, e2.end_date, e2.ipd, e2.opd, e2.remark, e2.room_service, e2.start_date " +
                "FROM employee e JOIN expenses e2 ON e2.user_id = e.user_id WHERE e.user_id = :id";

        List<Object[]> results = entityManager
                .createNativeQuery(jpql)
                .setParameter("id", userId)
                .getResultList();

        Map<String, Object> userMap = new LinkedHashMap<>();
        List<Map<String, Object>> expensesList = new ArrayList<>();

        for (Object[] row : results) {
            userMap.put("userId", row[0]);
            userMap.put("email", row[1]);
            userMap.put("empid", row[2]);
            userMap.put("remark", row[3]);
            userMap.put("status", row[4]);
            userMap.put("tname", row[5]);
            userMap.put("tposition", row[6]);
            userMap.put("tprefix", row[7]);
            userMap.put("tsurname", row[8]);
            userMap.put("budget_id", row[9]);
            userMap.put("dept_code", row[10]);

            Map<String, Object> expenseMap = new LinkedHashMap<>();
            expenseMap.put("id", row[11]);
            expenseMap.put("can_withdraw", row[12]);
            expenseMap.put("date_of_admission", row[13]);
            expenseMap.put("days", row[14]);
            expenseMap.put("description", row[15]);
            expenseMap.put("end_date", row[16]);
            expenseMap.put("ipd", row[17]);
            expenseMap.put("opd", row[18]);
            expenseMap.put("remark", row[19]);
            expenseMap.put("room_service", row[20]);
            expenseMap.put("start_date", row[21]);

            expensesList.add(expenseMap);
        }

        userMap.put("expenses", expensesList);

        return userMap;
    }

    public Object update(ExpensesRequest expensesRequest, Long expensesId) {
        Optional<Expenses> expensesOptional = expensesRepository.findById(expensesId);
        Expenses expenses = expensesOptional.orElseThrow(() -> new RuntimeException("Expenses not found"));
        Long empid = expenses.getEmployee().getUserId();
        Date admissionDate = expensesRequest.getAdMission();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(admissionDate);
        int year = calendar.get(Calendar.YEAR);
        System.out.println(year);
        float perDay = budgetRepository.getPerDay(empid);
        float opdExpenses = (expensesRepository.getUseOpdByYear(empid, year) != null)
                ? expensesRepository.getUseOpdByYear(empid, year)
                : 0.0f;
        float ipdExpenses = (expensesRepository.getUseIpdByYear(empid, year) != null)
                ? expensesRepository.getUseIpdByYear(empid, year)
                : 0.0f;

        float totalOpd = budgetRepository.getOpd(empid) - opdExpenses;
        float totalIpd = budgetRepository.getIpd(empid) - ipdExpenses;

        String oldTypeS = (expenses.getIpd() == 0) ? "opd" : "ipd";
        float oldUse = expenses.getCanWithdraw();
        if (oldTypeS.equals("opd")) {
            totalOpd = oldUse + totalOpd;
        } else {
            totalIpd = oldUse + totalIpd;
        }

        float withdrawOpd = expensesRequest.getOpd();
        float withdrawIpd = expensesRequest.getIpd();
        int days = expensesRequest.getDays();
        float roomService = expensesRequest.getRoomService();
        String types = expensesRequest.getTypes();
        float calPerDay = roomService / days;
        float deduct = perDay * days;
        float canWithdraw = 0;

        if (types.equals("opd")) {
            if (totalOpd == 0) {
                return "เบิกได้ 0 บาท";
            } else if (totalOpd >= withdrawOpd) {
                totalOpd = totalOpd - withdrawOpd;
                canWithdraw = withdrawOpd;
                if (totalOpd == 0) {
                    return canWithdraw;
                } else {
                    if (calPerDay > perDay) {
                        if (deduct > totalOpd) {
                            canWithdraw = withdrawOpd + totalOpd;
                        } else {
                            canWithdraw = withdrawOpd + deduct;
                        }
                    } else {
                        if (totalOpd - roomService < 0) {
                            canWithdraw = canWithdraw + totalOpd;
                        } else {
                            canWithdraw = withdrawOpd + roomService;
                        }
                    }
                }
            } else {
                canWithdraw = totalOpd;
            }
        } else if (types.equals("ipd")) {
            if (totalIpd == 0) {
                return "เบิกได้ 0 บาท";
            } else if (totalIpd >= withdrawIpd) {
                totalIpd = totalIpd - withdrawIpd;
                canWithdraw = withdrawIpd;
                if (totalIpd == 0) {
                    return canWithdraw;
                } else {
                    if (calPerDay > perDay) {
                        if (deduct > totalIpd) {
                            canWithdraw = withdrawIpd + totalIpd;
                        } else {
                            canWithdraw = withdrawIpd + deduct;
                        }
                    } else {
                        if (totalIpd - roomService < 0) {
                            canWithdraw = canWithdraw + totalIpd;
                        } else {
                            canWithdraw = withdrawIpd + roomService;
                        }
                    }
                }
            } else {
                canWithdraw = totalIpd;
            }
        } else {
            System.out.println("Type ผิดเว้ย");
        }

        // อัพเดทค่าใน object Expenses ตามที่ต้องการ
        expenses.setIpd(withdrawIpd);
        expenses.setOpd(withdrawOpd);
        expenses.setRoomService(roomService);
        expenses.setCanWithdraw(canWithdraw);
        expenses.setDays(days);
        expenses.setStartDate(expensesRequest.getStartDate());
        expenses.setEndDate(expensesRequest.getEndDate());
        expenses.setDateOfAdmission(expensesRequest.getAdMission());
        expenses.setDescription(expensesRequest.getDescription());
        expenses.setRemark(expensesRequest.getRemark());

        return expensesRepository.save(expenses);
    }

    public Object withDraw(ExpensesRequest expensesRequest, Long userId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(userId);
        Employee employee = employeeOptional.orElseThrow(() -> new RuntimeException("Employee not found"));
        String type1 = expensesRequest.getTypes();
        float perDay = budgetRepository.getPerDay(userId);
        float roomServiceLimit = perDay * expensesRequest.getDays();
        float roomServiceRequest = expensesRequest.getRoomService();
        // roomService can withdraw
        float roomServiceCanUse = (roomServiceRequest >= roomServiceLimit) ? roomServiceLimit : roomServiceRequest;
        // get MAXLimit of OPD and IPD
        float ipdMaxLimit = budgetRepository.getIpdLimit(userId);
        float opdMaxLimit = budgetRepository.getOpdLimit(userId);
        // get Used OPD and IPD
        float usedOpd = (expensesRepository.getUseOpd(userId) != null) ? expensesRepository.getUseOpd(userId) : 0.0f;
        float usedIpd = (expensesRepository.getUseIpd(userId) != null) ? expensesRepository.getUseIpd(userId) : 0.0f;
        // calculate for defined Limit can Withdraw expense
        float ipdLimit = ipdMaxLimit - usedIpd;
        float opdLimit = opdMaxLimit - usedOpd;
        // health cost request select by type
        float healthCostRequest = (type1.equals("ipd")) ? expensesRequest.getIpd() : expensesRequest.getOpd();
        // get health cost limit by type
        float healthCostLimit = (type1.equals("ipd")) ? ipdLimit : opdLimit;
        // Sum all cost request
        float healthCost = healthCostRequest + roomServiceCanUse;
        float canWithdraw = 0.0f;
        if (healthCostLimit <= 0) {
            return "คงเบิกได้ 0 บาท";
        }
        if (healthCostLimit >= healthCost) {
            canWithdraw = healthCost;
        } else {
            canWithdraw = healthCostLimit;
        }

        Expenses expense = Expenses.builder()
                .employee(employee)
                .ipd(expensesRequest.getIpd())
                .opd(expensesRequest.getOpd())
                .roomService(expensesRequest.getRoomService())
                .canWithdraw(canWithdraw)
                .days(expensesRequest.getDays())
                .startDate(expensesRequest.getStartDate())
                .endDate(expensesRequest.getEndDate())
                .dateOfAdmission(new Date())
                .description(expensesRequest.getDescription())
                .remark(expensesRequest.getRemark())
                .build();
        return expensesRepository.save(expense);
    }

    public Map<String, Double> getTotalExpense(Long uid) {
        // get MAXLimit of OPD and IPD
        float ipdMaxLimit = budgetRepository.getIpdLimit(uid);
        System.out.println("ipdMaxLimit "+ipdMaxLimit);
        float opdMaxLimit = budgetRepository.getOpdLimit(uid);
        System.out.println("opdMaxLimit " + opdMaxLimit);

        // useed OPD and IPD
        float usedOpd = (expensesRepository.getUseOpd(uid) != null) ? expensesRepository.getUseOpd(uid) : 0.0f;
        System.out.println("usedOpd"+usedOpd);
        
        float usedIpd = (expensesRepository.getUseIpd(uid) != null) ? expensesRepository.getUseIpd(uid) : 0.0f;
        System.out.println("usedIpd"+ usedIpd);

        float opdRemain = opdMaxLimit - usedOpd;
        System.out.println("opdRemain"+ opdRemain);

        float ipdRemain = ipdMaxLimit - usedIpd;
        System.out.println("ipdRemain"+ ipdRemain);

        float roomService = budgetRepository.getPerDay(uid);
        Map<String, Double> responseData = new HashMap<>();
        responseData.put("opd", (double) opdRemain);
        responseData.put("ipd", (double) ipdRemain);
        responseData.put("room", (double) roomService);

        return responseData;
    }
    
    public Map<String, Double> getExpenseRemainingByYear(Long uid,Integer year) {
        // get MAXLimit of OPD and IPD
        float ipdMaxLimit = budgetRepository.getIpdLimit(uid);
        System.out.println("ipdMaxLimit "+ipdMaxLimit);
        float opdMaxLimit = budgetRepository.getOpdLimit(uid);
        System.out.println("opdMaxLimit " + opdMaxLimit);

        // useed OPD and IPD
        Float usedOpd = expensesRepository.getUseOpdByYear(uid, year);
        float usedOpdValue = usedOpd != null ? usedOpd.floatValue() : 0.0f;
        System.out.println("usedOpd "+usedOpdValue);
        
        Float usedIpd = expensesRepository.getUseIpdByYear(uid, year);
        float usedIpdValue = usedIpd != null ? usedIpd.floatValue() : 0.0f;
        System.out.println("usedIpd "+ usedOpdValue);

        float opdRemain = opdMaxLimit - usedOpdValue;
        System.out.println("opdRemain "+ opdRemain);

        float ipdRemain = ipdMaxLimit - usedIpdValue;
        System.out.println("ipdRemain "+ ipdRemain);

        float roomService = budgetRepository.getPerDay(uid);
        Map<String, Double> responseData = new HashMap<>();
        responseData.put("opd", (double) opdRemain);
        responseData.put("ipd", (double) ipdRemain);
        responseData.put("room", (double) roomService);

        return responseData;
    }

    public Optional<Expenses> getExpenseById(Long id) {
        return expensesRepository.findById(id);
    }

}
