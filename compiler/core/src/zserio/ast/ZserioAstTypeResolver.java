package zserio.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ZserioAstVisitor which manages type resolving phase.
 */
public class ZserioAstTypeResolver extends ZserioAstWalker
{
    @Override
    public void visitSubtype(Subtype subtype)
    {
        if (!subtypesOnStack.isEmpty() && subtypesOnStack.get(0).equals(subtype))
        {
            final ParserStackedException stackedException = new ParserStackedException(subtype.getLocation(),
                    "Cyclic dependency detected in subtype '" + subtype.getName() + "' definition!");
            for (int i = 1; i < subtypesOnStack.size(); ++i)
            {
                final Subtype subtypeOnStack = subtypesOnStack.get(i);
                stackedException.pushMessage(subtypeOnStack.getLocation(),
                        "    Through subtype '" + subtypeOnStack.getName() + "' here");
            }
            throw stackedException;
        }

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
        typeReference.resolve();

        // make sure that typeReference.getBaseTypeReference() can be called after this resolve
        final ZserioType type = typeReference.getType();
        if (type instanceof Subtype || type instanceof InstantiateType)
            type.accept(this);
    }

    @Override
    public void visitTemplateArgument(TemplateArgument templateArgument)
    {
        templateArgument.visitChildren(this);
        templateArgument.resolve();
    }

    @Override
    public void visitInstantiateType(InstantiateType instantiateType)
    {
        instantiateType.visitChildren(this);
        instantiateType.resolve();
    }

    private void visitType(TemplatableType templatableType)
    {
        // skip template declarations
        if (templatableType.getTemplateParameters().isEmpty())
            templatableType.visitChildren(this);
    }

    private List<Subtype> subtypesOnStack = new ArrayList<Subtype>();
}
