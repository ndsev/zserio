package zserio.ast;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ZserioAstVisitor which handles templates instantiation.
 */
public class ZserioAstTemplator extends ZserioAstWalker
{
    public ZserioAstTemplator(ZserioAstTypeResolver typeResolver)
    {
        this.typeResolver = typeResolver;
    }

    public void visitPackage(Package pkg)
    {
        currentPackage = pkg;
        visibleInstantiateTypes = currentPackage.getVisibleInstantiateTypes();
        pkg.visitChildren(this);
        visibleInstantiateTypes = null;
        currentPackage = null;
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
            // instantiate instantiations in template arguments
            for (TemplateArgument templateArgument : typeReference.getTemplateArguments())
                templateArgument.accept(this);

            final ZserioType type = typeReference.getType();
            if (!(type instanceof TemplatableType) ||
                    ((TemplatableType)type).getTemplateParameters().isEmpty())
            {
                throw new InstantiationException(typeReference.getLocation(),
                        "'" + ZserioTypeUtil.getReferencedFullName(typeReference) + "' is not a template!",
                        instantiationReferenceStack);
            }

            try
            {
                final TemplatableType template = (TemplatableType)type;
                instantiationReferenceStack.push(typeReference);
                final TemplatableType.InstantiationResult instantiationResult = instantiate(template);
                final ZserioTemplatableType instantiation = instantiationResult.getInstantiation();
                typeReference.resolveInstantiation(instantiation);

                if (instantiationResult.isNewInstance())
                {
                    try
                    {
                        instantiation.accept(typeResolver);
                    }
                    catch (ParserException e)
                    {
                        throw new InstantiationException(e, instantiationReferenceStack);
                    }
                    instantiation.accept(this);
                }
            }
            finally
            {
                instantiationReferenceStack.pop();
            }
        }
        else
        {
            typeReference.visitChildren(this);
        }
    }

    private TemplatableType.InstantiationResult instantiate(TemplatableType template)
    {
        final InstantiateType instantiateType = getMatchingInstantiateType(template);
        if (instantiateType != null)
        {
            final Package instantiationPackage = instantiateType.getPackage();
            final String instantiationName = instantiateType.getName();
            return template.instantiate(instantiationReferenceStack, instantiationPackage, instantiationName);
        }
        return template.instantiate(instantiationReferenceStack, template.getPackage(), null);
    }

    private InstantiateType getMatchingInstantiateType(TemplatableType template)
    {
        InstantiateType matchingInstantiateType = null;
        final List<TemplateArgument> templateArguments =
                instantiationReferenceStack.peek().getTemplateArguments();
        for (InstantiateType instantiateType : visibleInstantiateTypes)
        {
            final TemplatableType instantiateTemplate = instantiateType.getTemplate();
            final PackageName instantiatePackageName = instantiateTemplate.getPackage().getPackageName();
            final PackageName templatePackageName = template.getPackage().getPackageName();
            if (instantiatePackageName.equals(templatePackageName) &&
                    instantiateTemplate.getName().equals(template.getName()) &&
                    instantiateType.getTypeReference().getTemplateArguments().equals(templateArguments))
            {
                if (matchingInstantiateType != null)
                {
                    final ParserStackedException stackedException = new ParserStackedException(
                            instantiateType.getLocation(), "Ambiguous instantiate of template '" +
                            ZserioTypeUtil.getReferencedFullName(instantiateType.getTypeReference()));
                    stackedException.pushMessage(matchingInstantiateType.getLocation(),
                            "    first requested here");
                    throw stackedException;
                }
                matchingInstantiateType = instantiateType;
            }
        }

        return matchingInstantiateType;
    }

    private final ZserioAstTypeResolver typeResolver;
    private final ArrayDeque<TypeReference> instantiationReferenceStack = new ArrayDeque<TypeReference>();

    private Package currentPackage = null;
    private Set<InstantiateType> visibleInstantiateTypes = null;
}
