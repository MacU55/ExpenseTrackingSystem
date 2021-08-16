package study.example.repository;

import org.springframework.data.domain.*;
import study.example.model.Expense;
import study.example.model.ExpenseType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public Page<Expense> findExpensesByDateAndType(

            Pageable pageable,
            LocalDate startExpenseDate,
            LocalDate finishExpenseDate,
            ExpenseType expenseType
            )
    {
/*
        Pageable pageable = PageRequest.of(pageNum - 1, 5,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

 */


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


        cq.where(predicates.toArray(new Predicate[0]));

        //return (Page<Expense>) em.createQuery(cq).getResultList(); = original
        return new PageImpl<Expense>(em.createQuery(cq).getResultList(), pageable,
                em.createQuery(cq).getResultList().size());

    }

    // method to get data from database for export into CSV file
    public List<Expense> findExpensesByDateAndTypeForExport(
            LocalDate startExpenseDate,
            LocalDate finishExpenseDate,
            ExpenseType expenseType) {

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


        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }


}

