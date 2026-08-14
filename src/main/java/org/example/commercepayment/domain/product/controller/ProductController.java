package org.example.commercepayment.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.product.dto.ProductPageResponse;
import org.example.commercepayment.domain.product.dto.ProductResponse;
import org.example.commercepayment.domain.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductPageResponse> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String salesStatus,
            @RequestParam(required = false) Boolean isSoldOut,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "LATEST") String sort
    ) {
        // 잘못된 입력에 대한 4xx 에러 처리
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다.");
        }
        if (size > 100) {
            throw new IllegalArgumentException("페이지 크기는 100 이하이어야 합니다.");
        }

        ProductPageResponse response = productService.getProducts(
                category, minPrice, maxPrice, salesStatus, isSoldOut, page, size, sort
        );

        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<List<ProductResponse>> list() {
//        return ResponseEntity.ok(productService.findAll());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}
