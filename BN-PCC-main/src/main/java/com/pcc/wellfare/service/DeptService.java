package com.pcc.wellfare.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.pcc.wellfare.CSVRepresentation.DeptCsvRepresentation;
import com.pcc.wellfare.model.Dept;
import com.pcc.wellfare.model.Employee;
import com.pcc.wellfare.repository.DeptRepository;
import com.pcc.wellfare.requests.CreateDeptRequests;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeptService {

	private final DeptRepository deptrepository;

	public Dept createDept(CreateDeptRequests createDeptRequests) {
		Dept dept = Dept
				.builder()
				.code(createDeptRequests.getCode())
				.deptid(createDeptRequests.getDeptid())
				.company(createDeptRequests.getCompany())
				.edivision(createDeptRequests.getEdivision())
				.tdivision(createDeptRequests.getTdivision())
				.divisionid(createDeptRequests.getDivisionid())
				.remark(createDeptRequests.getRemark())
				.edept(createDeptRequests.getEdept())
				.tdept(createDeptRequests.getTdept())
				.deptcode(createDeptRequests.getDeptcode())
				.build();
		return deptrepository.save(dept);
	}

	public Dept updateDept(String code, Dept updatedDept) {
		Optional<Dept> existingDeptOptional = deptrepository.findById(code);
		if (existingDeptOptional.isPresent()) {
			Dept existingDept = existingDeptOptional.get();
			existingDept.setCompany(updatedDept.getCompany());
			existingDept.setEdivision(updatedDept.getEdivision());
			existingDept.setTdivision(updatedDept.getTdivision());
			existingDept.setDivisionid(updatedDept.getDivisionid());
			existingDept.setEdept(updatedDept.getEdept());
			existingDept.setTdept(updatedDept.getTdept());
			existingDept.setDeptcode(updatedDept.getDeptcode());
			existingDept.setRemark(updatedDept.getRemark());
			return deptrepository.save(existingDept);
		} else {
			return null;
		}
	}

	public List<Dept> getAllDept() {
		return deptrepository.findAll();
	}

	public Optional<Dept> getDeptById(String deptid) {
		return deptrepository.findById(deptid);
	}

	public void deleteDept(String deptid) {
		deptrepository.deleteById(deptid);
	}

	public Integer uploadDepts(MultipartFile file) throws IOException {
		Set<Dept> depts = parseCsv(file);
		deptrepository.saveAll(depts);
		return depts.size();
	}

	private Set<Dept> parseCsv(MultipartFile file) throws IOException {
		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream() ,StandardCharsets.UTF_8))) {
			HeaderColumnNameMappingStrategy<DeptCsvRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
			strategy.setType(DeptCsvRepresentation.class);
			CsvToBean<DeptCsvRepresentation> csvToBean = new CsvToBeanBuilder<DeptCsvRepresentation>(reader)
					.withMappingStrategy(strategy)
					.withIgnoreEmptyLine(true)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			return csvToBean.parse()
					.stream()
					.map(csvLine -> Dept.builder()
							.divisionid(csvLine.getDivisionid())
							.remark(csvLine.getRemark())
							.edivision(csvLine.getEdivision())
							.tdivision(csvLine.getTdivision())
							.edept(csvLine.getEdept())
							.tdept(csvLine.getTdept())
							.company(csvLine.getCompany())
							.deptcode(csvLine.getDeptcode())
							.deptid(csvLine.getDeptid())
							.code(csvLine.getCode())
							.build())
					.collect(Collectors.toSet());
		}
	}
}
