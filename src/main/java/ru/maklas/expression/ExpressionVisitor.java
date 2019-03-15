package ru.maklas.expression;

public interface ExpressionVisitor {

    void visit(ValueExpression e);

    void visit(VariableExpression e);

    void visit(SignExpression e);

    void visit(FunctionExpression e);

    void visit(CummulativeResolveExpression e);

}
