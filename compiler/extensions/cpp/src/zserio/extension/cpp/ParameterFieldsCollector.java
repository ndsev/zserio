package zserio.extension.cpp;

import java.util.HashSet;
import java.util.Set;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Field;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
import zserio.ast.StructureType;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.ZserioAstWalker;
import zserio.ast.ZserioTemplatableType;

public class ParameterFieldsCollector extends ZserioAstWalker
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
     * Checks if the given field is used as a parameter.
     *
     * @param field Field to check.
     *
     * @return True when the given field is used as a parameter, false otherwise.
     */
    public boolean isUsedAsParameter(Field field)
    {
        return parameterFields.contains(field);
    }

    private void visitCompound(CompoundType compoundType)
    {
        if (!compoundType.getTemplateParameters().isEmpty())
        {
            // visit instantiations
            for (ZserioTemplatableType instantiation : compoundType.getInstantiations())
                instantiation.accept(this);
            return;
        }

        for (Field field : compoundType.getFields()) // it's enough to traverse fields
        {
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                typeInstantiation = arrayInstantiation.getElementTypeInstantiation();
            }

            if (typeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                for (InstantiatedParameter parameter :
                        ((ParameterizedTypeInstantiation)typeInstantiation).getInstantiatedParameters())
                {
                    final AstNode symbolObject = parameter.getArgumentExpression().getExprSymbolObject();
                    if (symbolObject instanceof Field)
                    {
                        parameterFields.add((Field)symbolObject);
                    }
                }
            }
        }
    }

    private final Set<Field> parameterFields = new HashSet<Field>();
};
