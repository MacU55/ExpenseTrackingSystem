package study.example.model;

public enum ExpenseType {
    TRANSPORT("Transport"), LODGING("Lodging"),
    EDUCATION("Education"), MEALS("Meals"),
    MISCELLANEOUS("Miscellaneous");

    String label;

    public String getLabel() {
        return label;
    }

    ExpenseType(String label) {
        this.label = label;

    }
}

