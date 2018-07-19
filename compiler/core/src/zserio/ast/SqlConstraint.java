package zserio.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for SQL constraints.
 */
public class SqlConstraint extends TokenAST
{
    /**
     * Gets original SQL constraint expression.
     *
     * @return Original SQL constraint expression as has been specified in the Zserio.
     */
    public Expression getConstraintExpr()
    {
        return constraintExpr;
    }

    /**
     * Gets translated SQL constraint expression.
     *
     * @return SQL constraint expression with translated Zserio values.
     */
    public Expression getTranslatedConstraintExpr()
    {
        return translatedConstraintExpr;
    }

    /**
     * Gets translated SQL constraint expression for table field.
     *
     * @return SQL constraint expression with translated NOT NULL constraint or null
     *         if there is no translated SQL constraint for field.
     */
    public Expression getTranslatedFieldConstraintExpr()
    {
        return translatedFieldConstraintExpr;
    }

    /**
     * Indicates if the SQL constraint allows 'NULL' value.
     *
     * @return True if the SQL constraint allows 'NULL' value.
     */
    public boolean isNullAllowed()
    {
        return isNullAllowed;
    }

    /**
     * Indicates if the SQL constraint contains 'PRIMARY KEY'.
     *
     * @return true if this SQL constraint contains 'PRIMARY KEY'.
     */
    public boolean isPrimaryKey()
    {
        return isPrimaryKey;
    }

    /**
     * Gets list of primary key column names specified by this constraint.
     *
     * @return List of primary key column names.
     */
    public List<String> getPrimaryKeyColumnNames()
    {
        return primaryKeyColumnNames;
    }

    /**
     * Gets list of unique column names specified by this constraint.
     *
     * @return List of unique column names.
     */
    public List<String> getUniqueColumnNames()
    {
        return uniqueColumnNames;
    }

    /**
     * Creates default SQL constraint fot table fields.
     *
     * This is used for table fields which have no SQL constraint specified in Zserio. If table field has
     * no SQL contraint, it should be translated to 'NOT NULL' constraint for SQLite (Zserio default behaviour).
     *
     * @return Created default SQL constraint.
     */
    public static SqlConstraint createDefaultFieldConstraint()
    {
        final SqlConstraint sqlConstraint = new SqlConstraint();

        // set translated constraint expression for table field
        sqlConstraint.setTranslatedFieldConstraintExpr("");

        return sqlConstraint;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        if (child.getType() != ZserioParserTokenTypes.STRING_LITERAL || constraintExpr != null)
            return false;

        if (!(child instanceof Expression))
            return false;

        constraintExpr = (Expression)child;

        return true;
    }

    @Override
    protected void check() throws ParserException
    {
        try
        {
            String translatedConstraint = "";
            if (constraintExpr != null)
            {
                primaryKeyColumnNames = extractColumnNames(primaryKeyConstraint);
                uniqueColumnNames = extractColumnNames(uniqueConstraint);
                isPrimaryKey = containsPrimaryKey();

                // replace all @-references
                translatedConstraint = StringEntityResolver.resolve(constraintExpr.getText());
                translatedConstraintExpr = createStringLiteralExpression(translatedConstraint);
            }
            else
            {
                primaryKeyColumnNames = new ArrayList<String>();
                uniqueColumnNames = new ArrayList<String>();
            }

            // set translated constraint expression for table field
            setTranslatedFieldConstraintExpr(translatedConstraint);
        }
        catch (StringEntityResolverException exception)
        {
            throw new ParserException(this, exception.getMessage());
        }
    }

    private void setTranslatedFieldConstraintExpr(String translatedConstraint)
    {
        // unlike SQLite, the default column constraint in Zserio is 'NOT NULL' and NULL-constraints have to be
        // explicitly set
        String fieldConstraint = translatedConstraint;

        // skip quotes
        if (fieldConstraint.length() > 1)
            fieldConstraint = fieldConstraint.substring(1, fieldConstraint.length() - 1);

        // remove duplicate white spaces to be able detect "NOT NULL" and "DEFAULT NULL" properly
        fieldConstraint = fieldConstraint.replaceAll("\\s+", " ");

        // trim leading and trailing whitespace
        fieldConstraint = fieldConstraint.trim();

        if (!fieldConstraint.contains("NOT NULL"))
        {
            // there is no "NOT NULL"
            if (fieldConstraint.contains("DEFAULT NULL"))
            {
                // there is "DEFAULT NULL" => null is allowed
                isNullAllowed = true;
            }
            else
            {
                // there is no "NOT NULL" and no "DEFAULT NULL"
                if (!fieldConstraint.contains("NULL"))
                {
                    // and there is no "NULL" => add "NOT NULL" constraint (default in Zserio)
                    if (!fieldConstraint.isEmpty())
                        fieldConstraint = fieldConstraint.concat(" ");
                    fieldConstraint = fieldConstraint.concat("NOT NULL");
                }
                else
                {
                    // and there is "NULL" => remove "NULL" constraint (unknown for SQLite)
                    fieldConstraint = fieldConstraint.replace("NULL", "");

                    // trim leading and trailing whitespace
                    fieldConstraint = fieldConstraint.trim();

                    // null is allowed
                    isNullAllowed = true;
                }
            }
        }

        translatedFieldConstraintExpr = (fieldConstraint.isEmpty()) ? null :
            createStringLiteralExpression("\"" + fieldConstraint + "\"");
    }

    private static Expression createStringLiteralExpression(String stringLiteral)
    {
        final Expression stringLiteralExpr = new Expression();
        stringLiteralExpr.setText(stringLiteral);
        stringLiteralExpr.setType(ZserioParserTokenTypes.STRING_LITERAL);

        return stringLiteralExpr;
    }

    private List<String> extractColumnNames(String constraintName)
    {
        final ArrayList<String> columnNames = new ArrayList<String>();
        final String sqlConstraintString = constraintExpr.getText();
        final int constraintIndex = sqlConstraintString.toUpperCase(Locale.ENGLISH).indexOf(constraintName);
        if (constraintIndex > -1)
        {
            final int leftBracketIndex = sqlConstraintString.indexOf('(', constraintIndex);
            if (leftBracketIndex > -1)
            {
                final int rightBracketIndex = sqlConstraintString.indexOf(')', leftBracketIndex);
                if (rightBracketIndex > -1)
                {
                    final String[] cols = sqlConstraintString.substring(leftBracketIndex + 1,
                            rightBracketIndex).split(",");
                    for (int i = 0; i < cols.length; i++ )
                        columnNames.add(cols[i].trim());
                }
            }
        }

        return columnNames;
    }

    private boolean containsPrimaryKey()
    {
        final String sqlConstraintString = constraintExpr.getText();

        return (sqlConstraintString.toUpperCase(Locale.ENGLISH).indexOf(primaryKeyConstraint) > -1);
    }

    private static final long serialVersionUID = 4009186108710189361L;

    private static final String primaryKeyConstraint = "PRIMARY KEY";
    private static final String uniqueConstraint = "UNIQUE";

    private Expression constraintExpr = null;
    private Expression translatedConstraintExpr = null;
    private Expression translatedFieldConstraintExpr = null;

    private List<String>    primaryKeyColumnNames;
    private List<String>    uniqueColumnNames;

    private boolean isNullAllowed = false;
    private boolean isPrimaryKey = false;
}
