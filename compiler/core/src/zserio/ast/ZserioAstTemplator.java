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
    public void visitTypeReference(TypeReference typeReference)
    {
        if (!typeReference.getTemplateArguments().isEmpty())
        {
            ZserioType type = currentPackage.getVisibleType(
                    typeReference.getReferencedPackageName(), typeReference.getReferencedTypeName());
            if (type != null && type instanceof ZserioTemplatableType)
            {
                ZserioTemplatableType template = (ZserioTemplatableType)type;
                if (template.getTemplateParameters().isEmpty()) // TODO[Mi-L@]: Improve message!
                    throw new ParserException(typeReference, "Not a template!");

                template.instantiate(typeReference.getTemplateArguments());
            }
            else // TODO[Mi-L@]: Improve message, maybe just not a templatable type.
                throw new ParserException(typeReference, "Not a type!");
        }
    }

    Package currentPackage = null;
}
