package study.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
            Predicate dateRange =  cb.greaterThanOrEqualTo(expense.get("expenseDate"), startExpenseDate);
            predicates.add(dateRange);
        }

        if(finishExpenseDate != null){
            Predicate dateRange =  cb.lessThanOrEqualTo(expense.get("expenseDate"), finishExpenseDate);
            predicates.add(dateRange);
        }

        if(expenseType != null){
            Predicate dateRange =  cb.equal(expense.get("expenseType"), expenseType);
            predicates.add(dateRange);
        }


        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
