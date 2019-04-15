package zserio.ast4;

import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * AST node for auto choice types.
 *
 * Auto choice types are Zserio types as well.
 */
public class UnionType extends CompoundType
{
    public UnionType(Token token, Package pkg, String name, List<Parameter> parameters, List<Field> fields,
            List<FunctionType> functions)
    {
        super(token, pkg, name, parameters, fields, functions);
    }

    @Override
    public void accept(ZserioVisitor visitor)
    {
        visitor.visitUnionType(this);
    }

    /*@Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitUnionType(this);
    }*/

    /*@Override
    protected void check() throws ParserException
    {
        super.check();
        checkTableFields();
    }*/ // TODO:
};
