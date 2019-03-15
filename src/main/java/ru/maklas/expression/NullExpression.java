package ru.maklas.expression;

/** Just returns 0, since no data is provided **/
public class NullExpression extends ValueExpression {

    private static final NullExpression instance = new NullExpression();

    public static Expression getInstance() {
        return instance;
    }

    NullExpression() {
        super(0, Source.NUMBER);
    }

    @Override
    public NullExpression cpy() {
        return this;
    }

    @Override
    public String toString() {
        return "0";
    }

}
