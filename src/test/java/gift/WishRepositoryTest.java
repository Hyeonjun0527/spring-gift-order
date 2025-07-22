package gift;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    WishRepository wishRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberRepository memberRepository;

    Member member;
    Product product;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("test@test.com", "password", gift.entity.MemberRole.USER));
        product = productRepository.save(new Product("테스트상품", 1000, "test.jpg"));
    }

    @Test
    @DisplayName("위시리스트 저장 성공")
    void saveWish() {
        Wish wish = new Wish(member, product, 2);
        Wish saved = wishRepository.save(wish);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMember().getId()).isEqualTo(member.getId());
        assertThat(saved.getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("회원별 위시리스트 조회")
    void findByMember() {
        // given
        Member member = memberRepository.save(new Member("user1@test.com", "pw", gift.entity.MemberRole.USER));
        Product product = productRepository.save(new Product("테스트상품", 1000, "img.jpg"));
        Wish wish = new Wish(member, product, 2);
        wishRepository.save(wish);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Wish> page = wishRepository.findByMember(member, pageable);
        List<Wish> list = page.getContent();

        // then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getProduct().getName()).isEqualTo("테스트상품");
    }

    @Test
    @DisplayName("상품 중복 확인")
    void existsByMemberAndProduct() {
        wishRepository.save(new Wish(member, product, 1));

        boolean exists = wishRepository.existsByMemberIdAndProductId(member.getId(), product.getId());

        assertThat(exists).isTrue();
    }
}
