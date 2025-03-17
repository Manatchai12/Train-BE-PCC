package com.pcc.wellfare.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pcc.wellfare.model.Employee;
import com.pcc.wellfare.repository.EmployeeRepository;
import com.pcc.wellfare.requests.CreateEmployeeRequest;
import com.pcc.wellfare.requests.EditEmployeeRequest;
import com.pcc.wellfare.response.ApiResponse;
import com.pcc.wellfare.response.ResponseData;
import com.pcc.wellfare.service.EmployeeService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeContoller {

	@Autowired
	private EmployeeRepository employeeRepository;
	private final EmployeeService employeeService;

	public EmployeeContoller(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping(value = "/findAll")
	public ResponseEntity<ApiResponse> findAllEmp() {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			List<Employee> allEmps = employeeService.getAllEmployees();
			data.setResult(allEmps);
			response.setResponseData(data);
			response.setResponseMessage("สำเร็จ");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@PostMapping(value = "/create")
	public ResponseEntity<ApiResponse> createEmployee(@RequestBody CreateEmployeeRequest createEmployeeRequest) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		System.out.println(createEmployeeRequest);
		try {
			Employee employee = employeeService.createEmployee(createEmployeeRequest);
			data.setResult(employee);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@PutMapping("/editEmployee")
	public ResponseEntity<ApiResponse> updateUser(@RequestParam Long userid,
			@RequestBody EditEmployeeRequest editEmployeeRequest) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			Employee updatedEmployee = employeeService.updateEmployee(userid, editEmployeeRequest);
			data.setResult(updatedEmployee);
			response.setResponseMessage("กรอกข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

	@GetMapping
	public ResponseEntity<List<Employee>> getAllEmployees() {
		List<Employee> employees = employeeService.getAllEmployees();
		return new ResponseEntity<>(employees, HttpStatus.OK);
	}

	@GetMapping("/search/{userid}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long userid) {
		return employeeService.searchById(userid).map(employee -> new ResponseEntity<>(employee, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{userid}")
	public ResponseEntity<ApiResponse> delete(@RequestParam Long userid) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		String employee = employeeService.deleteEmployee(userid);
		if (employee != null) {
			data.setResult(employee);
			response.setResponseMessage("ลบข้อมูลเรียบร้อย");
			response.setResponseData(data);
			return ResponseEntity.ok().body(response);
		} else {
			response.setResponseMessage("ไม่พบข้อมูลที่ตรงกับ ID ที่ระบุ");
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping("/searchUser")
	public Object search(@RequestParam(required = false) Long empid, @RequestParam(required = false) String tprefix,
			@RequestParam(required = false) String name, @RequestParam(required = false) String tposition,
			@RequestParam(required = false) String deptcode, @RequestParam(required = false) String remark,
			@RequestParam(required = false) String status, @RequestParam(required = false) String email)
			throws JsonProcessingException {
		return employeeService.searchUser(empid, tprefix, name, tposition, deptcode, remark, status, email);
	}

	@PostMapping(value = "/uploadEmps", consumes = { "multipart/form-data" })
	public ResponseEntity<Integer> uploadEmps(@RequestPart("file") MultipartFile file) throws IOException {
		return ResponseEntity.ok(employeeService.uploadEmps(file));
	}

	@GetMapping(value = "/getEmpsByPage")
	public Page<Employee> getEmpsByPage(Pageable pageeble) {
		return employeeService.getAllEmpsByPage(pageeble);
	}

	@GetMapping(value = "/seacrhUser/byNames")
	public ResponseEntity<ApiResponse> SearchFnameAndOrSnameStartingWith(@RequestParam String searchTerm) {
		ApiResponse response = new ApiResponse();
		ResponseData data = new ResponseData();
		try {
			if (searchTerm.contains(" ")) {
				// หากมีช่องว่าง แยกชื่อและนามสกุลออกจากกัน
				String[] names = searchTerm.split("\\s+", 2);
				String name = names[0];
				String surnameStart = names[1];
				List<Employee> emps = employeeService.findEmployeesByNameAndSurnameStartsWith(name, surnameStart);
				data.setResult(emps);
				response.setResponseMessage("Found");
				response.setResponseData(data);
				return ResponseEntity.ok(response);
			} else {
				// หากไม่มีช่องว่าง ให้ค้นหาโดยใช้ชื่อเท่านั้น
				List<Employee> emps = employeeService.findByTnameContainingOrTsurnameContaining(searchTerm, searchTerm);
				if (emps.isEmpty()) {
					response.setResponseMessage("Not Found");
					return ResponseEntity.ok(response);
				} else {
					data.setResult(emps);
					response.setResponseMessage("Found");
					response.setResponseData(data);
					return ResponseEntity.ok(response);
				}
			}
		} catch (Exception e) {
			response.setResponseMessage("ไม่พบข้อมูลที่ตรงกับที่ระบุ");
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping(value = "/seacrhUser/byEmpid")
	public ResponseEntity<ApiResponse> generateAutoComplete(@RequestParam String empid) {
		ApiResponse res = new ApiResponse();
		ResponseData data = new ResponseData();

		List<Employee> emps = employeeService.generateEmpidAutocomplete(empid);
		data.setResult(emps);
		res.setResponseMessage("Found");
		res.setResponseData(data);
		return ResponseEntity.ok(res);

	}

	@GetMapping("getIdByname")
	public Optional<Long> getIdBytnameAndSname(@RequestParam String tname, @RequestParam String sname) {
		return employeeRepository.findUserIdByTnameAndTsurname(tname, sname);
	}
}
