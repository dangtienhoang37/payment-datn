package com.springboot.app.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubBalanceRequest {
    UUID accountId;
    int balance;
}
