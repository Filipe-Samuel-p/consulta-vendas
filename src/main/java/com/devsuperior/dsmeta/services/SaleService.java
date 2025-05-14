package com.devsuperior.dsmeta.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import com.devsuperior.dsmeta.dto.ReportDTO;
import com.devsuperior.dsmeta.dto.SummaryDTO;
import com.devsuperior.dsmeta.projections.ReportProjection;
import com.devsuperior.dsmeta.projections.SummaryProjection;
import com.devsuperior.dsmeta.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SaleService {

	@Autowired
	private SaleRepository repository;

	public Page<ReportDTO> getReport(String minDateStr, String maxDateStr, String name, Pageable pageable) {
		LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());

		LocalDate maxDate = maxDateStr != null && !maxDateStr.isEmpty()
				? LocalDate.parse(maxDateStr) : today;

		LocalDate minDate = minDateStr != null && !minDateStr.isEmpty()
				? LocalDate.parse(minDateStr) : maxDate.minusYears(1L);

		String nameParam = name != null ? name : "";

		Page<ReportProjection> page = repository.reportSQL(minDate, maxDate, nameParam, pageable);

		return page.map(x -> new ReportDTO(x.getId(), x.getDate(), x.getAmount(), x.getSellerName()));
	}

	public List<SummaryDTO> getSummary(String minDateStr, String maxDateStr) {
		LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());

		LocalDate maxDate = maxDateStr != null && !maxDateStr.isEmpty()
				? LocalDate.parse(maxDateStr) : today;

		LocalDate minDate = minDateStr != null && !minDateStr.isEmpty()
				? LocalDate.parse(minDateStr) : maxDate.minusYears(1L);

		List<SummaryProjection> list = repository.summarySQL(minDate, maxDate);

		return list.stream()
				.map(x -> new SummaryDTO(x.getSellerName(), x.getTotal()))
				.collect(Collectors.toList());
	}
}