package gift;

import gift.entity.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
public class MemberJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_저장_및_조회_성공() {
        Member newMember = new Member("홍길동", "hong@naver.com", "password");
        memberRepository.save(newMember);

        Member found = memberRepository.findByEmail("hong@naver.com").orElseThrow();
        assertThat(found.getName()).isEqualTo("홍길동");
    }

    @Test
    void 이메일로_조회_실패() {
        Optional<Member> result = memberRepository.findByEmail("no@email.com");

        assertThat(result).isEmpty();
    }

    @Test
    void 회원_수정() {
        Member member = new Member("홍길동", "hong@naver.com", "password");
        entityManager.persist(member);

        Member found = memberRepository.findByEmail("hong@naver.com").orElseThrow();
        Member updatedMember = found.updateTo("윤강훈", "yghun021007@naver.com", "password");
        memberRepository.save(updatedMember);

        Member updated = memberRepository.findByEmail("yghun021007@naver.com").orElseThrow();
        assertThat(updated.getName()).isEqualTo("윤강훈");
    }

    @Test
    void 회원_삭제() {
        Member member = new Member("홍길동", "hong@naver.com", "password");
        entityManager.persist(member);
        entityManager.flush();
        entityManager.clear();

        memberRepository.delete(member);

        Optional<Member> result = memberRepository.findByEmail("hong@naver.com");
        assertThat(result).isEmpty();
    }

    @Test
    void 이메일_중복_저장_실패() {
        entityManager.persist(new Member("홍길동", "hong@naver.com", "password"));

        assertThatThrownBy(() ->
                entityManager.persist(new Member("윤강훈", "hong@naver.com", "otherpass"))
        ).isInstanceOf(Exception.class);
    }
}
