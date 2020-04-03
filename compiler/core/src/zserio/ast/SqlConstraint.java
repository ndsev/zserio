package zserio.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AST node for SQL constraints.
 */
public class SqlConstraint extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location      AST node location.
     * @param constaintExpr Constraint expression.
     */
    public SqlConstraint(AstLocation location, Expression constraintExpr)
    {
        super(location);

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
     * Indicates if the SQL constraint allows 'NULL' value.
     *
     * By default, no SQL constraint means that 'NULL' value is allowed.
     *
     * @return True if the SQL constraint allows 'NULL' value.
     */
    public static boolean isNullAllowed(SqlConstraint sqlConstraint)
    {
    	return (sqlConstraint != null) ? sqlConstraint.isNullAllowed : true;
    }
    
    /**
     * Evaluates the SQL constraint.
     */
    void evaluate()
    {
    	// check SQL constraint expression (must be a constant string)
        final String sqlConstraintString = constraintExpr.getStringValue();
        if (sqlConstraintString == null)
            throw new ParserException(constraintExpr, "SQL constraint expression must be a constant string!");

        primaryKeyColumnNames = extractColumnsFromConstraint(sqlConstraintString,
        		PRIMARY_KEY_TABLE_CONSTRAINT_REGEX);
        uniqueColumnNames = extractColumnsFromConstraint(sqlConstraintString, UNIQUE_TABLE_CONSTRAINT_REGEX);
        isPrimaryKey = hasConstraint(sqlConstraintString, PRIMARY_KEY_FIELD_CONSTRAINT_REGEX);
        isNullAllowed = !hasConstraint(sqlConstraintString, NOT_NULL_FIELD_CONSTRAINT_REGEX);
    }

    /**
     * Instantiate the sql constraint.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New sql constraint instantiated from this using the given template arguments.
     */
    SqlConstraint instantiate(List<TemplateParameter> templateParameters,
            List<TemplateArgument> templateArguments)
    {
        final Expression instantiatedConstraintExpr =
                getConstraintExpr().instantiate(templateParameters, templateArguments);

        return new SqlConstraint(getLocation(), instantiatedConstraintExpr);
    }

    private static List<String> extractColumnsFromConstraint(String sqlConstraintString, String constraintRegex)
    {
        final Pattern regexPattern = Pattern.compile(constraintRegex, Pattern.CASE_INSENSITIVE);
        final Matcher regexMatcher = regexPattern.matcher(sqlConstraintString);
        if (!regexMatcher.find())
        	return new ArrayList<String>();

        final String columnNamesGroup = regexMatcher.group(1).trim();
        final String[] columnNames = columnNamesGroup.split("\\s*,\\s*");

        return new ArrayList<String>(Arrays.asList(columnNames));
    }

    private static boolean hasConstraint(String sqlConstraintString, String constraintRegex)
    {
        final Pattern regexPattern = Pattern.compile(constraintRegex, Pattern.CASE_INSENSITIVE);

        return regexPattern.matcher(sqlConstraintString).find();
    }

    private static final String PRIMARY_KEY_TABLE_CONSTRAINT_REGEX = "PRIMARY\\s+KEY\\s*\\(([^\\)]+)\\)";
    private static final String UNIQUE_TABLE_CONSTRAINT_REGEX = "UNIQUE\\s*\\(([^\\)]+)\\)";
    private static final String PRIMARY_KEY_FIELD_CONSTRAINT_REGEX = "PRIMARY\\s+KEY";
    private static final String NOT_NULL_FIELD_CONSTRAINT_REGEX = "NOT\\s+NULL";
    
    private final Expression constraintExpr;

    private List<String> primaryKeyColumnNames = new ArrayList<String>();
    private List<String> uniqueColumnNames = new ArrayList<String>();

    private boolean isNullAllowed = false;
    private boolean isPrimaryKey = false;
}
