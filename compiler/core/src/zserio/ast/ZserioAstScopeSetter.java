package zserio.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ZserioAstVisitor which handles scopes of symbols.
 */
public class ZserioAstScopeSetter extends ZserioAstWalker
{
    @Override
    public void visitStructureType(StructureType structureType)
    {
        currentScope = structureType.getScope();
        fillExpressionScopes = true;

        structureType.visitChildren(this);

        currentScope = defaultScope;
        expressionScopes.clear();
        fillExpressionScopes = false;
    }

    @Override
    public void visitField(Field field)
    {
        field.getFieldType().accept(this);

        if (field.getAlignmentExpr() != null)
            field.getAlignmentExpr().accept(this);

        if (field.getOffsetExpr() != null)
            field.getOffsetExpr().accept(this);

        if (field.getInitializerExpr() != null)
            field.getInitializerExpr().accept(this);

        if (field.getOptionalClauseExpr() != null)
            field.getOptionalClauseExpr().accept(this);

        currentScope.setSymbol(field.getName(), field);

        if (field.getConstraintExpr() != null)
            field.getConstraintExpr().accept(this);

        if (field.getSqlConstraint() != null)
            field.getSqlConstraint().accept(this);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        currentScope = choiceType.getScope();
        fillExpressionScopes = true;

        for (Parameter parameter : choiceType.getParameters())
            parameter.accept(this);

        currentChoiceOrUnionScope = currentScope;
        currentScope = new Scope(currentChoiceOrUnionScope);

        choiceType.getSelectorExpression().accept(this);

        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
            choiceCase.accept(this);

        if (choiceType.getChoiceDefault() != null)
            choiceType.getChoiceDefault().accept(this);

        currentScope = currentChoiceOrUnionScope;
        currentChoiceOrUnionScope = null;

        for (FunctionType function : choiceType.getFunctions())
            function.accept(this);

        currentScope = defaultScope;
        expressionScopes.clear();
        fillExpressionScopes = false;
    }

    @Override
    public void visitChoiceCase(ChoiceCase choiceCase)
    {
        for (ChoiceCaseExpression caseExpression : choiceCase.getExpressions())
            caseExpression.accept(this);

        if (choiceCase.getField() != null)
            visitChoiceField(choiceCase.getField());
    }

    @Override
    public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        if (choiceDefault.getField() != null)
            visitChoiceField(choiceDefault.getField());
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        currentScope = unionType.getScope();
        fillExpressionScopes = true;

        for (Parameter parameter : unionType.getParameters())
            parameter.accept(this);

        currentChoiceOrUnionScope = currentScope;
        currentScope = new Scope(currentChoiceOrUnionScope);

        for (Field field : unionType.getFields())
            visitChoiceField(field);

        currentScope = currentChoiceOrUnionScope;
        currentChoiceOrUnionScope = null;

        for (FunctionType function : unionType.getFunctions())
            visitFunctionType(function);

        currentScope = defaultScope;
        expressionScopes.clear();
        fillExpressionScopes = false;
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        currentScope = enumType.getScope();

        enumType.visitChildren(this);

        currentScope = defaultScope;
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        enumItem.visitChildren(this);
        currentScope.setSymbol(enumItem.getName(), enumItem);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        currentScope = sqlTableType.getScope();

        sqlTableType.visitChildren(this);

        currentScope = defaultScope;
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        currentScope = sqlDatabaseType.getScope();

        sqlDatabaseType.visitChildren(this);

        currentScope = defaultScope;
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        currentScope = serviceType.getScope();

        serviceType.visitChildren(this);

        currentScope = defaultScope;
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        rpc.visitChildren(this);

        currentScope.setSymbol(rpc.getName(), rpc);
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        arrayType.visitChildren(this);
    }

    @Override
    public void visitFunctionType(FunctionType functionType)
    {
        for (Scope expressionScope : expressionScopes)
            expressionScope.setSymbol(functionType.getName(), functionType);

        functionType.visitChildren(this);

        currentScope.setSymbol(functionType.getName(), functionType);
    }

    @Override
    public void visitParameter(Parameter parameter)
    {
        parameter.visitChildren(this);

        currentScope.setSymbol(parameter.getName(), parameter);
    }

    @Override
    public void visitExpression(Expression expression)
    {
        expression.visitChildren(this);

        final Scope expressionScope = new Scope(currentScope);
        if (fillExpressionScopes)
            expressionScopes.add(expressionScope);
        expression.setEvaluationScope(expressionScope);
    }

    private void visitChoiceField(Field field)
    {
        field.getFieldType().accept(this);

        currentScope.setSymbol(field.getName(), field);
        currentChoiceOrUnionScope.setSymbol(field.getName(), field);

        if (field.getConstraintExpr() != null)
            field.getConstraintExpr().accept(this);

        currentScope.removeSymbol(field.getName());
    }

    private final Scope defaultScope = new Scope((ZserioScopedType)null);
    private Scope currentScope = defaultScope;
    private Scope currentChoiceOrUnionScope = null;

    private final List<Scope> expressionScopes = new ArrayList<Scope>();
    private boolean fillExpressionScopes = false;
}
