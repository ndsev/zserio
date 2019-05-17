package zserio.ast;

import java.util.List;

import org.antlr.v4.runtime.Token;


/**
 * AST node for Union types.
 *
 * Union types are Zserio types as well.
 */
public class UnionType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param token      ANTLR4 token to localize AST node in the sources.
     * @param pkg        Package to which belongs the union type.
     * @param name       Name of the union type.
     * @param parameters List of parameters for the union type.
     * @param fields     List of all fields of the union type.
     * @param functions  List of all functions of the union type.
     * @param docComment Documentation comment belonging to this node.
     */
    public UnionType(Token token, Package pkg, String name, List<Parameter> parameters, List<Field> fields,
            List<FunctionType> functions, DocComment docComment)
    {
        super(token, pkg, name, parameters, fields, functions, docComment);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitUnionType(this);
    }

    @Override
    void check()
    {
        super.check();
        checkTableFields();
    }
};
