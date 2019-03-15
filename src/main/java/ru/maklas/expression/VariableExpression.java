package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class VariableExpression extends Expression {

    String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        Double val = parameters.get(variableName);
        if (val == null) throw new ExpressionEvaluationException("Variable " + variableName + " passed without value");
        return val;
    }

    @Override
    protected void obtainVariables(Array<String> vars) {
        if (!vars.contains(variableName, false)) {
            vars.add(variableName);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VariableExpression && variableName.equals(((VariableExpression) obj).variableName);
    }

    @Override
    public String toString() {
        return variableName;
    }

    @Override
    public VariableExpression cpy() {
        return new VariableExpression(variableName);
    }
}
