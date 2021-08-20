package study.example.model;

public enum Role {
    EMPLOYEE("Employee"), MANAGER("Manager");

    public String label;

    public String getLabel(){
        return label;
    }

    Role(String label){
        this.label = label;
    }

}
