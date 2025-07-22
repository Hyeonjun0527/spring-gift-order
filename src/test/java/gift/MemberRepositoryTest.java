package gift;

import gift.config.JwtAuthFilter;
import gift.entity.Member;
import gift.entity.MemberRole;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class MemberRepositoryTest {

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save() {
        Member member = new Member("test@test.com", "password", MemberRole.USER);

        Member savedMember = memberRepository.save(member);

        assertAll(
                () -> assertThat(savedMember.getId()).isNotNull(),
                () -> assertThat(savedMember.getEmail()).isEqualTo(member.getEmail()),
                () -> assertThat(savedMember.getPassword()).isEqualTo(member.getPassword()),
                () -> assertThat(savedMember.getRole()).isEqualTo(MemberRole.USER)
        );
    }

    @Test
    void findByEmail() {
        String email = "test@test.com";
        memberRepository.save(new Member(email, "password", MemberRole.USER));

        Optional<Member> found = memberRepository.findByEmail(email);

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
    }
}
