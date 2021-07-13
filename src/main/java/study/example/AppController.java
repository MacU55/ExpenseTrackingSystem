package study.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;


@Controller
public class AppController {

    @Autowired
    private ExpenseService service;

    @RequestMapping("/")
    public String viewHomePage(Model model) {
        List<Expense> expenseList = service.listAll();
        model.addAttribute("expenseList", expenseList);

        return "index";
    }

    @RequestMapping("/new")
    public String showNewExpensePage(Model model) {
        Expense expense = new Expense();
        model.addAttribute("expense", expense);

        return "new_expense";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveExpense(@ModelAttribute("expense") Expense expense) {
        service.save(expense);
        return "redirect:/";
    }
/*

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

 */




    @RequestMapping("/edit/{id}")
    public ModelAndView showEditExpensePage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_expense");
        Expense expense = service.get(id);
        mav.addObject("expense", expense);

        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteExpense(@PathVariable(name = "id") int id) {
        service.delete(id);
        return "redirect:/";
    }
}

