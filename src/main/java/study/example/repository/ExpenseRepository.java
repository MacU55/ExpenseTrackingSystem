package study.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import study.example.model.Expense;

import java.time.LocalDate;
import java.util.List;


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

