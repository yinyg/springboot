package tech.hiyinyougen.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yinyg
 * @CreateTime 2020/6/29 10:07
 * @Description
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long id;
    private String username;
    private Integer age;
}
