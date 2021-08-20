package study.example.model;

import com.opencsv.bean.CsvBindByName;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "users", schema = "exptracksystem")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @CsvBindByName
    private Long id;

    @Column(name = "email")
    @NotBlank(message = "email must not be empty")
    @CsvBindByName
    private String email;

    @Column(name = "first_name")
    @NotBlank(message = "First name must not be empty")
    @CsvBindByName
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Last name must not be empty")
    @CsvBindByName
    private String lastName;

    @Column(name = "user_password", nullable = false)
    @NotBlank(message = "Password name must not be empty")
    @CsvBindByName
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @CsvBindByName
    private Role role;

    private EnumSet<Role> roleSet = EnumSet.allOf(Role.class);
    public boolean hasRole(String roleName) {
        for (Role role : this.roleSet) {
            if (role.getLabel().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenseList;

    public User() {
    }

    public User(Long id, String email, String firstName, String lastName, String password, Role role, EnumSet<Role> roleSet, List<Expense> expenseList) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.roleSet = roleSet;
        this.expenseList = expenseList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public EnumSet<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(EnumSet<Role> roleSet) {
        this.roleSet = roleSet;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", roleSet=" + roleSet +
                ", expenseList=" + expenseList +
                '}';
    }
}
