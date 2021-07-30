package study.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.example.model.Expense;
import study.example.model.ExpenseType;
import study.example.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository repo;

    public List<Expense> listAll(LocalDate startExpenseDate, LocalDate finishExpenseDate, ExpenseType expenseType) {
        return repo.findExpensesByDateAndType(startExpenseDate, finishExpenseDate, expenseType);

    }

    public void save(Expense expense) {
        repo.save(expense);
    }

    public Expense get(long id) {
        return repo.findById(id).get();
    }

    public void delete(long id) {
        repo.deleteById(id);
    }


}

