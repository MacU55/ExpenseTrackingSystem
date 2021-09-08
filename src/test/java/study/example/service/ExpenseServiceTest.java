package study.example.service;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import study.example.model.Expense;
import study.example.repository.ExpenseRepository;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Captor
    ArgumentCaptor <Expense> expenseCaptor;

    @Captor
    ArgumentCaptor <List<Expense>> listExpensesCaptor;

    @Test
    public void testSave() {

        Expense expense = new Expense();
        expenseService.save(expense);
        Mockito.verify(expenseRepository).save(expenseCaptor.capture());
        Expense value = expenseCaptor.getValue();
        assertEquals(expense, value);
        System.out.println("Test of method testSave() is successful");
    }

    @Test
    public void testGet() {

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
        System.out.println("Test of method testGet() is successful");
    }

    @Test
    public void testDelete() {

        Expense expense = new Expense();
        expense.setDescription("test of method delete() of ExpenseService");
        long id = 1L;
        expense.setId(id);
        expenseService.save(expense);
        expenseService.delete(expense.getId());
        verify(expenseRepository).deleteById(expense.getId());
        System.out.println("Test of method testDelete() is successful");
    }
/*
    @Test
    public void testFindExpensesByDateAndType() {
    }

 */
    @Test
    public void saveFromSCVToDatabase() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_csv_insert.csv");
        List<Expense> expenseList = CSVHelper.csvToExpenses(inputStream);
        expenseRepository.saveAll(expenseList);
        Mockito.verify(expenseRepository).saveAll(listExpensesCaptor.capture());
        List <Expense> value = listExpensesCaptor.getValue();
        assertEquals(expenseList, value);
        System.out.println("Test of method saveFromSCVToDatabase() is successful");
    }
}