package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.Comparator;

public class SignExpression extends Expression {

    public enum Sign {
        MINUS("-", 2),
        PLUS("+", 2),
        DIV("/", 1),
        MUL("*", 1),
        POW("^", 0);

        private String text;
        private int orderOfExecution; //0 goes first. 100 last

        Sign(String text, int orderOfExecution) {
            this.text = text;
            this.orderOfExecution = orderOfExecution;
        }

        public String asText() {
            return text;
        }

    }
    Sign sign;

    public SignExpression(Sign sign) {
        this.sign = sign;
    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        throw new ExpressionEvaluationException("Internal error. SignExpression shall not be evaluated");
    }

    public double evaluate(double a, double b) throws ExpressionEvaluationException {
        switch (sign){
            case MINUS: return a - b;
            case PLUS: return a + b;
            case DIV: return a / b;
            case MUL: return a * b;
            case POW: return ExpressionUtils.safePow(a, b);
        }
        throw new ExpressionEvaluationException("Unknown sign command: " + sign);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SignExpression && sign == ((SignExpression) obj).sign;
    }

    @Override
    public String toString() {
        return sign.asText();
    }

    public static final Comparator<Sign> signComparator = new Comparator<Sign>() {
        @Override
        public int compare(Sign s1, Sign s2) {
            int o1 = s1.orderOfExecution;
            int o2 = s2.orderOfExecution;
            return (o1 < o2) ? -1 : ((o1 == o2) ? 0 : 1);
        }
    };
}
