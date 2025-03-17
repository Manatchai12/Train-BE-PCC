package com.pcc.wellfare.CSVRepresentation;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeptCsvRepresentation {
	
	@CsvBindByName(column = "DeptID")
	private String deptid;
	
	@CsvBindByName(column = "Code")
	private String code;
	
	@CsvBindByName(column = "Company")
	private String company;
	
	@CsvBindByName(column = "EDivision")
	private String edivision;
	
	@CsvBindByName(column = "TDivision")
	private String tdivision;
	
	@CsvBindByName(column = "DivisionID")
	private String divisionid;
	
	@CsvBindByName(column = "EDept")
	private String edept;
	
	@CsvBindByName(column = "TDept")
	private String tdept;
	
	@CsvBindByName(column = "DeptCode")
	private String deptcode;
	
	@CsvBindByName(column = "Remark")
	private String remark;
}
