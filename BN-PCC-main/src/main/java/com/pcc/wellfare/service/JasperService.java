package com.pcc.wellfare.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.pcc.wellfare.model.Employee;
import com.pcc.wellfare.model.Expenses;
import com.pcc.wellfare.repository.EmployeeRepository;
import com.pcc.wellfare.repository.ExpensesRepository;
import com.pcc.wellfare.requests.ExpenseHistoryRequest;

import lombok.AllArgsConstructor;
import lombok.var;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@AllArgsConstructor
public class JasperService {

	@Autowired
	private final ExpensesRepository expensesRepository;
	
	@Autowired
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	private ResourceLoader resourceLoader;

	public byte[] printExpenseHistoryReport(Integer month, Integer year, String type,String reportType) throws JRException, IOException {
		Resource resource = resourceLoader.getResource("classpath:report/ExpenseHistory.jrxml");
	    InputStream expenseHistoryReportStream = resource.getInputStream();

	    List<Expenses> expenseList = new ArrayList<>();
	    String thaiMonth = "";
	    String buddhistYear = String.valueOf(year + 543);
	    Map<String, Object> params = new HashMap<String, Object>();
	    String typeName = thaiHealthType(type);


	    if (reportType.equals("byYear")) {
	        expenseList = getExpenseByYear(type, year);
	        thaiMonth = (year.equals(LocalDate.now().getYear())) ? getThaiMonth(1) + " - " + getThaiMonth(LocalDate.now().getMonth().getValue())
	        :getThaiMonth(1) + " - " + getThaiMonth(12) ;
	        
	    } else {
	        expenseList = getExpenseByMonth(type, month, year);
	        thaiMonth = getThaiMonth(month);
	    }

	    List<ExpenseHistoryRequest> expenseHistoryList = new ArrayList<>();
	    for (Expenses expense : expenseList) {
	        ExpenseHistoryRequest expenseHistoryModel = ExpenseHistoryRequest
	                .builder()
	                .dateCount(expense.getDays())
	                .dateOfAdmidtion(BuddhistDateInThaiFomat(expense.getDateOfAdmission()))
	                .description(expense.getDescription())
	                .endDate(BuddhistDateInThaiFomat(expense.getEndDate()))
	                .firstname(expense.getEmployee().getTname())
	                .remark(expense.getRemark())
	                .startDate(BuddhistDateInThaiFomat(expense.getStartDate()))
	                .surname(expense.getEmployee().getTsurname())
	                .withdraw((double) expense.getCanWithdraw())
	                .prefix(expense.getEmployee().getTprefix())
	                .build();
	        expenseHistoryList.add(expenseHistoryModel);
	    }
	    
	    params.put("expenseMonth", thaiMonth);
	    params.put("expenseYear", buddhistYear);
	    params.put("expenseType", typeName);
		
		JasperReport jasperReport = JasperCompileManager.compileReport(expenseHistoryReportStream);
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(expenseHistoryList);
		
		params.put("ExpenseHistoryDataSet", dataSource);
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
	    JasperExportManager.exportReportToPdfFile(jasperPrint, "Report.pdf");
	    File filePdf = new File("Report.pdf");
	    return FileUtils.readFileToByteArray(filePdf);
	}
	
	public String printExpenseHistoryReportBase64(Integer month, Integer year, String type, String reportType) throws JRException, IOException {
	    Resource resource = resourceLoader.getResource("classpath:report/ExpenseHistory.jrxml");
	    InputStream expenseHistoryReportStream = resource.getInputStream();

	    List<Expenses> expenseList = new ArrayList<>();
	    String thaiMonth = "";
	    String buddhistYear = String.valueOf(year + 543);
	    Map<String, Object> params = new HashMap<String, Object>();
	    String typeName = thaiHealthType(type);


	    if (reportType.equals("byYear")) {
	        expenseList = getExpenseByYear(type, year);
	        thaiMonth = (year.equals(LocalDate.now().getYear())) ? getThaiMonth(1) + " - " + getThaiMonth(LocalDate.now().getMonth().getValue())
	        :getThaiMonth(1) + " - " + getThaiMonth(12) ;
	        
	    } else {
	        expenseList = getExpenseByMonth(type, month, year);
	        thaiMonth = getThaiMonth(month);
	    }

	    List<ExpenseHistoryRequest> expenseHistoryList = new ArrayList<>();
	    for (Expenses expense : expenseList) {
	        ExpenseHistoryRequest expenseHistoryModel = ExpenseHistoryRequest
	                .builder()
	                .dateCount(expense.getDays())
	                .dateOfAdmidtion(BuddhistDateInThaiFomat(expense.getDateOfAdmission()))
	                .description(expense.getDescription())
	                .endDate(BuddhistDateInThaiFomat(expense.getEndDate()))
	                .firstname(expense.getEmployee().getTname())
	                .remark(expense.getRemark())
	                .startDate(BuddhistDateInThaiFomat(expense.getStartDate()))
	                .surname(expense.getEmployee().getTsurname())
	                .withdraw((double) expense.getCanWithdraw())
	                .prefix(expense.getEmployee().getTprefix())
	                .build();
	        expenseHistoryList.add(expenseHistoryModel);
	    }
	    
	    params.put("expenseMonth", thaiMonth);
	    params.put("expenseYear", buddhistYear);
	    params.put("expenseType", typeName);
	    JasperReport jasperReport = JasperCompileManager.compileReport(expenseHistoryReportStream);
	    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(expenseHistoryList);

	    params.put("ExpenseHistoryDataSet", dataSource);
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
	    byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
	    String base64String = Base64.getEncoder().encodeToString(bytes);

	    return base64String;
	}
	
	public String printExpenseHistoryReportByperson(Long uid, String reportType, String Htype, Integer month, Integer year) throws IOException, JRException {
		//prepare zone
		Resource resource = resourceLoader.getResource("classpath:report/ExpenseHistoryByPerson.jrxml");
	    InputStream expenseHistoryReportStream = resource.getInputStream();
	    Map<String, Object> params = new HashMap<String, Object>();
	    Employee emp = employeeRepository.findById(uid).get();
	    List<ExpenseHistoryRequest> expenseHistoryList = new ArrayList<>();
	    String thaiMonth = "";
	    String buddhistYear = String.valueOf(year + 543);
	    String typeName = thaiHealthType(Htype);
	    
	    //param insert zone
	    if (reportType.equals("byYear")) {
	    	thaiMonth = (year.equals(LocalDate.now().getYear())) ? getThaiMonth(1) + " - " + getThaiMonth(LocalDate.now().getMonth().getValue())
	        :getThaiMonth(1) + " - " + getThaiMonth(12) ;
	    }else {
	        thaiMonth = getThaiMonth(month);
	    }
	    List<Expenses> expenseList = getExpenseByPerson(uid,reportType,Htype,month,year);
	    for (Expenses expense : expenseList) {
	        ExpenseHistoryRequest expenseHistoryModel = ExpenseHistoryRequest
	                .builder()
	                .dateCount(expense.getDays())
	                .dateOfAdmidtion(BuddhistDateInThaiFomat(expense.getDateOfAdmission()))
	                .description(expense.getDescription())
	                .endDate(BuddhistDateInThaiFomat(expense.getEndDate()))
	                .remark(expense.getRemark())
	                .startDate(BuddhistDateInThaiFomat(expense.getStartDate()))
	                .withdraw((double) expense.getCanWithdraw())
	                .build();
	        expenseHistoryList.add(expenseHistoryModel);
	    }
	    
	    params.put("tPrefix", emp.getTprefix());
	    params.put("tFirstname",emp.getTname());
	    params.put("tSname",emp.getTsurname());
	    params.put("expenseMonth", thaiMonth);
	    params.put("expenseType", typeName);
	    params.put("expenseYear", buddhistYear);
	    
	    //compile zone
	    JasperReport jasperReport = JasperCompileManager.compileReport(expenseHistoryReportStream);
	    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(expenseHistoryList);
	    
	    params.put("ExpenseHistoryDataSet", dataSource);
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
	    byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
	    String base64String = Base64.getEncoder().encodeToString(bytes);

	    return base64String;
	}
	
	private List<Expenses> getExpenseByPerson(Long uid, String reportType, String Htype, Integer month, Integer year){
	    List<Expenses> expenseList = new ArrayList<>();
	    if(reportType.equals("byMonth")) {
	    	switch (Htype) {
			case "ipd": 
				return expensesRepository.getIpdExpenseByMonthAndYearAndUser(month, year, uid);
			case "opd": 
				return expensesRepository.getOpdExpenseByMonthAndYearAndUser(month, year, uid);
			case "all": 
				return expensesRepository.getAllExpenseByMonthAndUser(month, uid);
			default :
				return expenseList;
				}
	    }else {
	    	switch (Htype) {
			case "ipd": 
				return expensesRepository.getIpdExpenseByYearAndUser(year, uid);
			case "opd": 
				return expensesRepository.getOpdExpenseByYearAndUser(year, uid);
			case "all": 
				return expensesRepository.getAllExpenseByYearAndUser(year, uid);
			default :
				return expenseList;
				}
	    }
	}
	
	private List<Expenses> getExpenseByYear(String healthType,Integer year) {
	    List<Expenses> expenseList = new ArrayList<>();
	    switch (healthType) {
		case "ipd": {
			return expensesRepository.getIpdExpenseByYear(year);
		}
		case "opd": {
			return expensesRepository.getOpdExpenseByYear(year);
		}		
		case "all": {
			return expensesRepository.getAllExpenseByYear(year);
			}
		default:
			return expenseList;
		}
	   
	}
	
	private List<Expenses> getExpenseByMonth(String healthType,Integer month,Integer year) {
	    List<Expenses> expenseList = new ArrayList<>();
	    
	    switch (healthType) {
		case "ipd": {
			return expensesRepository.getIpdExpenseByMonthAndYear(month,year);
		}
		case "opd": {
			return expensesRepository.getOpdExpenseByMonthAndYear(month,year);
		}		
		case "all": {
			return expensesRepository.getAllExpenseByMonthAndYear(month,year);
			}
		default:
			return expenseList;
		}
	   
	}


	private String BuddhistDateInThaiFomat(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = sdf.format(date);

		// เพิ่มค่า 543 ในปีก่อนที่จะแสดงผลลัพธ์
		String[] parts = formattedDate.split("/");
		int day = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int year = Integer.parseInt(parts[2]) + 543;

		return String.format("%02d/%02d/%02d", day, month, year);
	}
	
	private String thaiHealthType(String type) {
		switch	(type) {
		case "ipd":
			return "ผู้ป่วยใน";
		case "opd":
			return "ผู้ป่วยนอก";
		case "all":
			return "ทั้งหมด";
		default : 
			return  "";
		}
	}

	public String getThaiMonth(int month) {
		switch (month) {
		case 1:
			return "มกราคม";
		case 2:
			return "กุมภาพันธ์";
		case 3:
			return "มีนาคม";
		case 4:
			return "เมษายน";
		case 5:
			return "พฤษภาคม";
		case 6:
			return "มิถุนายน";
		case 7:
			return "กรกฎาคม";
		case 8:
			return "สิงหาคม";
		case 9:
			return "กันยายน";
		case 10:
			return "ตุลาคม";
		case 11:
			return "พฤศจิกายน";
		case 12:
			return "ธันวาคม";
		default:
			return "";
		}
	}
}
