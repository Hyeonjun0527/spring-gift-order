package gift.service;

import gift.entity.Option;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Transactional
    public void subtractOptionQuantity(Long optionId, Integer quantity) {
        Option option = getOption(optionId);
        option.subtractOptionNum(quantity);
        optionRepository.save(option);
    }

    public Option getOption(Long optionId) {
        return optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("옵션을 찾을 수 없습니다."));
    }
}
