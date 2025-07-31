package gift.notification.application.port.out;

public interface NotificationPort {

    /**
     * 주문 완료에 대한 확인 메시지 발송을 비동기적으로 요청합니다.
     *
     * @param memberId 보낼 대상 회원의 ID
     * @param orderId  완료된 주문의 ID
     */
    void sendOrderConfirmationAsync(Long memberId, Long orderId);
} 