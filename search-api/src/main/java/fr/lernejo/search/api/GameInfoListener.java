package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GameInfoListener {
    private final RestHighLevelClient client;

    GameInfoListener(RestHighLevelClient client) {
        this.client = client;
    }

    @RabbitListener(queues = AmqpConfiguration.GAME_INFO_QUEUE)
    public void onMessage(String message, @Header("game_id") String id) throws IOException {
        IndexRequest request = new IndexRequest("games")
            .id(id)
            .source(message, XContentType.JSON);

        this.client.index(request, RequestOptions.DEFAULT);
    }
}
