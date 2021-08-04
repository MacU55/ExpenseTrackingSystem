package study.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import study.example.config.ResponseMessage;
import study.example.model.Expense;
import study.example.model.ExpenseType;
import study.example.service.ExpenseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
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
        if (export != null) {
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
    public void exportToCSVByDates(
            HttpServletResponse response,
            Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startExpenseDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishExpenseDate,
            HttpServletRequest httpServletRequest, @RequestParam(required = false) ExpenseType expenseType
    ) throws IOException {
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

    // handling to upload csv file to database
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("CSVFile") MultipartFile file) {
        String message = "";

        try {
            service.saveFromSCVToDatabase(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/csv/download/")
                    .path(file.getOriginalFilename())
                    .toUriString();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message, fileDownloadUri));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message, ""));
        }

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
    public String saveExpense(
            @Valid @ModelAttribute("expense") Expense expense,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) throws SQLException, IOException {

        if (bindingResult.hasErrors()) {
            LOGGER.error("incorrect data in  form");
            return "new_expense";
        }
        //service.saveImageToDatabase(imageFile);
        expense.setPhotoProof(imageFile.getBytes());
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

    // handling to download photo proof from database for selected expense
    @RequestMapping(value = "/downLoadPhotoProof/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "id") int id) {
        // Load file from database
        Expense expense = service.getFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/jpg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + expense.getId() + "\"" + ".jpg")
                .body(new ByteArrayResource(expense.getPhotoProof()));
    }

}

