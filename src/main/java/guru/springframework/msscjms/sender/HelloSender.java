package guru.springframework.msscjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscjms.config.JmsConfig;
import guru.springframework.msscjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

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


    @Async
    @Scheduled(fixedRate = 2000)
    public void sendReceiveMessage() throws JMSException {

        HelloWorldMessage helloWorldMessage =
                HelloWorldMessage.builder()
                        .id(UUID.randomUUID())
                        .message("Hello world")
                        .build();

        Message reply = jmsTemplate.sendAndReceive(JmsConfig.MY_QUEUE,
                new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        try {
                            Message message = session.createTextMessage(objectMapper.writeValueAsString(helloWorldMessage));
                            message.setStringProperty("_type", "guru.springframework.msscjms.model.HelloWorldMessage");
                            return message;
                        } catch (JsonProcessingException e) {
                            throw new JMSException("boom");
                        }
                    }
                });

        System.out.println(reply.getBody(String.class));
    }

}
