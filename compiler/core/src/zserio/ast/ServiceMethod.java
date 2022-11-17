package zserio.ast;

import java.util.List;

/**
 * AST node for service methods.
 */
public class ServiceMethod extends DocumentableAstNode implements ScopeSymbol
{
    /**
     * Constructor.
     *
     * @param location              AST node location.
     * @param name                  Name of the service method.
     * @param responseTypeReference Reference to the response type.
     * @param requestTypeReference  Reference to the request type.
     * @param docComments           List of documentation comments belonging to this node.
     */
    public ServiceMethod(AstLocation location, String name, TypeReference responseTypeReference,
            TypeReference requestTypeReference, List<DocComment> docComments)
    {
        super(location, docComments);

        this.name = name;
        this.responseTypeReference = responseTypeReference;
        this.requestTypeReference = requestTypeReference;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitServiceMethod(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        responseTypeReference.accept(visitor);
        requestTypeReference.accept(visitor);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets the reference to request method type.
     *
     * @return The reference to request method type.
     */
    public TypeReference getRequestTypeReference()
    {
        return requestTypeReference;
    }

    /**
     * Gets the reference to response method type.
     *
     * @return The reference to response method type.
     */
    public TypeReference getResponseTypeReference()
    {
        return responseTypeReference;
    }

    /**
     * Check the service method.
     */
    void check()
    {
        checkUsedType(responseTypeReference);
        checkUsedType(requestTypeReference);
    }

    private void checkUsedType(TypeReference typeReference)
    {
        final ZserioType referencedBaseType = typeReference.getBaseTypeReference().getType();
        if (!(referencedBaseType instanceof CompoundType) && !(referencedBaseType instanceof BytesType))
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types or bytes can be used in service methods, " +
                    "'" + typeReference.getReferencedTypeName() + "' is not a compound type!");
        }

        if (referencedBaseType instanceof CompoundType)
        {
            final CompoundType compoundType = (CompoundType)referencedBaseType;
            if (compoundType.getTypeParameters().size() > 0)
            {
                throw new ParserException(typeReference,
                        "Only non-parameterized compound types or bytes can be used in service methods, '" +
                        ZserioTypeUtil.getReferencedFullName(typeReference) + "' is a parameterized type!");
            }
            if (compoundType instanceof SqlTableType)
            {
                throw new ParserException(typeReference, "SQL table '" +
                        ZserioTypeUtil.getReferencedFullName(typeReference) +
                        "' cannot be used in service methods!");
            }
        }
    }

    private final String name;
    private final TypeReference responseTypeReference;
    private final TypeReference requestTypeReference;
}
