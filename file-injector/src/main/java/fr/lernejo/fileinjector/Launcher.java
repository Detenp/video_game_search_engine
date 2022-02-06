package fr.lernejo.fileinjector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) throws IOException {
        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            System.out.println("Hello after starting Spring");
            if (args.length >= 1) {
                ObjectMapper mapper = new ObjectMapper();

                List<MessageObject> messages = mapper.readValue(new File(args[0]),
                    new TypeReference<>() {});

                RabbitTemplate rt = springContext.getBean(RabbitTemplate.class);
                for (MessageObject message: messages) {
                    rt.setMessageConverter(new Jackson2JsonMessageConverter());
                    rt.convertAndSend("game_info", message, m -> {
                        m.getMessageProperties().getHeaders().put( "game_id", message.id());
                        return m;
                    });
                }
            }
        }
    }
}
