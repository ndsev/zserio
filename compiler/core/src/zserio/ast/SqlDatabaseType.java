package zserio.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for SQL Database types.
 *
 * SQL Database types are Zserio types as well.
 */
public final class SqlDatabaseType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param location    AST node location.
     * @param pkg         Package to which belongs the SQL database type.
     * @param name        Name of the SQL database type.
     * @param fields      List of all fields of the SQL database type.
     * @param docComments List of documentation comments belonging to this node.
     */
    public SqlDatabaseType(
            AstLocation location, Package pkg, String name, List<Field> fields, List<DocComment> docComments)
    {
        super(location, pkg, name, new ArrayList<TemplateParameter>(), new ArrayList<Parameter>(), fields,
                new ArrayList<Function>(), docComments);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSqlDatabaseType(this);
    }

    @Override
    void check()
    {
        super.check();

        checkSqlSymbolNames();
        checkNonSqlTableFields();
    }

    @Override
    SqlDatabaseType instantiateImpl(List<TemplateArgument> templateArguments, Package instantiationPackage)
    {
        throw new InternalError("SqlDatabaseType is not templatable!");
    }
}
