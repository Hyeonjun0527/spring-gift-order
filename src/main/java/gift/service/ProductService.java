package gift.service;

import gift.dto.OptionResponse;
import gift.dto.PaginationResponse;
import gift.dto.ProductOptionResponse;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.validation.ValidationUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final OptionService optionService;

    public ProductService(ProductRepository productRepository, OptionRepository optionRepository,
        OptionService optionService) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.optionService = optionService;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        ValidationUtil.validateProductRequest(request);

        Product product = new Product(
            null,
            request.name(),
            request.price(),
            request.imageUrl()
        );
        product = productRepository.save(product);
        Option option = product.getOptions().getFirst();
        option = optionRepository.save(option);
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl()
        );
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(
                () -> new IllegalArgumentException("Product(id: " + productId + ") not found"));
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl()
        );
    }

    public Product getProductToEntity(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(
                () -> new IllegalArgumentException("Product(id: " + productId + ") not found"));
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        ValidationUtil.validateProductRequest(request);

        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product(id: " + productId + ") not found");
        }
        Product existingProduct = product.get();

        String updatedName = existingProduct.getName();
        if (request.name() != null) {
            updatedName = request.name();
        }

        int updatedPrice = existingProduct.getPrice();
        if (request.price() != null) {
            updatedPrice = request.price();
        }

        String updatedImageUrl = existingProduct.getImageUrl();
        if (request.imageUrl() != null) {
            updatedImageUrl = request.imageUrl();
        }

        Product updatedProduct = new Product(
            productId,
            updatedName,
            updatedPrice,
            updatedImageUrl
        );
        updatedProduct = productRepository.save(updatedProduct);
        return new ProductResponse(
            updatedProduct.getId(),
            updatedProduct.getName(),
            updatedProduct.getPrice(),
            updatedProduct.getImageUrl()
        );
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if (productRepository.findById(productId).isEmpty()) {
            throw new IllegalArgumentException("Product(id: " + productId + ") not found");
        }
        productRepository.deleteById(productId);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
            ))
            .toList();
    }

    @Transactional
    public void subtractOptionQuantity(Long optionId, Integer quantity) {
        optionService.subtractOptionQuantity(optionId, quantity);
    }

    public PaginationResponse<ProductResponse> getAllProductsPaged(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        List<ProductResponse> content = page.getContent().stream()
            .map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
            ))
            .collect(Collectors.toList());

        return new PaginationResponse<>(
            content,
            page.getTotalPages(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    public PaginationResponse<ProductOptionResponse> getAllProductsWithOptionPaged(
        Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        List<ProductOptionResponse> content = page.getContent().stream()
            .map(product -> new ProductOptionResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getOptions().stream()
                    .map(option -> new OptionResponse(
                        option.getId(),
                        option.getName(),
                        option.getQuantity()
                    )).collect(Collectors.toList())
            ))
            .collect(Collectors.toList());

        return new PaginationResponse<>(
            content,
            page.getTotalPages(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

}