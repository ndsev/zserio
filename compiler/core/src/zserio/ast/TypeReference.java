package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.util.ParserException;

/**
 * AST node for Type References.
 *
 * A Type Reference is either a simple name or a sequence of simple names separated by dots referring to
 * a nested type, e.g. {@code Outer.Inner}.
 *
 * Type references are Zserio types as well.
 */
public class TypeReference extends AstNodeBase implements ZserioType
{
    /**
     * Constructor.
     *
     * @param location               AST node location.
     * @param ownerPackage           Package of the type reference owner.
     * @param referencedPackageName  Package name which the reference points to.
     * @param referencedTypeName     Type name which the reference points to.
     * @param templateArguments      Template arguments for the referenced type.
     * @param isTemplateArgument     True if the type reference is template argument.
     * @param checkIfNeedsParameters True if check if the referenced type needs parameters.
     */
    public TypeReference(AstLocation location, Package ownerPackage, PackageName referencedPackageName,
            String referencedTypeName, List<ZserioType> templateArguments, boolean isTemplateArgument,
            boolean checkIfNeedsParameters)
    {
        super(location);

        this.ownerPackage = ownerPackage;
        this.referencedPackageName = referencedPackageName;
        this.referencedTypeName = referencedTypeName;
        this.templateArguments = templateArguments;
        this.isTemplateArgument = isTemplateArgument;
        this.checkIfNeedsParameters = checkIfNeedsParameters;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTypeReference(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (ZserioType templateArgument : templateArguments)
            templateArgument.accept(visitor);
    }

    @Override
    public Package getPackage()
    {
        if (referencedType == null)
            return null;

        return referencedType.getPackage();
    }

    @Override
    public String getName()
    {
        return referencedTypeName;
    }

    /**
     * Gets referenced type.
     *
     * @return Referenced type.
     */
    public ZserioType getReferencedType()
    {
        return referencedType;
    }

    /**
     * Resolves this reference to the corresponding referenced type.
     */
    void resolve()
    {
        // resolve referenced type
        referencedType = ownerPackage.getVisibleType(this, referencedPackageName, referencedTypeName);
        if (referencedType == null)
        {
            throw new ParserException(this, "Unresolved referenced type '" +
                    ZserioTypeUtil.getReferencedFullName(this) + "'!");
        }

        // check referenced type
        if (referencedType instanceof ConstType && !isTemplateArgument)
            throw new ParserException(this, "Invalid usage of constant '" + referencedType.getName() +
                    "' as a type!");
        if (referencedType instanceof SqlDatabaseType)
            throw new ParserException(this, "Invalid usage of SQL database '" + referencedType.getName() +
                    "' as a type!");

        // TODO[Mi-L@]: What if it is a subtype to a template?
        if (referencedType instanceof TemplatableType)
        {
            final TemplatableType template = (TemplatableType)referencedType;
            if (!template.getTemplateParameters().isEmpty())
            {
                if (templateArguments.isEmpty())
                {
                    throw new ParserException(this,
                            "Missing template arguments for template '" + getName() + "'!");
                }

                referencedType = template.getInstantiation(templateArguments);
                if (referencedType == null)
                {
                    // this should not occur!
                    throw new InternalError("Template '" + ZserioTypeUtil.getReferencedFullName(this) +
                            "' is not properly instantiated!");
                }
            }
        }
    }

    /**
     * Checks the type reference.
     */
    void check()
    {
        if (checkIfNeedsParameters)
        {
            final ZserioType referencedBaseType = resolveBaseType(referencedType);
            if (referencedBaseType instanceof CompoundType)
            {
                final CompoundType referencedCompoundType = (CompoundType)referencedBaseType;
                if (referencedCompoundType.getTypeParameters().size() > 0)
                    throw new ParserException(this, "Referenced type '" + referencedTypeName +
                            "' is defined as parameterized type!");
            }
        }
    }

    ZserioType instantiate(List<String> templateParameters, List<ZserioType> templateArguments)
    {
        if (getReferencedPackageName().isEmpty()) // may be a template parameter
        {
            final int index = templateParameters.indexOf(getReferencedTypeName());
            if (index != -1)
            {
                if (!getTemplateArguments().isEmpty())
                    throw new ParserException(this, "Template parameter cannot be used as a template!");

                final ZserioType templateArgument = templateArguments.get(index);
                if (templateArgument instanceof TypeReference)
                {
                    // TODO[Mi-L@]: Consider redesign of the two flags (how to get rid of them).
                    // flags isTemplateArgument and checkIfNeedsParameters must be taken over from this!
                    final TypeReference referencedTemplateArgument = (TypeReference)templateArgument;
                    return new TypeReference(getLocation(), referencedTemplateArgument.ownerPackage,
                            referencedTemplateArgument.referencedPackageName,
                            referencedTemplateArgument.referencedTypeName,
                            referencedTemplateArgument.templateArguments,
                            isTemplateArgument, checkIfNeedsParameters);
                }
                return templateArgument;
            }
        }

        // instantiate template arguments first
        final List<ZserioType> instantiatedTemplateArguments = new ArrayList<ZserioType>();
        for (ZserioType templateArgument : getTemplateArguments())
        {
            if (templateArgument instanceof TypeReference)
            {
                instantiatedTemplateArguments.add(((TypeReference)templateArgument).instantiate(
                        templateParameters, templateArguments));
            }
            else
            {
                instantiatedTemplateArguments.add(templateArgument);
            }
        }

        return new TypeReference(getLocation(), ownerPackage, getReferencedPackageName(),
                getReferencedTypeName(), instantiatedTemplateArguments, isTemplateArgument,
                checkIfNeedsParameters);
    }

    List<ZserioType> getTemplateArguments()
    {
        return templateArguments;
    }

    PackageName getReferencedPackageName()
    {
        return referencedPackageName;
    }

    /* TODO[mikir] redundant with getName() */
    String getReferencedTypeName()
    {
        return referencedTypeName;
    }

    /**
     * Resolves base type from type reference or subtype.
     *
     * Note that this method does not resolve ArrayType and TypeInstantiation.
     *
     * @param type Generic Zserio type to resolve.
     *
     * @return The input parameter 'type' if 'type' is not type reference or subtype, otherwise base type of
     *         the type reference or subtype specified by input parameter 'type'.
     */
    static public ZserioType resolveBaseType(ZserioType type)
    {
        ZserioType baseType = type;

        if (baseType instanceof TypeReference)
            baseType = ((TypeReference)baseType).referencedType;

        if (baseType instanceof Subtype)
            baseType = ((Subtype)baseType).getTargetBaseType();

        return baseType;
    }

    /**
     * Resolves referenced type from type reference.
     *
     * @param type Generic Zserio type to resolve.
     *
     * @return The input parameter 'type' if 'type' is not type reference, otherwise referenced type of
     *         the type reference specified by input parameter 'type'.
     */
    static public ZserioType resolveType(ZserioType type)
    {
        ZserioType resolvedType = type;
        if (resolvedType instanceof TypeReference)
            resolvedType = ((TypeReference)resolvedType).referencedType;

        return resolvedType;
    }

    private final Package ownerPackage;
    private final PackageName referencedPackageName;
    private final String referencedTypeName;
    private final List<ZserioType> templateArguments;
    private final boolean isTemplateArgument;
    private final boolean checkIfNeedsParameters;

    private ZserioType referencedType = null;
}
