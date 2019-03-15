package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class FunctionExpression extends Expression {

    private Token functionToken;
    private Array<Expression> parameters;

    public FunctionExpression(Token functionToken, Array<Expression> parameters) {
        this.functionToken = functionToken;
        this.parameters = parameters;
    }

    public Token getFunctionToken() {
        return functionToken;
    }

    public String getFunctionName(){
        return functionToken.content;
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

        return ExpressionUtils.evaluateFunction(functionToken.getContent(), values);
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
                && functionToken.getContent().equals(((FunctionExpression) obj).functionToken.getContent())
                && parameters.equals(((FunctionExpression) obj).parameters);
    }

    @Override
    public FunctionExpression cpy() {
        Array<Expression> paramCpy = new Array<Expression>();
        for (Expression parameter : parameters) {
            paramCpy.add(parameter.cpy());
        }

        return new FunctionExpression(functionToken, paramCpy);
    }

    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visit(this);
        for (Expression parameter : parameters) {
            parameter.visit(visitor);
        }
    }

    @Override
    public String toString() {
        return functionToken.getContent() + "(" + parameters.toString(", ") + ")";
    }
}
