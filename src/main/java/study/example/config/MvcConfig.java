package study.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("start");
        registry.addViewController("/expense_list").setViewName("expense_list");
        registry.addViewController("/users").setViewName("users");
//        registry.addViewController("/shipper_home").setViewName("shipper_home");
    }

}
