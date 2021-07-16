package study.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


@Controller
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private ExpenseService service;

    @RequestMapping("/")
    public String viewHomePage(Model model) {
        LOGGER.trace("Entering method viewHomePage");
        List<Expense> expenseList = service.listAll();
        LOGGER.debug("getting list of expenses: ");
        model.addAttribute("expenseList", expenseList);
        LOGGER.info("expense list was returned successfully");
        return "index";
    }

    @RequestMapping("/new")
    public String showNewExpensePage(Model model) {
        Expense expense = new Expense();
        model.addAttribute("expense", expense);
        LOGGER.info("new expense is added successfully");
        return "new_expense";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveExpense(@Valid @ModelAttribute("expense") Expense expense, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("incorrect data in  form");
              return "new_expense";
        }
        service.save(expense);
        LOGGER.warn("It's testing logging with Spring Boot...");
        LOGGER.info("new expense is added successfully");
        return "redirect:/";
    }


    @RequestMapping("/edit/{id}")
    public ModelAndView showEditExpensePage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_expense");
        Expense expense = service.get(id);
        mav.addObject("expense", expense);
        LOGGER.info("expense was edited successfully");
        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteExpense(@PathVariable(name = "id") int id) {
        service.delete(id);
        LOGGER.info("selected expense was deleted successfully");
        return "redirect:/";
    }

}

