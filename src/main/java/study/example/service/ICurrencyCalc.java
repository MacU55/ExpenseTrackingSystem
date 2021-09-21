package study.example.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public interface ICurrencyCalc {


    Map<String, BigDecimal> calcQuantity(BigDecimal dollars);

}
