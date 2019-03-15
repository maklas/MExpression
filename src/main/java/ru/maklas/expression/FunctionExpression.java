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
    public String toString() {
        return functionName.getContent() + "(" + parameters.toString(", ") + ")";
    }
}
