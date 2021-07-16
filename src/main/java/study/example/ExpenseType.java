package study.example;

public enum ExpenseType {
    TRANSPORT("Транспорт"), LODGING("Жилье"),
    EDUCATION("Образование"), MEALS("Питание"),
    MISCELLANEOUS("Разное");

    String label;

    public String getLabel() {
        return label;
    }

    ExpenseType(String label) {
        this.label = label;

    }
}

