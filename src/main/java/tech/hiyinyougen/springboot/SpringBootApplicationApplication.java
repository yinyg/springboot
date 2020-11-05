package tech.hiyinyougen.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author yinyougen
 */
@SpringBootApplication
@MapperScan("tech.hiyinyougen.springboot.dao")
@EnableAsync
public class SpringBootApplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApplicationApplication.class, args);
	}

}
