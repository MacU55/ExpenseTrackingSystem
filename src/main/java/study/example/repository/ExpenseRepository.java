package study.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.example.model.Expense;


public interface ExpenseRepository extends JpaRepository<Expense, Long>, ExpenseRepositoryCustom {

}

 /*
    @Query("SELECT e FROM Expense e WHERE e.expenseType = :expenseType AND e.expenseDate" +
            " BETWEEN :startExpenseDate AND :finishExpenseDate")
    List<Expense> findExpensesBetweenTwoDates(
            @Param("startExpenseDate") LocalDate startExpenseDate,
            @Param("finishExpenseDate") LocalDate finishExpenseDate,
            @Param("expenseType") ExpenseType expenseType
    );
     */

