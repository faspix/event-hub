package service;

import com.faspix.notificationservice.client.UserServiceClient;
import com.faspix.notificationservice.dto.UserDTO;
import com.faspix.notificationservice.service.EmailNotificationService;
import com.faspix.notificationservice.service.EmailService;
import com.faspix.shared.dto.NotificationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.*;
import static utility.NotificationFactory.makeNotificationDTO;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void sendConfirmedRequestNotification_CacheHit_ShouldSendEmail() {
        NotificationDTO notification = makeNotificationDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("user@example.com");
        userDTO.setUserId("1");

        when(cacheManager.getCache("UserService::getUserById")).thenReturn(cache);
        when(cache.get("1", UserDTO.class)).thenReturn(null);

        emailNotificationService.sendConfirmedRequestNotification(notification);

        verify(emailService).sendEmail(any(), any(), any());
    }

    @Test
    void sendConfirmedRequestNotification_CacheMiss_ShouldFetchFromClientAndPutInCache() {
        NotificationDTO notification = makeNotificationDTO();

        when(cacheManager.getCache("UserService::getUserById")).thenReturn(cache);
        when(cache.get("1", UserDTO.class)).thenReturn(null);
        when(userServiceClient.getEmailById("1")).thenReturn("client@example.com");

        emailNotificationService.sendConfirmedRequestNotification(notification);

        verify(userServiceClient).getEmailById("1");
        verify(cache).put("1", "client@example.com");
        verify(emailService).sendEmail(eq("client@example.com"), eq(""), eq(""));
    }

    @Test
    void sendConfirmedRequestNotification_CacheNull_ShouldFetchFromClient() {
        NotificationDTO notification = makeNotificationDTO();

        when(cacheManager.getCache("UserService::getUserById")).thenReturn(null);
        when(userServiceClient.getEmailById("1")).thenReturn("client@example.com");

        emailNotificationService.sendConfirmedRequestNotification(notification);

        verify(userServiceClient).getEmailById("1");
        verify(emailService).sendEmail(eq("client@example.com"), eq(""), eq(""));
    }

    @Test
    void sendRejectedRequestNotification_CacheHit_ShouldSendEmail() {
        NotificationDTO notification = makeNotificationDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("reject@example.com");
        userDTO.setUserId("2");

        when(cacheManager.getCache("UserService::getUserById")).thenReturn(cache);
        when(cache.get("1", UserDTO.class)).thenReturn(userDTO);

        emailNotificationService.sendRejectedRequestNotification(notification);

        verify(emailService).sendEmail(eq("reject@example.com"), eq(""), eq(""));
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void sendRejectedRequestNotification_CacheMiss_ShouldFetchFromClientAndPutInCache() {
        NotificationDTO notification = makeNotificationDTO();

        when(cacheManager.getCache("UserService::getUserById")).thenReturn(cache);
        when(cache.get("1", UserDTO.class)).thenReturn(null);
        when(userServiceClient.getEmailById("1")).thenReturn("reject-client@example.com");

        emailNotificationService.sendRejectedRequestNotification(notification);

        verify(userServiceClient).getEmailById("1");
        verify(cache).put("1", "reject-client@example.com");
        verify(emailService).sendEmail(eq("reject-client@example.com"), eq(""), eq(""));
    }
}
