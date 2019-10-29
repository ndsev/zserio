package zserio.ast;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;

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
        pkg.visitChildren(this);
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
                final ZserioTemplatableType instantiation = instantiate(template);
                typeReference.resolveInstantiation(instantiation);

                // instantiate templates within the instantiation
                instantiation.accept(this);
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

    private TemplatableType instantiate(TemplatableType template)
    {
        try
        {
            final TypeReference instantiationReference = instantiationReferenceStack.peek();
            final List<TemplateArgument> templateArguments = instantiationReference.getTemplateArguments();
            final InstantiateType instantiateType = currentPackage.getVisibleInstantiateType(template,
                    templateArguments);
            final Package instantiationPackage = instantiateType != null ?
                    instantiateType.getPackage() : template.getPackage();
            final String instantiationName = instantiateType != null ?
                    instantiateType.getName() : generateInstantiationName(template, templateArguments);

            // try to find previous instantiation first
            final InstantiationMapKey key = new InstantiationMapKey(
                    instantiationPackage.getPackageName(), instantiationName);
            final TemplatableType previousInstantiation = findPreviousInstantiation(
                    template, templateArguments, key);
            if (previousInstantiation != null)
                return previousInstantiation;

            // instantiate the template
            final TemplatableType instantiation =
                    template.instantiate(instantiationReferenceStack, instantiationPackage, instantiationName);
            instantiationMap.put(key, instantiation);

            // resolve types within the instantiation
            instantiation.accept(typeResolver);

            return instantiation;
        }
        catch (ParserException e)
        {
            throw new InstantiationException(e, instantiationReferenceStack);
        }
    }

    private String generateInstantiationName(TemplatableType template, List<TemplateArgument> templateArguments)
    {
        final StringBuilder nameBuilder = new StringBuilder(template.getName());

        appendTemplateArgumentsToName(nameBuilder, templateArguments);

        final String generatedName = nameBuilder.toString();

        // check if generated name doesn't clash with a local type
        final ZserioType localType = template.getPackage().getLocalType(generatedName);
        if (localType != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    template.getLocation(),
                    "'" + generatedName + "' is already defined in package '" +
                    template.getPackage().getPackageName() + "'!");
            stackedException.pushMessage(localType.getLocation(), "    First defined here");
            throw stackedException;
        }

        return generatedName;
    }

    private void appendTemplateArgumentsToName(StringBuilder nameBuilder,
            List<TemplateArgument> templateArguments)
    {
        for (TemplateArgument templateArgument : templateArguments)
        {
            nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
            appendTemplateArgumentToName(nameBuilder, templateArgument);
        }
    }

    private void appendTemplateArgumentToName(StringBuilder nameBuilder, TemplateArgument templateArgument)
    {
        TypeReference typeReference = templateArgument.getTypeReference();
        final ZserioType type = typeReference.getType();
        if (type instanceof Subtype)
            typeReference = ((Subtype)type).getBaseTypeReference();

        final StringJoinUtil.Joiner joiner = new StringJoinUtil.Joiner(TEMPLATE_NAME_SEPARATOR);
        joiner.append(typeReference.getReferencedPackageName().toString(TEMPLATE_NAME_SEPARATOR));
        joiner.append(typeReference.getReferencedTypeName());
        nameBuilder.append(joiner.toString());

        appendTemplateArgumentsToName(nameBuilder, templateArgument.getTypeReference().getTemplateArguments());
    }

    private TemplatableType findPreviousInstantiation(TemplatableType template,
            List<TemplateArgument> templateArguments, InstantiationMapKey key)
    {
        final TemplatableType previousInstantiation = instantiationMap.get(key);
        if (previousInstantiation != null)
        {
            // check that the template arguments fit
            final String templateFullName = ZserioTypeUtil.getFullName(template);
            final String prevTemplateFullName = ZserioTypeUtil.getFullName(previousInstantiation.getTemplate());
            if (!templateFullName.equals(prevTemplateFullName) || !templateArguments.equals(
                    previousInstantiation.getInstantiationReferenceStack().peek().getTemplateArguments()))
            {
                final ParserStackedException stackedException = new ParserStackedException(
                        template.getLocation(),
                        "Instantiation name '" + key.getName() + "' already exits!");

                final Iterator<TypeReference> descendingIterator =
                        previousInstantiation.getInstantiationReferenceStack().descendingIterator();
                while (descendingIterator.hasNext())
                {
                    final TypeReference prevInstantiationReference = descendingIterator.next();
                    if (descendingIterator.hasNext())
                    {
                        stackedException.pushMessage(prevInstantiationReference.getLocation(),
                                "    Required in instantiation of '" +
                                        prevInstantiationReference.getReferencedTypeName() + "' from here");
                    }
                    else
                    {
                        final String message = previousInstantiation.getTemplate() == template
                                ? "    First instantiated here"
                                : "    First seen in instantiation of '" +
                                        prevInstantiationReference.getReferencedTypeName() + "' from here";
                        stackedException.pushMessage(prevInstantiationReference.getLocation(), message);
                    }
                }
                throw stackedException;
            }

            return previousInstantiation;
        }

        return null;
    }

    private static class InstantiationMapKey
    {
        public InstantiationMapKey(PackageName packageName, String name)
        {
            this.packageName = packageName;
            this.name = name;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof InstantiationMapKey))
                return false;

            if (this == other)
                return true;

            final InstantiationMapKey otherKey = (InstantiationMapKey)other;
            return packageName.equals(otherKey.packageName) &&
                    name.equals(otherKey.name);
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, packageName);
            hash = HashUtil.hash(hash, name);
            return hash;
        }

        public String getName()
        {
            return name;
        }

        private final PackageName packageName;
        private final String name;
    }

    private final ZserioAstTypeResolver typeResolver;
    private final ArrayDeque<TypeReference> instantiationReferenceStack = new ArrayDeque<TypeReference>();
    private final Map<InstantiationMapKey, TemplatableType> instantiationMap =
            new HashMap<InstantiationMapKey, TemplatableType>();

    private static final String TEMPLATE_NAME_SEPARATOR = "_";

    private Package currentPackage = null;
}
