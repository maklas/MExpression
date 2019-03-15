package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class FunctionExpression extends Expression {

    private Token functionName;
    private Array<Expression> parameters;

    public FunctionExpression(Token functionName, Array<Expression> parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public Token getFunctionName() {
        return functionName;
    }

    public Array<Expression> getParameters() {
        return parameters;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        Array<Double> values = new Array<Double>();
        for (Expression parameter : this.parameters) {
            values.add(parameter.evaluate(parameters));
        }

        return ExpressionUtils.evaluateFunction(functionName.getContent(), values);
    }

    @Override
    protected void obtainVariables(Array<String> vars) {
        for (Expression exp : parameters) {
            exp.obtainVariables(vars);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FunctionExpression
                && functionName.getContent().equals(((FunctionExpression) obj).functionName.getContent())
                && parameters.equals(((FunctionExpression) obj).parameters);
    }

    @Override
    public FunctionExpression cpy() {
        Array<Expression> paramCpy = new Array<Expression>();
        for (Expression parameter : parameters) {
            paramCpy.add(parameter.cpy());
        }

        return new FunctionExpression(functionName, paramCpy);
    }

    @Override
    public String toString() {
        return functionName.getContent() + "(" + parameters.toString(", ") + ")";
    }
}
