package gift.service;

import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.exception.DuplicateOptionNameException;
import gift.exception.LastOptionException;
import gift.exception.OptionNotExistException;
import gift.exception.ProductNotExistException;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OptionService {

    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final EntityManager entityManager;

    public OptionService(
            ProductRepository productRepository,
            OptionRepository optionRepository,
            EntityManager entityManager) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public OptionResponseDto create(Long productId, OptionRequestDto requestDto) {
        if (optionRepository.existsByProductIdAndName(productId, requestDto.name())) {
            throw new DuplicateOptionNameException(requestDto.name());
        }

        Product productReference = entityManager.getReference(Product.class, productId);

        Option option = new Option(requestDto.name(), requestDto.quantity(), productReference); // DB 조회 없이 프록시 객체 반환
        Option saved = optionRepository.save(option);

        return new OptionResponseDto(saved.getId(), saved.getName(), saved.getQuantity());
    }

    public List<OptionResponseDto> find(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotExistException(productId);
        }

        List<Option> options = optionRepository.findAllByProductId(productId);
        return options.stream()
                .map(option -> new OptionResponseDto(
                        option.getId(),
                        option.getName(),
                        option.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional
    public OptionResponseDto update(Long productId, Long optionId, OptionRequestDto requestDto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotExistException(productId));

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new OptionNotExistException(optionId));

        if (!option.getName().equals(requestDto.name())) {
            if (optionRepository.existsByProductAndName(product, requestDto.name())){
                throw new DuplicateOptionNameException(requestDto.name());
            }
        }

        Option updatedOption = option.updateTo(requestDto.name(), requestDto.quantity());
        optionRepository.save(updatedOption);

        return new OptionResponseDto(
                updatedOption.getId(),
                updatedOption.getName(),
                updatedOption.getQuantity()
        );
    }

    @Transactional
    public void delete(Long productId, Long optionId) {
        Option option = optionRepository.findByIdAndProductId(optionId, productId)
                .orElseThrow(() -> new OptionNotExistException(optionId));

        int optionCount = optionRepository.countByProduct(entityManager.getReference(Product.class, productId));
        if (optionCount <= 1) {
            throw new LastOptionException();
        }
        optionRepository.delete(option);
    }

    @Transactional
    public void reduceOptionQuantity(Long optionId, int amount) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new OptionNotExistException(optionId));
        option.decreaseQuantity(amount);
    }

}
