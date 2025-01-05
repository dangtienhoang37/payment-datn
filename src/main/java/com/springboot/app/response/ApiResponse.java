package com.springboot.app.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Setter
@Getter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private boolean isSucess = (code == 1000);
    private T data;

}
