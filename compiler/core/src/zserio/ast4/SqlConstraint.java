package zserio.ast4;

import java.util.List;
import java.util.Locale;

import org.antlr.v4.runtime.Token;

/**
 * AST node for SQL constraints.
 */
public class SqlConstraint extends AstNodeBase
{
    public SqlConstraint(Token token, Expression constraintExpr)
    {
        super(token);

        this.constraintExpr = constraintExpr;
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.beginSqlConstraint(this);

        constraintExpr.walk(listener);

        listener.endSqlConstraint(this);
    }

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
     * Creates default SQL constraint for table fields.
     *
     * This is used for table fields which have no SQL constraint specified in Zserio. If table field has
     * no SQL constraint, it should be translated to 'NOT NULL' constraint for SQLite (Zserio default behavior).
     *
     * @return Created default SQL constraint.
     */
    /*public static SqlConstraint createDefaultFieldConstraint()
    {
        final SqlConstraint sqlConstraint = new SqlConstraint();

        // set translated constraint expression for table field
        sqlConstraint.setTranslatedFieldConstraintExpr("");

        return sqlConstraint;
    }*/ // TODO:

    /*@Override
    protected void check() throws ParserException
    {
        String translatedConstraint = "";
        if (constraintExpr != null)
        {
            primaryKeyColumnNames = extractColumnNames(PRIMARY_KEY_CONSTRAINT);
            uniqueColumnNames = extractColumnNames(UNIQUE_CONSTRAINT);
            isPrimaryKey = containsPrimaryKey();

            // replace all @-references
            translatedConstraint = resolveConstraintReferences(constraintExpr.getText());
            translatedConstraintExpr = createStringLiteralExpression(translatedConstraint);
        }
        else
        {
            primaryKeyColumnNames = new ArrayList<String>();
            uniqueColumnNames = new ArrayList<String>();
        }

        // set translated constraint expression for table field
        setTranslatedFieldConstraintExpr(translatedConstraint);
    }*/ // TODO:

    /**
     * Sets the compound type which is owner of the field.
     *
     * @param compoundType Owner to set.
     */
    protected void setCompoundType(CompoundType compoundType)
    {
        this.compoundType = compoundType;
    }

    /*private void setTranslatedFieldConstraintExpr(String translatedConstraint)
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
    }*/ // TODO:

    private boolean containsPrimaryKey()
    {
        final String sqlConstraintString = constraintExpr.getExpressionString();

        return (sqlConstraintString.toUpperCase(Locale.ENGLISH).indexOf(PRIMARY_KEY_CONSTRAINT) > -1);
    }

    /*private String resolveConstraintReferences(String constraintText) throws ParserException
    {
        int referenceIndex = constraintText.indexOf(CONSTRAINT_REFERENCE_ESCAPE);
        if (referenceIndex < 0)
            return constraintText; // shortcut when there are no references

        final StringBuilder stringBuilder = new StringBuilder(constraintText);
        while (referenceIndex >= 0)
        {
            final int endIndex = findEndOfConstraintReference(stringBuilder, referenceIndex + 1);

            final String referencedText = stringBuilder.substring(referenceIndex + 1, endIndex);
            final String resolved = resolveConstraintReference(referencedText);

            stringBuilder.replace(referenceIndex, endIndex, resolved);
            referenceIndex = stringBuilder.indexOf(CONSTRAINT_REFERENCE_ESCAPE);
        }

        return stringBuilder.toString();
    }

    private static int findEndOfConstraintReference(StringBuilder buffer, int startIndex)
    {
        int endIndex = startIndex;
        while (endIndex < buffer.length())
        {
            final char c = buffer.charAt(endIndex);
            if (c != '.' && c != '_' && !Character.isLetterOrDigit(buffer.charAt(endIndex)))
                break;

            endIndex++;
        }

        return endIndex;
    }

    private String resolveConstraintReference(String referencedText) throws ParserException
    {
        final SymbolReference symbolReference = new SymbolReference(this, referencedText);
        symbolReference.check(compoundType);

        final ZserioType referencedType = symbolReference.getReferencedType();
        final Object referencedSymbol = symbolReference.getReferencedSymbol();
        String resolvedReferencedText;
        if (referencedType instanceof ConstType)
        {
            final BigInteger value = ((ConstType)referencedType).getValueExpression().getIntegerValue();
            if (value == null)
                throw new ParserException(this, "Reference '" + referencedText + "' refers " +
                        "to non-integer constant!");

            resolvedReferencedText = value.toString();
        }
        else if (referencedSymbol instanceof EnumItem)
        {
            resolvedReferencedText = ((EnumItem)referencedSymbol).getValue().toString();
        }
        else
        {
            throw new ParserException(this, "Reference '" + referencedText + "' does refer to neither " +
                    "enumeration type nor constant!");
        }

        return resolvedReferencedText;
    }*/

    private static final String PRIMARY_KEY_CONSTRAINT = "PRIMARY KEY";
    private static final String UNIQUE_CONSTRAINT = "UNIQUE";
    private static final String CONSTRAINT_REFERENCE_ESCAPE = "@";

    private CompoundType compoundType = null;

    private final Expression constraintExpr;
    private Expression translatedConstraintExpr = null;
    private Expression translatedFieldConstraintExpr = null;

    private List<String> primaryKeyColumnNames;
    private List<String> uniqueColumnNames;

    private boolean isNullAllowed = false;
    private boolean isPrimaryKey = false;
}
