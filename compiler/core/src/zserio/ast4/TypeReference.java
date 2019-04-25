package zserio.ast4;

import org.antlr.v4.runtime.Token;

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
     * @param token                 ANTLR4 token to localize AST node in the sources.
     * @param ownerPackage          Package of the type reference owner.
     * @param referencedPackageName Package name which the reference points to.
     * @param referencedTypeName    Type name which the reference points to.
     * @param isParameterized       True if the reference points to parameterized type.
     */
    public TypeReference(Token token, Package ownerPackage, PackageName referencedPackageName,
            String referencedTypeName, boolean isParameterized)
    {
        super(token);

        this.ownerPackage = ownerPackage;
        this.referencedPackageName = referencedPackageName;
        this.referencedTypeName = referencedTypeName;
        this.isParameterized = isParameterized;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTypeReference(this);
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
     * Gets whether the referenced type is parameterized.
     *
     * @return True for parameterized types.
     */
    public boolean isParameterized()
    {
        return isParameterized;
    }

    /**
     * Resolves this reference to the corresponding referenced type.
     */
    protected void resolve()
    {
        // resolve referenced type
        referencedType = ownerPackage.getVisibleType(this, referencedPackageName, referencedTypeName);
        if (referencedType == null)
            throw new ParserException(this, "Unresolved referenced type '" + referencedTypeName + "'!");

        // check referenced type
        // TODO: shall we put this to the checking listener?
        if (referencedType instanceof ConstType)
            throw new ParserException(this, "Invalid usage of constant '" + referencedType.getName() +
                    "' as a type!");
        if (referencedType instanceof SqlDatabaseType)
            throw new ParserException(this, "Invalid usage of SQL database '" + referencedType.getName() +
                    "' as a type!");
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
    private final boolean isParameterized;

    private ZserioType referencedType = null;
}
