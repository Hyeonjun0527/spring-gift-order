package gift.order.application.usecase;

import gift.member.domain.model.Member;
import gift.member.domain.port.out.MemberRepository;
import gift.order.application.port.in.OrderUseCase;
import gift.order.application.port.in.dto.OrderResponse;
import gift.order.application.port.in.dto.PlaceOrderRequest;
import gift.order.application.port.out.KakaoMessagePort;
import gift.order.application.port.out.OrderRepository;
import gift.order.domain.model.Order;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import gift.wish.domain.port.out.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class OrderInteractor implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final WishRepository wishRepository;
    private final KakaoMessagePort kakaoMessagePort;

    public OrderInteractor(OrderRepository orderRepository, MemberRepository memberRepository, ProductRepository productRepository, WishRepository wishRepository, KakaoMessagePort kakaoMessagePort) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessagePort = kakaoMessagePort;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Long memberId, PlaceOrderRequest request) {
        // 1. 사용자 및 옵션 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다. id: " + memberId));

        Option option = productRepository.findOptionById(request.optionId())
                .orElseThrow(() -> new NoSuchElementException("해당 옵션을 찾을 수 없습니다. id: " + request.optionId()));

        Product product = productRepository.findById(option.getProductId())
                .orElseThrow(() -> new NoSuchElementException("해당 상품을 찾을 수 없습니다. id: " + option.getProductId()));

        // 2. 재고 차감
        option.decreaseQuantity(request.quantity());
        productRepository.saveOption(option); // 변경된 재고 상태 저장

        // 3. 주문 생성 및 저장
        Order order = Order.create(
                memberId,
                request.optionId(),
                product.getName(),
                option.getName(),
                product.getPrice(),
                request.quantity(),
                request.message()
        );
        Order savedOrder = orderRepository.save(order);

        // 4. 위시리스트에서 해당 상품 삭제
        wishRepository.deleteByMemberIdAndOptionId(memberId, request.optionId());

        // 5. 카카오톡 메시지 발송
        if (member.kakaoAccessToken() != null) {
            kakaoMessagePort.sendOrderConfirmationMessage(member.kakaoAccessToken(), savedOrder, product, option);
        }

        return OrderResponse.from(savedOrder);
    }
} 