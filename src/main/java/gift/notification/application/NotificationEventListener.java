package gift.notification.application;

import gift.notification.application.port.out.NotificationPort;
import gift.order.application.event.OrderCompletedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventListener {

    private final NotificationPort notificationPort;

    public NotificationEventListener(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        notificationPort.sendOrderConfirmationAsync(event.getMemberId(), event.getOrderId());
    }
} 