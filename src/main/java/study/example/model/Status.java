package study.example.model;

public enum Status {
    PENDING("Pending"), APPROVED("Approved"), REJECTED("Rejected");

    String label;

    public String getLabel(){
        return label;
    }

    Status(String label) {
        this.label = label;
    }
}
