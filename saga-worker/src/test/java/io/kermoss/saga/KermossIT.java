package io.kermoss.saga;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import io.kermoss.SagaReactiveBusinessFlowApplication;

import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT , classes = SagaReactiveBusinessFlowApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@Transactional
public class KermossIT {
    private static AtomicBoolean isCalled = new AtomicBoolean(false);

    @Before
    public void setUpAll() throws InterruptedException {
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