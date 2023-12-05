package zserio.extension.doc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.BitmaskValue;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceCaseExpression;
import zserio.ast.ChoiceType;
import zserio.ast.EnumItem;
import zserio.extension.common.DefaultTreeWalker;
import zserio.tools.HashUtil;

/**
 * Used by choice collector.
 *
 * Used by choice collector gathers all choice types in packages and builds a special maps which
 *
 * - map enumeration item to the list of all choice cases which use the enumeration item
 * - map bitmask value to the list of all choice cases which use the bitmask value
 *
 * These maps are used by Package emitter to get information for see symbols.
 */
class UsedByChoiceCollector extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void beginChoice(ChoiceType choiceType)
    {
        for (ChoiceCase choiceCase : choiceType.getChoiceCases())
        {
            for (ChoiceCaseExpression choiceCaseExpression : choiceCase.getExpressions())
            {
                final Object symbolObject = choiceCaseExpression.getExpression().getExprSymbolObject();
                if (symbolObject instanceof EnumItem)
                    addEnumItemToUsedByChoiceMap((EnumItem)symbolObject, choiceType, choiceCase,
                            choiceCaseExpression);
                else if (symbolObject instanceof BitmaskValue)
                    addBitmaskValueToUsedByChoiceMap((BitmaskValue)symbolObject, choiceType, choiceCase,
                            choiceCaseExpression);
            }
        }
    }

    public Set<ChoiceCaseReference> getUsedByChoices(EnumItem enumItem)
    {
        final Set<ChoiceCaseReference> usedByChoices = enumItemUsedByChoiceMap.get(enumItem);

        return (usedByChoices != null) ? Collections.unmodifiableSet(usedByChoices) : EMPTY_CHOICE_TYPE_SET;
    }

    public Set<ChoiceCaseReference> getUsedByChoices(BitmaskValue bitmaskValue)
    {
        final Set<ChoiceCaseReference> usedByChoices = bitmaskValueUsedByChoiceMap.get(bitmaskValue);

        return (usedByChoices != null) ? Collections.unmodifiableSet(usedByChoices) : EMPTY_CHOICE_TYPE_SET;
    }

    public static final class ChoiceCaseReference implements Comparable<ChoiceCaseReference>
    {
        public ChoiceCaseReference(ChoiceType choiceType, ChoiceCase choiceCase,
                ChoiceCaseExpression choiceCaseExpression)
        {
            this.choiceType = choiceType;
            this.choiceCase = choiceCase;
            this.choiceCaseExpression = choiceCaseExpression;
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

        public ChoiceCaseExpression getChoiceCaseExpression()
        {
            return choiceCaseExpression;
        }

        private final ChoiceType choiceType;
        private final ChoiceCase choiceCase;
        private final ChoiceCaseExpression choiceCaseExpression;
    }

    private void addEnumItemToUsedByChoiceMap(EnumItem enumItem, ChoiceType choiceType, ChoiceCase choiceCase,
            ChoiceCaseExpression choiceCaseExpression)
    {
        Set<ChoiceCaseReference> usedByChoices = enumItemUsedByChoiceMap.get(enumItem);
        if (usedByChoices == null)
        {
            usedByChoices = new TreeSet<ChoiceCaseReference>();
            enumItemUsedByChoiceMap.put(enumItem, usedByChoices);
        }

        usedByChoices.add(new ChoiceCaseReference(choiceType, choiceCase, choiceCaseExpression));
    }

    private void addBitmaskValueToUsedByChoiceMap(BitmaskValue bitmaskValue, ChoiceType choiceType,
            ChoiceCase choiceCase, ChoiceCaseExpression choiceCaseExpression)
    {
        Set<ChoiceCaseReference> usedByChoices = bitmaskValueUsedByChoiceMap.get(bitmaskValue);
        if (usedByChoices == null)
        {
            usedByChoices = new TreeSet<ChoiceCaseReference>();
            bitmaskValueUsedByChoiceMap.put(bitmaskValue, usedByChoices);
        }

        usedByChoices.add(new ChoiceCaseReference(choiceType, choiceCase, choiceCaseExpression));
    }

    private static final Set<ChoiceCaseReference> EMPTY_CHOICE_TYPE_SET =
            Collections.unmodifiableSet(new TreeSet<ChoiceCaseReference>());

    private final Map<EnumItem, Set<ChoiceCaseReference>> enumItemUsedByChoiceMap =
            new HashMap<EnumItem, Set<ChoiceCaseReference>>();
    private final Map<BitmaskValue, Set<ChoiceCaseReference>> bitmaskValueUsedByChoiceMap =
            new HashMap<BitmaskValue, Set<ChoiceCaseReference>>();
}
