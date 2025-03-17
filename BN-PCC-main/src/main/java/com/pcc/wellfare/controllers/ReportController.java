package com.pcc.wellfare.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcc.wellfare.response.ApiResponse;
import com.pcc.wellfare.response.ResponseData;
import com.pcc.wellfare.service.JasperService;

import lombok.AllArgsConstructor;
import java.net.URLEncoder;

@RestController
@RequestMapping("/report")
@AllArgsConstructor
public class ReportController {

	private final JasperService jasperService;
	
	@GetMapping("/expenseHistoryReport")
    public ResponseEntity<byte[]> printExpenseHistoryReport(
            @RequestParam(required = false , defaultValue = "0") Integer month,
            @RequestParam Integer year,
            @RequestParam String type,
            @RequestParam String reportType) {
        try {
            byte[] report = jasperService.printExpenseHistoryReport(month, year, type, reportType);
            Integer buddhistYear = year + 543;
            String filename = URLEncoder.encode("รายงานการเบิกค่ารักษาพยาบาลประจำเดือน" + jasperService.getThaiMonth(month) + buddhistYear + ".pdf", "UTF-8");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", filename);
            headers.setContentLength(report.length);
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/expenseHistoryReportBase64")
    public ResponseEntity<ApiResponse> printExpenseHistoryReportBase64(
            @RequestParam(required = false , defaultValue = "0") Integer month,
            @RequestParam Integer year,
            @RequestParam String type,
            @RequestParam String reportType) {
    	ApiResponse api = new ApiResponse();
    	ResponseData data = new ResponseData();
        try {
            String base64String = jasperService.printExpenseHistoryReportBase64(month, year, type, reportType);
            data.setResult(base64String);
            api.setResponseData(data);
            api.setResponseMessage("Success");
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/expenseHistoryReportByEmployeeBase64")
    public ResponseEntity<ApiResponse> ExpenseHistoryReportBase64ByUser(
    		@RequestParam(required = false , defaultValue = "0") Integer month,
            @RequestParam Integer year,
            @RequestParam String type,
            @RequestParam String reportType,
            @RequestParam Long uid) {
        ApiResponse api = new ApiResponse();
    	ResponseData data = new ResponseData();
        try {
            String base64String = jasperService.printExpenseHistoryReportByperson(uid, reportType, type, month, year);
            data.setResult(base64String);
            api.setResponseData(data);
            api.setResponseMessage("Success");
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
