package router;

import com.faspix.notificationservice.router.NotificationRouter;
import com.faspix.notificationservice.service.NotificationService;
import com.faspix.shared.dto.NotificationDTO;
import com.faspix.shared.utility.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static utility.NotificationFactory.makeNotificationDTO;

@ExtendWith(MockitoExtension.class)
class NotificationRouterTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationRouter notificationRouter;

    @Test
    void receiveNotification_RequestConfirmed_ShouldCallSendConfirmed() {
        NotificationDTO notification = makeNotificationDTO();
        notification.setType(NotificationType.REQUEST_CONFIRMED);

        notificationRouter.receiveNotification(notification);

        verify(notificationService, times(1))
                .sendConfirmedRequestNotification(notification);
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void receiveNotification_RequestRejected_ShouldCallSendRejected() {
        NotificationDTO notification = makeNotificationDTO();
        notification.setType(NotificationType.REQUEST_REJECTED);

        notificationRouter.receiveNotification(notification);

        verify(notificationService, times(1))
                .sendRejectedRequestNotification(notification);
        verifyNoMoreInteractions(notificationService);
    }

}
