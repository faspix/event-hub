package com.faspix.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseUserShortDTO {

    private String userId;

    private String username;

}
