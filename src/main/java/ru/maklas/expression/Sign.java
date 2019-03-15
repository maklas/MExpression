package ru.maklas.expression;

import java.util.Comparator;

public enum Sign {
    MINUS("-", 2),
    PLUS("+", 2),
    DIV("/", 1),
    MUL("*", 1),
    POW("^", 0);

    private String text;
    private int orderOfExecution; //0 goes first.

    Sign(String text, int orderOfExecution) {
        this.text = text;
        this.orderOfExecution = orderOfExecution;
    }

    public String asText() {
        return text;
    }

    public static final Comparator<Sign> orderOfExecutionSignComparator = new Comparator<Sign>() {
        @Override
        public int compare(Sign s1, Sign s2) {
            int o1 = s1.orderOfExecution;
            int o2 = s2.orderOfExecution;
            return (o1 < o2) ? -1 : ((o1 == o2) ? 0 : 1);
        }
    };


}
