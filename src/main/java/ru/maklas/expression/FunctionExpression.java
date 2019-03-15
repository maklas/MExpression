package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class FunctionExpression extends Expression {

    private Token token;
    private Array<Expression> parameters;

    public FunctionExpression(Token token, Array<Expression> parameters) {
        this.token = token;
        this.parameters = parameters;
    }

    public Token getToken() {
        return token;
    }

    public String getFunctionName(){
        return token.content;
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

        return ExpressionUtils.evaluateFunction(token.getContent(), values);
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
                && token.getContent().equals(((FunctionExpression) obj).token.getContent())
                && parameters.equals(((FunctionExpression) obj).parameters);
    }

    @Override
    public FunctionExpression cpy() {
        Array<Expression> paramCpy = new Array<Expression>();
        for (Expression parameter : parameters) {
            paramCpy.add(parameter.cpy());
        }

        return new FunctionExpression(token, paramCpy);
    }

    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visit(this);
        for (Expression parameter : parameters) {
            parameter.visit(visitor);
        }
    }

    @Override
    protected boolean _simplify() {
        return super._simplify();
    }

    @Override
    public String toString() {
        return token.getContent() + "(" + parameters.toString(", ") + ")";
    }
}
