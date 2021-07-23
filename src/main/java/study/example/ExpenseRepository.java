package study.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startExpenseDate AND :finishExpenseDate")
    List<Expense> findExpensesBetweenTwoDates(@Param("startExpenseDate") @DateTimeFormat
            (iso = DateTimeFormat.ISO.DATE) LocalDate startExpenseDate, @Param("finishExpenseDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishExpenseDate);
}


