package study.example.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;


@Entity
@Table(name = "expenses", schema = "exptracksystem")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @CsvBindByName
    private Long id;

    @Column(name = "description")
    @NotBlank(message = "Please fill in 'Expense description' ")
    @CsvBindByName
    private String description;

    @Column(name = "expenseDate")
    @NotNull(message = "Expense date must not be empty")
    @PastOrPresent(message = "Expense date must be in past or in present")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @CsvBindByName
    @CsvDate(value = "yyyy-mm-dd")
    @CsvBindByPosition(position = 8)
    private LocalDate expenseDate = LocalDate.now(ZoneId.of("Europe/Kiev"));

    @Column(name = "amount")
    @NotNull //(message = "{amount.notValid}")
    @DecimalMin(value = "0.00", message = "Amount should not be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Integer part is 6 digits and fraction part is 2 digits")
    @CsvBindByName
    private BigDecimal amount;

    @Column(name = "expenseType")
    @Enumerated(EnumType.STRING)
    @CsvBindByName
    private ExpenseType expenseType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "photoProof")
    private byte[] photoProof;

    @Column(name = "user_id")
    @CsvBindByName
    private Long userId;

    @Column(name = "status", columnDefinition ="varchar(45) default 'PENDING'")
    @Enumerated(EnumType.STRING)
    @CsvBindByName
    private Status status = Status.PENDING;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public Expense() {
    }

    public Expense(Long id, String description, LocalDate expenseDate,
                   BigDecimal amount, ExpenseType expenseType, byte[] photoProof,
                   Long userId, Status status, User user) {
        this.id = id;
        this.description = description;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.expenseType = expenseType;
        this.photoProof = photoProof;
        this.userId = userId;
        this.status = status;
        this.user = user;
    }

    public Expense(String description, LocalDate expenseDate,
                   BigDecimal amount, ExpenseType expenseType, byte[] photoProof,
                   Long userId, Status status) {
        this.id = id;
        this.description = description;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.expenseType = expenseType;
        this.photoProof = photoProof;
        this.userId = userId;
        this.status = status;

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

    public byte[] getPhotoProof() {
        return photoProof;
    }

    public void setPhotoProof(byte[] photoProof) {
        this.photoProof = photoProof;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", expenseDate=" + expenseDate +
                ", amount=" + amount +
                ", expenseType=" + expenseType +
                ", photoProof=" + Arrays.toString(photoProof) +
                ", userId=" + userId +
                ", status=" + status +
                ", user=" + user +
                '}';
    }
}
