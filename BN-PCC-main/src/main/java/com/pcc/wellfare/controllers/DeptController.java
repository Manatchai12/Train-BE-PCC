package com.pcc.wellfare.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pcc.wellfare.model.Dept;
import com.pcc.wellfare.model.Employee;
import com.pcc.wellfare.requests.CreateDeptRequests;
import com.pcc.wellfare.response.ApiResponse;
import com.pcc.wellfare.response.ResponseData;
import com.pcc.wellfare.service.DeptService;
import com.pcc.wellfare.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/dept")
@RequiredArgsConstructor
public class DeptController {

	private final DeptService deptService;

	// public DeptContoller(DeptService deptService) {
	// this.deptService = deptService;
	// }

	@GetMapping
	public ResponseEntity<List<Dept>> getAllDept() {
		List<Dept> dept = deptService.getAllDept();
		return new ResponseEntity<>(dept, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ApiResponse> createDept(
			@RequestBody CreateDeptRequests createDeptRequests) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Dept dept = deptService.createDept(createDeptRequests);
			data.setResult(dept);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}

	}

	@PutMapping("/{code}")
	public ResponseEntity<Dept> updateDept(@PathVariable String code, @RequestBody Dept updatedDept) {
		Dept updated = deptService.updateDept(code, updatedDept);
		if (updated != null) {
			return new ResponseEntity<>(updated, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/search/{deptid}")
	public ResponseEntity<Dept> getDeptId(@PathVariable String deptid) {
		return deptService.getDeptById(deptid)
				.map(dept -> new ResponseEntity<>(dept, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{deptid}")
	public ResponseEntity<Void> deleteDept(@PathVariable String deptid) {
		deptService.deleteDept(deptid);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/uploadDepts", consumes = { "multipart/form-data" })
	public ResponseEntity<Integer> uploadDepts(
			@RequestPart("file") MultipartFile file) throws IOException {
		return ResponseEntity.ok(deptService.uploadDepts(file));
	}
}
