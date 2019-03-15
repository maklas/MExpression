package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.Comparator;

public class SignExpression extends Expression {

    private Token token;
    final Sign sign;

    public SignExpression(Token token) {
        this.token = token;
        switch (token.type){
            case plus:
                sign = Sign.PLUS;
                break;
            case minus:
                sign = Sign.MINUS;
                break;
            case multiply:
                sign = Sign.MUL;
                break;
            case divide:
                sign = Sign.DIV;
                break;
            case pow:
                sign = Sign.POW;
                break;
        }
        throw new RuntimeException("Unacceptable Token type for SignExpression");
    }

    public SignExpression(Token token, Sign sign) {
        this.token = token;
        this.sign = sign;
    }

    public Token getToken() {
        return token;
    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        throw new ExpressionEvaluationException("Internal error. SignExpression must not be evaluated");
    }

    public double evaluate(double a, double b) throws ExpressionEvaluationException {
        switch (sign){
            case MINUS: return a - b;
            case PLUS: return a + b;
            case DIV: return a / b;
            case MUL: return a * b;
            case POW: return ExpressionUtils.safePow(a, b);
        }
        throw new ExpressionEvaluationException("Unexpected sign command: " + sign);
    }


    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SignExpression && sign == ((SignExpression) obj).sign;
    }

    @Override
    public String toString() {
        return sign.asText();
    }

    @Override
    public SignExpression cpy() {
        return this;
    }

}
