package zserio.extension.common;

import java.util.HashSet;
import java.util.Set;

import zserio.ast.ArrayInstantiation;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.ast.StructureType;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.ZserioAstWalker;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioType;

/**
 * Implements collectors which traverse source tree and detects which types are used within a packed array.
 *
 * Intended to be used by generators to optimize the generated code such that types which are not used within
 * a packed arrays don't need to generate methods needed only for packing.
 */
public class PackedTypesCollector extends ZserioAstWalker
{
    @Override
    public void visitStructureType(StructureType structureType)
    {
        visitCompound(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        visitCompound(choiceType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        visitCompound(unionType);
    }

    /**
     * Checks if the give zserio base type is used within a packed array.
     *
     * @param baseType Zserio base type to check.
     *
     * @return True when the given base type is used within a packed array, false otherwise.
     */
    public boolean isUsedInPackedArray(ZserioType baseType)
    {
        return packedTypes.contains(baseType);
    }

    private void visitCompound(CompoundType compoundType)
    {
        for (Field field : compoundType.getFields()) // it's enough to traverse fields
        {
            boolean inPackedArray = false;
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                if (arrayInstantiation.isPacked())
                {
                    inPackedArray = true;
                    ++packedArraysDepth;
                }
                typeInstantiation = arrayInstantiation.getElementTypeInstantiation();
            }

            final ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (packedArraysDepth > 0)
            {
                packedTypes.add(fieldBaseType); // remember the base type (element base type in case of arrays)
                if (fieldBaseType instanceof CompoundType)
                {
                    final CompoundType fieldCompoundType = (CompoundType)fieldBaseType;
                    if (fieldCompoundType.getTemplate() != null)
                    {
                        // mark also templates as used in a packed array
                        packedTypes.add(fieldCompoundType.getTemplate());
                    }
                    if (!compoundType.equals(fieldCompoundType))
                    {
                        fieldCompoundType.accept(this);
                    }
                }
            }

            if (inPackedArray)
                --packedArraysDepth;
        }

        if (!compoundType.getTemplateParameters().isEmpty())
        {
            // visit instantiations
            for (ZserioTemplatableType instantiation : compoundType.getInstantiations())
                instantiation.accept(this);
        }
    }

    private int packedArraysDepth = 0;
    private final Set<ZserioType> packedTypes = new HashSet<ZserioType>();
};
