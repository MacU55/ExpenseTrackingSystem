package study.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CurrencyConfig {

    @Bean
    public Map<String, BigDecimal> exchangeRates(){
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("UAH", new BigDecimal("26.6"));
        map.put("EUR", new BigDecimal("0.9"));
        map.put("GBP", new BigDecimal("0.7"));
        return map;
    }
}
