package zserio.emit.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Rpc;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.emit.common.DefaultEmitter;

/**
 * Abstraction which handles used by list for all available zserio types.
 */
public class UsedByCollector extends DefaultEmitter
{
    @Override
    public void beginConst(ConstType constType)
    {
        storeType(constType, constType.getConstType());
    }

    @Override
    public void beginSubtype(Subtype subtype)
    {
        storeType(subtype, subtype.getTargetType());
    }

    @Override
    public void beginStructure(StructureType structureType)
    {
        storeType(structureType, getUsedTypesForCompoundType(structureType));
    }

    @Override
    public void beginChoice(ChoiceType choiceType)
    {
        final List<ZserioType> usedTypes = getUsedTypesForCompoundType(choiceType);
        addTypeToUsedTypes(choiceType.getSelectorExpression().getExprZserioType(), usedTypes);
        storeType(choiceType, usedTypes);

        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
        {
            for (ChoiceCaseExpression choiceCaseExpression : choiceCase.getExpressions())
            {
                final Object symbolObject = choiceCaseExpression.getExpression().getExprSymbolObject();
                if (symbolObject instanceof EnumItem)
                    addEnumItemToUsedByMap((EnumItem)symbolObject, choiceType);
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
        storeType(enumType, enumType.getEnumType());
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
        final List<ZserioType> usedTypes = new ArrayList<ZserioType>();
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
    public Map<ZserioType, Set<ZserioType>> getUsedByTypeMap()
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
    public List<ZserioType> getUsedTypes(ZserioType type)
    {
        final List<ZserioType> usedTypes = usedTypeMap.get(type);

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
    public <T extends ZserioType> List<T> getUsedByTypes(ZserioType type, Class<? extends T> usedByTypeClass)
    {
        final Set<ZserioType> usedByTypes = usedByTypeMap.get(type);
        final List<T> usedByList = new ArrayList<T>();
        if (usedByTypes != null)
        {
            for (ZserioType usedByType : usedByTypes)
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

    private static void addTypeToUsedTypes(ZserioType unresolvedUsedType, List<ZserioType> usedTypes)
    {
        final ZserioType resolvedUsedType = TypeReference.resolveType(unresolvedUsedType);
        if (!ZserioTypeUtil.isBuiltIn(resolvedUsedType))
            usedTypes.add(resolvedUsedType);
    }

    private List<ZserioType> getUsedTypesForCompoundType(CompoundType compoundType)
    {
        final List<ZserioType> usedTypes = new ArrayList<ZserioType>();
        for (Field field : compoundType.getFields())
            addTypeToUsedTypes(field.getFieldReferencedType(), usedTypes);

        return usedTypes;
    }

    private void storeType(ZserioType type, ZserioType unresolvedUsedType)
    {
        final List<ZserioType> usedTypes = new ArrayList<ZserioType>();
        addTypeToUsedTypes(unresolvedUsedType, usedTypes);
        storeType(type, usedTypes);
    }

    private void storeType(ZserioType type, List<ZserioType> usedTypes)
    {
        usedTypeMap.put(type, usedTypes);
        boolean isEmpty = true;
        for (ZserioType usedType : usedTypes)
        {
            final Set<ZserioType> usedByTypeSet = createUsedByTypeSet(usedType);
            usedByTypeSet.add(type);
            isEmpty = false;
        }

        if (!isEmpty)
            createUsedByTypeSet(type);
    }

    private Set<ZserioType> createUsedByTypeSet(ZserioType type)
    {
        Set<ZserioType> usedByTypeSet = usedByTypeMap.get(type);
        if (usedByTypeSet == null)
        {
            usedByTypeSet = new LinkedHashSet<ZserioType>();
            usedByTypeMap.put(type, usedByTypeSet);
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

    private static final List<ZserioType> EMPTY_ZSERIO_TYPE_LIST =
            Collections.unmodifiableList(new ArrayList<ZserioType>());
    private static final List<ChoiceType> EMPTY_CHOICE_TYPE_LIST =
            Collections.unmodifiableList(new ArrayList<ChoiceType>());

    private final Map<ZserioType, Set<ZserioType>> usedByTypeMap = new HashMap<ZserioType, Set<ZserioType>>();
    private final Map<ZserioType, List<ZserioType>> usedTypeMap = new HashMap<ZserioType, List<ZserioType>>();
    private final Map<EnumItem, List<ChoiceType>> enumItemUsedByChoiceMap =
            new HashMap<EnumItem, List<ChoiceType>>();
}
