package zserio.emit.doc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.InstantiateType;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioType;
import zserio.emit.common.DefaultTreeWalker;
import zserio.tools.HashUtil;

/**
 * Abstraction which handles used by list for all available Zserio types.
 */
class UsedByCollector extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void endRoot(Root root)
    {
        for (AstNode node : usedByTypeMap.keySet())
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
        storeType(constant, constant.getTypeInstantiation().getType());
    }

    @Override
    public void beginSubtype(Subtype subtype)
    {
        storeType(subtype, subtype.getTypeReference().getType());
    }

    @Override
    public void beginStructure(StructureType structureType)
    {
        storeType(structureType, getUsedTypesForCompoundType(structureType));
    }

    @Override
    public void beginChoice(ChoiceType choiceType)
    {
        final Set<AstNode> usedTypes = getUsedTypesForCompoundType(choiceType);
        final ZserioType selectorZserioType = choiceType.getSelectorExpression().getExprZserioType();
        if (selectorZserioType != null)
            addTypeToUsedTypes(selectorZserioType, usedTypes);
        storeType(choiceType, usedTypes);

        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
        {
            for (ChoiceCaseExpression choiceCaseExpression : choiceCase.getExpressions())
            {
                final Object symbolObject = choiceCaseExpression.getExpression().getExprSymbolObject();
                if (symbolObject instanceof EnumItem)
                    addEnumItemToUsedByMap((EnumItem)symbolObject, choiceType, choiceCase);
                else if (symbolObject instanceof BitmaskValue)
                    addBitmaskValueToUsedByMap((BitmaskValue)symbolObject, choiceType, choiceCase);
            }
        }
    }

    @Override
    public void beginUnion(UnionType unionType)
    {
        storeType(unionType, getUsedTypesForCompoundType(unionType));
    }

    @Override
    public void beginEnumeration(EnumType enumType)
    {
        storeType(enumType, enumType.getTypeInstantiation().getType());
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType)
    {
        storeType(bitmaskType, bitmaskType.getTypeInstantiation().getType());
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType)
    {
        storeType(sqlTableType, getUsedTypesForCompoundType(sqlTableType));
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        storeType(sqlDatabaseType, getUsedTypesForCompoundType(sqlDatabaseType));
    }

    @Override
    public void beginService(ServiceType serviceType)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();
        for (ServiceMethod method : serviceType.getMethodList())
        {
            addTypeToUsedTypes(method.getRequestType(), usedTypes);
            addTypeToUsedTypes(method.getResponseType(), usedTypes);
        }
        storeType(serviceType, usedTypes);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();
        for (PubsubMessage message : pubsubType.getMessageList())
        {
            addTypeToUsedTypes(message.getType(), usedTypes);
        }
        storeType(pubsubType, usedTypes);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType)
    {
        storeType(instantiateType, instantiateType.getTypeReference().getType());
    }

    /**
     * Gets nodes which are involved in some kind of collaboration.
     *
     * @return Set of all nodes that collaborate.
     */
    public Set<AstNode> getCollaboratingNodes()
    {
        return collaboratingNodes;
    }

    /**
     * Gets zserio types which are used by given type.
     *
     * @param type Zserio type for which to return a set.
     *
     * @return Set of all zserio types used by a given type.
     */
    public Set<AstNode> getUsedTypes(AstNode type)
    {
        if (type instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType templatable = (ZserioTemplatableType)type;
            if (!templatable.getTemplateParameters().isEmpty())
            {
                return getUsedTypesForTemplate(templatable);
            }
        }

        final Set<AstNode> usedTypes = usedTypeMap.get(type);
        return (usedTypes != null) ? Collections.unmodifiableSet(usedTypes) : EMPTY_USED_SET;
    }

    /**
     * Gets types which use given zserio type.
     *
     * @param type            Zserio type for which to return a set.
     * @param usedByTypeClass Zserio type which will be returned.
     *
     * @return Set of zserio types which use given zserio type.
     */
    public Set<AstNode> getUsedByTypes(AstNode type)
    {
        if (type instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType templatable = (ZserioTemplatableType)type;
            if (!templatable.getTemplateParameters().isEmpty())
            {
                return getUsedByTypesForTemplate(templatable);
            }
        }

        final Set<AstNode> usedByTypes = usedByTypeMap.get(type);
        return (usedByTypes != null) ? Collections.unmodifiableSet(usedByTypes) : EMPTY_USED_SET;
    }

    /**
     * Gets choice cases which use given enumeration item.
     *
     * @param enumItem Enumeration item for which to return a set.
     *
     * @return Set of choice cases which use given enumeration item.
     */
    public Set<ChoiceCaseReference> getUsedByChoices(EnumItem enumItem)
    {
        final Set<ChoiceCaseReference> usedByChoices = enumItemUsedByChoiceMap.get(enumItem);

        return (usedByChoices != null) ? Collections.unmodifiableSet(usedByChoices) : EMPTY_CHOICE_TYPE_SET;
    }

    /**
     * Gets choice cases which use given bitmask value.
     *
     * @param bitmaskValue Bitmask value for which to return a set.
     *
     * @return Set of choice cases which use given bitmask item.
     */
    public Set<ChoiceCaseReference> getUsedByChoices(BitmaskValue bitmaskValue)
    {
        final Set<ChoiceCaseReference> usedByChoices = bitmaskValueUsedByChoiceMap.get(bitmaskValue);

        return (usedByChoices != null) ? Collections.unmodifiableSet(usedByChoices) : EMPTY_CHOICE_TYPE_SET;
    }

    /**
     * Unique reference to the choice case.
     */
    public static class ChoiceCaseReference implements Comparable<ChoiceCaseReference>
    {
        public ChoiceCaseReference(ChoiceType choiceType, ChoiceCase choiceCase)
        {
            this.choiceType = choiceType;
            this.choiceCase = choiceCase;
        }

        @Override
        public int compareTo(ChoiceCaseReference other)
        {
            return choiceType.getName().compareTo(other.choiceType.getName());
        }

        @Override
        public boolean equals(Object other)
        {
            if ( !(other instanceof ChoiceCaseReference) )
                return false;

            return (this == other) || compareTo((ChoiceCaseReference)other) == 0;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, choiceType.getName());
            return hash;
        }

        public ChoiceType getChoiceType()
        {
            return choiceType;
        }

        public ChoiceCase getChoiceCase()
        {
            return choiceCase;
        }

        private final ChoiceType choiceType;
        private final ChoiceCase choiceCase;
    }

    private Set<AstNode> getUsedTypesForTemplate(ZserioTemplatableType template)
    {
        final Set<AstNode> usedTypesSet = new LinkedHashSet<AstNode>();
        for (ZserioTemplatableType instantiation : template.getInstantiations())
            usedTypesSet.addAll(getUsedTypes(instantiation));

        return Collections.unmodifiableSet(usedTypesSet);
    }

    public Set<AstNode> getUsedByTypesForTemplate(ZserioTemplatableType template)
    {
        final Set<AstNode> usedByTypesSet = new LinkedHashSet<AstNode>();
        for (ZserioTemplatableType instantiation : template.getInstantiations())
            usedByTypesSet.addAll(getUsedByTypes(instantiation));

        return Collections.unmodifiableSet(usedByTypesSet);
    }

    private static void addTypeToUsedTypes(AstNode usedType, Set<AstNode> usedTypes)
    {
        if (!(usedType instanceof BuiltInType))
            usedTypes.add(usedType);
    }

    private Set<AstNode> getUsedTypesForCompoundType(CompoundType compoundType)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();
        for (Field field : compoundType.getFields())
        {
            TypeInstantiation instantiation = field.getTypeInstantiation();
            if (instantiation instanceof ArrayInstantiation)
                instantiation = ((ArrayInstantiation)instantiation).getElementTypeInstantiation();
            addTypeToUsedTypes(instantiation.getType(), usedTypes);
        }

        return usedTypes;
    }

    private void storeType(AstNode node, ZserioType unresolvedUsedType)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();
        addTypeToUsedTypes(unresolvedUsedType, usedTypes);
        storeType(node, usedTypes);
    }

    private void storeType(AstNode node, Set<AstNode> usedTypes)
    {
        usedTypeMap.put(node, usedTypes);
        boolean isEmpty = true;
        for (AstNode usedType : usedTypes)
        {
            final Set<AstNode> usedByTypeSet = createUsedByTypeSet(usedType);
            usedByTypeSet.add(node);
            isEmpty = false;
        }

        if (!isEmpty)
            createUsedByTypeSet(node);
    }

    private Set<AstNode> createUsedByTypeSet(AstNode node)
    {
        Set<AstNode> usedByTypeSet = usedByTypeMap.get(node);
        if (usedByTypeSet == null)
        {
            usedByTypeSet = new LinkedHashSet<AstNode>();
            usedByTypeMap.put(node, usedByTypeSet);
        }

        return usedByTypeSet;
    }

    private void addEnumItemToUsedByMap(EnumItem enumItem, ChoiceType choiceType, ChoiceCase choiceCase)
    {
        Set<ChoiceCaseReference> usedByChoices = enumItemUsedByChoiceMap.get(enumItem);
        if (usedByChoices == null)
        {
            usedByChoices = new TreeSet<ChoiceCaseReference>();
            enumItemUsedByChoiceMap.put(enumItem, usedByChoices);
        }

        usedByChoices.add(new ChoiceCaseReference(choiceType, choiceCase));
    }

    private void addBitmaskValueToUsedByMap(BitmaskValue bitmaskValue, ChoiceType choiceType,
            ChoiceCase choiceCase)
    {
        Set<ChoiceCaseReference> usedByChoices = bitmaskValueUsedByChoiceMap.get(bitmaskValue);
        if (usedByChoices == null)
        {
            usedByChoices = new TreeSet<ChoiceCaseReference>();
            bitmaskValueUsedByChoiceMap.put(bitmaskValue, usedByChoices);
        }

        usedByChoices.add(new ChoiceCaseReference(choiceType, choiceCase));
    }

    private static final Set<AstNode> EMPTY_USED_SET =
            Collections.unmodifiableSet(new LinkedHashSet<AstNode>());
    private static final Set<ChoiceCaseReference> EMPTY_CHOICE_TYPE_SET =
            Collections.unmodifiableSet(new TreeSet<ChoiceCaseReference>());

    private final Map<AstNode, Set<AstNode>> usedByTypeMap = new HashMap<AstNode, Set<AstNode>>();
    private final Map<AstNode, Set<AstNode>> usedTypeMap = new HashMap<AstNode, Set<AstNode>>();
    private final Set<AstNode> collaboratingNodes = new LinkedHashSet<AstNode>();
    private final Map<EnumItem, Set<ChoiceCaseReference>> enumItemUsedByChoiceMap =
            new HashMap<EnumItem, Set<ChoiceCaseReference>>();
    private final Map<BitmaskValue, Set<ChoiceCaseReference>> bitmaskValueUsedByChoiceMap =
            new HashMap<BitmaskValue, Set<ChoiceCaseReference>>();
}
