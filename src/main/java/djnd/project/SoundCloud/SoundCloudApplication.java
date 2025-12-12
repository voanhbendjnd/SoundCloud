package djnd.project.SoundCloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.EnableCaching;

// @SpringBootApplication(exclude = {
// 		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
// 		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
// })
@SpringBootApplication
@EnableCaching
public class SoundCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoundCloudApplication.class, args);
	}

}
