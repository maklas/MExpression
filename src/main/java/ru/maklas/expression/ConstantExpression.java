package ru.maklas.expression;

/** Represents constant. Which is also a value. **/
public class ConstantExpression extends ValueExpression {

    private boolean negated = false;

    public ConstantExpression(Token token, double value) {
        super(token, value);
    }

    /** Constant name **/
    public String getName() {
        return token.content;
    }

    @Override
    public String toString() {
        return negated ? "-" + token.content : token.content;
    }

    @Override
    public ValueExpression negate() {
        negated = !negated;
        return super.negate();
    }

    @Override
    public ConstantExpression cpy() {
        return new ConstantExpression(token, getValue());
    }
}
