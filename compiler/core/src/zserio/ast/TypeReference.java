package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeReference extends AstNodeBase
{
    public TypeReference(AstLocation location, Package ownerPackage, BuiltInType builtinType,
            boolean isTemplateArgument)
    {
        super(location);

        this.ownerPackage = ownerPackage;
        referencedPackageName = PackageName.EMPTY;
        referencedTypeName = builtinType.getName();
        templateArguments = new ArrayList<TypeReference>();
        this.isTemplateArgument = isTemplateArgument;
        type = builtinType;
    }

    public TypeReference(AstLocation location, Package ownerPackage, PackageName referencedPackageName,
            String referencedTypeName, List<TypeReference> templateArguments, boolean isTemplateArgument)
    {
        super(location);

        this.ownerPackage = ownerPackage;
        this.referencedPackageName = referencedPackageName;
        this.referencedTypeName = referencedTypeName;
        this.templateArguments = templateArguments;
        this.isTemplateArgument = isTemplateArgument;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTypeReference(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (type instanceof BuiltInType)
            ((BuiltInType)type).accept(visitor);

        for (TypeReference templateArgument : templateArguments)
            templateArgument.accept(visitor);
    }

    public ZserioType getType()
    {
        return type;
    }

    public ZserioType getBaseType()
    {
        if (type instanceof Subtype)
            return ((Subtype)type).getBaseTypeReference().getType();
        return type;
    }

    public List<TypeReference> getTemplateArguments()
    {
        return Collections.unmodifiableList(templateArguments);
    }

    public PackageName getReferencedPackageName()
    {
        return referencedPackageName;
    }

    public String getReferencedTypeName()
    {
        return referencedTypeName;
    }

    /**
     * Resolves this reference to the corresponding referenced type.
     */
    void resolve()
    {
        // TODO[Mi-L@][typeref] Hack for built-in types.
        //                      Note that instantiated templates are also resolved, but we don't know yet how
        //                      to instantiate TypeReferenced pointing to instantiated template.
        if (type instanceof BuiltInType)
            return; // already resolved

        // resolve referenced type
        type = ownerPackage.getVisibleType(this, referencedPackageName, referencedTypeName);
        if (type == null)
        {
            throw new ParserException(this, "Unresolved referenced type '" +
                    ZserioTypeUtil.getReferencedFullName(this) + "'!");
        }

        // check referenced type
        if (type instanceof ConstType && !isTemplateArgument)
            throw new ParserException(this, "Invalid usage of constant '" + type.getName() +
                    "' as a type!");
        if (type instanceof SqlDatabaseType)
            throw new ParserException(this, "Invalid usage of SQL database '" + type.getName() +
                    "' as a type!");
        if (type instanceof TemplatableType)
        {
            final TemplatableType template = (TemplatableType)type;
            if (!template.getTemplateParameters().isEmpty() && templateArguments.isEmpty())
                throw new ParserException(this,
                        "Missing template arguments for template '" + getReferencedTypeName() + "'!");
        }
    }

    void resolveInstantiation(ZserioTemplatableType instantiation)
    {
        type = instantiation;
    }

    /**
     * Instantiate the type reference.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New type reference instantiated from this using the given template arguments.
     */
    TypeReference instantiate(List<TemplateParameter> templateParameters, List<TypeReference> templateArguments)
    {
        if (getReferencedPackageName().isEmpty()) // may be a template parameter
        {
            final int index = TemplateParameter.indexOf(templateParameters, referencedTypeName);
            if (index != -1)
            {
                if (!getTemplateArguments().isEmpty())
                    throw new ParserException(this, "Template parameter cannot be used as a template!");

                final TypeReference templateArgument = templateArguments.get(index);

                // TODO[Mi-L@]: Consider redesign of the isTemplateArgument flag (how to get rid of it).
                // flag isTemplateArgument must be taken over from this!
                return templateArgument.instantiateImpl(getLocation(), templateArgument.templateArguments,
                        isTemplateArgument, templateParameters, templateArguments);
            }
        }

        // instantiate template arguments first
        final List<TypeReference> instantiatedTemplateArguments = new ArrayList<TypeReference>();
        for (TypeReference templateArgument : getTemplateArguments())
        {
            instantiatedTemplateArguments.add(
                    templateArgument.instantiate(templateParameters, templateArguments));
        }

        return instantiateImpl(getLocation(), instantiatedTemplateArguments, isTemplateArgument,
                templateParameters, templateArguments);
    }

    private TypeReference instantiateImpl(AstLocation location,
            List<TypeReference> instantiatedTemplateArguments, boolean isTemplateArgument,
            List<TemplateParameter> templateParameters, List<TypeReference> passedTemplateArguments)
    {
        if (type instanceof BuiltInType) // TODO[Mi-L@][typeref] Hack for built-in types.
        {
            if (type instanceof BitFieldType)
            {
                return new TypeReference(location, ownerPackage,
                        ((BitFieldType)type).instantiate(templateParameters, passedTemplateArguments),
                        isTemplateArgument);
            }
            else if (type instanceof ArrayType)
            {
                return new TypeReference(location, ownerPackage,
                        ((ArrayType)type).instantiate(templateParameters, passedTemplateArguments),
                        isTemplateArgument);
            }
            else
            {
                return new TypeReference(location, ownerPackage, (BuiltInType)type, isTemplateArgument);
            }
        }
        else
        {
            return new TypeReference(location, ownerPackage, referencedPackageName, referencedTypeName,
                    instantiatedTemplateArguments, isTemplateArgument);
        }
    }

    private final Package ownerPackage;
    private final PackageName referencedPackageName;
    private final String referencedTypeName;
    private final List<TypeReference> templateArguments;
    private final boolean isTemplateArgument;

    private ZserioType type = null;
}