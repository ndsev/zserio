package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;


/**
 * AST node for SQL Database types.
 *
 * SQL Database types are Zserio types as well.
 */
public class SqlDatabaseType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param token      ANTLR4 token to localize AST node in the sources.
     * @param pkg        Package to which belongs the SQL database type.
     * @param name       Name of the SQL database type.
     * @param fields     List of all fields of the SQL database type.
     * @param docComment Documentation comment belonging to this node.
     */
    public SqlDatabaseType(Token token, Package pkg, String name, List<Field> fields, DocComment docComment)
    {
        super(token, pkg, name, new ArrayList<Parameter>(), fields, new ArrayList<FunctionType>(), docComment);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSqlDatabaseType(this);
    }

    @Override
    void check()
    {
        // evaluates common compound type
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
