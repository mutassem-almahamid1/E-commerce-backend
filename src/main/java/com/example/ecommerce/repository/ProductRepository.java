package com.example.ecommerce.repository;

import com.example.ecommerce.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("SELECT p FROM Product p " +
            "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:category IS NULL OR LOWER(p.category.name) = LOWER(:category))")
    Page<Product> search(@Param("name") String name,
                         @Param("category") String category,
                         Pageable pageable);
    Optional<Product> findByName(String name);
    List<Product> findByCategoryName(String categoryName);
}
