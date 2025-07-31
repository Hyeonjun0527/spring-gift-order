package gift.product.application.usecase;

import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.AdminUpdateProductRequest;
import gift.product.application.port.in.dto.CreateProductRequest;
import gift.product.application.port.in.dto.UpdateProductRequest;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductInteractor implements ProductUseCase {
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductInteractor.class);

    public ProductInteractor(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id: " + id));
    }

    @Override
    public Product addProduct(CreateProductRequest request) {
        List<Option> options = request.options()
                .stream()
                .map(req -> Option.create(null, null, req.name(), req.quantity()))
                .collect(Collectors.toList());

        Product product = Product.create(null, request.name(), request.price(), request.imageUrl(), options);
        
        return productRepository.save(product);
    }

    @Override
    public void updateProduct(Long id, UpdateProductRequest request) {
        log.info("1. Interactor: updateProduct 시작. request: {}", request);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id: " + id));

        List<Option> newOptions = request.options().stream()
                .map(optionRequest -> Option.of(
                        optionRequest.id(),
                        product.getId(),
                        optionRequest.name(),
                        optionRequest.quantity()))
                .collect(Collectors.toList());
        
        log.info("2. Interactor: Option 도메인 객체로 변환 완료. updatedOptions: {}", newOptions);
        log.info("2.5 Interactor: Option 도메인 객체로 변환 완료. product: {}", product);

        product.updateInfo(request.name(), request.price(), request.imageUrl(), newOptions);
        log.info("4.5. Interactor : Product 상태 : {}", product);
        productRepository.save(product);
        log.info("5. Interactor: productRepository.save 호출 완료.");
    }

    @Override
    public void updateProductForAdmin(Long id, AdminUpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id: " + id));
        product.updateInfo(request.name(),
                request.price(),
                request.imageUrl(),
                product.getOptions()
        );
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("상품을 찾을 수 없습니다. id: " + id);
        }
        productRepository.deleteById(id);
    }
} 