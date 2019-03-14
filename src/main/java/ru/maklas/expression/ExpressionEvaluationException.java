package ru.maklas.expression;

public class ExpressionEvaluationException extends Exception {

    public ExpressionEvaluationException() {

    }

    public ExpressionEvaluationException(String message) {
        super(message);
    }

    public ExpressionEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpressionEvaluationException(Throwable cause) {
        super(cause);
    }

}
