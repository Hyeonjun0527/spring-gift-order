package gift.order.adapter.web;

import gift.common.annotation.LoginMember;
import gift.member.domain.model.Member;
import gift.order.application.port.in.OrderUseCase;
import gift.order.application.port.in.dto.OrderResponse;
import gift.order.application.port.in.dto.PlaceOrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
        @LoginMember Member member,
        @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderUseCase.placeOrder(member.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 