package org.example.commercepayment.domain.product.entity;

import com.sparta.paymentsystem.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    @Column(nullable = false, columnDefinition = "int UNSIGNED")
    private int price;

    @Column(nullable = false, columnDefinition = "int UNSIGNED DEFAULT 0")
    private int stock = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ✅ 추가된 필드: 카테고리
    @Column(nullable = false, length = 100)
    private String category;

    // ✅ 추가된 필드: 판매 상태 (ON_SALE, SOLD_OUT, DISCONTINUED)
    @Column(nullable = false, length = 30)
    private String salesStatus;

    public Product(String name, int price, int stock, String description, String category, String salesStatus) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다");
        }
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.category = category;
        this.salesStatus = salesStatus;
    }

    // 비즈니스 로직: 재고 변경 시 자동으로 상태 업데이트
    public void decreaseStock(int quantity) {
        if (this.stock - quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= quantity;

        // 재고가 0이 되면 자동으로 품절 상태로 변경
        if (this.stock == 0 && "ON_SALE".equals(this.salesStatus)) {
            this.salesStatus = "SOLD_OUT";
        }
    }
}