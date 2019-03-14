package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

/** Compiled expression from string. Can be evaluated and produces output as a double **/
public abstract class Expression {

    public abstract double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException ;

    /**
     * Tries to simplify this Expression.Returns true if any simplification took place.
     * Simplification might change data in this expression, which might lead to loss of original information.
     * E.g inlining <b>2.0 + pi</b> into <b>5.141592...</b>, so use it only if this instance of expression
     * will be re-evaluated with different parameters.
     * Otherwise, most likely not worth calling at all.
     */
    public boolean simplify(){
        boolean simplifiedAtLeastOnce = _simplify();
        boolean simplified = simplifiedAtLeastOnce;
        while (simplified){
            simplified = _simplify();
        }
        return simplifiedAtLeastOnce;
    }

    /** Do one iteration of simplification. Returns true if any simplification took place **/
    private boolean _simplify(){
        return false; //TODO
    }

    /*
    Evaluable Expressions:
    (Val) - just return value
    (Const) - just return Constant value
    (Val + Val) return sum of Values
    (Val * Val) return multiplication
    (f(a, b)) Evaluate A and B, return function result
    (A ^ B) Math.pow(A, B)

    Example:
    (-(-a)) -> -a & (-exp)
    (A + B * C) -> (B * C) & (A + exp)
    (Val + () + max((), ()) * A^(b + const) - abs(()))
    |
    V

    Val + () + max
     */

}
