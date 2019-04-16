package zserio.ast4;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * AST node for SQL database types.
 *
 * SQL database types are Zserio types as well.
 */
public class SqlDatabaseType extends CompoundType
{
    public SqlDatabaseType(Token token, Package pkg, String name, List<Field> fields)
    {
        super(token, pkg, name, new ArrayList<Parameter>(), fields, new ArrayList<FunctionType>());
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSqlDatabaseType(this);
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
}
