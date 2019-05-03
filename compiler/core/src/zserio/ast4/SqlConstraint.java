package zserio.ast4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import zserio.antlr.Zserio4Parser;

/**
 * AST node for SQL constraints.
 */
public class SqlConstraint extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token         ANTLR4 token to localize AST node in the sources.
     * @param constaintExpr Constraint expression.
     */
    public SqlConstraint(Token token, Expression constraintExpr)
    {
        super(token);

        this.constraintExpr = constraintExpr;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSqlConstraint(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        constraintExpr.accept(visitor);
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
        return Collections.unmodifiableList(primaryKeyColumnNames);
    }

    /**
     * Gets list of unique column names specified by this constraint.
     *
     * @return List of unique column names.
     */
    public List<String> getUniqueColumnNames()
    {
        return Collections.unmodifiableList(uniqueColumnNames);
    }

    /**
     * Creates default SQL constraint for table fields.
     *
     * This is used for table fields which have no SQL constraint specified in Zserio. If table field has
     * no SQL constraint, it should be translated to 'NOT NULL' constraint for SQLite (Zserio default behavior).
     *
     * @return Created default SQL constraint.
     */
    protected static SqlConstraint createDefaultFieldConstraint(Package pkg)
    {
        return new SqlConstraint(null, createStringLiteralExpression(pkg, ""));
    }

    /**
     * Resolves the SQL constraint.
     */
    protected void resolve()
    {
        final String sqlConstraintString = constraintExpr.getText();
        primaryKeyColumnNames = extractColumnNames(sqlConstraintString, PRIMARY_KEY_CONSTRAINT);
        uniqueColumnNames = extractColumnNames(sqlConstraintString, UNIQUE_CONSTRAINT);
        isPrimaryKey = containsPrimaryKey(sqlConstraintString);

        // replace all @-references
        final String translatedConstraint = resolveConstraintReferences(sqlConstraintString);
        translatedConstraintExpr = createStringLiteralExpression(compoundType.getPackage(),
                translatedConstraint);

        // set translated constraint expression for table field
        translatedFieldConstraintExpr = createTranslatedFieldConstraintExpr(translatedConstraint);
    }

    /**
     * Sets the compound type which is owner of the field.
     *
     * @param compoundType Owner to set.
     */
    protected void setCompoundType(CompoundType compoundType)
    {
        this.compoundType = compoundType;
    }

    private Expression createTranslatedFieldConstraintExpr(String translatedConstraint)
    {
        // unlike SQLite, the default column constraint in Zserio is 'NOT NULL' and NULL-constraints have to be
        // explicitly set
        String fieldConstraint = translatedConstraint;

        // skip quotes
        if (fieldConstraint.length() > 1)
            fieldConstraint = fieldConstraint.substring(1, fieldConstraint.length() - 1);

        // remove duplicated white spaces to be able detect NOT_NULL_CONSTRAINT/DEFAULT_NULL_CONSTRAINT properly
        fieldConstraint = fieldConstraint.replaceAll("\\s+", " ");

        // trim leading and trailing whitespace
        fieldConstraint = fieldConstraint.trim();

        if (!fieldConstraint.contains(NOT_NULL_CONSTRAINT))
        {
            // there is no NOT_NULL_CONSTRAINT
            if (fieldConstraint.contains(DEFAULT_NULL_CONSTRAINT))
            {
                // there is DEFAULT_NULL_CONSTRAINT => null is allowed
                isNullAllowed = true;
            }
            else
            {
                // there is no NOT_NULL_CONSTRAINT and no DEFAULT_NULL_CONSTRAINT
                if (!fieldConstraint.contains(NULL_CONSTRAINT))
                {
                    // and there is no NULL_CONSTRAINT => add NOT_NULL_CONSTRAINT constraint (default in Zserio)
                    if (!fieldConstraint.isEmpty())
                        fieldConstraint = fieldConstraint.concat(" ");
                    fieldConstraint = fieldConstraint.concat(NOT_NULL_CONSTRAINT);
                }
                else
                {
                    // and there is NULL_CONSTRAINT => remove NULL_CONSTRAINT constraint (unknown for SQLite)
                    fieldConstraint = fieldConstraint.replace(NULL_CONSTRAINT, "");

                    // trim leading and trailing whitespace
                    fieldConstraint = fieldConstraint.trim();

                    // null is allowed
                    isNullAllowed = true;
                }
            }
        }

        return (fieldConstraint.isEmpty()) ? null : createStringLiteralExpression(compoundType.getPackage(),
                "\"" + fieldConstraint + "\"");
    }

    private static Expression createStringLiteralExpression(Package pkg, String stringLiteral)
    {
        final CommonToken stringLiteralToken = new CommonToken(Zserio4Parser.STRING_LITERAL, stringLiteral);

        return new Expression(null, pkg, stringLiteralToken);
    }

    private static List<String> extractColumnNames(String sqlConstraintString, String constraintName)
    {
        final ArrayList<String> columnNames = new ArrayList<String>();
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

    private static boolean containsPrimaryKey(String sqlConstraintString)
    {
        return (sqlConstraintString.toUpperCase(Locale.ENGLISH).indexOf(PRIMARY_KEY_CONSTRAINT) > -1);
    }

    private String resolveConstraintReferences(String sqlConstraintString)
    {
        int referenceIndex = sqlConstraintString.indexOf(CONSTRAINT_REFERENCE_ESCAPE);
        if (referenceIndex < 0)
            return sqlConstraintString; // shortcut when there are no references

        final StringBuilder stringBuilder = new StringBuilder(sqlConstraintString);
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

    private String resolveConstraintReference(String referencedText)
    {
        final SymbolReference symbolReference = new SymbolReference(this, referencedText);
        symbolReference.resolve(compoundType.getPackage(), compoundType);

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
    }

    private static final String PRIMARY_KEY_CONSTRAINT = "PRIMARY KEY";
    private static final String UNIQUE_CONSTRAINT = "UNIQUE";
    private static final String NOT_NULL_CONSTRAINT = "NOT NULL";
    private static final String NULL_CONSTRAINT = "NULL";
    private static final String DEFAULT_NULL_CONSTRAINT = "DEFAULT NULL";
    private static final String CONSTRAINT_REFERENCE_ESCAPE = "@";

    private final Expression constraintExpr;

    private CompoundType compoundType = null;

    private Expression translatedConstraintExpr = null;
    private Expression translatedFieldConstraintExpr = null;

    private List<String> primaryKeyColumnNames = new ArrayList<String>();
    private List<String> uniqueColumnNames = new ArrayList<String>();

    private boolean isNullAllowed = false;
    private boolean isPrimaryKey = false;
}
