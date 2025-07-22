package gift.service;

import gift.dto.ProductOptionRequestDto;
import gift.dto.ProductOptionResponseDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
public class ProductOptionService {

    private final ProductOptionRepository optionRepository;
    private final ProductRepository productRepository;

    public ProductOptionService(ProductOptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    // 1. 옵션 추가 (쓰기 작업 → REQUIRED)
    @Transactional(propagation = Propagation.REQUIRED)
    public ProductOptionResponseDto addOption(Long productId, ProductOptionRequestDto dto) {
        // 상품 존재 여부 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

        // 이름 중복 체크
        if (optionRepository.existsByProductIdAndName(productId, dto.getName())) {
            throw new IllegalArgumentException("동일한 상품 내에 같은 옵션 이름이 존재합니다.");
        }

        // 유효성 검증
        validateOption(dto.getName(), dto.getQuantity());

        // 옵션 저장
        ProductOption option = new ProductOption(product, dto.getName(), dto.getQuantity());
        ProductOption saved = optionRepository.save(option);

        return new ProductOptionResponseDto(saved);
    }

    // 2. 옵션 목록 조회 (읽기 전용 → SUPPORTS)
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<ProductOptionResponseDto> getOptionsByProductId(Long productId, Pageable pageable) {
        Page<ProductOption> options = optionRepository.findByProductId(productId, pageable);
        return options.map(ProductOptionResponseDto::new);
    }

    // 3. 옵션 수량 차감 (쓰기 → REQUIRED)
    @Transactional(propagation = Propagation.REQUIRED)
    public void subtractOptionQuantity(Long optionId, int quantity) {
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new NoSuchElementException("옵션이 존재하지 않습니다."));
        option.subtract(quantity); // Dirty Checking으로 반영 → 트랜잭션 필요함
    }

    // 유효성 검증
    private void validateOption(String name, int quantity) {
        if (name.length() > 50) {
            throw new IllegalArgumentException("옵션 이름은 50자 이하이어야 합니다.");
        }

        String pattern = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]+$";
        if (!Pattern.matches(pattern, name)) {
            throw new IllegalArgumentException("옵션 이름에 허용되지 않는 문자가 포함되어 있습니다.");
        }

        if (quantity < 1 || quantity >= 100000000) {
            throw new IllegalArgumentException("수량은 1 이상 100,000,000 미만이어야 합니다.");
        }
    }
}
