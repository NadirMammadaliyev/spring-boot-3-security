package client;

import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.error.AnnotationErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        value = "client",
        url = "https://bumblebee-b374.onrender.com/api/v1/feign-controller",
        primary = false,
        configuration = Client.FeignConfiguration.class)
public interface Client {

    @GetMapping("/hello")
    String sayHello();

    class FeignConfiguration {
        @Bean
        Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }

        @Bean
        public ErrorDecoder feignErrorDecoder() {
            return AnnotationErrorDecoder.builderFor(Client.class)
                    .withResponseBodyDecoder(new JacksonDecoder())
                    .build();
        }
    }
}
