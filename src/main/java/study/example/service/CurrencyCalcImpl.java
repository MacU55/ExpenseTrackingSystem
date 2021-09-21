package study.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyCalcImpl implements ICurrencyCalc {

    @Autowired
    private Map<String, BigDecimal> exchangeRates;

    public Map<String, BigDecimal> calcQuantity(BigDecimal dollars) {

        Map<String, BigDecimal> resultMap = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : exchangeRates.entrySet()) {
            BigDecimal converted = dollars.multiply(entry.getValue());
            resultMap.put(entry.getKey(), converted);
        }
        return resultMap;
    }


}
