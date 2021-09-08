package study.example.repository;

import org.springframework.security.core.userdetails.UserDetails;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import study.example.model.User;

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
    EntityManager em;

    public List<Expense> findExpensesByDateAndType(
            LocalDate startExpenseDate,
            LocalDate finishExpenseDate,
            ExpenseType expenseType) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery <Expense> cq = cb.createQuery(Expense.class);

        Root <Expense> expense = cq.from(Expense.class);
        List <Predicate> predicates = new ArrayList<>();

        if (startExpenseDate != null) {
            Predicate date = cb.greaterThanOrEqualTo(expense.get("expenseDate"), startExpenseDate);
            predicates.add(date);
        }

        if (finishExpenseDate != null) {
            Predicate date = cb.lessThanOrEqualTo(expense.get("expenseDate"), finishExpenseDate);
            predicates.add(date);
        }

        if (expenseType != null) {
            Predicate type = cb.equal(expense.get("expenseType"), expenseType);
            predicates.add(type);
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Expense> findExpensesByDateAndTypeAndEmployee(

            LocalDate startExpenseDate,
            LocalDate finishExpenseDate,
            ExpenseType expenseType,
            Long employeeId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Expense> cq = cb.createQuery(Expense.class);

        Root<Expense> expense = cq.from(Expense.class);
        List<Predicate> predicates = new ArrayList<>();

        if (startExpenseDate != null) {
            Predicate date = cb.greaterThanOrEqualTo(expense.get("expenseDate"), startExpenseDate);
            predicates.add(date);
        }

        if (finishExpenseDate != null) {
            Predicate date = cb.lessThanOrEqualTo(expense.get("expenseDate"), finishExpenseDate);
            predicates.add(date);
        }

        if (expenseType != null) {
            Predicate type = cb.equal(expense.get("expenseType"), expenseType);
            predicates.add(type);
        }

        if(employeeId != null){
            Predicate predicateEmp = cb.equal(expense.get("userId"), employeeId);
            predicates.add(predicateEmp);
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
