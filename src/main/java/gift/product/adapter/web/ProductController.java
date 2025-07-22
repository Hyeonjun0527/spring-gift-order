package gift.product.adapter.web;

import gift.product.adapter.web.mapper.ProductWebMapper;
import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.CreateProductRequest;
import gift.product.application.port.in.dto.ProductResponse;
import gift.product.application.port.in.dto.UpdateProductRequest;
import gift.product.domain.model.Product;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable) {
        Page<Product> products = productUseCase.getProducts(pageable);
        Page<ProductResponse> response = products.map(ProductWebMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long id) {
        Product product = productUseCase.getProduct(id);
        return ResponseEntity.ok(ProductWebMapper.toResponse(product));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productUseCase.addProduct(request);
        ProductResponse response = ProductWebMapper.toResponse(product);
        return ResponseEntity.created(URI.create("/api/products/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable("id") Long id,
                                              @Valid @RequestBody UpdateProductRequest request) {
        productUseCase.updateProduct(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
} 