package gift.product.domain.port.out;

import gift.product.domain.model.Option;

import java.util.Optional;

public interface OptionRepository {
    Optional<Option> findById(Long id);

    Option save(Option option);
} 