package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for SQL database types.
 *
 * SQL database types are Zserio types as well.
 */
public class SqlDatabaseType extends CompoundType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitSqlDatabaseType(this);
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            setName(child.getText());
            break;

        case ZserioParserTokenTypes.FIELD:
            if (!(child instanceof Field))
                return false;
            addField((Field)child);
            break;

        default:
            return false;
        }

        return true;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        evaluateHiddenDocComment(this);
        setDocComment(getHiddenDocComment());
    }

    @Override
    protected void check() throws ParserException
    {
        super.check();

        // check if all fields are SQL tables
        for (Field databaseField : getFields())
        {
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(databaseField.getFieldType());
            if (!(fieldBaseType instanceof SqlTableType))
                throw new ParserException(databaseField,
                        "Field '" + databaseField.getName() + "' is not a sql table!");
        }
    }

    private static final long serialVersionUID = 661949362821042274L;
}
