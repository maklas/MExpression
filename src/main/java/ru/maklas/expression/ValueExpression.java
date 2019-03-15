package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * An expression that represents a real value that can be accessed with {@link #getValue()}.
 * It could be a simple number or a constant.
 */
public class ValueExpression extends Expression {

    protected Token token;
    protected double value;

    public ValueExpression(Token token, double value) {
        this.token = token;
        this.value = value;
    }

    /**
     * <p>
     * Token of the constant in original expression text.
     * For {@link ValueExpression} class, token type could only be one of:
     * <li>{@link Token.Type#constant} ({@link ConstantExpression})</li>
     * <li>{@link Token.Type#number} ({@link ValueExpression})</li>
     * <li>{@link Token.Type#nill} ({@link NullExpression})</li>
     * </p>
     */
    public Token getToken() {
        return token;
    }

    /** @see Token.Type#number **/
    public boolean isNumber(){
        return token.type == Token.Type.number;
    }

    /** @see Token.Type#constant **/
    public boolean isConstant(){
        return token.type == Token.Type.constant;
    }

    /**  @see Token.Type#nill **/
    public boolean isNull(){
        return token.type == Token.Type.nill;
    }

    @Override
    public double evaluate(ObjectMap<String, Double> parameters) {
        return getValue();
    }

    public double getValue() {
        return value;
    }

    /** Negates the value**/
    public ValueExpression negate(){
        this.value = - value;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ValueExpression && value == ((ValueExpression) obj).value);
    }

    @Override
    public void visit(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return value % 1 == 0 ? Long.toString(((long) value)) : String.valueOf(value);
    }

    @Override
    public ValueExpression cpy() {
        return new ValueExpression(token, value);
    }

    public static ValueExpression forToken(Token token){
        switch (token.type){
            case constant:
                return new ConstantExpression(token, ExpressionUtils.getConstantValue(token.content));
            case number:
                double v = Double.parseDouble(token.content);
                return v == 0 ? NullExpression.getInstance() : new ValueExpression(token, v);
            case nill:
                return NullExpression.getInstance();
        }
        throw new RuntimeException(token.type + " is not of Value type ");
    }

    public static ValueExpression forNumber(double value){
        return value == 0 ? NullExpression.getInstance() : new ValueExpression(Token.nullToken, value);
    }
}
