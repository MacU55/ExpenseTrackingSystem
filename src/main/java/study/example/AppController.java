package study.example;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
    public String viewHomePage(Model model, @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
            LocalDate startExpenseDate, @RequestParam(required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
            LocalDate finishExpenseDate) throws IOException {

            List<Expense> expenseList = service.listAll(startExpenseDate, finishExpenseDate);
            model.addAttribute("expenseList", expenseList);
            LOGGER.info("expense list was returned successfully");
            model.addAttribute("startExpenseDate", startExpenseDate);
            model.addAttribute("finishExpenseDate", finishExpenseDate);
            return "index";

    }

    // handling to download csv file by selecting dates range
    @RequestMapping(value = "/", method = {RequestMethod.POST}, params = "export")
    public void exportToCSVByDates(HttpServletResponse response,  @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
            LocalDate startExpenseDate, @RequestParam(required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
            LocalDate finishExpenseDate) throws IOException {

        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);
        List<Expense> expenseList = service.listAll(startExpenseDate, finishExpenseDate);
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

    // handling to upload csv file
    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model, BindingResult bindingResult) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {

            // parse CSV file to create a list of `Expenses` objects
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                // create csv bean reader
                CsvToBean<Expense> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(Expense.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of expenses
                List<Expense> expenseList = csvToBean.parse();

                // TODO: save users in DB?

                // save expenses list on model
                model.addAttribute("expenseList", expenseList);
                model.addAttribute("status", true);

            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }
        }

        return "file-upload-status";
    }




}

