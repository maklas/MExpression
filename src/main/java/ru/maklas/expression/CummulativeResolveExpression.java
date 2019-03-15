package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * List of expressions that have signs inbetween them. So that they could be calculated.
 */
public class CummulativeResolveExpression extends Expression {

    Array<Expression> expressions;

    public CummulativeResolveExpression(Array<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException {
        Array<Expression> resultingExpression = new Array<Expression>(true, expressions.size + 2);
        if (expressions.get(0) instanceof SignExpression) {
            throw new ExpressionEvaluationException("First element must not be sign");
        }
        resultingExpression.add(new ValueExpression(expressions.get(0).evaluate(parameters), ValueExpression.Source.NUMBER));
        boolean previousWasValue = true;
        for (int i = 1; i < expressions.size; i++) {
            Expression e = expressions.get(i);

            if (previousWasValue && !(e instanceof SignExpression)){ //Было число и дальше подают число. Надо поставить знак умножения.
                resultingExpression.add(SignExpression.getInstance(SignExpression.Sign.MUL));
                resultingExpression.add(new ValueExpression(e.evaluate(parameters), ValueExpression.Source.NUMBER));
            } else if (previousWasValue) {
                previousWasValue = false;
                resultingExpression.add(e);
            } else if (e instanceof SignExpression){
                throw new ExpressionEvaluationException("Internal error. 2 signs in a row");
            } else {
                resultingExpression.add(new ValueExpression(e.evaluate(parameters), ValueExpression.Source.NUMBER));
                previousWasValue = true;
            }
        }
        while (resultingExpression.size != 1){
            int bestChoice = 1;
            SignExpression bestChoiceSign = (SignExpression) resultingExpression.get(bestChoice);
            for (int i = 3; i < resultingExpression.size; i+= 2) {
                SignExpression e = (SignExpression) resultingExpression.get(i);
                if (SignExpression.signComparator.compare(bestChoiceSign.sign, e.sign) > 0){
                    bestChoice = i;
                    bestChoiceSign = e;
                }
            }

            int insertionIndex = bestChoice - 1;
            ValueExpression a = (ValueExpression) resultingExpression.get(bestChoice - 1);
            ValueExpression b = (ValueExpression) resultingExpression.get(bestChoice + 1);
            ValueExpression result = new ValueExpression(bestChoiceSign.evaluate(a.getValue(), b.getValue()), ValueExpression.Source.NUMBER);
            resultingExpression.removeIndex(insertionIndex);
            resultingExpression.removeIndex(insertionIndex);
            resultingExpression.removeIndex(insertionIndex);
            resultingExpression.insert(insertionIndex, result);
        }

        return ((ValueExpression) resultingExpression.first()).getValue();
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
        return obj instanceof CummulativeResolveExpression
                //TODO not exactly. 3 * 2 == 2 * 3, But this pone wouldn't satisfy
                && expressions.equals(((CummulativeResolveExpression) obj).expressions);
    }

    @Override
    public CummulativeResolveExpression cpy() {
        Array<Expression> expCpy = new Array<Expression>();
        for (Expression parameter : expressions) {
            expCpy.add(parameter.cpy());
        }

        return new CummulativeResolveExpression(expCpy);
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
            if (curr instanceof SignExpression && ((SignExpression) curr).getSign() == SignExpression.Sign.POW){
                sb.append(curr.toString());
            } else if (prev instanceof SignExpression && ((SignExpression) prev).getSign() == SignExpression.Sign.POW) {
                sb.append(curr);
            } else if (prev instanceof ValueExpression && ((ValueExpression) prev).getSource() == ValueExpression.Source.NUMBER && curr instanceof VariableExpression) {
                sb.append(curr);
            } else if (curr instanceof CummulativeResolveExpression) {
                sb.append(" (").append(curr).append(")");
            } else {
                sb.append(" ").append(curr);
            }
        }
        return sb.toString();
    }
}
