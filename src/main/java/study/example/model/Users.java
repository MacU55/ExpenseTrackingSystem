package study.example.model;

import com.opencsv.bean.CsvBindByName;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", schema = "users")
public class Users {

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

    @Column(name = "user_password")
    @NotBlank(message = "Password name must not be empty")
    @CsvBindByName
    private String userPassword;

    public Users() {

    }

    public Users(Long id, String email, String firstName, String lastName, String userPassword) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userPassword = userPassword;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
