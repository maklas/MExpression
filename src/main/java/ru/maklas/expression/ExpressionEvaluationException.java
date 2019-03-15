package ru.maklas.expression;

import org.jetbrains.annotations.Nullable;

public class ExpressionEvaluationException extends Exception {

    private Token[] tokens;

    /** Simple Expression Error with message**/
    public ExpressionEvaluationException(String message) {
        super(message);
    }

    /** Expression Exception that tells which tokens exactly are a source of a problem **/
    public ExpressionEvaluationException(String message, Token... tokens) {
        super(message);
        this.tokens = tokens;
    }

    @Nullable
    public Token[] getTokens() {
        return tokens;
    }
}
