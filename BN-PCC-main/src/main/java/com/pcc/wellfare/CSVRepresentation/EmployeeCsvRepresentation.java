package com.pcc.wellfare.CSVRepresentation;

import java.util.Date;

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
public class EmployeeCsvRepresentation {
	
	@CsvBindByName(column = "EmpID")
	private String empid;
	
	@CsvBindByName(column = "Tprefix")
	private String tprefix;
	
	@CsvBindByName(column = "TName")
	private String tname;
	
	@CsvBindByName(column = "Tsurname")
	private String tsurname;
	
	@CsvBindByName(column = "TPosition")
	private String tposition;
	
	@CsvBindByName(column = "Level")
	private String level;
	
	@CsvBindByName(column = "E-Mail")
	private String email;
	
	@CsvBindByName(column = "Status")
	private String status;
	
	@CsvBindByName(column = "Remark")
	private String remark;
	
	@CsvBindByName(column = "Code")
	private String code;
	
	@CsvBindByName(column = "StartDate")
	private String startdate;

}
