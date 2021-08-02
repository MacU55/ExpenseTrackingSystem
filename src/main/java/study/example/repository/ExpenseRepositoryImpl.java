package study.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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

    public void saveImageFileToDatabase(MultipartFile file) throws IOException, SQLException{


    }

}

