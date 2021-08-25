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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import study.example.config.ResponseMessage;
import study.example.model.Expense;
import study.example.model.ExpenseType;
import study.example.model.Role;
import study.example.model.User;
import study.example.repository.UserRepository;
import study.example.service.CustomUserDetails;
import study.example.service.ExpenseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;


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
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    // mapping to submit user's data while login
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public String login(@RequestParam(value = "email") String email,
//                        @RequestParam(value = "password") String password, HttpSession session) {
//        Result result = customUserDetails.login(email, password);
//        session.setAttribute("user", result.getData());
//
//        if(result.getStatus() == 200){
//            return  "redirect:/profile";
//        } else {
//            return "redirect:/login?error";
//        }
//    }

    @RequestMapping("/success")
    public void loginPageRedirect(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication authResult) throws IOException, ServletException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
String userName1;
String userLabel;
        if (principal instanceof UserDetails) {
            userName1 = ((UserDetails)principal).getUsername();
            userLabel = ((CustomUserDetails)principal).getUserLabel();
        } else {
            userName1 = principal.toString();
            userLabel = principal.toString();
        }

    String role = authResult.getAuthorities().toString();
//    Role result = customUserDetails.getUserRole();

    if (userLabel.equals("Employee")) {
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/expense_list"));
    } else if (userLabel.equals("Manager")) {
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/users"));
    }
    LOGGER.info("from 'Controller', user's role:" + role);
//    LOGGER.info("from 'Controller', role's label:" + customUserDetails.getUserLabel());
    LOGGER.info("from 'Controller', authResult:" + authResult.getPrincipal());
    LOGGER.info("from 'Controller', user's name:" + userName1);
    LOGGER.info("from 'Controller', user's label:" + userLabel);
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
        User user = (User) userRepo.findByEmail(currentUser.getUsername());
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("user", user);
        LOGGER.info("User's email is : " + user.getEmail() +", " + "and user's role is : " + user.getRole());
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
        String[] csvHeader = {"Expense Id", "Description", "Expense date", "Amount", "Expense type", "User Id"};
        String[] nameMapping = {"id", "description", "expenseDate", "amount", "expenseType", "userId"};

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
            expenseService.saveFromSCVToDatabase(file);

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
        return "edit_expense";
    }

    // handling to save new expense
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveExpense(
            @Valid @ModelAttribute("expense") Expense expense,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, Model model,
            @AuthenticationPrincipal UserDetails currentUser

    ) throws SQLException, IOException {

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
        LOGGER.info("new expense is added successfully");
        return "redirect:/expense_list";
    }

    // handling to edit selected expense
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditExpensePage(@PathVariable(name = "id") int id,
                                            @AuthenticationPrincipal UserDetails currentUser) throws IOException {

        User loggedUser = (User) userRepo.findByEmail(currentUser.getUsername());
        Long loggedUserId = loggedUser.getId();

        ModelAndView mav = new ModelAndView("edit_expense");

        ModelAndView mavPermissions = new ModelAndView(REDIRECT_NO_PERMISSIONS_HTML);
        Expense expense = expenseService.get(id);

        Long userId = expense.getUserId();
        if (!Objects.equals(loggedUserId, userId)) {
            return mavPermissions;
        } else {
            mav.addObject("expense", expense);
            LOGGER.info("selected expense was edited successfully");
            return mav;
        }

    }

    // handling to delete selected expense
    @RequestMapping("/delete/{id}")
    public String deleteExpense(@PathVariable(name = "id") int id,

                                @AuthenticationPrincipal UserDetails currentUser) {

        User loggedUser = (User) userRepo.findByEmail(currentUser.getUsername());
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

        User loggedUser = (User) userRepo.findByEmail(currentUser.getUsername());
        Long loggedUserId = loggedUser.getId();
        Expense expense = expenseService.get(id);
        Long userId = expense.getUserId();

        if (!Objects.equals(loggedUserId, userId)) {
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, NO_PERMISSIONS_HTML).build();
        } else {
            Expense expenseGetFile = expenseService.getInstance(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/jpg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + expenseGetFile.getId() + "\"" + ".jpg")
                    .body(new ByteArrayResource(expense.getPhotoProof()));
        }
    }
}

