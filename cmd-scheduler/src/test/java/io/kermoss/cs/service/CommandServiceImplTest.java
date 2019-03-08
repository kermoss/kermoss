package io.kermoss.cs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.kermoss.cs.domain.Command;

@RunWith(MockitoJUnitRunner.class)
public class CommandServiceImplTest {
	@Mock
	private JdbcTemplate jdbcTemplate;
    
    @InjectMocks
	private CommandServiceImpl commandServiceImplUnderTest;
	
    @Test
    public void testFindFailedCommands() {
    	ArrayList<Object> resultQuery = new ArrayList<>();
		when(jdbcTemplate.query(anyString(),any(RowMapper.class))).thenReturn(resultQuery);
    	List<Command> findFailedCommands = commandServiceImplUnderTest.findFailedCommands();
    	assertThat(resultQuery).isEqualTo(findFailedCommands);
    	verify(jdbcTemplate).query(anyString(),any(RowMapper.class));
    }
    



}