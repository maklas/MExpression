package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Expression that represents a variable. Value can only be known at time of evaluation!
 */
public class VariableExpression extends Expression {

    private Token token;
    private String name;

    VariableExpression(Token token) {
        this.token = token;
        this.name = token.content;
    }

    /** Literal name of the variable **/
    public String getName() {
        return name;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        Double val = parameters.get(name);
        if (val == null) throw new ExpressionEvaluationException("Variable '" + name + "' passed without value");
        return val;
    }

    @Override
    protected void obtainVariables(Array<String> vars) {
        if (!vars.contains(name, false)) {
            vars.add(name);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VariableExpression && name.equals(((VariableExpression) obj).name);
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public VariableExpression cpy() {
        return new VariableExpression(token);
    }
}
