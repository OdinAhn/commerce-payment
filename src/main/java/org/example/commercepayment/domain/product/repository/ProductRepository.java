package org.example.commercepayment.domain.product.repository;

import jakarta.persistence.LockModeType;
import org.example.commercepayment.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 재고 차감 전용 조회
    // 락이 없으면 동시에 여러개를 주문 시 재고가 부족해서 통과가 된다.
    // 예외도 안나고 조용히 에러 발생
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> findAllByIdForUpdate(@Param("ids") List<Long> ids);
}