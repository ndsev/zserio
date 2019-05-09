package zserio.ast;

import java.util.List;

/**
 * Implementation of ZserioAstVisitor which manages evaluating phase.
 */
public class ZserioAstEvaluator extends ZserioAstWalker
{
    /**
     * Constructor.
     */
    public ZserioAstEvaluator()
    {
        this.evaluationScope = null;
    }

    /**
     * Constructor with forced evaluation scope.
     *
     * @param evaluationScope Evaluation scope to use.
     */
    public ZserioAstEvaluator(Scope evaluationScope)
    {
        this.evaluationScope = evaluationScope;
    }

    @Override
    public void visitPackage(Package pkg)
    {
        currentPackage = pkg;

        pkg.visitChildren(this);

        currentPackage = null;
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        constType.visitChildren(this);
        constType.evaluate();
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        structureType.visitChildren(this);
        structureType.evaluate();
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        // force selector expression evaluation
        final Expression selectorExpression = choiceType.getSelectorExpression();
        selectorExpression.accept(this);

        // extend scope for case expressions to support enumeration values if necessary
        final ZserioType selectorExprZserioType = selectorExpression.getExprZserioType();
        if (selectorExprZserioType instanceof EnumType)
        {
            final Scope enumScope = ((EnumType)selectorExprZserioType).getScope();
            for (ChoiceCase choiceCase : choiceType.getChoiceCases())
            {
                final List<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
                for (ChoiceCaseExpression caseExpression : caseExpressions)
                    caseExpression.getExpression().addEvaluationScope(enumScope);
            }
        }

        choiceType.visitChildren(this);
        choiceType.evaluate();
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        unionType.visitChildren(this);
        unionType.evaluate();
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        enumType.visitChildren(this);
        enumType.evaluate();
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        sqlTableType.visitChildren(this);
        sqlTableType.evaluate();
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        sqlDatabaseType.visitChildren(this);
        sqlDatabaseType.evaluate();
    }

    @Override
    public void visitField(Field field)
    {
        field.visitChildren(this);
        field.evaluate(currentPackage);
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        enumItem.visitChildren(this);
        enumItem.evaluate();
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        sqlConstraint.visitChildren(this);
        sqlConstraint.evaluate();
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        rpc.visitChildren(this);
        rpc.evaluate();
    }

    @Override
    public void visitFunction(FunctionType functionType)
    {
        functionType.visitChildren(this);
        functionType.evaluate();
    }

    @Override
    public void visitExpression(Expression expression)
    {
        expression.visitChildren(this);
        if (evaluationScope == null)
            expression.evaluate();
        else
            expression.evaluate(evaluationScope);
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        arrayType.visitChildren(this);
        arrayType.evaluate();
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        typeInstantiation.visitChildren(this);
        typeInstantiation.evaluate();
    }

    @Override
    public void visitBitFieldType(BitFieldType bitFieldType)
    {
        bitFieldType.visitChildren(this);
        bitFieldType.evaluate();
    }

    private final Scope evaluationScope;

    private Package currentPackage = null;
};
