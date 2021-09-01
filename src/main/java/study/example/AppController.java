package study.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import study.example.model.*;
import study.example.repository.UserRepository;
import study.example.service.CustomUserDetails;
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
import java.util.Objects;
import java.util.Optional;


@Controller
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);
    public static final String NO_PERMISSIONS_HTML = "/no_permissions.html";
    public static final String REDIRECT_NO_PERMISSIONS_HTML = "redirect:" + NO_PERMISSIONS_HTML;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CustomUserDetails customUserDetails;

    @ModelAttribute
    LocalDate initLocalDate() {
        return LocalDate.now();
    }

    //start page
    @GetMapping("")
    public String viewStartPage() {
        return "start";
    }

    // mapping for viewing page login.html
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    //handling for redirecting to certain page user upon its role
    @RequestMapping("/success")
    public void loginPageRedirect(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication authResult) throws IOException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        String userLabel;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
            userLabel = ((CustomUserDetails) principal).getUserLabel();
        } else {
            userEmail = principal.toString();
            userLabel = principal.toString();
        }

        if (userLabel.equals("Employee")) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/expense_list"));
        } else if (userLabel.equals("Manager")) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/users"));
        }

        LOGGER.info("logged user's email:" + userEmail);
        LOGGER.info("logged user's label:" + userLabel);
    }


    //show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    //show page after login
    @GetMapping("/index")
    public String showIndexPage() {
        return "index";
    }

    // make register process
    @PostMapping("/process_register")
    public String processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);
        return "register_success";
    }

    // show users list
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    // handling expense list page
    @RequestMapping(value = "/expense_list", method = {RequestMethod.GET, RequestMethod.POST})
    public String viewHomePage(
            Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startExpenseDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishExpenseDate,
            @RequestParam(required = false) ExpenseType expenseType,
            @RequestParam(required = false) String export,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        if (export != null) {
            return "forward:/export";
        }
        User user = userRepo.findByEmail(currentUser.getUsername());
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("user", user);
        LOGGER.info("User's email is : " + user.getEmail() + ", " + "and user's role is : " + user.getRole());
        List<Expense> expenseList = expenseService.listAll(startExpenseDate, finishExpenseDate, expenseType);
        model.addAttribute("expenseList", expenseList);
        LOGGER.info("expense list was returned successfully");
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("startExpenseDate", startExpenseDate);
        model.addAttribute("finishExpenseDate", finishExpenseDate);
        model.addAttribute("expenseType", expenseType);
        return "expense_list";
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

        List<Expense> expenseList = expenseService.listAll(startExpenseDate, finishExpenseDate, expenseType);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Expense Id", "Description", "Expense date", "Amount", "Expense type", "User Id", "Status"};
        String[] nameMapping = {"id", "description", "expenseDate", "amount", "expenseType", "userId", "status"};

        csvWriter.writeHeader(csvHeader);

        for (Expense expense : expenseList) {
            csvWriter.write(expense, nameMapping);
        }
        csvWriter.close();
    }

    // handling to upload csv file to database
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("CSVFile") MultipartFile file) {
        try {
            expenseService.saveFromSCVToDatabase(file);
//            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/api/csv/download/")
//                    .path(file.getOriginalFilename())
//                    .toUriString();

            return "success_actions/upload_csv_file_success";
        } catch (Exception e) {
            return "fail_actions/upload_csv_file_fail";
        }
    }

    // handling to forward on form for creating new expense
    @RequestMapping("/new")
    public String showNewExpensePage(Model model) {
        Expense expense = new Expense();
        model.addAttribute("expense", expense);
        return "edit_expense";
    }

    // handling to save new expense
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveExpense(
            @Valid @ModelAttribute("expense") Expense expense,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, Model model,
            @AuthenticationPrincipal UserDetails currentUser

    ) throws IOException {

        if (bindingResult.hasErrors()) {
            LOGGER.error("incorrect data in  form");
            return "edit_expense";
        }
        if (!imageFile.isEmpty()) {
            expense.setPhotoProof(imageFile.getBytes());
        }

        User user = userRepo.findByEmail(currentUser.getUsername());
        model.addAttribute("currentUser", user);
        Long userId = user.getId();
        expense.setUserId(userId);
        LOGGER.info("user id is assigned to expense successfully");
        expenseService.save(expense);
        LOGGER.info("an expense is saved successfully");
        return "redirect:/expense_list";
    }

    // handling to edit selected expense
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditExpensePage(@PathVariable(name = "id") int id,
                                            @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        User loggedUser = userRepo.findByEmail(currentUser.getUsername());
        Long loggedUserId = loggedUser.getId();

        ModelAndView mav = new ModelAndView("edit_expense");

        ModelAndView mavPermissions = new ModelAndView(REDIRECT_NO_PERMISSIONS_HTML);
        Expense expense = expenseService.get(id);

        Long userId = expense.getUserId();
        if (!Objects.equals(loggedUserId, userId)) {
            return mavPermissions;
        } else {
            mav.addObject("expense", expense);
            LOGGER.info("selected expense was loaded successfully");
            return mav;
        }

    }

    // handling to delete selected expense
    @RequestMapping("/delete/{id}")
    public String deleteExpense(@PathVariable(name = "id") int id,

                                @AuthenticationPrincipal UserDetails currentUser) {

        User loggedUser = userRepo.findByEmail(currentUser.getUsername());
        Long loggedUserId = loggedUser.getId();
        Expense expense = expenseService.get(id);
        Long userId = expense.getUserId();
        if (!Objects.equals(loggedUserId, userId)) {
            return REDIRECT_NO_PERMISSIONS_HTML;
        } else {
            expenseService.delete(id);
            LOGGER.info("selected expense was deleted successfully");
            return "forward:/expense_list";
        }
    }

    // handling to download photo proof from database for selected expense
    @RequestMapping(value = "/downLoadPhotoProof/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "id") int id,
                                                 @AuthenticationPrincipal UserDetails currentUser) {

        User loggedUser = userRepo.findByEmail(currentUser.getUsername());
        Long loggedUserId = loggedUser.getId();
        Expense expense = expenseService.get(id);
        Long userId = expense.getUserId();
//
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userLabel;
        if (principal instanceof UserDetails) {
            userLabel = ((CustomUserDetails) principal).getUserLabel();
        } else {
            userLabel = principal.toString();
        }

        if ((!Objects.equals(loggedUserId, userId)) & (userLabel.equals("Employee"))) {
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, NO_PERMISSIONS_HTML).build();
        } else {
            Expense expenseGetFile = expenseService.getInstance(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/jpg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + expenseGetFile.getId() + "\"" + ".jpg")
                    .body(new ByteArrayResource(expense.getPhotoProof()));
        }
    }

    // handling to change expense status
    @RequestMapping(value = "/showChangeStatusTemplate/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public String showChangeStatus(@PathVariable(name = "id") int id, Model model
//                                       @ModelAttribute Expense expense
    ) {
        Expense expenseToChangeStatus = expenseService.get(id);
        model.addAttribute(expenseToChangeStatus);
        User employee = expenseToChangeStatus.getUser();
        String employeeFirstName = employee.getFirstName();
        String employeeLastName = employee.getLastName();
        String employeeEmail = employee.getEmail();
        model.addAttribute("employeeFirstName", employeeFirstName);
        model.addAttribute("employeeLastName", employeeLastName);
        model.addAttribute("employeeEmail", employeeEmail);

        return "changeStatus";
    }

    // assign new status for selected expense
    @RequestMapping(value = "/assignNewStatus/{id}", method = RequestMethod.POST)
    public String assignNewStatus(@PathVariable(name = "id") Long id,
                                  @Valid @ModelAttribute(name = "expense") Expense expense,
                                  @RequestParam(name = "statusNew") Status status, Model model,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            LOGGER.error("incorrect data in form");
            return "changeStatus";
        }

        Expense changedExpense=expenseService.get(id);
        changedExpense.setStatus(status);
        expenseService.save(changedExpense);
        model.addAttribute("newExpense", changedExpense);
        User employee = changedExpense.getUser();
        String employeeEmail = employee.getEmail();
        model.addAttribute("employeeEmail", employeeEmail);
        LOGGER.info("new status:" + status);
        return "change_status_success";
    }

    // handling to show expense list for selected employee
    @RequestMapping(value = "/selectedEmployeeExpenseList/{email}", method = {RequestMethod.POST, RequestMethod.GET})
    public String employeeExpenseList(@PathVariable(name = "email") String email, Model model,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startExpenseDate,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishExpenseDate,
                                      @RequestParam(required = false) ExpenseType expenseType
    ) {

        User employee = userRepo.findByEmail(email);
        Long employeeId = employee.getId();
        String employeeFirstName = employee.getFirstName();
        String employeeLastName = employee.getLastName();
        model.addAttribute("employee", employee);
        model.addAttribute("id", employeeId);
        model.addAttribute("employeeFirstName", employeeFirstName);
        model.addAttribute("employeeLastName", employeeLastName);
        LOGGER.info("User's email is : " + employee.getEmail() + ", " + "and employee's role is : " + employee.getRole());
        List<Expense> expenseList = expenseService.listAll(startExpenseDate, finishExpenseDate, expenseType, employeeId);
        model.addAttribute("expenseList", expenseList);
        LOGGER.info("expense list was returned successfully");
        model.addAttribute("startExpenseDate", startExpenseDate);
        model.addAttribute("finishExpenseDate", finishExpenseDate);
        model.addAttribute("expenseType", expenseType);
        return "employees_expense_list";
    }

    // handling to back to expense list for selected employee
//    @RequestMapping(value = "/selectedEmployeeExpenseList/{email}", method = {RequestMethod.POST, RequestMethod.GET})
//    public String backToEmployeeExpenseList(@PathVariable(name = "email") String email){
//        re
//    }

}

