package study.example;

import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.apache.logging.log4j.core.appender.rewrite.MapRewritePolicy.Mode.Add;


@Entity
@Table(name = "expenses", schema = "exptracksystem")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="description")
    @NotBlank (message = "Please fill in 'Expense description' ")
    private String description;

    // date for using in listAll()
    @Column(name="expenseDate")
    @NotNull (message = "Expense date must not be empty")
    @PastOrPresent (message = "Expense date must be in past or in present")
    @DateTimeFormat (pattern = "yyyy-MM-dd")
    private LocalDate expenseDate = LocalDate.now(ZoneId.of( "Europe/Kiev" ));

    @Column(name="amount")
    @NotNull //(message = "{amount.notValid}")
    @DecimalMin (value = "0.00", message = "Amount should not be greater than 0")
    @Digits(integer=6, fraction=2, message = "Integer part is 6 digits and fraction part is 2 digits")
    private BigDecimal amount;

    @Column(name="expenseType")
    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    protected Expense() {
    }

    protected Expense(Long id, String description, LocalDate expenseDate, BigDecimal amount, ExpenseType expenseType) {
        super();
        this.id = id;
        this.description = description;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.expenseType = expenseType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }
}
