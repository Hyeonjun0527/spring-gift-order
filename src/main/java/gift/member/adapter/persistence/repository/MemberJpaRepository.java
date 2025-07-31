package gift.member.adapter.persistence.repository;

import gift.member.adapter.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByKakaoInfoKakaoId(Long kakaoId);

    boolean existsByEmail(String email);
} 