package study.example.model;

public enum Role {
    EMPLOYEE("Employee"), MANAGER("Manager");

    public String label;

    Role(String label){
        this.label = label;
    }

}
