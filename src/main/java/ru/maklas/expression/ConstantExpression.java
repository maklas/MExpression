package ru.maklas.expression;

public class ConstantExpression extends ValueExpression {

    private final String name;

    public ConstantExpression(double value, String name) {
        super(value, Source.CONST);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public ConstantExpression cpy() {
        return new ConstantExpression(getValue(), name);
    }
}
