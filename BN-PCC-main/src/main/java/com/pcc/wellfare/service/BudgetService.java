package com.pcc.wellfare.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.pcc.wellfare.CSVRepresentation.BudgetCsvRepresentation;
import com.pcc.wellfare.model.Budget;
import com.pcc.wellfare.repository.BudgetRepository;
import com.pcc.wellfare.repository.EmployeeRepository;
import com.pcc.wellfare.requests.CreateBudgetRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BudgetService {

	private final EntityManager entityManager;
	private final BudgetRepository budgetRepository;
	private final EmployeeRepository empRepository;

	public Budget create(CreateBudgetRequest createBudgetRequest) {

		Budget budget = Budget.builder()
				.opd(createBudgetRequest.getOpd())
				.ipd(createBudgetRequest.getIpd())
				.level(createBudgetRequest.getLevel())
				.room(createBudgetRequest.getRoom()).build();

		return budgetRepository.save(budget);
	}

	public Budget editBudget(Long budgetId, CreateBudgetRequest createBudgetRequest) {

		Budget budget = budgetRepository.findById(budgetId)
				.orElseThrow(() -> new RuntimeException("budgetId not found: "));

		budget.setIpd(createBudgetRequest.getIpd());
		budget.setOpd(createBudgetRequest.getOpd());
		budget.setLevel(createBudgetRequest.getLevel());
		budget.setRoom(createBudgetRequest.getRoom());

		return budgetRepository.save(budget);
	}
	
	public Integer uploadBudget(MultipartFile file) throws IOException {
        Set<Budget> Budgets = parseCsv(file);
        budgetRepository.saveAll(Budgets);
        return Budgets.size();
    }

	private Set<Budget> parseCsv(MultipartFile file) throws IOException {
		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			HeaderColumnNameMappingStrategy<BudgetCsvRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
			strategy.setType(BudgetCsvRepresentation.class);
			CsvToBean<BudgetCsvRepresentation> csvToBean = new CsvToBeanBuilder<BudgetCsvRepresentation>(reader)
					.withMappingStrategy(strategy)
					.withIgnoreEmptyLine(true)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			return csvToBean
					.parse().stream().map(csvLine -> Budget.builder()
							.level(csvLine.getLevel())
							.opd(csvLine.getOpd())
							.ipd(csvLine.getIpd())
							.room(csvLine.getRoom()).build())
					.collect(Collectors.toSet());
		}
	}
	
	public void deleteBudget(Long budgetId) {
		empRepository.updateEmployeeBudgetIdToNull(budgetId);
        budgetRepository.deleteById(budgetId);
    }
}
