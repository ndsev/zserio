package zserio.ast;

import zserio.antlr.util.ParserException;
import zserio.ast.Package;

/**
 * Implementation of ZserioAstVisitor which handles templates instantiation.
 */
public class ZserioAstTemplator extends ZserioAstWalker
{
    @Override
    public void visitPackage(Package currentPackage)
    {
        this.currentPackage = currentPackage;

        currentPackage.visitChildren(this);

        this.currentPackage = null;
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        if (structureType.getTemplateParameters().isEmpty())
            structureType.visitChildren(this);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        if (choiceType.getTemplateParameters().isEmpty())
            choiceType.visitChildren(this);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        if (unionType.getTemplateParameters().isEmpty())
            unionType.visitChildren(this);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        if (sqlTableType.getTemplateParameters().isEmpty())
            sqlTableType.visitChildren(this);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        if (!typeReference.getTemplateArguments().isEmpty()) // if is a template instantiation
        {
            // instantiate possible instantiations in template arguments first!
            // TODO[Mi-L@]: This is needed when the template argument is not used in the template,
            //              otherwise it is instantiated correctly. Is it ok?
            for (ZserioType templateArgument : typeReference.getTemplateArguments())
                templateArgument.accept(this);

            ZserioType type = currentPackage.getVisibleType(
                    typeReference.getReferencedPackageName(), typeReference.getReferencedTypeName());
            if (type != null && type instanceof ZserioTemplatableType)
            {
                ZserioTemplatableType template = (ZserioTemplatableType)type;
                if (template.getTemplateParameters().isEmpty()) // TODO[Mi-L@]: Improve message!
                    throw new ParserException(typeReference, "Not a template!");

                ZserioTemplatableType instantiation =
                        template.instantiate(typeReference.getTemplateArguments());
                instantiation.accept(this);
            }
            else // TODO[Mi-L@]: Improve message, maybe just not a templatable type.
                throw new ParserException(typeReference, "Not a type!");
        }
    }

    private Package currentPackage = null;
}
