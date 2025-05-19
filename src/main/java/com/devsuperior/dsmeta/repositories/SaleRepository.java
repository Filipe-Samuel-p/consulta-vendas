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
import org.springframework.data.repository.query.Param;


public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query(nativeQuery = true, value =
            "SELECT sale.id, sale.date, sale.amount, seller.name AS sellerName " +
                    "FROM tb_sales sale " +
                    "INNER JOIN tb_seller seller ON sale.seller_id = seller.id " +
                    "WHERE (:minDate IS NULL OR sale.date >= :minDate) " +
                    "AND (:maxDate IS NULL OR sale.date <= :maxDate) " +
                    "AND (:name IS NULL OR LOWER(seller.name) LIKE LOWER(CONCAT('%', :name, '%')))",
            countQuery =
                    "SELECT COUNT(*) " +
                            "FROM tb_sales sale " +
                            "INNER JOIN tb_seller seller ON sale.seller_id = seller.id " +
                            "WHERE (:minDate IS NULL OR sale.date >= :minDate) " +
                            "AND (:maxDate IS NULL OR sale.date <= :maxDate) " +
                            "AND (:name IS NULL OR LOWER(seller.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<ReportProjection> reportSQL(
            @Param("minDate") LocalDate minDate,
            @Param("maxDate") LocalDate maxDate,
            @Param("name") String name,
            Pageable pageable
    );

    @Query(nativeQuery = true, value =
            "SELECT seller.name AS sellerName, SUM(sale.amount) AS total " +
                    "FROM tb_sales sale " +
                    "INNER JOIN tb_seller seller ON sale.seller_id = seller.id " +
                    "WHERE (:minDate IS NULL OR sale.date >= :minDate) " +
                    "AND (:maxDate IS NULL OR sale.date <= :maxDate) " +
                    "GROUP BY seller.name " +
                    "ORDER BY seller.name")
    List<SummaryProjection> summarySQL(
            @Param("minDate") LocalDate minDate,
            @Param("maxDate") LocalDate maxDate
    );

}

