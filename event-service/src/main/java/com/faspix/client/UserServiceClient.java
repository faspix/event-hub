package com.faspix.client;

import com.faspix.dto.ResponseUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("users/{userId}")
    ResponseUserDTO getUserById(@PathVariable Long userId);

}
