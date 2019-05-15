package zserio.ast;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;
import zserio.ast.TypeInstantiation.InstantiatedParameter;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for SQL Table types.
 *
 * SQL Ttypes are Zserio types as well.
 */
public class SqlTableType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param token           ANTLR4 token to localize AST node in the sources.
     * @param pkg             Package to which belongs the SQL table type.
     * @param name            Name of the SQL table type.
     * @param sqlUsingId      SQL using id associated to the SQL table type.
     * @param fields          List of all fields of to the SQL table type.
     * @param sqlConstraint   SQL constraint of the SQL table type.
     * @param sqlWithoutRowId SQL without row id associated to the SQL table type.
     * @param docComment      Documentation comment belonging to this node.
     */
    public SqlTableType(Token token, Package pkg, String name, String sqlUsingId, List<Field> fields,
            SqlConstraint sqlConstraint, boolean sqlWithoutRowId, DocComment docComment)
    {
        super(token, pkg, name, new ArrayList<Parameter>(), fields, new ArrayList<FunctionType>(), docComment);

        this.sqlUsingId = sqlUsingId;
        this.sqlConstraint = sqlConstraint;
        this.sqlWithoutRowId = sqlWithoutRowId;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSqlTableType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        if (sqlConstraint != null)
            sqlConstraint.accept(visitor);
    }

    /**
     * Gets 'using' specification for SQL virtual tables.
     *
     * @return 'using' specification for SQL virtual tables or null if 'using' has not been specified.
     */
    public String getVirtualTableUsingString()
    {
        return sqlUsingId;
    }

    /**
     * Checks if this SQL table needs types specification in schema.
     *
     * This method is needed to distinguish between FTS5 and other virtual tables because only FTS5 does not
     * support types specification in schema.
     *
     * @return true if this SQL table needs types specification in schema.
     */
    public boolean needsTypesInSchema()
    {
        // FTS5 virtual tables do not support column types in table declaration
        if (isVirtual() && sqlUsingId.equals("fts5"))
            return false;

        return true;
    }

    /**
     * Gets SQL constraint defined in this SQL table.
     *
     * @return SQL constraint defined in this SQL table.
     */
    public SqlConstraint getSqlConstraint()
    {
        return sqlConstraint;
    }

    /**
     * Checks if given field is primary key in the table.
     *
     * @param field Field to check.
     *
     * @return true if given field is primary key or if it is part of composite primary key.
     */
    public boolean isFieldPrimaryKey(Field field)
    {
        return sqlPrimaryKeyFields.contains(field);
    }

    /**
     * Checks if the table has without rowid optimization.
     *
     * @return true if the table is without rowid, otherwise false.
     */
    public boolean isWithoutRowId()
    {
        return sqlWithoutRowId;
    }

    @Override
    void check()
    {
        super.check();
        checkTableFields();
        checkExplicitParameters();

        if (!isVirtual())
        {
            checkOrdinaryTableFields();
            checkPrimaryKeyConstraint();
            checkUniqueConstraint();
        }
    }

    private boolean isVirtual()
    {
        return sqlUsingId != null;
    }

    private void checkExplicitParameters()
    {
        final HashMap<String, AbstractMap.SimpleEntry<String, Expression> > paramTypeMap =
                new HashMap<String, AbstractMap.SimpleEntry<String, Expression> >();

        for (Field tableField : getFields())
        {
            final List<InstantiatedParameter> instantiatedParameters = tableField.getInstantiatedParameters();
            for (InstantiatedParameter instantiatedParam : instantiatedParameters)
            {
                final Expression argumentExpression = instantiatedParam.getArgumentExpression();
                if (argumentExpression.isExplicitVariable())
                {
                    final Parameter param = instantiatedParam.getParameter();
                    final String paramName = param.getName();
                    final ZserioType type = TypeReference.resolveBaseType(param.getParameterType());
                    final String typeName = ZserioTypeUtil.getFullName(type);

                    final AbstractMap.SimpleEntry<String, Expression> prevEntry = paramTypeMap.get(paramName);
                    if (prevEntry != null)
                    {
                        final String prevTypeName = prevEntry.getKey();
                        if (!prevTypeName.equals(typeName))
                        {
                            final Expression prevExpression = prevEntry.getValue();
                            throw new ParserException(argumentExpression, "Type of explicit parameter '" +
                                    paramName + "' resolved to '" + typeName + "' but first used as '" +
                                    prevTypeName + "' at " + prevExpression.getLocation().getLine() + ":" +
                                    prevExpression.getLocation().getColumn() + "!");
                        }
                    }
                    else
                    {
                        paramTypeMap.put(paramName, new AbstractMap.SimpleEntry<String, Expression>(
                                typeName, argumentExpression));
                    }
                }
            }
        }
    }

    private void checkOrdinaryTableFields()
    {
        if (getFields().isEmpty())
            throw new ParserException(this, "Ordinary table must have at least one field!");

        for (Field tableField : getFields())
        {
            if (tableField.getIsVirtual())
            {
                throw new ParserException(tableField,
                        "Ordinary table '" + this.getName() + "' cannot contain virtual column '" +
                        tableField.getName() + "'!");
            }
        }
    }

    private void checkPrimaryKeyConstraint()
    {
        boolean first = true;
        boolean found = false;
        for (Field tableField : getFields())
        {
            final SqlConstraint fieldSqlConstraint = tableField.getSqlConstraint();
            if (fieldSqlConstraint != null && fieldSqlConstraint.isPrimaryKey())
            {
                if (found)
                {
                    ZserioToolPrinter.printWarning(fieldSqlConstraint, "Duplicated primary key " +
                            "column '" + tableField.getName() + "' in sql table '" + getName() + "'.");
                }
                else
                {
                    if (!first)
                        ZserioToolPrinter.printWarning(fieldSqlConstraint, "Primary key column '" +
                                tableField.getName() + "' is not the first one in sql table '" + getName() +
                                "'.");
                    found = true;
                }

                checkPrimaryKeyColumn(tableField);
            }

            first = false;
        }

        checkPrimaryKeyInSqlConstraint(found);
        checkPrimaryKeyForWithoutRowId();
    }

    private void checkPrimaryKeyInSqlConstraint(boolean primaryKeyFound)
    {
        final List<String> primaryKeyColumnNames = (sqlConstraint != null) ?
                sqlConstraint.getPrimaryKeyColumnNames() : new ArrayList<String>();
        if (primaryKeyFound)
        {
            if (!primaryKeyColumnNames.isEmpty())
                ZserioToolPrinter.printWarning(sqlConstraint, "Multiple primary keys in sql table '" +
                        getName() + "'.");
        }
        else
        {
            if (primaryKeyColumnNames.isEmpty())
            {
                if (sqlWithoutRowId)
                    throw new ParserException(this, "No primary key in without rowid table '" + getName() +
                            "'!");

                ZserioToolPrinter.printWarning(this, "No primary key in sql table '" + getName() + "'.");
            }
        }

        int columnIndex = 0;
        for (String columnName : primaryKeyColumnNames)
        {
            checkPrimaryKeyByColumnName(columnName, columnIndex);
            columnIndex++;
        }
    }

    private void checkPrimaryKeyForWithoutRowId()
    {
        // single integer primary key for without rowid table brings performance drop
        if (sqlWithoutRowId && sqlPrimaryKeyFields.size() == 1)
        {
            for (Field primaryKeyField : sqlPrimaryKeyFields)
            {
                final ZserioType fieldBaseType =
                        TypeReference.resolveBaseType(primaryKeyField.getFieldType());
                if (fieldBaseType instanceof BooleanType || fieldBaseType instanceof IntegerType)
                    ZserioToolPrinter.printWarning(this, "Single integer primary key in without rowid " +
                            "table '" + getName() + "' brings performance drop.");
            }
        }
    }

    private void checkUniqueConstraint()
    {
        if (sqlConstraint != null)
        {
            final List<String> uniqueColumnNames = sqlConstraint.getUniqueColumnNames();
            for (String columnName : uniqueColumnNames)
            {
                boolean found = false;
                for (Field tableField : getFields())
                {
                    if (tableField.getName().equals(columnName))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                    throw new ParserException(sqlConstraint, "Unique column '" + columnName +
                            "' not found in sql table '" + getName() + "'!");
            }
        }
    }

    private void checkPrimaryKeyColumn(Field tableField)
    {
        sqlPrimaryKeyFields.add(tableField);
        if (tableField.getSqlConstraint().isNullAllowed())
            ZserioToolPrinter.printWarning(tableField, "Primary key column '" + tableField.getName() +
                    "' can contain NULL in sql table '" + getName() + "'.");
    }

    private void checkPrimaryKeyByColumnName(String columnName, int columnIndex)
    {
        boolean found = false;
        int fieldIndex = 0;
        for (Field tableField : getFields())
        {
            if (tableField.getName().equals(columnName))
            {
                found = true;
                checkPrimaryKeyColumn(tableField);
                break;
            }

            fieldIndex++;
        }

        if (!found)
            throw new ParserException(sqlConstraint, "Primary key column '" + columnName +
                    "' not found in sql table '" + getName() + "'!");

        if (fieldIndex != columnIndex)
        {
            ZserioToolPrinter.printWarning(sqlConstraint, "Primary key column '" + columnName +
                    "' is in bad order in sql table '" + getName() + "'.");
        }
    }

    private final String sqlUsingId;
    private final SqlConstraint sqlConstraint;
    private final boolean sqlWithoutRowId;

    private final Set<Field> sqlPrimaryKeyFields = new HashSet<Field>();
}
