package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for SQL table types.
 *
 * SQL table types are Zserio types as well.
 */
public class SqlTableType extends CompoundType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitSqlTableType(this);
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
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (getName() == null)
                setName(child.getText());
            else
                sqlUsingId = child.getText();
            break;

        case ZserioParserTokenTypes.FIELD:
        case ZserioParserTokenTypes.VFIELD:
            if (!(child instanceof Field))
                return false;
            addField((Field)child);
            break;

        case ZserioParserTokenTypes.SQL:
            if (!(child instanceof SqlConstraint))
                return false;
            sqlConstraint = (SqlConstraint)child;
            sqlConstraint.setCompoundType(this);
            break;

        case ZserioParserTokenTypes.SQL_WITHOUT_ROWID:
            if (isVirtual())
                throw new ParserException(child, "Virtual table cannot be without rowid!");
            sqlWithoutRowId = true;
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
        checkTableFields();

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

    private void checkOrdinaryTableFields() throws ParserException
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

    private void checkPrimaryKeyConstraint() throws ParserException
    {
        boolean first = true;
        boolean found = false;
        for (Field tableField : getFields())
        {
            final SqlConstraint fieldSqlConstraint = tableField.getSqlConstraint();
            if (fieldSqlConstraint.isPrimaryKey())
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

    private void checkPrimaryKeyInSqlConstraint(boolean primaryKeyFound) throws ParserException
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

    private void checkPrimaryKeyForWithoutRowId() throws ParserException
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

    private void checkUniqueConstraint() throws ParserException
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

    private void checkPrimaryKeyByColumnName(String columnName, int columnIndex) throws ParserException
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

    private static final long   serialVersionUID = -4079404455157794418L;

    private String sqlUsingId;
    private SqlConstraint sqlConstraint;
    private final Set<Field> sqlPrimaryKeyFields = new HashSet<Field>();
    private boolean sqlWithoutRowId = false;
}
