package fr.lernejo.fileinjector;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LauncherTest {

    @Test
    void main_terminates_before_5_sec() {
        assertTimeoutPreemptively(
            Duration.ofSeconds(5L),
            () -> Launcher.main(new String[]{}));
    }

    @Test
    void sendGameTest() throws IOException {
        // Pruge the queue before testing
        URL url = new URL("http://guest:guest@localhost:15672/api/queues/rdkfegbx/game_info/contents");
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("DELETE");
        http.setRequestProperty("Accept", "application/json");

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();


        ClassLoader R = LauncherTest.class.getClassLoader();
        Launcher.main(new String[]{Objects.requireNonNull(R.getResource("1game.json")).getPath()});

        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            RabbitTemplate rt = springContext.getBean(RabbitTemplate.class);

            Message message = rt.receive("game_info");
            assert message != null;
            assertEquals(new String(message.getBody(), StandardCharsets.UTF_8), "{\"id\":69,\"title\":\"Dauntless\",\"thumbnail\":\"https://www.freetogame.com/g/1/thumbnail.jpg\",\"short_description\":\"A free-to-play, co-op action RPG with gameplay similar to Monster Hunter.\",\"game_url\":\"https://www.freetogame.com/open/dauntless\",\"genre\":\"MMORPG\",\"platform\":\"PC (Windows)\",\"publisher\":\"Phoenix Labs\",\"developer\":\"Phoenix Labs, Iron Galaxy\",\"release_date\":\"2019-05-21\",\"freetogame_profile_url\":\"https://www.freetogame.com/dauntless\"}");
        }
        // purge the queue after testing
        http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("DELETE");
        http.setRequestProperty("Accept", "application/json");

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();
    }
}
