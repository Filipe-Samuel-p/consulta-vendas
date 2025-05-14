package com.devsuperior.dsmeta.repositories;

import java.time.LocalDate;
import java.util.List;

import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.projections.ReportProjection;
import com.devsuperior.dsmeta.projections.SummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query(nativeQuery = true, value =
            "SELECT sale.id, sale.date, sale.amount, seller.name AS sellerName " +
                    "FROM tb_sales sale " +
                    "INNER JOIN tb_seller seller ON sale.seller_id = seller.id " +
                    "WHERE (:minDate IS NULL OR sale.date >= :minDate) " +
                    "AND (:maxDate IS NULL OR sale.date <= :maxDate) " +
                    "AND (LOWER(seller.name) LIKE LOWER(CONCAT('%', :name, '%')))",
            countQuery =
                    "SELECT COUNT(*) " +
                            "FROM tb_sales sale " +
                            "INNER JOIN tb_seller seller ON sale.seller_id = seller.id " +
                            "WHERE (:minDate IS NULL OR sale.date >= :minDate) " +
                            "AND (:maxDate IS NULL OR sale.date <= :maxDate) " +
                            "AND (LOWER(seller.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<ReportProjection> reportSQL(LocalDate minDate, LocalDate maxDate, String name, Pageable pageable);


    @Query(nativeQuery = true, value =
            "SELECT seller.name AS sellerName, SUM(sale.amount) AS total " +
                    "FROM tb_sales sale " +
                    "INNER JOIN tb_seller seller ON sale.seller_id = seller.id " +
                    "WHERE (:minDate IS NULL OR sale.date >= :minDate) " +
                    "AND (:maxDate IS NULL OR sale.date <= :maxDate) " +
                    "GROUP BY seller.name " +
                    "ORDER BY seller.name")
    List<SummaryProjection> summarySQL(LocalDate minDate, LocalDate maxDate);
}

