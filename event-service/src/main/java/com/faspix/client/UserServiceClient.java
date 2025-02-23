package com.faspix.client;

import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    @GetMapping("users/{userId}")
    ResponseUserDTO getUserById(@PathVariable String userId);

    @PostMapping("users/batch")
    Set<ResponseUserShortDTO> getUsersByIds(@RequestBody Set<String> userIds);
}
