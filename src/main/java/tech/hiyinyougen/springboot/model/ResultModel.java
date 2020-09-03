package tech.hiyinyougen.springboot.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Author yinyg
 * @CreateTime 2020/6/29 10:28
 * @Description
 */
@Builder
@Data
public class ResultModel {
    private Boolean success;
    private Object data;
    private String message;
}
