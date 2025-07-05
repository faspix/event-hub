package utility;

import com.faspix.shared.dto.NotificationDTO;
import com.faspix.shared.utility.NotificationType;

public class NotificationFactory {

    public static NotificationDTO makeNotificationDTO() {
        return NotificationDTO.builder()
                .eventName("test")
                .type(NotificationType.REQUEST_CONFIRMED)
                .userId("1")
                .build();
    }

}
