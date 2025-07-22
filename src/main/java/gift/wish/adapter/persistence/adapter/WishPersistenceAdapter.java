package gift.wish.adapter.persistence.adapter;

import gift.common.annotation.Adapter;
import gift.wish.adapter.persistence.entity.WishEntity;
import gift.wish.adapter.persistence.mapper.WishEntityMapper;
import gift.wish.domain.model.Wish;
import gift.wish.domain.port.out.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Adapter
@Transactional
public class WishPersistenceAdapter implements WishRepository {
    private final WishJpaRepository wishJpaRepository;

    public WishPersistenceAdapter(WishJpaRepository wishJpaRepository) {
        this.wishJpaRepository = wishJpaRepository;
    }

    @Override
    public Page<Wish> findByMemberId(Long memberId, Pageable pageable) {
        Page<WishEntity> entities = wishJpaRepository.findByMemberId(memberId, pageable);
        return entities.map(WishEntityMapper::toDomain);
    }

    @Override
    public Wish save(Wish wish) {
        WishEntity entity = WishEntityMapper.toEntity(wish);
        WishEntity saved = wishJpaRepository.save(entity);
        return WishEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Wish> findById(Long id) {
        return wishJpaRepository.findById(id).map(WishEntityMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        wishJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId) {
        return wishJpaRepository.findByMemberIdAndProductId(memberId, productId).map(WishEntityMapper::toDomain);
    }
}
