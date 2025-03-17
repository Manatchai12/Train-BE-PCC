package com.pcc.wellfare.repository;

import com.pcc.wellfare.model.Employee;
import com.pcc.wellfare.model.Expenses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {

    @Query(value = "SELECT SUM(e.can_withdraw) AS total_opd, SUM(ipd) AS total_ipd " +
            "FROM Expenses e " +
            "WHERE e.user_id = CAST(?1 AS bigint) AND EXTRACT(YEAR FROM e.date_of_admission) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY e.user_id", nativeQuery = true)
    List<Object[]> getUse(Long empId);


    @Query(value = "SELECT SUM(e.can_withdraw) AS total_opd " +
            "FROM Expenses e " +
            "WHERE e.user_id = CAST(?1 AS bigint) AND EXTRACT(YEAR FROM e.date_of_admission) = EXTRACT(YEAR FROM CURRENT_DATE) and opd != 0" , nativeQuery = true)
    Float getUseOpd(Long userId);
    
    @Query(value = "SELECT SUM(e.can_withdraw) AS total_ipd " +
            "FROM Expenses e " +
            "WHERE e.user_id = CAST(?1 AS bigint) AND EXTRACT(YEAR FROM e.date_of_admission) = EXTRACT(YEAR FROM CURRENT_DATE) and ipd != 0" , nativeQuery = true)
    Float getUseIpd(Long userId);
    
    @Query(value = "SELECT SUM(can_withdraw) " +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "AND opd != 0 ", nativeQuery = true)
    Float getUseOpdByYear(Long uid,Integer year);
    
    @Query(value = "SELECT SUM(can_withdraw) " +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "AND ipd != 0 ", nativeQuery = true)
    Float getUseIpdByYear(Long uid,Integer year);

    


//    List<Expenses> findByEmpId(Long empid);
    
    List<Expenses> findByDateOfAdmissionIsNotNull();

    List<Expenses> findByEmployeeUserId(Long userId);


    Page<Expenses> findAllByEmployeeUserId(Long userId, Pageable pageable);
    
    @Query(value = "UPDATE Employee e SET e.budget_id = NULL WHERE e.budget_id = :budgetId" , nativeQuery = true)
    void updateExpenseBudgetIdToNull(Long budgetId);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(MONTH FROM date_of_admission) = :month " +
            "AND EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND ipd != 0 " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getIpdExpenseByMonthAndYear(Integer month,Integer year);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(MONTH FROM date_of_admission) = :month " +
            "AND EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND opd != 0 " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getOpdExpenseByMonthAndYear(Integer month,Integer year);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND ipd != 0 " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getIpdExpenseByYear(Integer year);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND opd != 0 " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getOpdExpenseByYear(Integer year);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getAllExpenseByYear(Integer year);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(MONTH FROM date_of_admission) = :month " +
            "AND EXTRACT(YEAR FROM date_of_admission) = :year " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getAllExpenseByMonthAndYear(Integer month,Integer year);
    
    List<Expenses> findAllByEmployee(Employee employee);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "ORDER BY date_of_admission ASC ", nativeQuery = true)
    List<Expenses> getAllExpenseByYearAndUser(Integer year, Long uid);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(MONTH FROM date_of_admission) = :month " +
            "AND user_id = CAST(:uid AS bigint) " +
            "ORDER BY date_of_admission ASC ", nativeQuery = true)
    List<Expenses> getAllExpenseByMonthAndUser(Integer month, Long uid);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "AND ipd != 0 "+
            "ORDER BY date_of_admission ASC ", nativeQuery = true)
    List<Expenses> getIpdExpenseByYearAndUser(Integer year, Long uid);

    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "AND opd != 0 "+
            "ORDER BY date_of_admission ASC ", nativeQuery = true)
    List<Expenses> getOpdExpenseByYearAndUser(Integer year, Long uid);

    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(MONTH FROM date_of_admission) = :month " +
            "AND EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "AND opd != 0 " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getOpdExpenseByMonthAndYearAndUser(Integer month,Integer year, Long uid);
    
    @Query(value = "SELECT *" +
            "FROM Expenses " +
            "WHERE EXTRACT(MONTH FROM date_of_admission) = :month " +
            "AND EXTRACT(YEAR FROM date_of_admission) = :year " +
            "AND user_id = CAST(:uid AS bigint) " +
            "AND ipd != 0 " +
            "ORDER BY date_of_admission ASC", nativeQuery = true)
    List<Expenses> getIpdExpenseByMonthAndYearAndUser(Integer month,Integer year, Long uid);
}
