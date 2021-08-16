package study.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepositoryCustom {

    List<Expense> findExpensesByDateAndTypeForExport(LocalDate startExpenseDate,
                                                     LocalDate finishExpenseDate,
                                                     ExpenseType expenseType);

    Page<Expense> findExpensesByDateAndType(Pageable pageable,
                                            LocalDate startExpenseDate,
                                            LocalDate finishExpenseDate,
                                            ExpenseType expenseType);
}
