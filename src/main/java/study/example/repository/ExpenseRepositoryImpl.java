package study.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepositoryCustom {

    @Autowired
    EntityManager em ;

    public List<Expense> findExpensesByDateAndType(
        LocalDate startExpenseDate,
        LocalDate finishExpenseDate,
        ExpenseType expenseType){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Expense> cq = cb.createQuery(Expense.class);

        Root<Expense> expense = cq.from(Expense.class);
        List<Predicate> predicates = new ArrayList<>();


        if(startExpenseDate != null){
            Predicate date =  cb.greaterThanOrEqualTo(expense.get("expenseDate"), startExpenseDate);
            predicates.add(date);
        }

        if(finishExpenseDate != null){
            Predicate date =  cb.lessThanOrEqualTo(expense.get("expenseDate"), finishExpenseDate);
            predicates.add(date);
        }

        if(expenseType != null){
            Predicate type =  cb.equal(expense.get("expenseType"), expenseType);
            predicates.add(type);
        }


        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }


    public void saveFromCSVToDatabase(MultipartFile file) throws IOException, SQLException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String jdbcURL = "jdbc:mysql://localhost:3306/exptracksystem?serverTimezone=Europe/Kiev&autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false";
        String username = "root";
        String password = "root";

        //String csvFilePath = "Reviews-simple.csv";

        int batchSize = 20;

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            String sql = "INSERT INTO expenses (description, expenseDate, amount, expenseType) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            BufferedReader lineReader = new BufferedReader(new FileReader(fileName));
            String lineText = null;

            int count = 0;

            lineReader.readLine(); // skip header line

            while ((lineText = lineReader.readLine()) != null) {
                String[] data = lineText.split(",");
                String description = data[0];
                String expenseDate = data[1];
                String amount = data[2];
                String expenseType = data.length == 4 ? data[3] : "";

                statement.setString(1, description);

                Timestamp sqlTimestamp = Timestamp.valueOf(expenseDate);
                statement.setTimestamp(2, sqlTimestamp);

                BigDecimal fRating = BigDecimal.valueOf(Long.parseLong(amount));
                statement.setBigDecimal(3, fRating);

                statement.setString(4, expenseType);


                statement.addBatch();

                if (count % batchSize == 0) {
                    statement.executeBatch();
                }
            }

            lineReader.close();

            // execute the remaining queries
            statement.executeBatch();

            connection.commit();
            connection.close();

        } catch (IOException ex) {
            System.err.println(ex);
        } catch (SQLException ex) {
            ex.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void downloadBLOBFromDatabase(Long id) {

    }
}

