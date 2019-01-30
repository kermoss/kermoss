package io.kermoss.cs.task;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import io.kermoss.cs.domain.Command;
import io.kermoss.cs.service.CommandService;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = CommandSchedulerIT.LocalRibbonClientConfiguration.class)
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

    @TestConfiguration
    public static class LocalRibbonClientConfiguration {
        @Bean
        public ServerList<Server> ribbonServerList() {
            return new StaticServerList<>(new Server("localhost", wiremock.port()));
        }
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
