import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import study.example.AppMain;
import study.example.repository.ExpenseRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AppMain.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class IntegrationTestCurrencyConversion {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    public void givenDollarsAndRates_whenMockMVC_thenVerifyResponse() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/currencyCalc")
                .param("dollarAmount", "10");
        this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.UAH").value(266))
                .andExpect(jsonPath("$.EUR").value(9))
                .andExpect(jsonPath("$.GBP").value(7));
    }
}
