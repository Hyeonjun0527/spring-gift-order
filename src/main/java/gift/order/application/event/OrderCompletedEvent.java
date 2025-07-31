package gift.order.application.event;

public class OrderCompletedEvent {
    private final Long orderId;
    private final Long memberId;

    public OrderCompletedEvent(Long orderId, Long memberId) {
        this.orderId = orderId;
        this.memberId = memberId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMemberId() {
        return memberId;
    }
} 