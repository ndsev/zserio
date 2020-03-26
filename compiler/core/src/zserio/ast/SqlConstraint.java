package zserio.ast;

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zserio.antlr.ZserioParser;

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
     * Gets evaluated SQL constraint expression.
     *
     * @return SQL constraint expression with evaluated Zserio values.
     */
    public Expression getEvaluatedConstraintExpr()
    {
        return evaluatedConstraintExpr;
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
     * Resolves the SQL constraint.
     *
     * @param compoundType Compound type which owns the SQL constraint.
     */
    void resolve(CompoundType compoundType)
    {
        // store package of the owner
        pkg = compoundType.getPackage();

        // resolve all @-references
        final String sqlConstraintString = constraintExpr.getText();
        resolveConstraintReferences(compoundType, sqlConstraintString);
    }

    /**
     * Evaluates the SQL constraint.
     */
    void evaluate()
    {
        final String sqlConstraintString = constraintExpr.getText();
        primaryKeyColumnNames = extractColumnsFromConstraint(sqlConstraintString,
        		PRIMARY_KEY_TABLE_CONSTRAINT_REGEX);
        uniqueColumnNames = extractColumnsFromConstraint(sqlConstraintString, UNIQUE_TABLE_CONSTRAINT_REGEX);
        isPrimaryKey = hasConstraint(sqlConstraintString, PRIMARY_KEY_FIELD_CONSTRAINT_REGEX);
        isNullAllowed = !hasConstraint(sqlConstraintString, NOT_NULL_FIELD_CONSTRAINT_REGEX);

        // set evaluated constraint expression
        final String evaluatedConstraint = evaluateConstraint();
        evaluatedConstraintExpr = createStringLiteralExpression(pkg, evaluatedConstraint);
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

    private void resolveConstraintReferences(CompoundType compoundType, String sqlConstraintString)
    {
        int startIndex = 0;
        int referenceIndex = sqlConstraintString.indexOf(CONSTRAINT_REFERENCE_ESCAPE);
        while (referenceIndex >= 0)
        {
            evaluatedConstraintStrings.add(sqlConstraintString.substring(startIndex, referenceIndex));

            final int endIndex = findEndOfConstraintReference(sqlConstraintString, referenceIndex + 1);
            final String referencedText = sqlConstraintString.substring(referenceIndex + 1, endIndex);
            final SymbolReference symbolReference = new SymbolReference(this, referencedText);
            symbolReference.resolve(compoundType.getPackage(), compoundType);
            constraintReferences.add(new AbstractMap.SimpleEntry<SymbolReference, String>(symbolReference,
                    referencedText));

            startIndex = endIndex;
            referenceIndex = sqlConstraintString.indexOf(CONSTRAINT_REFERENCE_ESCAPE, startIndex);
        }

        if (startIndex < sqlConstraintString.length())
            evaluatedConstraintStrings.add(sqlConstraintString.substring(startIndex));
    }

    private static int findEndOfConstraintReference(String sqlConstrainString, int startIndex)
    {
        int endIndex = startIndex;
        while (endIndex < sqlConstrainString.length())
        {
            final char c = sqlConstrainString.charAt(endIndex);
            if (c != '.' && c != '_' && !Character.isLetterOrDigit(sqlConstrainString.charAt(endIndex)))
                break;

            endIndex++;
        }

        return endIndex;
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

    private String evaluateConstraint()
    {
        final StringBuilder stringBuilder = new StringBuilder();
        int numUsedReferences = 0;
        for (String evaluatedConstraintString : evaluatedConstraintStrings)
        {
            stringBuilder.append(evaluatedConstraintString);
            if (numUsedReferences < constraintReferences.size())
            {
                final Map.Entry<SymbolReference, String> referenceEntry =
                        constraintReferences.get(numUsedReferences);
                final SymbolReference symbolReference = referenceEntry.getKey();
                final AstNode referencedSymbol = symbolReference.getReferencedSymbol();

                // used to call evaluation explicitly because the symbol does not have to be evaluated yet
                // TODO[mikir] this might be implemented directly in expressions or in symbol reference
                final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();

                if (referencedSymbol instanceof Constant)
                {
                    final Constant referencedConstant = (Constant)referencedSymbol;
                    referencedConstant.accept(evaluator);

                    final BigInteger value = referencedConstant.getValueExpression().getIntegerValue();
                    if (value == null)
                        throw new ParserException(this, "Reference '" + referenceEntry.getValue() +
                                "' refers to non-integer constant!");

                    stringBuilder.append(value.toString());
                }
                else if (referencedSymbol instanceof EnumItem)
                {
                    // referenced type should be the EnumType
                    final ZserioType referencedEnumType = symbolReference.getReferencedType();
                    referencedEnumType.accept(evaluator);

                    stringBuilder.append(((EnumItem)referencedSymbol).getValue().toString());
                }
                else if (referencedSymbol instanceof BitmaskValue)
                {
                    // referenced type should be the BitmaskType
                    final ZserioType referencedBitmaskType = symbolReference.getReferencedType();
                    referencedBitmaskType.accept(evaluator);

                    stringBuilder.append(((BitmaskValue)referencedSymbol).getValue().toString());
                }
                else
                {
                    throw new ParserException(this, "Reference '" + referenceEntry.getValue() +
                            "' does refer to neither enumeration, bitmask nor constant!");
                }

                numUsedReferences++;
            }
        }

        return stringBuilder.toString();
    }

    private static Expression createStringLiteralExpression(Package pkg, String stringLiteral)
    {
        return new Expression(null, pkg, ZserioParser.STRING_LITERAL, stringLiteral,
                Expression.ExpressionFlag.NONE);
    }

    private static final String CONSTRAINT_REFERENCE_ESCAPE = "@";

    private static final String PRIMARY_KEY_TABLE_CONSTRAINT_REGEX = "PRIMARY\\s+KEY\\s*\\(([^\\)]+)\\)";
    private static final String UNIQUE_TABLE_CONSTRAINT_REGEX = "UNIQUE\\s*\\(([^\\)]+)\\)";
    private static final String PRIMARY_KEY_FIELD_CONSTRAINT_REGEX = "PRIMARY\\s+KEY";
    private static final String NOT_NULL_FIELD_CONSTRAINT_REGEX = "NOT\\s+NULL";
    
    private final Expression constraintExpr;

    private final List<String> evaluatedConstraintStrings = new ArrayList<String>();
    private final List<Map.Entry<SymbolReference, String>> constraintReferences =
            new ArrayList<Map.Entry<SymbolReference, String>>();
    private Package pkg;

    private Expression evaluatedConstraintExpr = null;

    private List<String> primaryKeyColumnNames = new ArrayList<String>();
    private List<String> uniqueColumnNames = new ArrayList<String>();

    private boolean isNullAllowed = false;
    private boolean isPrimaryKey = false;
}
