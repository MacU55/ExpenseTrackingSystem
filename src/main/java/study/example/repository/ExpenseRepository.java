package study.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.example.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, ExpenseRepositoryCustom {

}
/*
    @Query(value="INSERT INTO exptracksystem (photoProof) VALUES (?) WHERE exptracksystem.Expense = Expense expense")
    void saveImageFileToDatabase(@Param("exptracksystem.Expense") Expense expense,
                                 @Param("imageFile") MultipartFile imageFile);

 */
 /*
    @Query("SELECT e FROM Expense e WHERE e.expenseType = :expenseType AND e.expenseDate" +
            " BETWEEN :startExpenseDate AND :finishExpenseDate")
    List<Expense> findExpensesBetweenTwoDates(
            @Param("startExpenseDate") LocalDate startExpenseDate,
            @Param("finishExpenseDate") LocalDate finishExpenseDate,
            @Param("expenseType") ExpenseType expenseType
    );
     */

