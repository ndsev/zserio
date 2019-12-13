package zserio.ast;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zserio.tools.HashUtil;

/**
 * Implementation of ZserioAstVisitor which handles templates instantiation.
 */
public class ZserioAstTemplator extends ZserioAstWalker
{
    public ZserioAstTemplator(ZserioAstTypeResolver typeResolver)
    {
        this.typeResolver = typeResolver;
    }

    @Override
    public void visitRoot(Root root)
    {
        root.visitChildren(this);
    }

    @Override
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
            final ZserioType type = typeReference.getType();
            if (!(type instanceof TemplatableType))
            {
                throw new InstantiationException(typeReference.getLocation(),
                        "'" + ZserioTypeUtil.getReferencedFullName(typeReference) +
                        "' is not a templatable type!", instantiationReferenceStack);
            }

            final TemplatableType templatable = (TemplatableType)type;

            if (templatable.getTemplate() == null)
            {
                // hasn't instantiated yet
                if (templatable.getTemplateParameters().isEmpty())
                {
                    throw new InstantiationException(typeReference.getLocation(),
                            "'" + ZserioTypeUtil.getReferencedFullName(typeReference) + "' is not a template!",
                            instantiationReferenceStack);
                }

                // template is not instantiated yet => instantiate instantiations in template arguments
                for (TemplateArgument templateArgument : typeReference.getTemplateArguments())
                    templateArgument.accept(this);

                try
                {
                    instantiationReferenceStack.push(typeReference);
                    final ZserioTemplatableType instantiation = instantiate(templatable);
                    typeReference.resolveInstantiation(instantiation);
                }
                finally
                {
                    instantiationReferenceStack.pop();
                }
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
            final boolean hasExplicitInstantiation = (instantiateType != null);
            final Package instantiationPackage = hasExplicitInstantiation ?
                    instantiateType.getPackage() : template.getPackage();
            final String instantiationShortName = hasExplicitInstantiation ?
                    instantiateType.getName() : generateShortInstantiationName(template, templateArguments);
            final String instantiationFullName = generateFullInstantiationName(template, templateArguments);

            // try to find previous instantiation first
            String instantiationName = instantiationShortName;
            InstantiationMapKey key = new InstantiationMapKey(instantiationPackage.getPackageName(),
                    instantiationName);
            InstantiationMapValue previousValue = instantiationMap.get(key);
            if (previousValue != null)
            {
                // previous instantiation has been found => check full name which is unique
                if (previousValue.getFullName().equals(instantiationFullName))
                    return previousValue.getTemplatableType();

                // short instantiation name clash => try hashing
                if (!previousValue.hasExplicitInstantiation())
                {
                    // rename the previous one using hash
                    final String previousInstantiationName = createHashedShortInstantionName(
                            instantiationShortName, previousValue.getFullName());
                    setInstantiationName(previousValue.getTemplatableType(), previousInstantiationName,
                            previousValue.hasExplicitInstantiation());

                    // check it again with hashed short instantiation name
                    instantiationName = createHashedShortInstantionName(instantiationShortName,
                            instantiationFullName);
                    key = new InstantiationMapKey(instantiationPackage.getPackageName(), instantiationName);
                    previousValue = instantiationMap.get(key);

                    if (previousValue != null)
                        throwInstantiationNameClash(template, previousValue.getTemplatableType(),
                                key.getShortName());
                }
            }

            // instantiate the template
            final TemplatableType newInstantiation = template.instantiate(instantiationReferenceStack,
                    instantiationPackage);
            final InstantiationMapValue value = new InstantiationMapValue(newInstantiation,
                    instantiationFullName, hasExplicitInstantiation);
            instantiationMap.put(key, value);

            // resolve instantiation name
            setInstantiationName(newInstantiation, instantiationName, hasExplicitInstantiation);

            // resolve types within the instantiation
            newInstantiation.accept(typeResolver);

            // instantiate templates within the instantiation
            newInstantiation.accept(this);

            return newInstantiation;
        }
        catch (InstantiationException e)
        {
            throw e;
        }
        catch (ParserException e)
        {
            throw new InstantiationException(e, instantiationReferenceStack);
        }
    }

    private String generateFullInstantiationName(TemplatableType template,
            List<TemplateArgument> templateArguments)
    {
        return generateInstantiationName(template, templateArguments, false);
    }

    private String generateShortInstantiationName(TemplatableType template,
            List<TemplateArgument> templateArguments)
    {
        return generateInstantiationName(template, templateArguments, true);
    }

    private String generateInstantiationName(TemplatableType template, List<TemplateArgument> templateArguments,
            boolean generateShortName)
    {
        final StringBuilder nameBuilder = new StringBuilder(template.getName());
        for (TemplateArgument templateArgument : templateArguments)
        {
            ZserioType type = templateArgument.getTypeReference().getType();
            if (type instanceof Subtype)
            {
                // resolves subtypes only => template inst must be called after base type or instantiate type
                type = ((Subtype)type).getBaseTypeReference().getType();
            }

            if (!generateShortName && !(type instanceof BuiltInType))
            {
                final PackageName typePackageName = type.getPackage().getPackageName();
                if (!typePackageName.isEmpty())
                {
                    nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
                    nameBuilder.append(typePackageName.toString(TEMPLATE_NAME_SEPARATOR));
                }
            }

            nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
            nameBuilder.append(type.getName());
        }

        if (generateShortName)
        {
            // check if generated name is not too long (leave space to append hash in case of clash)
            final int maxNameLengthWithoutHash = MAX_TEMPLATE_NAME_LENGTH - 8 - 1;
            if (nameBuilder.length() > maxNameLengthWithoutHash)
            {
                nameBuilder.delete(maxNameLengthWithoutHash - 1, nameBuilder.length());
                nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
            }
        }

        return nameBuilder.toString();
    }

    private void setInstantiationName(TemplatableType template, String instantiationName,
            boolean hasExplicitInstantiation)
    {
        if (!hasExplicitInstantiation)
            checkInstantiationName(template, instantiationName);
        template.resolveInstantiationName(instantiationName);
    }

    private void checkInstantiationName(TemplatableType template, String instantiationName)
    {
        final ZserioType localType = template.getPackage().getLocalType(instantiationName);
        if (localType != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    template.getLocation(), "'" + instantiationName + "' is already defined in package '" +
                    template.getPackage().getPackageName() + "'!");
            stackedException.pushMessage(localType.getLocation(), "    First defined here");
            throw stackedException;
        }
    }

    private String createHashedShortInstantionName(String shortInstantiationName, String fullInstantiationName)
    {
        // calculate our hash code not to depend on Java hashCode implementation (which might differ)
        int fullNameHash = HashUtil.HASH_SEED;
        final int textLength = fullInstantiationName.length();
        for (int i = 0; i < textLength; i++)
            fullNameHash = HashUtil.hash(fullNameHash, fullInstantiationName.charAt(i));

        // append hash of full name to the short name
        final String generatedNameHash = String.format("%08X", fullNameHash);
        final StringBuilder nameBuilder = new StringBuilder(shortInstantiationName);
        nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
        nameBuilder.append(generatedNameHash);

        return nameBuilder.toString();
    }

    private void throwInstantiationNameClash(TemplatableType template, TemplatableType previousInstantiation,
        String previousInstantiationName)
    {
        final ParserStackedException stackedException = new ParserStackedException(template.getLocation(),
                "Instantiation name '" + previousInstantiationName + "' already exits!");

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

    private static class InstantiationMapKey
    {
        public InstantiationMapKey(PackageName packageName, String shortName)
        {
            this.packageName = packageName;
            this.shortName = shortName;
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
                    shortName.equals(otherKey.shortName);
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, packageName);
            hash = HashUtil.hash(hash, shortName);

            return hash;
        }

        public String getShortName()
        {
            return shortName;
        }

        private final PackageName packageName;
        private final String shortName;
    }

    private static class InstantiationMapValue
    {
        public InstantiationMapValue(TemplatableType templatableType, String fullName,
                boolean hasExplicitInstantiation)
        {
            this.templatableType = templatableType;
            this.fullName = fullName;
            this.hasExplicitInstantiation = hasExplicitInstantiation;
        }

        public TemplatableType getTemplatableType()
        {
            return templatableType;
        }

        public String getFullName()
        {
            return fullName;
        }

        public boolean hasExplicitInstantiation()
        {
            return hasExplicitInstantiation;
        }

        private final TemplatableType templatableType;
        private final String fullName;
        private final boolean hasExplicitInstantiation;
    }

    private static final String TEMPLATE_NAME_SEPARATOR = "_";
    // Common file systems have maximum file name length limited to 255. Besides of that very long names are
    // real pain for users. We should deal with this somehow.
    private static final int MAX_TEMPLATE_NAME_LENGTH = 96;

    private final ZserioAstTypeResolver typeResolver;
    private final ArrayDeque<TypeReference> instantiationReferenceStack = new ArrayDeque<TypeReference>();
    private final Map<InstantiationMapKey, InstantiationMapValue> instantiationMap =
            new HashMap<InstantiationMapKey, InstantiationMapValue>();

    private Package currentPackage = null;
}
