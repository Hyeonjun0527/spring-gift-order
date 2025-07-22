package gift.product.application.port.in;

import gift.product.application.port.in.dto.AdminUpdateProductRequest;
import gift.product.application.port.in.dto.CreateProductRequest;
import gift.product.application.port.in.dto.UpdateProductRequest;
import gift.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductUseCase {
    Page<Product> getProducts(Pageable pageable);

    Product getProduct(Long id);

    Product addProduct(CreateProductRequest request);

    void updateProduct(Long id, UpdateProductRequest request);
    
    void updateProductForAdmin(Long id, AdminUpdateProductRequest request);

    void deleteProduct(Long id);
} 