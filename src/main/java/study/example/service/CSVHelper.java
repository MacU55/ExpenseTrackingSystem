package study.example.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseEnum;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import javax.persistence.EnumType;

public class CSVHelper {

    public static List<Expense> csvToExpenses(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Expense> expenseList = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Expense expense = new Expense();
                expense.setDescription(csvRecord.get("description"));
                expense.setExpenseDate(LocalDate.parse(csvRecord.get("expenseDate")));
                expense.setAmount(new BigDecimal(csvRecord.get("amount")));
                expense.setExpenseType(ExpenseType.valueOf(csvRecord.get("expenseType")));

                expenseList.add(expense);
            }

            return expenseList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
