package service;

import com.faspix.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_validInput_messageSent() {
        String recipient = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendEmail(recipient, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage, notNullValue());
        assertThat(sentMessage.getTo(), arrayContaining(recipient));
        assertThat(sentMessage.getSubject(), equalTo(subject));
        assertThat(sentMessage.getText(), equalTo(body));
    }

    @Test
    void sendEmail_nullBody_messageSentWithNullBody() {
        String recipient = "recipient@example.com";
        String subject = "Subject with null body";

        emailService.sendEmail(recipient, subject, null);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage, notNullValue());
        assertThat(sentMessage.getText(), nullValue());
    }

    @Test
    void sendEmail_emptySubject_messageSentWithEmptySubject() {
        String recipient = "recipient@example.com";
        String body = "Body without subject";

        emailService.sendEmail(recipient, "", body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage, notNullValue());
        assertThat(sentMessage.getSubject(), equalTo(""));
    }

    @Test
    void sendEmail_nullRecipient_shouldThrowException() {
        String subject = "Subject";
        String body = "Body";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendEmail(null, subject, body)
        );

        assertThat(exception, notNullValue());
    }

}
