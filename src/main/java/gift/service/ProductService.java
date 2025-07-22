package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.exception.ProductNotExistException;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    public ProductService(ProductRepository productRepository,  OptionRepository optionRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public ProductResponseDto create(ProductRequestDto requestDto) {
        Product product = new Product(requestDto.name(), requestDto.price(), requestDto.imageUrl());
        Product newProduct = productRepository.save(product);

        List<Option> options = requestDto.options().stream()
                .map(optionDto -> new Option(optionDto.name(), optionDto.quantity(), newProduct))
                .toList();
        optionRepository.saveAll(options);

        return new ProductResponseDto( newProduct.getId(), newProduct.getName(), newProduct.getPrice(), newProduct.getImageUrl());
    }

    public ProductResponseDto find(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotExistException(productId));

        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice(), product.getImageUrl());
    }

    @Transactional
    public ProductResponseDto update(Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotExistException(productId));

        Product updatedProduct = product.updateTo(requestDto.name(),  requestDto.price(), requestDto.imageUrl());
        productRepository.save(updatedProduct);

        return new ProductResponseDto(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getImageUrl()
        );
    }

    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotExistException(productId));
        optionRepository.deleteAllByProduct(product);
        productRepository.delete(product);
    }

    public List<ProductResponseDto> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .stream()
                .map(p -> new ProductResponseDto(p.getId(), p.getName(), p.getPrice(), p.getImageUrl()))
                .toList();
    }
}
