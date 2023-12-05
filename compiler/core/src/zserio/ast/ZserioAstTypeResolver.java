package zserio.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ZserioAstVisitor which manages type resolving phase.
 */
public final class ZserioAstTypeResolver extends ZserioAstWalker
{
    @Override
    public void visitSubtype(Subtype subtype)
    {
        checkCycle(subtypesOnStack, subtype, "subtype");

        subtypesOnStack.add(subtype);

        subtype.visitChildren(this);
        subtype.resolve();

        subtypesOnStack.remove(subtypesOnStack.size() - 1);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        visitType(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        visitType(choiceType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        visitType(unionType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        visitType(sqlTableType);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.visitChildren(this);
        typeReference.resolve(templateParameters);

        // make sure that typeReference.getBaseTypeReference() can be called after this resolve
        // note: this can cause cycles which are guarded by subtypesOnStack and instantiateTypesOnStack
        final ZserioType type = typeReference.getType();
        if (type instanceof Subtype || type instanceof InstantiateType)
            type.accept(this);
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        typeInstantiation.visitChildren(this);
        typeInstantiation.resolve();
    }

    @Override
    public void visitInstantiateType(InstantiateType instantiateType)
    {
        // cannot be checked in resolve since the cycle happens via visitChildren
        checkCycle(instantiateTypesOnStack, instantiateType, "template instantiation");

        instantiateTypesOnStack.add(instantiateType);

        instantiateType.visitChildren(this);
        instantiateType.resolve();

        instantiateTypesOnStack.remove(instantiateTypesOnStack.size() - 1);
    }

    private void visitType(TemplatableType templatableType)
    {
        templateParameters = templatableType.getTemplateParameters();

        templatableType.visitChildren(this);

        templateParameters = null;
    }

    private <T extends ZserioType> void checkCycle(List<T> typesOnStack, T type, String typeName)
    {
        if (!typesOnStack.isEmpty() && typesOnStack.get(0) == type)
        {
            final ParserStackedException stackedException = new ParserStackedException(type.getLocation(),
                    "Cyclic dependency detected in " + typeName + " '" + type.getName() + "'!");
            for (int i = 1; i < typesOnStack.size(); ++i)
            {
                final T typeOnStack = typesOnStack.get(i);
                stackedException.pushMessage(typeOnStack.getLocation(),
                        "    Through " + typeName + " '" + typeOnStack.getName() + "' here");
            }
            throw stackedException;
        }
    }

    private final List<Subtype> subtypesOnStack = new ArrayList<Subtype>();
    private final List<InstantiateType> instantiateTypesOnStack = new ArrayList<InstantiateType>();
    private List<TemplateParameter> templateParameters = null;
}
