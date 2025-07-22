package gift.product.application.usecase;

import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.AdminUpdateProductRequest;
import gift.product.application.port.in.dto.CreateProductRequest;
import gift.product.application.port.in.dto.UpdateProductRequest;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductInteractor implements ProductUseCase {
    private final ProductRepository productRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id: " + id));
    }

    @Override
    public Product addProduct(CreateProductRequest request) {
        List<Option> options = request.optionRequests() // 여기가 optionRequests가 맞습니다.
                .stream()
                .map(req -> Option.create(null, null, req.name(), req.quantity()))
                .toList();
        Product product = Product.create(null, request.name(), request.price(), request.imageUrl(), options);
        return productRepository.save(product);
    }

    @Override
    public void updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id: " + id));

        List<Option> updatedOptions = request.options().stream()
                .map(optionRequest -> Option.create(
                        findOptionIdByName(product, optionRequest.name()),
                        product.getId(),
                        optionRequest.name(),
                        optionRequest.quantity()))
                .collect(Collectors.toList());

        product.updateInfo(request.name(), request.price(), request.imageUrl(), updatedOptions);
        productRepository.save(product);
    }

    @Override
    public void updateProductForAdmin(Long id, AdminUpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id: " + id));
        product.updateInfo(request.name(),
                request.price(),
                request.imageUrl(),
                product.getOptions()
        );
        productRepository.save(product);
    }

    private Long findOptionIdByName(Product product, String name) {
        return product.getOptions().stream()
                .filter(option -> option.getName().equals(name))
                .findFirst()
                .map(Option::getId)
                .orElse(null);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다. id: " + id);
        }
        productRepository.deleteById(id);
    }
} 