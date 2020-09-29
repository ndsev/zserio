package zserio.ast;

import java.util.List;

/**
 * AST node for service methods.
 */
public class ServiceMethod extends DocumentableAstNode
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

    /**
     * Gets name of the service method.
     *
     * @return Service method name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets request type of the method.
     *
     * @return Request type of this method.
     */
    public CompoundType getRequestType()
    {
        return (CompoundType)requestTypeReference.getBaseTypeReference().getType();
    }

    /**
     * Gets response type of the method.
     *
     * @return Response type of this method.
     */
    public CompoundType getResponseType()
    {
        return (CompoundType)responseTypeReference.getBaseTypeReference().getType();
    }

    /**
     * Checks the method's types.
     */
    void check()
    {
        checkUsedType(responseTypeReference);
        checkUsedType(requestTypeReference);
    }

    private void checkUsedType(TypeReference typeReference)
    {
        final ZserioType referencedBaseType = typeReference.getBaseTypeReference().getType();
        if (!(referencedBaseType instanceof CompoundType))
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types can be used in service methods, " +
                    "'" + typeReference.getReferencedTypeName() + "' is not a compound type!");
        }

        final CompoundType compoundType = (CompoundType)referencedBaseType;
        if (compoundType.getTypeParameters().size() > 0)
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types can be used in service methods, '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) + "' is a parameterized type!");
        }
        if (compoundType instanceof SqlTableType)
        {
            throw new ParserException(typeReference, "SQL table '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) +
                    "' cannot be used in service methods!");
        }
    }

    private final String name;
    private final TypeReference responseTypeReference;
    private final TypeReference requestTypeReference;
}
