package ru.maklas.expression;

/** Just returns 0, since no data is provided **/
public class NullExpression extends ValueExpression {

    private static final NullExpression instance = new NullExpression();

    public static NullExpression getInstance() {
        return instance;
    }

    private NullExpression() {
        super(Token.nullToken, 0);
    }

    @Override
    public NullExpression cpy() {
        return this;
    }

    @Override
    public NullExpression negate() {
        return this;
    }

    @Override
    public String toString() {
        return "0";
    }
}
