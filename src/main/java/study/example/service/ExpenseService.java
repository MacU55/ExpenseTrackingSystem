package study.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import study.example.exceptions.MyFileNotFoundException;
import study.example.model.Expense;
import study.example.model.ExpenseType;
import study.example.repository.ExpenseRepository;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository repo;

    public List<Expense> listAllForExport(LocalDate startExpenseDate, LocalDate finishExpenseDate, ExpenseType expenseType) {
        return repo.findExpensesByDateAndTypeForExport(startExpenseDate, finishExpenseDate, expenseType);
    }

    public Page<Expense> listAll(LocalDate startExpenseDate, LocalDate finishExpenseDate, ExpenseType expenseType,
          int pageNum, String sortField, String sortDir) {

                Pageable pageable = PageRequest.of(pageNum - 1, 5,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
                );

        return repo.findExpensesByDateAndType(pageable, startExpenseDate, finishExpenseDate, expenseType);
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

    // method to insert data from csv file to database
    public void saveFromSCVToDatabase(MultipartFile file) {
        try {
            List<Expense> expenseList = CSVHelper.csvToExpenses(file.getInputStream());
            repo.saveAll(expenseList);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    //to find image file for downloading (photoProof)
    public Expense getInstance(int fileId) {
        return repo.findById((long) fileId)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }

    // to download image file (photoProof)
    public ResponseEntity<Resource> firstdownloadPhotoProof(Expense expense){

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/jpg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + expense.getId() + "\"" + ".jpg")
                .body(new ByteArrayResource(expense.getPhotoProof()));
    }
}

