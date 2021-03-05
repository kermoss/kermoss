package io.kermoss.saga;



import java.util.concurrent.atomic.AtomicBoolean;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import io.kermoss.SagaReactiveBusinessFlowApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT , classes = SagaReactiveBusinessFlowApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@Transactional
public class KermossIT {
    private static AtomicBoolean isCalled = new AtomicBoolean(false);

    @BeforeEach
    public  void setUpAll() throws InterruptedException {
        if(!isCalled.get()) {
            RestTemplate template = new RestTemplate();
            template.getForEntity("http://localhost:8081/pizza/test/order", String.class);
            isCalled.set(true);
            Thread.sleep(4000);

        }
    }

    @Test
    public void test(){

    }
	
}