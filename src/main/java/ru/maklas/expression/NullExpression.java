package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

/** Just returns 0, since no data is provided **/
public class NullExpression extends Expression {

    public NullExpression() {
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) {
        return 0;
    }

    @Override
    public String toString() {
        return "0";
    }
}
