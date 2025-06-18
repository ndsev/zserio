package zserio.ast;

import java.util.List;

import zserio.antlr.ZserioLexer;

/**
 * Implementation of ZserioAstVisitor which manages evaluating phase.
 */
public final class ZserioAstEvaluator extends ZserioAstWalker
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
        structureType.visitChildren(this);

        if (!structureType.getTemplateParameters().isEmpty())
            visitInstantiations(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        // force selector expression evaluation
        final Expression selectorExpression = choiceType.getSelectorExpression();
        selectorExpression.accept(this);

        // extend scope for case expressions to support enumeration items and bitmask values if necessary
        final ZserioType selectorExprZserioType = selectorExpression.getExprZserioType();
        if (selectorExprZserioType instanceof EnumType || selectorExprZserioType instanceof BitmaskType)
        {
            final Scope additionalScope = ((ZserioScopedType)selectorExprZserioType).getScope();
            final AddEvaluationScopeVisitor addScopeVisitor = new AddEvaluationScopeVisitor(additionalScope);
            for (ChoiceCase choiceCase : choiceType.getChoiceCases())
            {
                final List<ChoiceCaseExpression> caseExpressions = choiceCase.getExpressions();
                for (ChoiceCaseExpression caseExpression : caseExpressions)
                    caseExpression.getExpression().accept(addScopeVisitor);
            }
        }

        choiceType.visitChildren(this);

        if (!choiceType.getTemplateParameters().isEmpty())
        {
            visitInstantiations(choiceType);
        }
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        unionType.visitChildren(this);

        if (!unionType.getTemplateParameters().isEmpty())
        {
            visitInstantiations(unionType);
        }
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
    public void visitBitmaskType(BitmaskType bitmaskType)
    {
        bitmaskType.visitChildren(this);
        bitmaskType.evaluate();
    }

    @Override
    public void visitField(Field field)
    {
        field.visitChildren(this);
        field.evaluate();
    }

    @Override
    public void visitBitmaskValue(BitmaskValue bitmaskValue)
    {
        bitmaskValue.visitChildren(this);
        bitmaskValue.evaluate();
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        sqlTableType.visitChildren(this);

        if (!sqlTableType.getTemplateParameters().isEmpty())
        {
            visitInstantiations(sqlTableType);
        }
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        sqlConstraint.visitChildren(this);
        sqlConstraint.evaluate();
    }

    @Override
    public void visitRule(Rule rule)
    {
        rule.visitChildren(this);
        rule.evaluate();
    }

    @Override
    public void visitExpression(Expression expression)
    {
        if (expression.getType() == ZserioLexer.ISSET)
        {
            // first operand of isset operator must be a bitmask, force it's evaluation
            expression.op1().accept(this);

            final ZserioType op1ZserioType = expression.op1().getExprZserioType();
            if (op1ZserioType instanceof BitmaskType)
            {
                // use the bitmask type as an additional scope for second operand to allow
                // direct use of bitmask values
                final Scope additionalScope = ((BitmaskType)op1ZserioType).getScope();
                final AddEvaluationScopeVisitor addScopeVisitor =
                        new AddEvaluationScopeVisitor(additionalScope);
                expression.op2().accept(addScopeVisitor);
            }
        }

        expression.visitChildren(this);

        if (evaluationScope == null)
            expression.evaluate();
        else
            expression.evaluate(evaluationScope);
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        typeInstantiation.visitChildren(this);
        typeInstantiation.evaluate();
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
                throw new InstantiationException(e, instantiation.getInstantiationReferenceStack());
            }
        }
    }

    private static final class AddEvaluationScopeVisitor extends ZserioAstWalker
    {
        public AddEvaluationScopeVisitor(Scope additionalEvaluationScope)
        {
            this.additionalEvaluationScope = additionalEvaluationScope;
        }

        @Override
        public void visitExpression(Expression expression)
        {
            expression.visitChildren(this);
            expression.addEvaluationScope(additionalEvaluationScope);
        }

        private final Scope additionalEvaluationScope;
    }

    private final Scope evaluationScope;
};
