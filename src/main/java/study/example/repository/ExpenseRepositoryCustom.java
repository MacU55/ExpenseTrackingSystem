package study.example.repository;


import study.example.model.Expense;
import study.example.model.ExpenseType;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepositoryCustom {

    List<Expense> findExpensesByDateAndType(LocalDate startExpenseDate,
                                            LocalDate finishExpenseDate,
                                            ExpenseType expenseType);

    List<Expense> findExpensesByDateAndTypeAndEmployee(LocalDate startExpenseDate,
                                                       LocalDate finishExpenseDate,
                                                       ExpenseType expenseType,
                                                       Long employeeId);

}
