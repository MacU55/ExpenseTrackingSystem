package study.example.repository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import study.example.model.Expense;
import study.example.model.ExpenseType;
import study.example.model.User;

import java.io.IOException;
import java.sql.SQLException;
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
