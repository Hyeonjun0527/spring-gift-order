package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.Product;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        Product product = findProductOrThrow(id);
        return new ProductResponseDto(product);
    }

    @Transactional // JPA가 자동으로 변경된 엔티티를 저장!
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product(requestDto.getName(), requestDto.getPrice(), requestDto.getImageUrl());
        Product saved = productRepository.save(product);
        return new ProductResponseDto(saved);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = findProductOrThrow(id);

        product.setName(requestDto.getName());
        product.setPrice(requestDto.getPrice());
        product.setImageUrl(requestDto.getImageUrl());

        return new ProductResponseDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        findProductOrThrow(id);
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductResponseDto::new);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
    }

    public long getProductCount() {
        return productRepository.count();
    }
}
