package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//@EnableOAuth2Sso
@SpringBootApplication
@EnableZuulProxy
@EnableBinding(Source.class)
public class EdgeServiceApplication {
    @Bean
    AlwaysSampler alwaysSampler() {
        return new AlwaysSampler();
    }

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceApplication.class, args);
    }
}

@RestController
class QuoteController {
    @Autowired
    private Source source;

    @Autowired
    RestTemplate restTemplate;
//    private OAuth2RestOperations restTemplate;

    @RequestMapping(value = "/newquote", method = RequestMethod.POST)
    public void accept(@RequestBody Quote quote) {
        this.source.output().send(MessageBuilder.withPayload(quote.getText()).build());
    }

    @RequestMapping("/quotorama")
    String getRandomQuote() {
        return restTemplate.getForObject("http://QUOTE-SERVICE/random", String.class);
//        return "This is my quote, and I like it!";
    }
}

class Quote {
    private Long id;
    private String text;
    private String source;

    public Quote() {
    }

    public Quote(String text, String source) {

        this.text = text;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}