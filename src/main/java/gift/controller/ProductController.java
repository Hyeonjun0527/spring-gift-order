package gift.controller;

import gift.dto.OptionResponse;
import gift.dto.PaginationResponse;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        try {
            ProductResponse productResponse = productService.createProduct(request);
            return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        try {
            ProductResponse productResponse = productService.getProduct(productId);
            return new ResponseEntity<>(productResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{productId}/options")
    public ResponseEntity<List<OptionResponse>> getProductOptions(@PathVariable Long productId) {
        Product product = productService.getProductToEntity(productId);
        List<OptionResponse> options = product.getOptions().stream()
            .map(option -> new OptionResponse(
                option.getId(),
                option.getName(),
                option.getQuantity()
            )).collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
        @PathVariable Long productId,
        @RequestBody ProductRequest request) {
        try {
            ProductResponse updatedProductResponse = productService.updateProduct(productId,
                request);
            return new ResponseEntity<>(updatedProductResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public PaginationResponse<ProductResponse> getAllProductsPaged(
        @PageableDefault(size = 10, sort = "name")
        Pageable pageable
    ) {
        return productService.getAllProductsPaged(pageable);
    }

}