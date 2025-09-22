package com.example.ecommerce.repository;

import com.example.ecommerce.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * البحث المحسن في المنتجات مع دعم الفلترة المتقدمة
     * يدعم البحث بالاسم، الفئة، ونطاق الأسعار
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))) " +
            "AND (:category IS NULL OR :category = '' OR LOWER(p.category.name) = LOWER(TRIM(:category))) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false))")
    Page<Product> search(@Param("name") String name,
                         @Param("category") String category,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         @Param("inStock") Boolean inStock,
                         Pageable pageable);

    /**
     * البحث البسيط (للتوافق مع الكود الموجود)
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))) " +
            "AND (:category IS NULL OR :category = '' OR LOWER(p.category.name) = LOWER(TRIM(:category)))")
    Page<Product> search(@Param("name") String name,
                         @Param("category") String category,
                         Pageable pageable);

    /**
     * البحث في المنتجات بالاسم فقط
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))")
    Page<Product> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * البحث في المنتجات حسب الفئة
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.category.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * البحث في المنتجات ضمن نطاق سعري
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);

    /**
     * العثور على المنتجات المتوفرة في المخزون
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.stockQuantity > 0")
    Page<Product> findAvailableProducts(Pageable pageable);

    /**
     * العثور على المنتجات النافدة
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.stockQuantity = 0")
    Page<Product> findOutOfStockProducts(Pageable pageable);

    /**
     * العثور على المنتجات ذات المخزون المنخفض
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.stockQuantity > 0 " +
            "AND p.stockQuantity <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // الـ methods الموجودة
    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.category.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    /**
     * العثور على منتج بالاسم (مع تجاهل الحالة)
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "AND LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByNameIgnoreCase(@Param("name") String name);

    /**
     * العثور على المنتجات الأكثر مبيعاً (يحتاج جدول المبيعات)
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.deletedAt IS NULL " +
            "ORDER BY p.id DESC")
    Page<Product> findPopularProducts(Pageable pageable);
}
