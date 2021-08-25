package study.example.model;

public enum Role {
     MANAGER("Manager"), EMPLOYEE("Employee");

    public String label;

    public String getLabel(){
        return label;
    }

    Role(String label){
        this.label = label;
    }

}
