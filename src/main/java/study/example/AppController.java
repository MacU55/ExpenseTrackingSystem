package study.example;

//import org.apache.log4j.RollingFileAppender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import study.example.model.Expense;
import study.example.model.ExpenseType;
import study.example.service.ExpenseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Controller
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private ExpenseService service;

    @ModelAttribute
    LocalDate initLocalDate() {
        return LocalDate.now();
    }

    // handling start page
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String viewHomePage(
       Model model,
       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startExpenseDate,
       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishExpenseDate,
       @RequestParam(required = false) ExpenseType expenseType,
       @RequestParam(required = false) String export
    ) {
       if(export != null){
           return "forward:/export";
       }
        List<Expense> expenseList = service.listAll(startExpenseDate, finishExpenseDate, expenseType);
       model.addAttribute("expenseList", expenseList);
       LOGGER.info("expense list was returned successfully");
       model.addAttribute("startExpenseDate", startExpenseDate);
       model.addAttribute("finishExpenseDate", finishExpenseDate);
       model.addAttribute("expenseType", expenseType);
       return "index";
      }

    // handling to download csv file by selecting dates range
    @RequestMapping(value = "/export", method = {RequestMethod.POST})
    public void exportToCSVByDates(HttpServletResponse response, Model model, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startExpenseDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                           LocalDate finishExpenseDate, HttpServletRequest httpServletRequest,  @RequestParam(required = false) ExpenseType expenseType)
            throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);

        List<Expense> expenseList = service.listAll(startExpenseDate, finishExpenseDate, expenseType);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Expense Id", "Description", "Expense date", "Amount", "Expense type"};
        String[] nameMapping = {"id", "description", "expenseDate", "amount", "expenseType"};

        csvWriter.writeHeader(csvHeader);

        for (Expense expense : expenseList) {
            csvWriter.write(expense, nameMapping);
        }
        csvWriter.close();
    }


    // handling to forward on form for creating new expense
    @RequestMapping("/new")
    public String showNewExpensePage(Model model) {
        Expense expense = new Expense();
        model.addAttribute("expense", expense);
        return "new_expense";
    }

    // handling to save new expense
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveExpense(@Valid @ModelAttribute("expense") Expense expense, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("incorrect data in  form");
            return "new_expense";
        }
        service.save(expense);
        LOGGER.info("new expense is added successfully");
        return "redirect:/";
    }

    // handling to edit selected expense
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditExpensePage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_expense");
        Expense expense = service.get(id);
        mav.addObject("expense", expense);
        LOGGER.info("selected expense was edited successfully");
        return mav;
    }

    // handling to delete selected expense
    @RequestMapping("/delete/{id}")
    public String deleteExpense(@PathVariable(name = "id") int id) {
        service.delete(id);
        LOGGER.info("selected expense was deleted successfully");
        return "redirect:/";
    }


}

