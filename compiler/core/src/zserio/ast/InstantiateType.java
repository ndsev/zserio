package zserio.ast;

/**
 * AST node for explicit template instantiation.
 */
public class InstantiateType extends AstNodeBase implements ZserioType
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param pkg           Package to which belongs the instantiation.
     * @param typeReference Reference to a template instantiation.
     * @param name          Name to be assigned to the instantiated template.
     */
    public InstantiateType(AstLocation location, Package pkg, TypeReference typeReference,
            String name)
    {
        super(location);

        this.pkg = pkg;
        this.typeReference = typeReference;
        this.name = name;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitInstantiateType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        typeReference.accept(visitor);
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets reference to the template instantiation.
     *
     * @return Type reference.
     */
    public TypeReference getTypeReference()
    {
        return typeReference;
    }

    /**
     * Resolves the instantiate type.
     */
    void resolve()
    {
        final ZserioType baseType = typeReference.getBaseType();
        if (!(baseType instanceof TemplatableType) ||
                ((TemplatableType)baseType).getTemplateParameters().isEmpty())
        {
            throw new ParserException(baseType,
                    "'" + ZserioTypeUtil.getReferencedFullName(typeReference) + "' is not a template!");
        }

        template = (TemplatableType)baseType;
    }

    /**
     * Gets template which will be instantiated.
     *
     * @return Templatable type.
     */
    TemplatableType getTemplate()
    {
        return template;
    }

    private final Package pkg;
    private final TypeReference typeReference;
    private final String name;

    private TemplatableType template = null;
}
