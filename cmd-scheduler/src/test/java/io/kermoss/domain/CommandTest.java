package io.kermoss.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.cs.domain.Command;

@RunWith(MockitoJUnitRunner.class)
public class CommandTest {
    
    
    @Test
    public void testCommand() {
        Command command = new Command("Aqw", "Failed");
        assertThat(command.getId()).isEqualTo("Aqw");
        assertThat(command.getStatus()).isEqualTo("Failed");
    }
    
    @Test
    public void testCommandModifiers() {
        Command command = new Command();
        command.setId("asw");
        command.setStatus("Completed");
        assertThat(command.getId()).isEqualTo("asw");
        assertThat(command.getStatus()).isEqualTo("Completed");
    }
    
    
}
