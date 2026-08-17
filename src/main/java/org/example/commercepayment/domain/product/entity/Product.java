package org.example.commercepayment.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.global.entity.BaseTimeEntity;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;

// 상품. 주문·결제의 기준 데이터이며, 재고 증감은 아래 두 메서드로만 이뤄진다.
@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Product(String name, int price, int stock, String description) {
        if (price < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
        if (stock < 0) {
            throw new BusinessException(ErrorCode.INVALID_STOCK);
        }
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // 주문 시 재고 선차감 메서드
    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        if (quantity > this.stock) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }

    // 주문 취소 시 선차감된 재고 되돌리는 메서드
    public void restoreStock(int quantity) {
        if (quantity <= 0)  {   // 음수와 0 입력을 방지하는 조건 문
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        this.stock += quantity;
    }

}

