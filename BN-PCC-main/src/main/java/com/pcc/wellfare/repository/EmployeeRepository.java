package com.pcc.wellfare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcc.wellfare.model.Employee;

import jakarta.transaction.Transactional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmpid(String empid);

    Optional<Employee> findByEmpid(String empid);

    Optional<Employee> findById(Long userId);
    
    List<Employee> findByTnameStartingWith(String letter);
    
    Optional<Employee> findByTsurnameStartingWith(String letter);
    
    List<Employee> findByTnameEqualsAndTsurnameStartingWith(String name, String surnameStart);
    
    List<Employee> findByTnameContainingOrTsurnameContaining(String name, String surname);
   
    @Query(value = "SELECT e.user_id FROM Employee e WHERE e.tname = ?1 AND e.tsurname = ?2", nativeQuery = true)
    Optional<Long> findUserIdByTnameAndTsurname(String tname, String tsurname);
    
    @Query(value = "SELECT e.user_id FROM Employee e WHERE e.empid = ?1", nativeQuery = true)
    Optional<Long> findUserIdByEmpid(String empid);
    
    List<Employee> findByEmpidStartingWith(String empid);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE Employee SET budget_id = NULL WHERE budget_id = :budgetId", nativeQuery = true)
    void updateEmployeeBudgetIdToNull(@Param("budgetId") Long budgetId);


}
