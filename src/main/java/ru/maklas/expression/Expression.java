package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/** Compiled expression from string. Can be evaluated and produces output as a double **/
public abstract class Expression {

    //Used for
    private static final ObjectMap<String, Double> noValueMap = new ObjectMap<String, Double>();

    /**
     * Solves expression, providing result as a double
     * @throws ExpressionEvaluationException if expression validation failed or variables don't have any values.
     */
    public final double evaluate() throws ExpressionEvaluationException {
        return evaluate(noValueMap);
    }

    /** gets list of all variables in this equation **/
    public final Array<String> variables(){
        Array<String> vars = new Array<String>();
        obtainVariables(vars);
        return vars;
    }

    protected void obtainVariables(Array<String> vars) {

    }

    /**
     * Solves expression, providing result as a double.
     * @throws ExpressionEvaluationException if expression validation failed or variables don't have any values.
     */
    public abstract double evaluate(ObjectMap<String, Double> parameters) throws ExpressionEvaluationException ;

    /**
     * Tries to simplify this Expression.Returns true if any simplification took place.
     * Simplification might change data in this expression, which might lead to loss of original information.
     * E.g inlining <b>2.0 + pi</b> into <b>5.141592...</b>, so use it only if this instance of expression
     * will be re-evaluated with different parameters.
     * Otherwise, most likely not worth calling at all.
     */
    public final boolean simplify(){
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
}
