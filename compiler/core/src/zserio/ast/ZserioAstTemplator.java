package zserio.ast;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zserio.tools.HashUtil;
import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * Implementation of ZserioAstVisitor which handles templates instantiation.
 */
public final class ZserioAstTemplator extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param typeResolver Type resolver.
     * @param warningsConfig Warnings config.
     */
    public ZserioAstTemplator(ZserioAstTypeResolver typeResolver, WarningsConfig warningsConfig)
    {
        this.typeResolver = typeResolver;
        this.warningsConfig = warningsConfig;
    }

    @Override
    public void visitRoot(Root root)
    {
        root.visitChildren(this);
        resolveInstantiationNames();
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
                    final ZserioTemplatableType instantiation = instantiate(templatable,
                            typeReference.getTemplateArguments());
                    typeReference.resolveInstantiation(instantiation);
                }
                catch (InstantiationException e)
                {
                    // prevent recurrent InstantitationExcpetion (which is also ParserException)
                    throw e;
                }
                catch (ParserException e)
                {
                    throw new InstantiationException(e, instantiationReferenceStack);
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

    private TemplatableType instantiate(TemplatableType template, List<TemplateArgument> templateArguments)
    {
        // check if a instantiate type exists
        final InstantiateType instantiateType = currentPackage.getVisibleInstantiateType(template,
                templateArguments);
        Package instantiationPackage;
        String instantiationShortName;
        String instantiationHashCode;
        if (instantiateType != null)
        {
            instantiationPackage = instantiateType.getPackage();
            instantiationShortName = instantiateType.getName();
            instantiationHashCode = "";
        }
        else
        {
            instantiationPackage = template.getPackage();
            instantiationShortName = generateShortInstantiationName(template, templateArguments);
            instantiationHashCode = generateInstantiationHashCode(template, templateArguments);
        }

        // try to find previous instantiation first
        final ShortNameKey shortNameKey = new ShortNameKey(instantiationPackage.getPackageName(),
                instantiationShortName);
        final InstantiationMapKey key = new InstantiationMapKey(shortNameKey, instantiationHashCode);
        final InstantiationMapValue previousValue = instantiationMap.get(key);
        if (previousValue != null)
        {
            // previous instantiation has been found => check template arguments which are unique
            if (!previousValue.getTemplateArguments().equals(templateArguments))
            {
                // short name and hash code are the same => clash which we can't resolve
                throw getInstantiationNameClashException(template, previousValue.getTemplatableType(),
                        instantiationShortName);
            }

            // instantiation found
            return previousValue.getTemplatableType();
        }

        // warn about default instantiations
        if (instantiateType == null)
        {
            final Iterator<TypeReference> descendingIt = instantiationReferenceStack.descendingIterator();
            while (descendingIt.hasNext())
            {
                final TypeReference instantiationReference = descendingIt.next();
                if (descendingIt.hasNext())
                {
                    if (warningsConfig.isEnabled(WarningsConfig.DEFAULT_INSTANTIATION))
                    {
                        ZserioToolPrinter.printWarning(instantiationReference.getLocation(),
                                "    In instantiation of '" +
                                instantiationReference.getReferencedTypeName() + "' required from here");
                    }
                }
                else
                {
                    ZserioToolPrinter.printWarning(instantiationReference,
                            "Default instantiation of '" + instantiationReference.getReferencedTypeName() +
                            "' as '" + instantiationShortName + ".",
                            warningsConfig, WarningsConfig.DEFAULT_INSTANTIATION);
                }
            }
        }

        // remember instantiation short name clashes
        final boolean isShortNameKeyClash = instantiationClashMap.containsKey(shortNameKey);
        instantiationClashMap.put(shortNameKey, isShortNameKeyClash);

        // instantiate the template
        final TemplatableType newInstantiation = template.instantiate(instantiationReferenceStack,
                instantiationPackage);

        // remember the instantiation
        // note: must be done before templates within the instantiation (e.g. field types) are instantiated
        //       to prevent stack overflow in case of templated compound type recursion
        final InstantiationMapValue value = new InstantiationMapValue(newInstantiation, templateArguments);
        instantiationMap.put(key, value);

        // resolve types within the instantiation
        newInstantiation.accept(typeResolver);

        // instantiate templates within the instantiation
        newInstantiation.accept(this);

        return newInstantiation;
    }

    private String generateInstantiationHashCode(TemplatableType template,
            List<TemplateArgument> templateArguments)
    {
        final String fullInstantiationName = generateFullInstantiationName(template, templateArguments);

        // calculate our hash code not to depend on Java hashCode implementation (which might differ)
        int fullNameHash = HashUtil.HASH_SEED;
        final int textLength = fullInstantiationName.length();
        for (int i = 0; i < textLength; i++)
            fullNameHash = HashUtil.hash(fullNameHash, fullInstantiationName.charAt(i));

        return String.format("%08X", fullNameHash);
    }

    private String generateFullInstantiationName(TemplatableType template,
            List<TemplateArgument> templateArguments)
    {
        return generateInstantiationName(template, templateArguments, true);
    }

    private String generateShortInstantiationName(TemplatableType template,
            List<TemplateArgument> templateArguments)
    {
        return generateInstantiationName(template, templateArguments, false);
    }

    private String generateInstantiationName(TemplatableType template, List<TemplateArgument> templateArguments,
            boolean generateFullName)
    {
        final StringBuilder nameBuilder = new StringBuilder(template.getName());

        for (TemplateArgument templateArgument : templateArguments)
            appendTemplateArgument(nameBuilder, templateArgument, generateFullName);

        if (!generateFullName)
        {
            // check if generated name is not too long (leave space to append hash in case of clash)
            if (nameBuilder.length() > MAX_TEMPLATE_NAME_LENGTH)
            {
                nameBuilder.delete(MAX_TEMPLATE_NAME_LENGTH - 1, nameBuilder.length());
                nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
            }
        }

        return nameBuilder.toString();
    }

    private void appendTemplateArgument(StringBuilder nameBuilder, TemplateArgument templateArgument,
            boolean generateFullName)
    {
        final TypeReference argumentTypeReference = templateArgument.getTypeReference();
        ZserioType argumentType = argumentTypeReference.getType();
        final List<TemplateArgument> innerTemplateArguments = argumentTypeReference.getTemplateArguments();
        if (argumentType instanceof TemplatableType && !innerTemplateArguments.isEmpty())
        {
            // the argument is inner template instantiation => check instantiate type
            final InstantiateType instantiateType = currentPackage.getVisibleInstantiateType(
                (TemplatableType)argumentType, innerTemplateArguments);
            if (instantiateType != null)
            {
                if (generateFullName)
                {
                    nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
                    nameBuilder.append(
                            instantiateType.getPackage().getPackageName().toString(TEMPLATE_NAME_SEPARATOR));
                }
                nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
                nameBuilder.append(instantiateType.getName());
                return;
            }
        }

        if (argumentType instanceof Subtype)
        {
            // resolves subtypes only => template inst must be called after base type or instantiate type
            argumentType = ((Subtype)argumentType).getBaseTypeReference().getType();
        }

        if (generateFullName && !(argumentType instanceof BuiltInType))
        {
            final PackageName typePackageName = argumentType.getPackage().getPackageName();
            if (!typePackageName.isEmpty())
            {
                nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
                nameBuilder.append(typePackageName.toString(TEMPLATE_NAME_SEPARATOR));
            }
        }

        nameBuilder.append(TEMPLATE_NAME_SEPARATOR);
        nameBuilder.append(argumentType.getName());

        for (TemplateArgument innerArgument : innerTemplateArguments)
            appendTemplateArgument(nameBuilder, innerArgument, generateFullName);
    }

    private ParserStackedException getInstantiationNameClashException(TemplatableType template,
            TemplatableType previousInstantiation, String previousInstantiationName)
    {
        final ParserStackedException stackedException = new ParserStackedException(template.getLocation(),
                "Instantiation name '" + previousInstantiationName + "' already exists!");

        final Iterator<TypeReference> descendingIterator =
                previousInstantiation.getInstantiationReferenceStack().descendingIterator();
        while (descendingIterator.hasNext())
        {
            final TypeReference prevInstantiationReference = descendingIterator.next();
            if (descendingIterator.hasNext())
            {
                stackedException.pushMessage(prevInstantiationReference.getLocation(),
                        "    In instantiation of '" +
                                prevInstantiationReference.getReferencedTypeName() + "' required from here");
            }
            else
            {
                final String message = previousInstantiation.getTemplate() == template
                        ? "    First instantiated here"
                        : "    In instantiation of '" +
                                prevInstantiationReference.getReferencedTypeName() + "' first seen from here";
                stackedException.pushMessage(prevInstantiationReference.getLocation(), message);
            }
        }

        return stackedException;
    }

    private void resolveInstantiationNames()
    {
        for (Map.Entry<InstantiationMapKey, InstantiationMapValue> entry : instantiationMap.entrySet())
        {
            final ShortNameKey shortNameKey = entry.getKey().getShortNameKey();
            final String hashCode = entry.getKey().getHashCode();
            final TemplatableType templatableType = entry.getValue().getTemplatableType();
            String instantiationName = shortNameKey.getShortName();
            // if hash code is empty, it is instantiate type which cannot be renamed
            if (!hashCode.isEmpty())
            {
                // check short name clash with another template instantiation or local type
                if (instantiationClashMap.get(shortNameKey) ||
                        templatableType.getPackage().getLocalSymbol(instantiationName) != null)
                {
                    // add hash code
                    final int maxInstantiationNameLength = MAX_TEMPLATE_NAME_LENGTH - 1 - hashCode.length();
                    if (instantiationName.length() > maxInstantiationNameLength)
                        instantiationName = instantiationName.substring(0, maxInstantiationNameLength);
                    instantiationName += TEMPLATE_NAME_SEPARATOR;
                    instantiationName += hashCode;
                }
                checkInstantiationName(templatableType, instantiationName);
            }

            templatableType.resolveInstantiationName(instantiationName);
        }
    }

    private void checkInstantiationName(TemplatableType template, String instantiationName)
    {
        final PackageSymbol localSymbol = template.getPackage().getLocalSymbol(instantiationName);
        if (localSymbol != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    template.getLocation(), "'" + instantiationName + "' is already defined in package '" +
                    template.getPackage().getPackageName() + "'!");
            stackedException.pushMessage(localSymbol.getLocation(), "    First defined here");

            throw new InstantiationException(stackedException, template.getInstantiationReferenceStack());
        }
    }

    private static final class ShortNameKey
    {
        public ShortNameKey(PackageName packageName, String shortName)
        {
            this.packageName = packageName;
            this.shortName = shortName;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof ShortNameKey))
                return false;

            if (this != other)
            {
                final ShortNameKey otherKey = (ShortNameKey)other;
                if (!packageName.equals(otherKey.packageName))
                    return false;
                if (!shortName.equals(otherKey.shortName))
                    return false;
            }

            return true;
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

    private static final class InstantiationMapKey
    {
        public InstantiationMapKey(ShortNameKey shortNameKey, String hashCode)
        {
            this.shortNameKey = shortNameKey;
            this.hashCode = hashCode;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof InstantiationMapKey))
                return false;

            if (this != other)
            {
                final InstantiationMapKey otherKey = (InstantiationMapKey)other;
                if (!shortNameKey.equals(otherKey.shortNameKey))
                    return false;
                if (!hashCode.equals(otherKey.hashCode))
                    return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, shortNameKey);
            hash = HashUtil.hash(hash, hashCode);

            return hash;
        }

        public ShortNameKey getShortNameKey()
        {
            return shortNameKey;
        }

        public String getHashCode()
        {
            return hashCode;
        }

        private final ShortNameKey shortNameKey;
        private final String hashCode;
    }

    private static final class InstantiationMapValue
    {
        public InstantiationMapValue(TemplatableType templatableType, List<TemplateArgument> templateArguments)
        {
            this.templatableType = templatableType;
            this.templateArguments = templateArguments;
        }

        public TemplatableType getTemplatableType()
        {
            return templatableType;
        }

        public List<TemplateArgument> getTemplateArguments()
        {
            return templateArguments;
        }

        private final TemplatableType templatableType;
        private final List<TemplateArgument> templateArguments;
    }

    private static final String TEMPLATE_NAME_SEPARATOR = "_";
    // Common file systems have maximum file name length limited to 255. Besides of that very long names are
    // real pain for users. We should deal with this somehow. Btw, older Windows application still insists on
    // 260 character long paths.
    private static final int MAX_TEMPLATE_NAME_LENGTH = 64;

    private final ZserioAstTypeResolver typeResolver;
    private final WarningsConfig warningsConfig;
    private final ArrayDeque<TypeReference> instantiationReferenceStack = new ArrayDeque<TypeReference>();
    private final Map<InstantiationMapKey, InstantiationMapValue> instantiationMap =
            new HashMap<InstantiationMapKey, InstantiationMapValue>();
    private final Map<ShortNameKey, Boolean> instantiationClashMap = new HashMap<ShortNameKey, Boolean>();

    private Package currentPackage = null;
}
