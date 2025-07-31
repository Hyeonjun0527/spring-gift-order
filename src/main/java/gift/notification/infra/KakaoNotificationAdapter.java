package gift.notification.infra;

import gift.infra.client.kakao.KakaoMessageApiClient;
import gift.member.domain.model.Member;
import gift.member.domain.port.out.MemberRepository;
import gift.notification.application.port.out.NotificationPort;
import gift.order.domain.model.Order;
import gift.order.domain.port.out.OrderRepository;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component
public class KakaoNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(KakaoNotificationAdapter.class);

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final KakaoMessageApiClient kakaoMessageApiClient;


    public KakaoNotificationAdapter(MemberRepository memberRepository, OrderRepository orderRepository,
                                    ProductRepository productRepository, KakaoMessageApiClient kakaoMessageApiClient) {
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.kakaoMessageApiClient = kakaoMessageApiClient;
    }

    @Async
    @Override
    @Transactional(readOnly = true)
    public void sendOrderConfirmationAsync(Long memberId, Long orderId) {
        try {
            log.info("비동기 주문 완료 알림 발송 시작. orderId: {}, memberId: {}", orderId, memberId);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다. id: " + memberId));

            if (member.kakaoInfo() == null || member.kakaoInfo().accessToken() == null) {
                log.info("카카오톡 토큰이 없어 메시지 발송을 건너뜁니다. memberId: {}", memberId);
                return;
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NoSuchElementException("해당 주문을 찾을 수 없습니다. id: " + orderId));

            Option option = productRepository.findOptionById(order.optionId())
                    .orElseThrow(() -> new NoSuchElementException("해당 옵션을 찾을 수 없습니다. id: " + order.optionId()));

            Product product = productRepository.findById(option.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("해당 상품을 찾을 수 없습니다. id: " + option.getProductId()));

            kakaoMessageApiClient.sendOrderConfirmationMessage(member.kakaoInfo().accessToken(), order, product, option);

        } catch (Exception e) {
            log.error("비동기 주문 완료 알림 발송 중 오류 발생. orderId: {}", orderId, e);
        }
    }
} 