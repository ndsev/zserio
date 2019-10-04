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
    public void visitStructureType(StructureType structureType)
    {
        if (structureType.getTemplateParameters().isEmpty())
            structureType.visitChildren(this);
        else
            visitInstantiations(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        if (choiceType.getTemplateParameters().isEmpty())
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
        }
        else
        {
            visitInstantiations(choiceType);
        }
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        if (unionType.getTemplateParameters().isEmpty())
            unionType.visitChildren(this);
        else
            visitInstantiations(unionType);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        enumType.visitChildren(this);
        enumType.evaluate();
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        enumItem.visitChildren(this);
        enumItem.evaluate();
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        if (sqlTableType.getTemplateParameters().isEmpty())
            sqlTableType.visitChildren(this);
        else
            visitInstantiations(sqlTableType);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        sqlConstraint.visitChildren(this);
        sqlConstraint.evaluate();
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

    @Override
    public void visitRpc(Rpc rpc)
    {
        rpc.visitChildren(this);
        rpc.evaluate();
    }

    private void visitInstantiations(ZserioTemplatableType template)
    {
        for (ZserioTemplatableType instantiation : template.getInstantiations())
        {
            try
            {
                instantiation.accept(this);
            }
            catch (ParserException e)
            {
                final ParserStackedException stackedException = new ParserStackedException(e);
                stackedException.pushMessage(instantiation.getInstantiationLocation(),
                        "In instantiation of '" + template.getName() +
                        "' required from here");
                throw stackedException;
            }
        }
    }

    private final Scope evaluationScope;
};
