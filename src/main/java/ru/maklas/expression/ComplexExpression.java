package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * List of expressions that have signs between them. So that they could be calculated accordingly.
 */
public class ComplexExpression extends Expression {

    private Array<Expression> expressions;

    public ComplexExpression(Array<Expression> expressions) {
        this.expressions = expressions;
    }

    public Array<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        Array<Object> results = new Array<Object>(true, expressions.size + 2);

        if (expressions.get(0) instanceof SignExpression) {
            throw new ExpressionEvaluationException("First element must not be sign");
        }

        results.add(expressions.get(0).evaluate(parameters));
        boolean previousWasValue = true;
        for (int i = 1; i < expressions.size; i++) {
            Expression e = expressions.get(i);

            if (previousWasValue && !(e instanceof SignExpression)){ //Было число и дальше подают число. Надо поставить знак умножения.
                results.add(Sign.MUL);
                results.add(e.evaluate(parameters));
            } else if (previousWasValue) {
                previousWasValue = false;
                results.add(((SignExpression) e).sign);
            } else if (e instanceof SignExpression){
                throw new ExpressionEvaluationException("Internal error. 2 signs in a row");
            } else {
                results.add(e.evaluate(parameters));
                previousWasValue = true;
            }
        }
        while (results.size != 1){
            int bestChoice = 1;
            Sign bestChoiceSign = (Sign) results.get(bestChoice);
            for (int i = 3; i < results.size; i+= 2) {
                Sign e = (Sign) results.get(i);
                if (Sign.orderOfExecutionSignComparator.compare(bestChoiceSign, e) > 0){
                    bestChoice = i;
                    bestChoiceSign = e;
                }
            }

            int insertionIndex = bestChoice - 1;
            Double a = (Double) results.get(bestChoice - 1);
            Double b = (Double) results.get(bestChoice + 1);
            Double result = ExpressionUtils.evaluate(bestChoiceSign, a, b);
            results.removeIndex(insertionIndex);
            results.removeIndex(insertionIndex);
            results.removeIndex(insertionIndex);
            results.insert(insertionIndex, result);
        }

        return ((Double) results.first());
    }
    @Override
    protected void obtainVariables(Array<String> vars) {
        for (Expression exp : expressions) {
            if (!(exp instanceof SignExpression)) {
                exp.obtainVariables(vars);
            }
        }
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComplexExpression
                //TODO not exactly. 3 * 2 == 2 * 3, But this pone wouldn't satisfy
                && expressions.equals(((ComplexExpression) obj).expressions);
    }

    @Override
    public ComplexExpression cpy() {
        Array<Expression> expCpy = new Array<Expression>();
        for (Expression parameter : expressions) {
            expCpy.add(parameter.cpy());
        }

        return new ComplexExpression(expCpy);
    }


    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visit(this);
        for (Expression exp : expressions) {
            exp.visit(visitor);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(expressions.get(0).toString());
        for (int i = 1; i < expressions.size; i++) {
            Expression curr = expressions.get(i);
            Expression prev = expressions.get(i - 1);
            if (curr instanceof SignExpression && ((SignExpression) curr).getSign() == Sign.POW){
                sb.append(curr.toString());
            } else if (prev instanceof SignExpression && ((SignExpression) prev).getSign() == Sign.POW) {
                sb.append(curr);
            } else if (prev instanceof ValueExpression && ((ValueExpression) prev).isNumber() && curr instanceof VariableExpression) {
                sb.append(curr);
            } else if (curr instanceof ComplexExpression) {
                sb.append(" (").append(curr).append(")");
            } else {
                sb.append(" ").append(curr);
            }
        }
        return sb.toString();
    }

    /**
     * Hide sub-ComplexExpressions and function parameters
     */
    public String toStringNonRecursive() {
        StringBuilder sb = new StringBuilder();
        sb.append(expressions.get(0).toString());
        for (int i = 1; i < expressions.size; i++) {
            Expression curr = expressions.get(i);
            Expression prev = expressions.get(i - 1);
            if (curr instanceof SignExpression && ((SignExpression) curr).getSign() == Sign.POW){
                sb.append(curr.toString());
            } else if (prev instanceof SignExpression && ((SignExpression) prev).getSign() == Sign.POW) {
                sb.append(curr);
            } else if (prev instanceof ValueExpression && ((ValueExpression) prev).isNumber() && curr instanceof VariableExpression) {
                sb.append(curr);
            } else if (curr instanceof ComplexExpression) {
                sb.append(" (").append("~").append(")");
            } else if (curr instanceof FunctionExpression){
                sb.append(" ").append(((FunctionExpression) curr).getFunctionName()).append("(");
                if (((FunctionExpression) curr).getParameters().size > 0){
                    sb.append("~");
                }

                for (int j = 1; j < ((FunctionExpression) curr).getParameters().size; j++) {
                    sb.append(", ~");
                }
                sb.append(")");
            } else {
                sb.append(" ").append(curr);
            }
        }
        return sb.toString();
    }
}
