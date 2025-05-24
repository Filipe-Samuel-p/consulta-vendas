package com.devsuperior.dsmeta.repositories;

import java.time.LocalDate;
import java.util.List;

import com.devsuperior.dsmeta.dto.ReportDTO;
import com.devsuperior.dsmeta.dto.SummaryDTO;
import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT new com.devsuperior.dsmeta.dto.SummaryDTO(seller.name, SUM(sale.amount)) " +
            "FROM Sale sale " +
            "JOIN sale.seller seller " +
            "WHERE sale.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY seller.name")
    List<SummaryDTO> findSalesSummaryByDate(@Param("minDate") LocalDate minDate,
                                            @Param("maxDate") LocalDate maxDate);

    @Query("SELECT new com.devsuperior.dsmeta.dto.ReportDTO(s.id, s.date, s.amount, s.seller.name) " +
            "FROM Sale s " +
            "WHERE s.date BETWEEN :minDate AND :maxDate " +
            "AND LOWER(s.seller.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ReportDTO> findSalesReportByDateAndName(@Param("minDate") LocalDate minDate,
                                                 @Param("maxDate") LocalDate maxDate,
                                                 @Param("name") String name,
                                                 Pageable pageable);
}


