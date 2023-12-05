package zserio.extension.doc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.BuiltInType;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.InstantiateType;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.Rule;
import zserio.ast.RuleGroup;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TemplateArgument;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioTemplatableType;
import zserio.extension.common.DefaultTreeWalker;

/**
 * Used by collector.
 *
 * Used by collector gathers all symbols in packages and builds a special maps which
 *
 * - map symbol to the list of all symbols which use the symbol
 * - map symbol to the list of all symbols which is used by the symbol
 *
 * These maps are used by Symbol collaboration dot emitter to get information for collaboration diagrams.
 * These maps are used by Package emitter as well to get information for UsedBy section.
 */
final class UsedByCollector extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void endRoot(Root root)
    {
        for (AstNode node : usedBySymbolsMap.keySet())
        {
            final ZserioTemplatableType templatable =
                    (node instanceof ZserioTemplatableType) ? ((ZserioTemplatableType)node) : null;
            if (templatable != null && templatable.getTemplate() != null)
                collaboratingNodes.add(templatable.getTemplate());
            else
                collaboratingNodes.add(node);
        }
    }

    @Override
    public void beginConst(Constant constant)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        addSymbol(constant.getTypeInstantiation().getTypeReference(), usedSymbols);
        addSymbolsUsedInExpression(constant.getValueExpression(), usedSymbols);
        storeSymbol(constant, usedSymbols);
    }

    @Override
    public void beginRuleGroup(RuleGroup ruleGroup)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        for (Rule rule : ruleGroup.getRules())
            addSymbolsUsedInExpression(rule.getRuleIdExpression(), usedSymbols);
        storeSymbol(ruleGroup, usedSymbols);
    }

    @Override
    public void beginSubtype(Subtype subtype)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        addSymbol(subtype.getTypeReference(), usedSymbols);
        storeSymbol(subtype, usedSymbols);
    }

    @Override
    public void beginStructure(StructureType structureType)
    {
        storeSymbol(structureType, getUsedSymbolsForCompoundType(structureType));
    }

    @Override
    public void beginChoice(ChoiceType choiceType)
    {
        final Set<AstNode> usedSymbols = getUsedSymbolsForCompoundType(choiceType);
        addSymbolsUsedInExpression(choiceType.getSelectorExpression(), usedSymbols);
        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
        {
            for (ChoiceCaseExpression caseExpression : choiceCase.getExpressions())
                addSymbolsUsedInExpression(caseExpression.getExpression(), usedSymbols);
        }
        storeSymbol(choiceType, usedSymbols);
    }

    @Override
    public void beginUnion(UnionType unionType)
    {
        storeSymbol(unionType, getUsedSymbolsForCompoundType(unionType));
    }

    @Override
    public void beginEnumeration(EnumType enumType)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        addSymbol(enumType.getTypeInstantiation().getTypeReference(), usedSymbols);
        for (EnumItem item : enumType.getItems())
        {
            if (item.getValueExpression() != null)
                addSymbolsUsedInExpression(item.getValueExpression(), usedSymbols);
        }
        storeSymbol(enumType, usedSymbols);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        addSymbol(bitmaskType.getTypeInstantiation().getTypeReference(), usedSymbols);
        for (BitmaskValue value: bitmaskType.getValues())
        {
            if (value.getValueExpression() != null)
                addSymbolsUsedInExpression(value.getValueExpression(), usedSymbols);
        }
        storeSymbol(bitmaskType, usedSymbols);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType)
    {
        final Set<AstNode> usedSymbols = getUsedSymbolsForCompoundType(sqlTableType);
        if (sqlTableType.getSqlConstraint() != null)
            addSymbolsUsedInExpression(sqlTableType.getSqlConstraint().getConstraintExpr(), usedSymbols);
        storeSymbol(sqlTableType, usedSymbols);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        storeSymbol(sqlDatabaseType, getUsedSymbolsForCompoundType(sqlDatabaseType));
    }

    @Override
    public void beginService(ServiceType serviceType)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        for (ServiceMethod method : serviceType.getMethodList())
        {
            addSymbol(method.getRequestTypeReference(), usedSymbols);
            addSymbol(method.getResponseTypeReference(), usedSymbols);
        }
        storeSymbol(serviceType, usedSymbols);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        for (PubsubMessage message : pubsubType.getMessageList())
        {
            addSymbol(message.getTypeReference(), usedSymbols);
            addSymbolsUsedInExpression(message.getTopicDefinitionExpr(), usedSymbols);
        }
        storeSymbol(pubsubType, usedSymbols);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();
        addSymbol(instantiateType.getTypeReference(), usedSymbols);
        storeSymbol(instantiateType, usedSymbols);
    }

    public Set<AstNode> getCollaboratingNodes()
    {
        return collaboratingNodes;
    }

    public Set<AstNode> getUsedSymbols(AstNode type)
    {
        if (type instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType templatable = (ZserioTemplatableType)type;
            if (!templatable.getTemplateParameters().isEmpty())
            {
                return getUsedSymbolsForTemplate(templatable);
            }
        }

        final Set<AstNode> usedSymbols = usedSymbolsMap.get(type);
        return (usedSymbols != null) ? Collections.unmodifiableSet(usedSymbols) : EMPTY_USED_SET;
    }

    public Set<AstNode> getUsedBySymbols(AstNode type)
    {
        if (type instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType templatable = (ZserioTemplatableType)type;
            if (!templatable.getTemplateParameters().isEmpty())
            {
                return getUsedBySymbolsForTemplate(templatable);
            }
        }

        final Set<AstNode> usedBySymbols = usedBySymbolsMap.get(type);
        return (usedBySymbols != null) ? Collections.unmodifiableSet(usedBySymbols) : EMPTY_USED_SET;
    }

    private Set<AstNode> getUsedSymbolsForTemplate(ZserioTemplatableType template)
    {
        final Set<AstNode> usedSymbolsSet = new LinkedHashSet<AstNode>();
        for (ZserioTemplatableType instantiation : template.getInstantiations())
            usedSymbolsSet.addAll(getUsedSymbols(instantiation));

        return Collections.unmodifiableSet(usedSymbolsSet);
    }

    private Set<AstNode> getUsedBySymbolsForTemplate(ZserioTemplatableType template)
    {
        final Set<AstNode> usedBySymbolsSet = new LinkedHashSet<AstNode>();
        for (ZserioTemplatableType instantiation : template.getInstantiations())
            usedBySymbolsSet.addAll(getUsedBySymbols(instantiation));

        return Collections.unmodifiableSet(usedBySymbolsSet);
    }

    private Set<AstNode> getUsedSymbolsForCompoundType(CompoundType compoundType)
    {
        final Set<AstNode> usedSymbols = new LinkedHashSet<AstNode>();

        for (Parameter parameter : compoundType.getTypeParameters())
            addSymbol(parameter.getTypeReference(), usedSymbols);

        for (Field field : compoundType.getFields())
        {
            TypeInstantiation instantiation = field.getTypeInstantiation();
            if (instantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)instantiation;
                if (arrayInstantiation.getLengthExpression() != null)
                    addSymbolsUsedInExpression(arrayInstantiation.getLengthExpression(), usedSymbols);

                instantiation = ((ArrayInstantiation)instantiation).getElementTypeInstantiation();
            }

            if (instantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)instantiation;
                addSymbolsUsedInExpression(dynamicBitFieldInstantiation.getLengthExpression(), usedSymbols);
            }

            if (instantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedInstantiation =
                        (ParameterizedTypeInstantiation)instantiation;
                for (InstantiatedParameter param : parameterizedInstantiation.getInstantiatedParameters())
                    addSymbolsUsedInExpression(param.getArgumentExpression(), usedSymbols);
            }

            addSymbol(instantiation.getTypeReference(), usedSymbols);

            if (field.getAlignmentExpr() != null)
                addSymbolsUsedInExpression(field.getAlignmentExpr(), usedSymbols);
            if (field.getOffsetExpr() != null)
                addSymbolsUsedInExpression(field.getOffsetExpr(), usedSymbols);
            if (field.getInitializerExpr() != null)
                addSymbolsUsedInExpression(field.getInitializerExpr(), usedSymbols);
            if (field.getOptionalClauseExpr() != null)
                addSymbolsUsedInExpression(field.getOptionalClauseExpr(), usedSymbols);
            if (field.getConstraintExpr() != null)
                addSymbolsUsedInExpression(field.getConstraintExpr(), usedSymbols);

            // only for SQL table fields
            if (field.getSqlConstraint() != null)
                addSymbolsUsedInExpression(field.getSqlConstraint().getConstraintExpr(), usedSymbols);
        }

        for (Function function : compoundType.getFunctions())
            addSymbolsUsedInExpression(function.getResultExpression(), usedSymbols);

        return usedSymbols;
    }

    private static void addSymbolsUsedInExpression(Expression expression, Set<AstNode> usedSymbols)
    {
        for (Constant usedConstant : expression.getReferencedSymbolObjects(Constant.class))
            addUserSymbol(usedConstant, usedSymbols);

        for (EnumType usedBitmask : expression.getReferencedSymbolObjects(EnumType.class))
            addUserSymbol(usedBitmask, usedSymbols);

        for (BitmaskType usedBitmask : expression.getReferencedSymbolObjects(BitmaskType.class))
            addUserSymbol(usedBitmask, usedSymbols);
    }

    private static void addSymbol(TypeReference usedSymbolReference, Set<AstNode> usedSymbols)
    {
        addUserSymbol(usedSymbolReference.getType(), usedSymbols);

        for (TemplateArgument templateArgument : usedSymbolReference.getTemplateArguments())
            addSymbol(templateArgument.getTypeReference(), usedSymbols);
    }

    private static void addUserSymbol(AstNode usedSymbol, Set<AstNode> usedSymbols)
    {
        if (!(usedSymbol instanceof BuiltInType))
            usedSymbols.add(usedSymbol);
    }

    private void storeSymbol(AstNode node, Set<AstNode> usedSymbols)
    {
        usedSymbolsMap.put(node, usedSymbols);
        boolean isEmpty = true;
        for (AstNode usedSymbol : usedSymbols)
        {
            final Set<AstNode> usedBySymbolsSet = createUsedBySymbolsSet(usedSymbol);
            usedBySymbolsSet.add(node);
            isEmpty = false;
        }

        if (!isEmpty)
            createUsedBySymbolsSet(node);
    }

    private Set<AstNode> createUsedBySymbolsSet(AstNode node)
    {
        Set<AstNode> usedBySymbolsSet = usedBySymbolsMap.get(node);
        if (usedBySymbolsSet == null)
        {
            usedBySymbolsSet = new LinkedHashSet<AstNode>();
            usedBySymbolsMap.put(node, usedBySymbolsSet);
        }

        return usedBySymbolsSet;
    }

    private static final Set<AstNode> EMPTY_USED_SET =
            Collections.unmodifiableSet(new LinkedHashSet<AstNode>());

    private final Map<AstNode, Set<AstNode>> usedBySymbolsMap = new HashMap<AstNode, Set<AstNode>>();
    private final Map<AstNode, Set<AstNode>> usedSymbolsMap = new HashMap<AstNode, Set<AstNode>>();
    private final Set<AstNode> collaboratingNodes = new LinkedHashSet<AstNode>();
}
