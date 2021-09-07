package study.example.service;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import study.example.model.Expense;
import study.example.repository.ExpenseRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;


    @InjectMocks
    private ExpenseService expenseService;

    @Test
    public void saveExpenseByRepo() {

        Expense expense = new Expense();
        expense.setDescription("new test Expense description");
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        Expense newExpense = expenseRepository.save(expense);
        assertThat(newExpense.getDescription()).isNotNull();
        Assert.assertEquals(expense, newExpense);
        System.out.println("Test of method saveExpenseByRepo() is successful");
    }

    @Test
    public void saveExpenseByService() {

        Expense expense = new Expense();
        expense.setDescription("test of method save() of ExpenseService");
        expenseService.save(expense);
        Assert.assertEquals("test of method save() of ExpenseService", expense.getDescription());
        System.out.println("Test of method saveExpenseByService() of ExpenseService is successful");
    }

    @Test
    public void getExpenseByService() {

        Expense expense = new Expense();
        expense.setDescription("test of method get() of ExpenseService");
        long id = 1L;
        expense.setId(id);
        expenseService.save(expense);
        when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));
        expenseService.get(id);
        Assert.assertNotNull(expense.getId());
        Assert.assertNotNull(expense.getDescription());
        Assert.assertEquals("test of method get() of ExpenseService", expense.getDescription());
        System.out.println("Test of method getExpenseByService() is successful");
    }

    @Test
    public void deleteExpenseByService() {

        Expense expense = new Expense();
        expense.setDescription("test of method delete() of ExpenseService");
        long id = 1L;
        expense.setId(id);
        expenseService.save(expense);
        expenseService.delete(expense.getId());
        verify(expenseRepository).deleteById(expense.getId());
        System.out.println("Test of method deleteExpenseByService() is successful");
    }


}