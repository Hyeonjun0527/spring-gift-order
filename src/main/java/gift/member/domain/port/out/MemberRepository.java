package gift.member.domain.port.out;

import gift.member.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    boolean existsByEmail(String email);

    Member save(Member member);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByKakaoId(Long kakaoId);

    List<Member> findAll();

    Optional<Member> findById(Long id);

    boolean existsById(Long id);

    void deleteById(Long id);

    void deleteAll();
}