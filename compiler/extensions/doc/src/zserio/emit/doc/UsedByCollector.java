package zserio.emit.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.emit.common.DefaultEmitter;

/**
 * Abstraction which handles used by list for all available zserio types.
 */
public class UsedByCollector extends DefaultEmitter
{
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
        final List<AstNode> usedTypes = getUsedTypesForCompoundType(choiceType);
        addTypeToUsedTypes(choiceType.getSelectorExpression().getExprZserioType(), usedTypes);
        storeType(choiceType, usedTypes);

        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
        {
            for (ChoiceCaseExpression choiceCaseExpression : choiceCase.getExpressions())
            {
                final Object symbolObject = choiceCaseExpression.getExpression().getExprSymbolObject();
                if (symbolObject instanceof EnumItem)
                    addEnumItemToUsedByMap((EnumItem)symbolObject, choiceType);
                else if (symbolObject instanceof BitmaskValue)
                    addBitmaskValueToUsedByMap((BitmaskValue)symbolObject, choiceType);
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
        final List<AstNode> usedTypes = new ArrayList<AstNode>();
        for (Rpc rpc : serviceType.getRpcList())
        {
            addTypeToUsedTypes(rpc.getRequestType(), usedTypes);
            addTypeToUsedTypes(rpc.getResponseType(), usedTypes);
        }
        storeType(serviceType, usedTypes);
    }

    /**
     * Gets used by map.
     *
     * @return Map which maps zserio types to set of used by zserio types.
     */
    public Map<AstNode, Set<AstNode>> getUsedByTypeMap()
    {
        return Collections.unmodifiableMap(usedByTypeMap);
    }

    /**
     * Gets zserio types which are used by given type.
     *
     * @param type Zserio type for which to return used zserio types.
     *
     * @return List of all zserio types used by a given type.
     */
    public List<AstNode> getUsedTypes(AstNode type)
    {
        final List<AstNode> usedTypes = usedTypeMap.get(type);

        return (usedTypes != null) ? Collections.unmodifiableList(usedTypes) : EMPTY_ZSERIO_TYPE_LIST;
    }

    /**
     * Gets types which use given zserio type.
     *
     * @param type            Zserio type for which to return a list.
     * @param usedByTypeClass Zserio type which will be returned.
     *
     * @return List of zserio types which use given zserio type.
     */
    @SuppressWarnings("unchecked")
    public <T extends AstNode> List<T> getUsedByTypes(AstNode type, Class<? extends T> usedByTypeClass)
    {
        final Set<AstNode> usedByTypes = usedByTypeMap.get(type);
        final List<T> usedByList = new ArrayList<T>();
        if (usedByTypes != null)
        {
            for (AstNode usedByType : usedByTypes)
            {
                if (usedByTypeClass.isInstance(usedByType))
                    usedByList.add((T)usedByType);
            }
        }

        return usedByList;
    }

    /**
     * Gets choice types which use given enumeration item.
     *
     * @param enumItem Enumeration item for which to return a list.
     *
     * @return List of choice types which use given enumeration item.
     */
    public List<ChoiceType> getUsedByChoices(EnumItem enumItem)
    {
        final List<ChoiceType> usedByChoices = enumItemUsedByChoiceMap.get(enumItem);

        return (usedByChoices != null) ? Collections.unmodifiableList(usedByChoices) : EMPTY_CHOICE_TYPE_LIST;
    }

    /**
     * Gets choice types which use given bitmask value.
     *
     * @param bitmaskValue Bitmask value for which to return a list.
     *
     * @return List of choice types which use given enumeration item.
     */
    public List<ChoiceType> getUsedByChoices(BitmaskValue bitmaskValue)
    {
        final List<ChoiceType> usedByChoices = bitmaskValueUsedByChoiceMap.get(bitmaskValue);

        return (usedByChoices != null) ? Collections.unmodifiableList(usedByChoices) : EMPTY_CHOICE_TYPE_LIST;
    }

    private static void addTypeToUsedTypes(AstNode usedType, List<AstNode> usedTypes)
    {
        if (!(usedType instanceof BuiltInType))
            usedTypes.add(usedType);
    }

    private List<AstNode> getUsedTypesForCompoundType(CompoundType compoundType)
    {
        final List<AstNode> usedTypes = new ArrayList<AstNode>();
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
        final List<AstNode> usedTypes = new ArrayList<AstNode>();
        addTypeToUsedTypes(unresolvedUsedType, usedTypes);
        storeType(node, usedTypes);
    }

    private void storeType(AstNode node, List<AstNode> usedTypes)
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

    private void addEnumItemToUsedByMap(EnumItem enumItem, ChoiceType choiceType)
    {
        List<ChoiceType> usedByChoices = enumItemUsedByChoiceMap.get(enumItem);
        if (usedByChoices == null)
        {
            usedByChoices = new ArrayList<ChoiceType>();
            enumItemUsedByChoiceMap.put(enumItem, usedByChoices);
        }

        usedByChoices.add(choiceType);
    }

    private void addBitmaskValueToUsedByMap(BitmaskValue bitmaskValue, ChoiceType choiceType)
    {
        List<ChoiceType> usedByChoices = bitmaskValueUsedByChoiceMap.get(bitmaskValue);
        if (usedByChoices == null)
        {
            usedByChoices = new ArrayList<ChoiceType>();
            bitmaskValueUsedByChoiceMap.put(bitmaskValue, usedByChoices);
        }

        usedByChoices.add(choiceType);
    }

    private static final List<AstNode> EMPTY_ZSERIO_TYPE_LIST =
            Collections.unmodifiableList(new ArrayList<AstNode>());
    private static final List<ChoiceType> EMPTY_CHOICE_TYPE_LIST =
            Collections.unmodifiableList(new ArrayList<ChoiceType>());

    private final Map<AstNode, Set<AstNode>> usedByTypeMap = new HashMap<AstNode, Set<AstNode>>();
    private final Map<AstNode, List<AstNode>> usedTypeMap = new HashMap<AstNode, List<AstNode>>();
    private final Map<EnumItem, List<ChoiceType>> enumItemUsedByChoiceMap =
            new HashMap<EnumItem, List<ChoiceType>>();
    private final Map<BitmaskValue, List<ChoiceType>> bitmaskValueUsedByChoiceMap =
            new HashMap<BitmaskValue, List<ChoiceType>>();
}
