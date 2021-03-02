package ar.ne.rcs.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.embedded.RedisServer;

@EnableScheduling
@SpringBootApplication
public class ServerApplication {

    public ServerApplication() {
        try {
            RedisServer redisServer = new RedisServer(48076);
            redisServer.start();
            System.out.println("Redis server started!");
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
