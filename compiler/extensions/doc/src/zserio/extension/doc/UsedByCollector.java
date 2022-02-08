package zserio.extension.doc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BuiltInType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.InstantiateType;
import zserio.ast.Parameter;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.Root;
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
import zserio.ast.ZserioType;
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
        storeType(constant, constant.getTypeInstantiation().getTypeReference());
    }

    @Override
    public void beginSubtype(Subtype subtype)
    {
        storeType(subtype, subtype.getTypeReference());
    }

    @Override
    public void beginStructure(StructureType structureType)
    {
        storeType(structureType, getUsedTypesForCompoundType(structureType));
    }

    @Override
    public void beginChoice(ChoiceType choiceType)
    {
        storeType(choiceType, getUsedTypesForCompoundType(choiceType));
    }

    @Override
    public void beginUnion(UnionType unionType)
    {
        storeType(unionType, getUsedTypesForCompoundType(unionType));
    }

    @Override
    public void beginEnumeration(EnumType enumType)
    {
        storeType(enumType, enumType.getTypeInstantiation().getTypeReference());
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType)
    {
        storeType(bitmaskType, bitmaskType.getTypeInstantiation().getTypeReference());
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
            addTypeToUsedTypes(method.getRequestTypeReference(), usedTypes);
            addTypeToUsedTypes(method.getResponseTypeReference(), usedTypes);
        }
        storeType(serviceType, usedTypes);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();
        for (PubsubMessage message : pubsubType.getMessageList())
        {
            addTypeToUsedTypes(message.getTypeReference(), usedTypes);
        }
        storeType(pubsubType, usedTypes);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType)
    {
        storeType(instantiateType, instantiateType.getTypeReference());
    }

    public Set<AstNode> getCollaboratingNodes()
    {
        return collaboratingNodes;
    }

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

    private Set<AstNode> getUsedTypesForTemplate(ZserioTemplatableType template)
    {
        final Set<AstNode> usedTypesSet = new LinkedHashSet<AstNode>();
        for (ZserioTemplatableType instantiation : template.getInstantiations())
            usedTypesSet.addAll(getUsedTypes(instantiation));

        return Collections.unmodifiableSet(usedTypesSet);
    }

    private Set<AstNode> getUsedByTypesForTemplate(ZserioTemplatableType template)
    {
        final Set<AstNode> usedByTypesSet = new LinkedHashSet<AstNode>();
        for (ZserioTemplatableType instantiation : template.getInstantiations())
            usedByTypesSet.addAll(getUsedByTypes(instantiation));

        return Collections.unmodifiableSet(usedByTypesSet);
    }

    private static void addTypeToUsedTypes(TypeReference usedTypeReference, Set<AstNode> usedTypes)
    {
        final ZserioType usedType = usedTypeReference.getType();
        if (!(usedType instanceof BuiltInType))
            usedTypes.add(usedType);

        for (TemplateArgument templateArgument : usedTypeReference.getTemplateArguments())
            addTypeToUsedTypes(templateArgument.getTypeReference(), usedTypes);
    }

    private Set<AstNode> getUsedTypesForCompoundType(CompoundType compoundType)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();

        for (Parameter parameter : compoundType.getTypeParameters())
            addTypeToUsedTypes(parameter.getTypeReference(), usedTypes);

        for (Field field : compoundType.getFields())
        {
            TypeInstantiation instantiation = field.getTypeInstantiation();
            if (instantiation instanceof ArrayInstantiation)
                instantiation = ((ArrayInstantiation)instantiation).getElementTypeInstantiation();

            addTypeToUsedTypes(instantiation.getTypeReference(), usedTypes);
        }

        return usedTypes;
    }

    private void storeType(AstNode node, TypeReference usedTypeReference)
    {
        final Set<AstNode> usedTypes = new LinkedHashSet<AstNode>();
        addTypeToUsedTypes(usedTypeReference, usedTypes);
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

    private static final Set<AstNode> EMPTY_USED_SET =
            Collections.unmodifiableSet(new LinkedHashSet<AstNode>());

    private final Map<AstNode, Set<AstNode>> usedByTypeMap = new HashMap<AstNode, Set<AstNode>>();
    private final Map<AstNode, Set<AstNode>> usedTypeMap = new HashMap<AstNode, Set<AstNode>>();
    private final Set<AstNode> collaboratingNodes = new LinkedHashSet<AstNode>();
}
