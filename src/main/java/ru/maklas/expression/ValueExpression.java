package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

public class ValueExpression extends Expression {

    public enum Source {
        NUMBER, //Вписанное число
        CONST, //Константа из списка констант.
    }

    private double value;
    private Source source;

    public ValueExpression(double value, Source source) {
        this.value = value;
        this.source = source;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) {
        return getValue();
    }

    public double getValue() {
        return value;
    }

    public Source getSource() {
        return source;
    }

    public ValueExpression negate(){
        this.value = - value;
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
