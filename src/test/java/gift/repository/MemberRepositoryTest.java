package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.dto.MemberResponse;
import gift.entity.Member;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member("test1@test.com", "password123", "USER");
    }

    @Test
    void save() {
        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test1@test.com");
        assertThat(savedMember.getPassword()).isEqualTo("password123");
        assertThat(savedMember.getRole()).isEqualTo("USER");
    }

    @Test
    void findByEmail() {
        memberRepository.save(member);
        Optional<Member> foundMember = memberRepository.findByEmail("test1@test.com");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("test1@test.com");
    }

    @Test
    void deleteById() {
        Member savedMember = memberRepository.save(member);
        memberRepository.deleteById(savedMember.getId());
        Optional<Member> deletedMember = memberRepository.findById(savedMember.getId());

        assertThat(deletedMember).isEmpty();
    }

    @Test
    void findAllPaged() {
        for (int i = 1; i <= 15; i++) {
            memberRepository.save(new Member("test" + i + "@test.com", "password", "USER"));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<MemberResponse> page = memberRepository.findAll(pageable)
            .map(member -> new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getRole()
            ));

        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().getFirst().email()).startsWith("test1");
    }
}