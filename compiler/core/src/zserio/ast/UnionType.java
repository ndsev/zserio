package zserio.ast;

import java.util.List;


/**
 * AST node for Union types.
 *
 * Union types are Zserio types as well.
 */
public class UnionType extends TemplatableCompoundType
{
    /**
     * Constructor.
     *
     * @param location              AST node location.
     * @param pkg                   Package to which belongs the union type.
     * @param name                  Name of the union type.
     * @param templateParameters    List of template parameters.
     * @param typeParameters        List of parameters for the union type.
     * @param fields                List of all fields of the union type.
     * @param functions             List of all functions of the union type.
     * @param docComment            Documentation comment belonging to this node.
     */
    public UnionType(AstLocation location, Package pkg, String name, List<String> templateParameters,
            List<Parameter> typeParameters, List<Field> fields, List<FunctionType> functions,
            DocComment docComment)
    {
        super(location, pkg, name, templateParameters, typeParameters, fields, functions, docComment);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitUnionType(this);
    }

    @Override
    UnionType instantiateImpl(String name, List<ZserioType> templateArguemnts)
    {
        // TODO[Mi-L@]:
        return null;
    }

    @Override
    void check()
    {
        super.check();
        checkTableFields();
    }
};
