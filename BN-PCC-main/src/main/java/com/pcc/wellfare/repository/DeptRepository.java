package com.pcc.wellfare.repository;

import com.pcc.wellfare.model.Dept;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeptRepository extends JpaRepository<Dept, String> {

    Optional<Dept> findByCode(String deptcode);

}
