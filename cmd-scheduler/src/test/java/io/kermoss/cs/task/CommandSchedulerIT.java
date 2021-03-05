package io.kermoss.cs.task;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import io.kermoss.cs.domain.Command;
import io.kermoss.cs.service.CommandService;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class CommandSchedulerIT {
    @Autowired
    private CommandService commandService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ClassRule
    public static WireMockClassRule wiremock = new WireMockClassRule(wireMockConfig().dynamicPort());

    @Before
    public void setup() throws IOException {
        jdbcTemplate.execute("CREATE TABLE KERMOSS_CMD (id VARCHAR(50), status VARCHAR(25), startedTimestamp INTEGER)");
        stubFor(post(urlMatching("//command-executor/commands/.*/restart"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.CREATED.value())
                    .withUniformRandomDelay(0, 2000)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            )
        );
    }

    @Test
    public void shouldRestartFailedCommand() {
        final Command cmd = this.createFailedCommand();

        try {
            Thread.sleep(5000);
        } catch (final InterruptedException theException) {}

        verify(moreThanOrExactly(1), postRequestedFor(urlEqualTo(String.format("//command-executor/commands/%s/restart", cmd.getId()))));
    }

    @Test
    public void shouldRestartStartedTimedOutCommand() {
        final Command cmd = this.createStartedTimedOutCommand();

        try {
            Thread.sleep(5000);
        } catch (final InterruptedException theException) {}

        verify(moreThanOrExactly(1), postRequestedFor(urlEqualTo(String.format("//command-executor/commands/%s/restart", cmd.getId()))));
    }

    

    private Command createStartedTimedOutCommand() {
        final Command cmd = new Command(UUID.randomUUID().toString(), "STARTED");
        this.jdbcTemplate.execute(String.format("INSERT INTO KERMOSS_CMD(id, status, startedTimestamp) VALUES('%s', '%s', '0')", cmd.getId(), cmd.getStatus()));
        return cmd;
    }

    private Command createFailedCommand() {
        final Command cmd = new Command(UUID.randomUUID().toString(), "FAILED");
        this.jdbcTemplate.execute(String.format("INSERT INTO KERMOSS_CMD(id, status) VALUES('%s', '%s')", cmd.getId(), cmd.getStatus()));
        return cmd;
    }
}
