package io.kermoss.cs.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "${kermoss.cs.target-service.feign-service-name}")
public interface CommandFeignClient {
    @PostMapping("${kermoss.cs.target-service.context-root}command-executor/commands/{commandId}/restart")
    ResponseEntity<Object> restartCommand(@PathVariable("commandId") final String commandId);
}
