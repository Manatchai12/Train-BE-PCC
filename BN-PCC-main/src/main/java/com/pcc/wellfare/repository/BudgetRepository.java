package com.pcc.wellfare.repository;

import com.pcc.wellfare.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByLevel(String level);


    @Query(value = "select opd,ipd from budget b where \"level\" = (select \"level\"  " +
            "from employee e where e.user_id = CAST(?1 AS bigint))", nativeQuery = true)
    List<Object[]> getCanUse(Long empId);


    @Query(value = "SELECT CAST(opd AS float) FROM budget b WHERE b.id = (SELECT budget_id " +
            "FROM employee e WHERE e.user_id = CAST(?1 AS bigint)) LIMIT 1", nativeQuery = true)
    float getOpd(Long empId);

    @Query(value = "SELECT CAST(ipd AS float) FROM budget b WHERE b.id = (SELECT budget_id " +
            "FROM employee e WHERE e.user_id = CAST(?1 AS bigint)) LIMIT 1", nativeQuery = true)
    float getIpd(Long empId);

    @Query(value = "SELECT CAST(ค่าห้อง_ค่าอาหาร AS float) FROM budget b WHERE id = (SELECT budget_id " +
            "FROM employee e WHERE e.user_id = CAST(?1 AS bigint)) LIMIT 1", nativeQuery = true)
    float getPerDay(Long empId);
    
    @Query(value = "SELECT CAST(ipd AS float) FROM budget b WHERE id = (SELECT budget_id " +
            "FROM employee e WHERE e.user_id = CAST(?1 AS bigint)) LIMIT 1", nativeQuery = true)
    float getIpdLimit(Long uid);
    
    @Query(value = "SELECT CAST(opd AS float) FROM budget b WHERE id = (SELECT budget_id " +
            "FROM employee e WHERE e.user_id = CAST(?1 AS bigint)) LIMIT 1", nativeQuery = true)
    float getOpdLimit(Long uid);

    @Query(value = "select \"ค่าห้อง_ค่าอาหาร\" from budget b where \"level\" = (select \"level\"  " +
            "from employee e where e.empId = CAST(?1 AS bigint))", nativeQuery = true)
    List<Object[]> getCanUseDay(Long empId);



}
