package guru.springframework.msscjms.sender;

import guru.springframework.msscjms.config.JmsConfig;
import guru.springframework.msscjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloSender {

    private final JmsTemplate jmsTemplate;

    @Async
    @Scheduled(fixedRate = 2000)
    public void sendMessage() {

        System.out.println("I'm sending a message");

        HelloWorldMessage helloWorldMessage =
                HelloWorldMessage.builder()
                        .id(UUID.randomUUID())
                        .message("Hello world")
                        .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, helloWorldMessage);

        System.out.println("Message sent");
    }
}
