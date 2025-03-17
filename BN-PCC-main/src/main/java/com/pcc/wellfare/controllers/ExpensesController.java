package com.pcc.wellfare.controllers;

import com.pcc.wellfare.model.Expenses;
import com.pcc.wellfare.repository.BudgetRepository;
import com.pcc.wellfare.repository.ExpensesRepository;
import com.pcc.wellfare.requests.ExpensesRequest;
import com.pcc.wellfare.response.ApiResponse;
import com.pcc.wellfare.response.ResponseData;
import com.pcc.wellfare.service.ExpensesService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/expenses")
public class ExpensesController {

	private final ExpensesService expensesService;
	private final BudgetRepository budgetRepository;
	private final ExpensesRepository expensesRepository;

	public ExpensesController(ExpensesRepository expensesRepository, ExpensesService expensesService,
			BudgetRepository budgetRepository) {
		this.expensesService = expensesService;
		this.budgetRepository = budgetRepository;
		this.expensesRepository = expensesRepository;
	}

	// @GetMapping("/expenses/{userId}")
	// public List<EmployeeExpensesRepository> getExpensesByUserId(@PathVariable
	// Long userId) {
	// List<Expenses> expensesList = expensesService.getExpensesByUserId(userId);
	// Map<Long, EmployeeExpensesRepository> employeeExpensesMap = new HashMap<>();

	// for (Expenses expenses : expensesList) {
	// Long employeeId = expenses.getEmployee().getUserId();
	// if (!employeeExpensesMap.containsKey(employeeId)) {
	// EmployeeExpensesRepository employeeExpensesRepository = new
	// EmployeeExpensesRepository();
	// employeeExpensesRepository.setId(expenses.getId());
	// employeeExpensesRepository.setEmployee(expenses.getEmployee());
	// employeeExpensesRepository.setExpensesList(new ArrayList<>());
	// employeeExpensesMap.put(employeeId, employeeExpensesRepository);
	// }
	// employeeExpensesMap.get(employeeId).getExpensesList().add(expenses);
	// }

	// return new ArrayList<>(employeeExpensesMap.values());
	// }

	// @GetMapping("/user/{userId}")
	// public List<Expenses> getByUserId(@PathVariable Long userId) {
	// return expensesService.getExpensesByUserId(userId);
	// }

	@PostMapping(value = "/create")
	public ResponseEntity<ApiResponse> createExpenses(@RequestBody ExpensesRequest expensesRequest,
			@RequestParam Long userId) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Object expenses = expensesService.create(expensesRequest, userId);
			data.setResult(expenses);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	// @PutMapping(value = "/update/{expensesId}")
	// public ResponseEntity<ApiResponse> updateExpenses(@RequestBody
	// ExpensesRequest expensesRequest,
	// @RequestParam Long userId, @PathVariable Long expensesId) {
	// ApiResponse response = new ApiResponse();
	// ResponseData data = new ResponseData();
	// try {
	// Object expenses = expensesService.update(expensesRequest, userId,
	// expensesId);
	// data.setResult(expenses);
	// response.setResponseMessage("แก้ไขข้อมูลเรียบร้อย");
	// response.setResponseData(data);
	// return ResponseEntity.ok().body(response);
	// } catch (Exception e) {
	// response.setResponseMessage(e.getMessage());
	// return ResponseEntity.internalServerError().body(response);
	// }
	// }

	@PutMapping(value = "/update/{expensesId}")
	public ResponseEntity<ApiResponse> updateExpenses(@RequestBody ExpensesRequest expensesRequest,
			@PathVariable Long expensesId) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Object expenses = expensesService.update(expensesRequest, expensesId);
			data.setResult(expenses);
			response.setResponseMessage("แก้ไขข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@GetMapping(value = "/getAllExpenseInUsed")
	public ResponseEntity<ApiResponse> getAllExpenseInUsed() {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			List<Expenses> expensesInused = expensesService.findAllExpensesWithDateOfAdmissionNotNull();
			data.setResult(expensesInused);
			response.setResponseMessage("ดึงข้อมูลสำเร็จ");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@GetMapping(value = "/getExpenseRemaining")
	public ResponseEntity<ApiResponse> getTotal(Long userId) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Object budgets = expensesService.getTotalExpense(userId);
			data.setResult(budgets);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}
	
	@GetMapping(value = "/getExpenseRemaining/{uid}/{year}")
	public ResponseEntity<ApiResponse> getRemaingByYear(
			@PathVariable("uid")Long userId,
			@PathVariable("year") Integer year) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Object budgets = expensesService.getExpenseRemainingByYear(userId,year);
			data.setResult(budgets);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@GetMapping(value = "/info")
	public ResponseEntity<ApiResponse> getInfo(Long id) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();

		try {
			Optional<Expenses> expense = expensesService.findbyid(id);
			data.setResult(expense);
			response.setResponseData(data);
			response.setResponseMessage("สำเร็จ");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@GetMapping(value = "/getExpenses")
	public ResponseEntity<ApiResponse> getExpenses(Long userId) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Object budgets = expensesService.getExpenses(userId);
			data.setResult(budgets);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	private double parseDoubleWithComma(String value) {
		value = value.replaceAll(",", "");
		return Double.parseDouble(value);
	}

	@DeleteMapping(value = "/deleteExpenses/{expensesId}")
	public ResponseEntity<ApiResponse> deleteExpenses(@PathVariable Long expensesId) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Optional<Expenses> expensesOptional = expensesRepository.findById(expensesId);

			if (expensesOptional.isPresent()) {
				Expenses expenses = expensesOptional.get();
				expensesRepository.delete(expenses);

				response.setResponseMessage("ลบข้อมูลเรียบร้อย");
				response.setResponseData(data);
				return ResponseEntity.ok().body(response);
			} else {
				response.setResponseMessage("ไม่พบข้อมูลที่ต้องการลบ");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			response.setResponseMessage("เกิดข้อผิดพลาดในระหว่างการลบข้อมูล");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping(value = "/getExpenseByPage")
	public Page<Expenses> getExpenseByPage(Pageable pageeble) {
		return expensesService.getAllExpenseByPage(pageeble);
	}

	@GetMapping("/searchExpenses/{userId}")
	public Map<String, Object> findByEmpid(@PathVariable Long userId) {
		Map<String, Object> expensesList = expensesService.getExpensesById(userId);
		return expensesList;
	}

	@GetMapping("/getExpenseByPage/filter")
	public Page<Expenses> getExpensesByPageAndFilter(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchType,
			@RequestParam(required = false) String searchValue) {
		Pageable pageable = PageRequest.of(page, size);
		if (searchType.equals("name")) {
			return expensesService.getExpensesByName(pageable, searchValue);
		} else if (searchType.equals("code")) {
			return expensesService.getExpenseByEmpCode(pageable, searchValue);
		} else {
			return Page.empty();
		}
//        if(searchType == "name") {
//        	
//        }
//        return expensesService.getAllExpenseByFilter(pageable,searchType,searchValue);
	}

	@GetMapping(value = "/getExpense/{id}")
    ResponseEntity<ApiResponse> getExpenseById(@PathVariable Long id) {
        ApiResponse response = new ApiResponse();
        ResponseData data = new ResponseData();
        try {
            Optional<Expenses> expense = expensesService.getExpenseById(id);
            if(expense.isPresent()) {
                data.setResult(expense);
                response.setResponseMessage("สำเร็จ");
                response.setResponseData(data);
                return ResponseEntity.ok().body(response);
            }
            else {
                response.setResponseMessage("ไม่พบข้อมูลที่ต้องการ");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setResponseMessage("เกิดข้อผิดพลาด");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
