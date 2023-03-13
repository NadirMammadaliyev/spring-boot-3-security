package az.nadir.springsecurity.config;

import az.nadir.springsecurity.auth.AuthenticationService;
import client.Client;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private Client client;

    @Scheduled(fixedRate = 60000)
    public void sendFeignRequest() throws Exception {
        String result =  client.sayHello();
        log.info("Called sayHello method");
    }
}
