package study.example;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "expenses", schema = "exptracksystem")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="description")
    private String description;

    @Column(name="expenseDate")
    @DateTimeFormat (pattern = "yyyy-MM-dd")
    private Date expenseDate;

    @Column(name="amount")
    private float amount;

    @Column(name="expenseType")
    private String expenseType;

    protected Expense() {
    }

    protected Expense(Long id, String description, Date expenseDate, float amount, String expenseType) {
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

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }
}
