package gift;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.WishRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishJpaTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 위시리스트를_회원_id로_페이징_조회() {
        // given
        Member member = new Member("홍길동", "hong@email.com", "password");
        entityManager.persist(member);

        Product product1 = new Product("아이스 아메리카노", 4500, "ice_americano.jpg");
        Product product2 = new Product("카페라떼", 5000, "cafe_latte.jpg");
        Product product3 = new Product("아인슈페너", 5500, "einspanner.jpg");

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);

        entityManager.persist(new Wish(member, product1));
        entityManager.persist(new Wish(member, product2));
        entityManager.persist(new Wish(member, product3));

        entityManager.flush();
        entityManager.clear();

        // when
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        List<Wish> found = wishRepository.findAllByMemberId(member.getId(), pageable);

        // then
        assertThat(found).hasSize(2);
        assertThat(found)
                .extracting(w -> w.getProduct().getName())
                .containsAnyOf("아이스 아메리카노", "카페라떼", "콜드브루");
    }

    @Test
    void 위시를_삭제한다() {
        // given
        Member member = new Member("홍길동", "hong@email.com", "password");
        Product product = new Product("콜드브루", 4800, "coldbrew.jpg");

        // when
        entityManager.persist(member);
        entityManager.persist(product);
        Wish wish = new Wish(member, product);
        entityManager.persist(wish);
        entityManager.flush();

        wishRepository.delete(wish);
        entityManager.flush();
        entityManager.clear();

        List<Wish> result = wishRepository.findByMemberId(member.getId());

        // then
        assertThat(result).isEmpty();
    }
}