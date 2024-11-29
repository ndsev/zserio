package zserio.ast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zserio.antlr.ZserioParser;
import zserio.ast.Scope.FoundSymbol;

/**
 * AST node for expressions defined in the language.
 */
public final class Expression extends AstNodeBase
{
    /**
     * Defines expression flag for constructors.
     */
    public enum ExpressionFlag
    {
        /** no flag */
        NONE,
        /** the explicit keyword was before expression in the source */
        IS_EXPLICIT,
        /** the expression is top level dot operator */
        IS_TOP_LEVEL_DOT,
        /** the expression is identifier which is dot right operand */
        IS_DOT_RIGHT_OPERAND_ID,
        /** the expression is identifier which is dot left operand */
        IS_DOT_LEFT_OPERAND_ID
    }
    ;

    /**
     * Constructor.
     *
     * @param location       AST node location.
     * @param pkg            Package to which the expression belongs.
     * @param expressionType Expression grammar token type.
     * @param expressionText Expression grammar token text.
     * @param expressionFlag Flag for the expression.
     */
    public Expression(AstLocation location, Package pkg, int expressionType, String expressionText,
            ExpressionFlag expressionFlag)
    {
        this(location, pkg, expressionType, expressionText, expressionFlag, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param location       AST node location.
     * @param pkg            Package to which the expression belongs.
     * @param expressionType Expression grammar token type.
     * @param expressionText Expression grammar token text.
     * @param expressionFlag Flag for the expression.
     * @param operand1       Operand of the expression.
     */
    public Expression(AstLocation location, Package pkg, int expressionType, String expressionText,
            ExpressionFlag expressionFlag, Expression operand1)
    {
        this(location, pkg, expressionType, expressionText, expressionFlag, operand1, null, null);
    }

    /**
     * Constructor.
     *
     * @param location       AST node location.
     * @param pkg            Package to which the expression belongs.
     * @param expressionType Expression grammar token type.
     * @param expressionText Expression grammar token text.
     * @param expressionFlag Flag for the expression.
     * @param operand1       Left operand of the expression.
     * @param operand2       Right operand of the expression.
     */
    public Expression(AstLocation location, Package pkg, int expressionType, String expressionText,
            ExpressionFlag expressionFlag, Expression operand1, Expression operand2)
    {
        this(location, pkg, expressionType, expressionText, expressionFlag, operand1, operand2, null);
    }

    /**
     * Constructor.
     *
     * @param location       AST node location.
     * @param pkg            Package to which the expression belongs.
     * @param expressionType Expression grammar token type.
     * @param expressionText Expression grammar token text.
     * @param expressionFlag Flag for the expression.
     * @param operand1       Left operand of the expression.
     * @param operand2       Middle operand of the expression.
     * @param operand3       Right operand of the expression.
     */
    public Expression(AstLocation location, Package pkg, int expressionType, String expressionText,
            ExpressionFlag expressionFlag, Expression operand1, Expression operand2, Expression operand3)
    {
        super(location);

        this.pkg = pkg;
        type = expressionType;
        text = stripExpressionText(expressionType, expressionText);
        this.expressionFlag = expressionFlag;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;

        initialize();
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitExpression(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (operand1 != null)
        {
            operand1.accept(visitor);
            if (operand2 != null)
            {
                operand2.accept(visitor);
                if (operand3 != null)
                    operand3.accept(visitor);
            }
        }
    }

    /**
     * Gets the expression type given by the parser.
     *
     * This method should not be public but it is used by expression formatters at the moment.
     *
     * @return Expression type given by grammar.
     */
    public int getType()
    {
        return type;
    }

    /**
     * Gets the expression text given by the parser.
     *
     * This method should not be public but it is used by expression formatters at the moment.
     *
     * @return Expression text given by grammar.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Gets the first operand for the expression.
     *
     * This method should not be public but it is used by expression formatters at the moment.
     *
     * @return Returns the first operand.
     */
    public Expression op1()
    {
        return operand1;
    }

    /**
     * Gets the second operand for the expression.
     *
     * This method should not be public but it is used by expression formatters at the moment.
     *
     * @return Returns the second operand.
     */
    public Expression op2()
    {
        return operand2;
    }

    /**
     * Gets the third operand for the expression.
     *
     * This method should not be public but it is used by expression formatters at the moment.
     *
     * @return Returns the third operand.
     */
    public Expression op3()
    {
        return operand3;
    }

    /**
     * Checks if the expression contains explicit variable.
     *
     * @return Returns true if expression is explicit otherwise false.
     */
    public boolean isExplicitVariable()
    {
        return expressionFlag == ExpressionFlag.IS_EXPLICIT;
    }

    /**
     * Checks if the expression is most left identifier.
     *
     * @return Returns true if expression is not explicit and is dot left operand or single identifier.
     */
    public boolean isMostLeftId()
    {
        return (!isExplicitVariable() && type == ZserioParser.ID &&
                (expressionFlag == ExpressionFlag.IS_DOT_LEFT_OPERAND_ID ||
                        expressionFlag != ExpressionFlag.IS_DOT_RIGHT_OPERAND_ID));
    }

    /**
     * Defines evaluated type of the expression.
     */
    public enum ExpressionType
    {
        /** Unknown expression. Used during evaluation only. Method getExprType() never returns this value. */
        UNKNOWN,

        /**
         * Integer expression. Result of expression can be read using getIntegerValue(). Actually, integer
         * result is needed to evaluate const types, length of bit field types and enumeration item values.
         */
        INTEGER,

        /** Float expression. Result of expression is not available. */
        FLOAT,

        /** String expression. */
        STRING,

        /** Boolean expression. */
        BOOLEAN,

        /** Expression which result is enumeration type. */
        ENUM,

        /** Expression which result is bitmask type. */
        BITMASK,

        /** Expression which result is compound type. */
        COMPOUND,

        /** Expression which result is extern type. */
        EXTERN,

        /** Expression which result is bytes type. */
        BYTES
    }
    ;

    /**
     * Gets the evaluated type of the expression.
     *
     * @return Returns the type of the expression.
     */
    public ExpressionType getExprType()
    {
        return expressionType;
    }

    /**
     * Gets the evaluated Zserio type for the expression.
     *
     * @return Returns the Zserio type for the expression.
     */
    public ZserioType getExprZserioType()
    {
        return zserioType;
    }

    /**
     * Gets the evaluated identifier symbol object for the expression.
     *
     * @return Returns the identifier symbol object for the expression.
     */
    public AstNode getExprSymbolObject()
    {
        return symbolObject;
    }

    /**
     * Gets the owner of the expression.
     *
     * Owner is valid only for expressions which represent fields, parameters and functions.
     *
     * @return The owner of the expression.
     */
    public ZserioType getExprOwner()
    {
        return expressionOwner;
    }

    /**
     * Gets value for integer expression.
     *
     * @return Returns value for integer expression or null if expression is not integer or if value of integer
     *         expression is not possible to evaluate during compile time.
     */
    public BigInteger getIntegerValue()
    {
        return expressionIntegerValue.getValue();
    }

    /**
     * Gets value for string expression.
     *
     * @return Returns value for string expression or null if expression is not string or if value of string
     *         expression is not possible to evaluate during compile time.
     */
    public String getStringValue()
    {
        return expressionStringValue;
    }

    /**
     * Gets lower bound for integer expression.
     *
     * @return Returns lower bound for integer expression or null if expression is not integer or if lower bound
     *         of integer expression is not possible to evaluate during compile time.
     */
    public BigInteger getIntegerLowerBound()
    {
        return expressionIntegerValue.getLowerBound();
    }

    /**
     * Gets upper bound for integer expression.
     *
     * @return Returns upper bound for integer expression or null if expression is not integer or if upper bound
     *         of integer expression is not possible to evaluate during compile time.
     */
    public BigInteger getIntegerUpperBound()
    {
        return expressionIntegerValue.getUpperBound();
    }

    /**
     * Gets needs BigInteger flag.
     *
     * @return Returns true if the expression contains value which needs BigInteger type.
     */
    public boolean needsBigInteger()
    {
        return expressionIntegerValue.needsBigInteger();
    }

    /**
     * Gets needs BigInteger casting to native flag.
     *
     * @return Returns true if the expression contains value which needs BigInteger type but it is assigned
     *                 to the native type.
     */
    public boolean needsBigIntegerCastingToNative()
    {
        return needsBigIntegerCastingToNative;
    }

    /**
     * Gets all objects of given class referenced from the expression.
     *
     * @param <T> Type of the object which should be found.
     * @param clazz Class of which object should be found.
     *
     * @return Set of objects of given class referenced from the expression.
     */
    public <T extends AstNode> Set<T> getReferencedSymbolObjects(Class<? extends T> clazz)
    {
        // LinkedHashSet is used intentionally to guarantee insert order
        final Set<T> referencedSymbolObjects = new LinkedHashSet<T>();
        addReferencedSymbolObject(referencedSymbolObjects, clazz);

        return referencedSymbolObjects;
    }

    /**
     * Optional field information returned from the getReferencedOptionalFields() method.
     */
    public final static class OptionalFieldInfo
    {
        /**
         * Constructor.
         *
         * @param dotPrefix Dot prefix to construct from.
         * @param leftAndExprs Left 'and' expressions to construct from.
         */
        public OptionalFieldInfo(List<AstNode> dotPrefix, List<Expression> leftAndExprs)
        {
            this.dotPrefix = dotPrefix;
            this.leftAndExprs = leftAndExprs;
        }

        /**
         * Gets the dot prefix.
         *
         * The dot prefix is a list of AST nodes which correspond to the dot expression prefix
         * of corresponded optional Field object.
         *
         * Example:
         *
         * "compoundOuter.compoundInner.optionalField" will return AST node list
         * [compoundOuter, compoundInner] for optional field 'optionalField'.
         *
         * @return Dot prefix or empty list.
         */
        public List<AstNode> getDotPrefix()
        {
            return dotPrefix;
        }

        /**
         * Gets the left 'and' expressions.
         *
         * Example:
         *
         * "value == 0 'and' (hasOptionalField == true) 'and' optionalField == 0" will return expressions
         * ["value == 0 'and' (hasOptionalField == true)", "value == 0", (hasOptionalField == true)"]
         * for optional field 'optionalField'.
         *
         * @return Left 'and' expression or null.
         */
        public List<Expression> getLeftAndExprs()
        {
            return leftAndExprs;
        }

        private final List<AstNode> dotPrefix;
        private final List<Expression> leftAndExprs;
    }

    /**
     * Gets all optional Field objects referenced from the expression.
     *
     * Method returns as well additional information about each referenced optional Field. This info
     * is stored in OptionalFieldInfo object in map value.
     *
     * @return Map of optional Field objects referenced from the expression.
     */
    public Map<Field, OptionalFieldInfo> getReferencedOptionalFields()
    {
        // LinkedHashSet is used intentionally to guarantee insert order
        final Map<Field, OptionalFieldInfo> referencedOptionalFields =
                new LinkedHashMap<Field, OptionalFieldInfo>();
        addReferencedOptionalField(
                referencedOptionalFields, new ArrayList<AstNode>(), new ArrayList<Expression>());

        return referencedOptionalFields;
    }

    /**
     * Returns true if the expression requires the context of its owner.
     *
     * This is true if the expression references the compound type that contains the expression (i.e. a field,
     * a parameter) or if the expression contains compound function.
     *
     * @return Returns true if the expression needs reference to its owner.
     */
    public boolean requiresOwnerContext()
    {
        // check if expression contains a field
        if (!(getReferencedSymbolObjects(Field.class).isEmpty()))
            return true;

        // check if expression contains a parameter
        if (!(getReferencedSymbolObjects(Parameter.class).isEmpty()))
            return true;

        // check if expression contains a function type
        if (!(getReferencedSymbolObjects(Function.class).isEmpty()))
            return true;

        return false;
    }

    /**
     * Checks if expression contains token "index".
     *
     * @return Returns true if this expression contains token "index".
     */
    public boolean containsIndex()
    {
        return containsOperand(ZserioParser.INDEX);
    }

    /**
     * Checks if expression contains ternary operator 'a ? b : c'.
     *
     * @return Returns true if this expression contains ternary operator.
     */
    public boolean containsTernaryOperator()
    {
        return containsOperand(ZserioParser.QUESTIONMARK);
    }

    /**
     * Sets lexical scope for the expression evaluation.
     *
     * This method is called by ZserioAstScopeSetter.
     *
     * @param evaluationScope Lexical scope for evaluation to set.
     */
    void setEvaluationScope(Scope evaluationScope)
    {
        this.evaluationScope = evaluationScope;
    }

    /**
     * Adds additional lexical scope to the expression evaluation scope.
     *
     * @param additionalEvaluationScope Additional scope for evaluation to add.
     */
    void addEvaluationScope(Scope additionalEvalutionScope)
    {
        evaluationScope.add(additionalEvalutionScope);
    }

    /**
     * Evaluates the expression.
     */
    void evaluate()
    {
        evaluate(evaluationScope);
    }

    /**
     * This method evaluates one expression.
     *
     * It is supposed that the previous expression properties have been set to initialization values
     * (set by constructor).
     *
     * If given forced evaluation scope is different to expression evaluation scope, the method forces
     * evaluation even if the expression has been already evaluated (this is used for function called within
     * owner structure).
     *
     * @param forcedEvaluationScope Forced scope for evaluation.
     */
    void evaluate(Scope forcedEvaluationScope)
    {
        if (evaluationState == EvaluationState.IN_EVALUATION)
            throw new ParserException(this, "Cyclic dependency detected in expression evaluation!");

        // force evaluation if different scope is specified
        if (forcedEvaluationScope != evaluationScope && evaluationState != EvaluationState.NOT_EVALUATED)
            initialize();

        if (evaluationState == EvaluationState.NOT_EVALUATED)
        {
            evaluationState = EvaluationState.IN_EVALUATION;

            switch (type)
            {
            case ZserioParser.LPAREN: // parenthesizedExpression
                evaluateParenthesizedExpression();
                break;

            case ZserioParser.RPAREN: // functionCallExpression
                evaluateFunctionCallExpression(forcedEvaluationScope);
                break;

            case ZserioParser.LBRACKET: // arrayExpression
                evaluateArrayElement();
                break;

            case ZserioParser.DOT: // dotExpression
                evaluateDotExpression();
                break;

            case ZserioParser.ISSET:
                evaluateIsSetOperator(); // isSetExpression
                break;

            case ZserioParser.LENGTHOF:
                evaluateLengthOfOperator(); // lengthofExpression
                break;

            case ZserioParser.VALUEOF:
                evaluateValueOfOperator(); // valueofExpression
                break;

            case ZserioParser.NUMBITS:
                evaluateNumBitsOperator(); // numbitsExpression
                break;

            case ZserioParser.PLUS: // unaryExpression or additiveExpression
                if (operand2 == null)
                    evaluateUnaryPlusMinus(false);
                else
                    evaluateArithmeticExpression();
                break;

            case ZserioParser.MINUS: // unaryExpression or additiveExpression
                if (operand2 == null)
                    evaluateUnaryPlusMinus(true);
                else
                    evaluateArithmeticExpression();
                break;

            case ZserioParser.BANG: // unaryExpression
                evaluateNegationOperator();
                break;

            case ZserioParser.TILDE: // unaryExpression
                evaluateBitNotExpression();
                break;

            case ZserioParser.MULTIPLY: // multiplicativeExpression
            case ZserioParser.DIVIDE:
            case ZserioParser.MODULO:
                evaluateArithmeticExpression();
                break;

            case ZserioParser.LSHIFT: // shiftExpression
            case ZserioParser.RSHIFT:
            case ZserioParser.AND: // bitwiseAndExpression
            case ZserioParser.XOR: // bitwiseXorExpression
            case ZserioParser.OR: // bitwiseOrExpression
                evaluateBitExpression();
                break;

            case ZserioParser.LT: // relationalExpression
            case ZserioParser.LE:
            case ZserioParser.GT:
            case ZserioParser.GE:
            case ZserioParser.EQ: // equalityExpression
            case ZserioParser.NE:
                evaluateRelationalExpression();
                break;

            case ZserioParser.LOGICAL_AND: // logicalAndExpression
            case ZserioParser.LOGICAL_OR: // logicalOrExpression
                evaluateLogicalExpression();
                break;

            case ZserioParser.QUESTIONMARK: // ternaryExpression
                evaluateConditionalExpression();
                break;

            case ZserioParser.BINARY_LITERAL: // literalExpression
                expressionType = ExpressionType.INTEGER;
                final String binText = stripBinaryLiteral(getText());
                expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(binText, 2));
                break;

            case ZserioParser.OCTAL_LITERAL: // literalExpression
                expressionType = ExpressionType.INTEGER;
                expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText(), 8));
                break;

            case ZserioParser.DECIMAL_LITERAL: // literalExpression
                expressionType = ExpressionType.INTEGER;
                expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText()));
                break;

            case ZserioParser.HEXADECIMAL_LITERAL: // literalExpression
                expressionType = ExpressionType.INTEGER;
                final String hexText = stripHexadecimalLiteral(getText());
                expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(hexText, 16));
                break;

            case ZserioParser.BOOL_LITERAL: // literalExpression
                expressionType = ExpressionType.BOOLEAN;
                break;

            case ZserioParser.STRING_LITERAL: // literalExpression
                expressionType = ExpressionType.STRING;
                expressionStringValue = stripStringLiteral(getText());
                break;

            case ZserioParser.FLOAT_LITERAL: // literalExpression
            case ZserioParser.DOUBLE_LITERAL:
                expressionType = ExpressionType.FLOAT;
                break;

            case ZserioParser.INDEX: // indexExpression
                evaluateIndexExpression();
                break;

            case ZserioParser.ID: // identifierExpression
                evaluateIdentifier(forcedEvaluationScope);
                break;

            default:
                throw new ParserException(this, "Illegal expression type '" + type + "'!");
            }

            evaluationState = EvaluationState.EVALUATED;
        }
    }

    /**
     * This method propagates 'needs BigInteger' flag into already evaluated parts of expression.
     *
     * Method is necessary because Java expression formatter needs to have BigInteger flag set correctly for all
     * parts of expression.
     *
     * Example 1: 4 * 3 + uint64Value. Expression '4 * 3' will be evaluated before 'uint64Value'. Thus, literal
     * expressions '4' and '3' will not have set BigInteger flag. Such BigInteger flags can be set only
     * afterwards during evaluation of expression 'uint64Value'.
     *
     * Example 2: 4 * 3. Expression '4 * 3' will never have BigInteger flag set. Such BigInteger flags can be
     * set only after whole expression evaluation during checking of expression type in assignment. Therefore,
     * this method must be public.
     */
    void propagateNeedsBigInteger()
    {
        if (expressionType == ExpressionType.INTEGER && !expressionIntegerValue.needsBigInteger())
        {
            expressionIntegerValue = new ExpressionIntegerValue(expressionIntegerValue.getValue(),
                    expressionIntegerValue.getLowerBound(), expressionIntegerValue.getUpperBound(), true);

            if (operand1 != null)
            {
                operand1.propagateNeedsBigInteger();
                if (operand2 != null)
                {
                    operand2.propagateNeedsBigInteger();
                    if (operand3 != null)
                        operand3.propagateNeedsBigInteger();
                }
            }
        }
    }

    /**
     * Sets needs BigInteger casting flag.
     *
     * Method is necessary because Java expression formatter needs to know if expression which uses BigInteger
     * is assigned to the native type. In this case, casting to long native type is necessary.
     *
     * Example:
     *
     * CastUInt64ToUInt8Expression
     * {
     *     uint64  uint64Value;
     *
     *     function uint8 uint8Value()
     *     {
     *         return uint64Value;
     *     }
     * };
     */
    void setNeedsBigIntegerCastingNative()
    {
        needsBigIntegerCastingToNative = true;
    }

    /**
     * Instantiate the expression.
     *
     * @param templateParameters Template parameters.
     * @param templateArguments Template arguments.
     *
     * @return New expression instantiated from this using the given template arguments.
     */
    Expression instantiate(List<TemplateParameter> templateParameters, List<TemplateArgument> templateArguments)
    {
        if (operand1 == null)
        {
            String instantiatedText = text;
            if (isMostLeftId())
            {
                // expression is single ID or is left dot operand
                final int index = TemplateParameter.indexOf(templateParameters, text);
                if (index != -1)
                {
                    // template parameter has been found
                    final TypeReference templateArgumentReference =
                            templateArguments.get(index).getTypeReference();
                    final PackageName templateArgumentPackageName =
                            templateArgumentReference.getReferencedPackageName();
                    if (!templateArgumentPackageName.isEmpty())
                    {
                        // found template argument is type reference with specified package
                        return createInstantiationTree(templateArgumentPackageName,
                                templateArgumentReference.getType().getPackage().getTopLevelPackageName(),
                                templateArgumentReference.getReferencedTypeName());
                    }
                    instantiatedText = templateArgumentReference.getReferencedTypeName();
                }
            }

            return new Expression(getLocation(), pkg, type, instantiatedText, expressionFlag, null, null, null);
        }
        else
        {
            return new Expression(getLocation(), pkg, type, text, expressionFlag,
                    operand1.instantiate(templateParameters, templateArguments),
                    operand2 == null ? null : operand2.instantiate(templateParameters, templateArguments),
                    operand3 == null ? null : operand3.instantiate(templateParameters, templateArguments));
        }
    }

    private Expression createInstantiationTree(PackageName templateArgumentPackageName,
            PackageName topLevelPackageName, String templateArgumentName)
    {
        // strip top level package because all expressions are given without top level package
        final List<String> templateArgumentPackageIds = templateArgumentPackageName.getIdList();
        final List<String> topLevelPackageIds = topLevelPackageName.getIdList();
        final int firstIdIndex = topLevelPackageIds.size();
        if (firstIdIndex >= templateArgumentPackageIds.size())
            throw new InternalError("Mismatch of package name and top level package name!");

        final List<String> expressionIds = new ArrayList<String>();
        expressionIds.addAll(
                templateArgumentPackageIds.subList(firstIdIndex, templateArgumentPackageIds.size()));
        expressionIds.add(templateArgumentName);

        return createDotExpression(expressionIds);
    }

    private Expression createDotExpression(List<String> expressionIds)
    {
        Expression operand1 = null;
        if (!expressionIds.isEmpty())
        {
            operand1 = new Expression(getLocation(), pkg, ZserioParser.ID, expressionIds.get(0),
                    ExpressionFlag.IS_DOT_LEFT_OPERAND_ID, null, null, null);
            for (int i = 1; i < expressionIds.size(); i++)
            {
                final Expression operand2 = new Expression(getLocation(), pkg, ZserioParser.ID,
                        expressionIds.get(i), ExpressionFlag.IS_DOT_RIGHT_OPERAND_ID, null, null, null);
                final Expression dotOperand = new Expression(getLocation(), pkg, ZserioParser.DOT, ".",
                        (i + 1 == expressionIds.size()) ? ExpressionFlag.IS_TOP_LEVEL_DOT : ExpressionFlag.NONE,
                        operand1, operand2, null);
                operand1 = dotOperand;
            }
        }

        return operand1;
    }

    @SuppressWarnings("unchecked")
    private <T extends AstNode> void addReferencedSymbolObject(
            Set<T> referencedObjectList, Class<? extends AstNode> elementClass)
    {
        if (symbolObject != null && elementClass.isInstance(symbolObject))
            referencedObjectList.add((T)symbolObject);

        if (operand1 != null)
        {
            operand1.addReferencedSymbolObject(referencedObjectList, elementClass);
            if (operand2 != null)
            {
                operand2.addReferencedSymbolObject(referencedObjectList, elementClass);
                if (operand3 != null)
                    operand3.addReferencedSymbolObject(referencedObjectList, elementClass);
            }
        }
    }

    private void addReferencedOptionalField(Map<Field, OptionalFieldInfo> referencedOptionalFields,
            ArrayList<AstNode> currentDotPrefix, ArrayList<Expression> currentLeftAndExprs)
    {
        if (type == ZserioParser.ID)
        {
            // this is a leaf
            if (symbolObject != null)
            {
                if (symbolObject instanceof Field)
                {
                    final Field field = (Field)symbolObject;
                    if (field.isOptional())
                    {
                        final List<AstNode> currentDotPrefixCopy = new ArrayList<AstNode>(currentDotPrefix);
                        final List<Expression> currentLeftAndExprsCopy =
                                new ArrayList<Expression>(currentLeftAndExprs);
                        referencedOptionalFields.put(
                                field, new OptionalFieldInfo(currentDotPrefixCopy, currentLeftAndExprsCopy));
                    }
                }
                currentDotPrefix.add(symbolObject);
            }
        }

        if (operand1 != null)
        {
            if (type != ZserioParser.DOT)
                currentDotPrefix.clear();
            operand1.addReferencedOptionalField(
                    referencedOptionalFields, currentDotPrefix, currentLeftAndExprs);
            if (operand2 != null)
            {
                if (type != ZserioParser.DOT)
                {
                    currentDotPrefix.clear();
                    if (type == ZserioParser.LOGICAL_AND)
                        addReferencedLeftAndExpr(operand1, currentLeftAndExprs); // add all 'and' operands
                    else
                        currentLeftAndExprs.clear();
                }
                operand2.addReferencedOptionalField(
                        referencedOptionalFields, currentDotPrefix, currentLeftAndExprs);
                if (operand3 != null)
                {
                    currentDotPrefix.clear(); // dot operator is not ternary
                    currentLeftAndExprs.clear(); // and operator is not ternary
                    operand3.addReferencedOptionalField(
                            referencedOptionalFields, currentDotPrefix, currentLeftAndExprs);
                }
            }
        }
    }

    private void addReferencedLeftAndExpr(Expression expr, ArrayList<Expression> currentLeftAndExprs)
    {
        Expression currentExpr = expr;
        currentLeftAndExprs.add(currentExpr);
        while (currentExpr.type == ZserioParser.LPAREN)
            currentExpr = currentExpr.operand1;

        if (currentExpr.type == ZserioParser.LOGICAL_AND)
        {
            addReferencedLeftAndExpr(currentExpr.operand1, currentLeftAndExprs);
            addReferencedLeftAndExpr(currentExpr.operand2, currentLeftAndExprs);
        }
    }

    private boolean containsOperand(int operandTokenType)
    {
        if (type == operandTokenType)
            return true;

        if (operand1 != null)
        {
            if (operand1.containsOperand(operandTokenType))
                return true;

            if (operand2 != null)
            {
                if (operand2.containsOperand(operandTokenType))
                    return true;

                if (operand3 != null)
                    if (operand3.containsOperand(operandTokenType))
                        return true;
            }
        }

        return false;
    }

    private void evaluateParenthesizedExpression()
    {
        expressionType = operand1.expressionType;
        expressionIntegerValue = operand1.expressionIntegerValue;
        expressionStringValue = operand1.expressionStringValue;
        zserioType = operand1.zserioType;
        symbolObject = operand1.symbolObject;
        symbolInstantiation = operand1.symbolInstantiation;
        unresolvedIdentifiers = operand1.unresolvedIdentifiers;
    }

    private void evaluateFunctionCallExpression(Scope forcedEvaluationScope)
    {
        if (!(operand1.symbolObject instanceof Function))
            throw new ParserException(operand1, "'" + operand1.text + "' is not a function!");

        final Function function = (Function)operand1.symbolObject;
        final Expression functionResultExpression = function.getResultExpression();

        // function expression should know only symbols available on a place of the function call:
        // - if it's called within its owner object, it can see only symbols defined before the call
        try
        {
            ZserioAstEvaluator evaluator;
            if (forcedEvaluationScope.getOwner() == functionResultExpression.evaluationScope.getOwner())
                evaluator = new ZserioAstEvaluator(forcedEvaluationScope); // called within the owner
            else
                evaluator = new ZserioAstEvaluator(); // called externally from different compound type
            functionResultExpression.accept(evaluator);
        }
        catch (ParserException e)
        {
            final AstLocation location = getLocation();

            final ParserStackedException stackedException = new ParserStackedException(e);
            stackedException.pushMessage(location,
                    "    In function '" + function.getName() + "' "
                            + "called from here");
            throw stackedException;
        }

        evaluateExpressionType(function.getReturnTypeReference());
        expressionIntegerValue = new ExpressionIntegerValue(
                functionResultExpression.expressionIntegerValue.getValue(),
                functionResultExpression.expressionIntegerValue.getLowerBound(),
                functionResultExpression.expressionIntegerValue.getUpperBound(),
                expressionIntegerValue.needsBigInteger()); // needsBigInteger must be according to type
        expressionStringValue = functionResultExpression.expressionStringValue;
    }

    private void evaluateArrayElement()
    {
        if (!(operand1.symbolInstantiation instanceof ArrayInstantiation))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        if (operand2.expressionType != ExpressionType.INTEGER)
            throw new ParserException(operand2, "Integer expression expected!");

        final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)operand1.symbolInstantiation;
        evaluateExpressionType(arrayInstantiation.getElementTypeInstantiation());
    }

    private void evaluateDotExpression()
    {
        if (operand1.zserioType == null)
        {
            // left operand is unknown => it still can be a part of package
            evaluatePackageDotExpression();
        }
        else if (operand1.zserioType instanceof EnumType)
        {
            // left operand is enumeration type
            evaluateEnumDotExpression();
        }
        else if (operand1.zserioType instanceof BitmaskType)
        {
            // left operand is bitmask type
            evaluateBitmaskDotExpression();
        }
        else if (operand1.zserioType instanceof CompoundType)
        {
            // left operand is compound type
            evaluateCompoundDotExpression();
        }
        else
        {
            throw new ParserException(operand1, "Unexpected dot expression '" + operand1.text + "'!");
        }
    }

    private void evaluatePackageDotExpression()
    {
        // try to resolve operand1 as package name and operand2 as type
        final PackageName.Builder op1UnresolvedPackageNameBuilder = new PackageName.Builder();
        op1UnresolvedPackageNameBuilder.append(pkg.getTopLevelPackageName());
        for (Expression unresolvedIdentifier : operand1.unresolvedIdentifiers)
            op1UnresolvedPackageNameBuilder.addId(unresolvedIdentifier.text);

        final PackageSymbol identifierSymbol =
                pkg.getVisibleSymbol(this, op1UnresolvedPackageNameBuilder.get(), operand2.text);
        if (identifierSymbol != null)
        {
            // identifier found
            operand2.symbolObject = identifierSymbol;
            evaluateIdentifierPackageSymbol(identifierSymbol);

            // set symbolObject to all unresolved identifier expressions (needed for formatters)
            final Package identifierPackage = identifierSymbol.getPackage();
            for (Expression unresolvedIdentifier : operand1.unresolvedIdentifiers)
                unresolvedIdentifier.symbolObject = identifierPackage;
        }
        else
        {
            // identifier still not found
            if (expressionFlag == ExpressionFlag.IS_TOP_LEVEL_DOT)
            {
                // and we are top level dot
                throw new ParserException(this,
                        "Unresolved symbol '" + op1UnresolvedPackageNameBuilder.get().toString() +
                                "' within expression scope!");
            }

            // this can happened for long package name, we must wait for dot
            unresolvedIdentifiers.addAll(operand1.unresolvedIdentifiers);
            unresolvedIdentifiers.add(operand2);
        }
    }

    private void evaluateEnumDotExpression()
    {
        final EnumType enumType = (EnumType)(operand1.zserioType);
        final Scope enumScope = enumType.getScope();
        final String dotOperand = operand2.text;
        final FoundSymbol foundSymbol = enumScope.findSymbol(dotOperand);
        if (foundSymbol == null || !(foundSymbol.getSymbol() instanceof EnumItem))
        {
            throw new ParserException(
                    this, "'" + dotOperand + "' undefined in enumeration '" + enumType.getName() + "'!");
        }

        symbolObject = foundSymbol.getSymbol();
        operand2.symbolObject = symbolObject; // this is used by formatters (operand2 was not evaluated)
        evaluateExpressionType(enumType);
    }

    private void evaluateBitmaskDotExpression()
    {
        final BitmaskType bitmaskType = (BitmaskType)(operand1.zserioType);
        final Scope bitmaskScope = bitmaskType.getScope();
        final String dotOperand = operand2.text;
        final FoundSymbol foundSymbol = bitmaskScope.findSymbol(dotOperand);
        if (foundSymbol == null || !(foundSymbol.getSymbol() instanceof BitmaskValue))
        {
            throw new ParserException(
                    this, "'" + dotOperand + "' undefined in bitmask '" + bitmaskType.getName() + "'!");
        }

        symbolObject = foundSymbol.getSymbol();
        operand2.symbolObject = symbolObject; // this is used by formatters (operand2 was not evaluated)
        evaluateExpressionType(bitmaskType);
    }

    private void evaluateCompoundDotExpression()
    {
        final CompoundType compoundType = (CompoundType)(operand1.zserioType);
        final Scope compoundScope = compoundType.getScope();
        final String dotOperand = operand2.text;
        final FoundSymbol foundSymbol = compoundScope.findSymbol(dotOperand);
        if (foundSymbol == null)
            throw new ParserException(
                    this, "'" + dotOperand + "' undefined in compound '" + compoundType.getName() + "'!");

        symbolObject = foundSymbol.getSymbol();
        operand2.symbolObject = symbolObject; // this is used by formatter (operand2 was not evaluated)
        if (symbolObject instanceof Field)
        {
            evaluateExpressionType(((Field)symbolObject).getTypeInstantiation());
        }
        else if (symbolObject instanceof Parameter)
        {
            evaluateExpressionType(((Parameter)symbolObject).getTypeReference());
        }
        else if (symbolObject instanceof Function)
        {
            // function type, we must wait for "()"
        }
        else
        {
            throw new ParserException(
                    this, "'" + dotOperand + "' undefined in compound '" + compoundType.getName() + "'!");
        }
    }

    private void evaluateIsSetOperator()
    {
        if (operand1.expressionType != ExpressionType.BITMASK)
            throw new ParserException(operand1, "'" + operand1.text + "' is not a bitmask!");

        if (operand2.expressionType != ExpressionType.BITMASK)
            throw new ParserException(operand2, "'" + operand2.text + "' is not a bitmask!");

        if (operand1.zserioType != operand2.zserioType)
        {
            throw new ParserException(operand2,
                    "'" + operand2.zserioType.getName() + "' does not match to '" +
                            operand1.zserioType.getName() + "'!");
        }

        expressionType = ExpressionType.BOOLEAN;
    }

    private void evaluateLengthOfOperator()
    {
        expressionType = ExpressionType.INTEGER;

        if (operand1.symbolInstantiation instanceof ArrayInstantiation)
        {
            final Expression lengthExpr =
                    ((ArrayInstantiation)operand1.symbolInstantiation).getLengthExpression();
            if (lengthExpr != null && lengthExpr.getIntegerValue() != null)
            {
                expressionIntegerValue = new ExpressionIntegerValue(lengthExpr.getIntegerValue());
            }
            else
            {
                expressionIntegerValue = new ExpressionIntegerValue(
                        VarIntegerType.VARSIZE_LOWER_BOUND, VarIntegerType.VARSIZE_UPPER_BOUND);
            }
        }
        else if (operand1.expressionType == ExpressionType.STRING)
        {
            if (operand1.expressionStringValue != null)
            {
                expressionIntegerValue = new ExpressionIntegerValue(BigInteger.valueOf(
                        operand1.expressionStringValue.getBytes(StandardCharsets.UTF_8).length));
            }
            else
            {
                expressionIntegerValue = new ExpressionIntegerValue(
                        VarIntegerType.VARSIZE_LOWER_BOUND, VarIntegerType.VARSIZE_UPPER_BOUND);
            }
        }
        else if (operand1.expressionType == ExpressionType.BYTES)
        {
            expressionIntegerValue = new ExpressionIntegerValue(
                    VarIntegerType.VARSIZE_LOWER_BOUND, VarIntegerType.VARSIZE_UPPER_BOUND);
        }
        else
        {
            throw new ParserException(
                    operand1, "'" + operand1.text + "' is not supported by lengthof operator!");
        }
    }

    private void evaluateValueOfOperator()
    {
        if (operand1.expressionType != ExpressionType.ENUM && operand1.expressionType != ExpressionType.BITMASK)
            throw new ParserException(operand1, "'" + operand1.text + "' is not an enumeration or bitmask!");

        expressionType = ExpressionType.INTEGER;
        expressionIntegerValue = operand1.expressionIntegerValue;
    }

    private void evaluateNumBitsOperator()
    {
        if (operand1.expressionType != ExpressionType.INTEGER)
            throw new ParserException(operand1, "Integer expression expected!");

        expressionType = ExpressionType.INTEGER;
        expressionIntegerValue = operand1.expressionIntegerValue.numbits();
    }

    private void evaluateUnaryPlusMinus(boolean isNegate)
    {
        if (operand1.expressionType != ExpressionType.INTEGER &&
                operand1.expressionType != ExpressionType.FLOAT)
            throw new ParserException(this, "Integer or float expressions expected!");

        if (operand1.expressionType == ExpressionType.FLOAT)
        {
            expressionType = ExpressionType.FLOAT;
        }
        else
        {
            expressionType = ExpressionType.INTEGER;
            expressionIntegerValue =
                    (isNegate) ? operand1.expressionIntegerValue.negate() : operand1.expressionIntegerValue;
        }
    }

    private void evaluateNegationOperator()
    {
        final Expression op1 = op1();
        if (op1.expressionType != ExpressionType.BOOLEAN)
            throw new ParserException(this, "Boolean expression expected!");

        expressionType = ExpressionType.BOOLEAN;
    }

    private void evaluateBitNotExpression()
    {
        final Expression op1 = op1();
        if (op1.expressionType != ExpressionType.INTEGER && op1.expressionType != ExpressionType.BITMASK)
            throw new ParserException(this, "Integer or bitmask expression expected!");

        expressionType = op1.expressionType;
        expressionIntegerValue = op1.expressionIntegerValue.not();
        zserioType = op1.zserioType;
    }

    private void evaluateArithmeticExpression()
    {
        if (type == ZserioParser.PLUS && operand1.expressionType == ExpressionType.STRING &&
                operand2.expressionType == ExpressionType.STRING)
        {
            expressionType = ExpressionType.STRING;

            if (operand1.expressionStringValue == null || operand2.expressionStringValue == null)
                throw new ParserException(this, "Constant string expressions expected!");

            expressionStringValue = operand1.expressionStringValue + operand2.expressionStringValue;
        }
        else
        {
            if ((operand1.expressionType != ExpressionType.INTEGER &&
                        operand1.expressionType != ExpressionType.FLOAT) ||
                    (operand2.expressionType != ExpressionType.INTEGER &&
                            operand2.expressionType != ExpressionType.FLOAT))
            {
                throw new ParserException(this, "Integer or float expressions expected!");
            }

            if (operand1.expressionType == ExpressionType.FLOAT ||
                    operand2.expressionType == ExpressionType.FLOAT)
            {
                expressionType = ExpressionType.FLOAT;
            }
            else
            {
                expressionType = ExpressionType.INTEGER;
                switch (getType())
                {
                case ZserioParser.PLUS:
                    expressionIntegerValue =
                            operand1.expressionIntegerValue.add(operand2.expressionIntegerValue);
                    break;

                case ZserioParser.MINUS:
                    expressionIntegerValue =
                            operand1.expressionIntegerValue.subtract(operand2.expressionIntegerValue);
                    break;

                case ZserioParser.MULTIPLY:
                    expressionIntegerValue =
                            operand1.expressionIntegerValue.multiply(operand2.expressionIntegerValue);
                    break;

                case ZserioParser.DIVIDE:
                    expressionIntegerValue =
                            operand1.expressionIntegerValue.divide(operand2.expressionIntegerValue);
                    break;

                case ZserioParser.MODULO:
                    expressionIntegerValue =
                            operand1.expressionIntegerValue.remainder(operand2.expressionIntegerValue);
                    break;

                default:
                    throw new ParserException(this, "Illegal expression type " + type + "!");
                }

                if (expressionIntegerValue.needsBigInteger())
                {
                    operand1.propagateNeedsBigInteger();
                    operand2.propagateNeedsBigInteger();
                }
            }
        }
    }

    private void evaluateBitExpression()
    {
        expressionType = operand1.expressionType;

        if (operand1.expressionType != operand2.expressionType ||
                (expressionType != ExpressionType.INTEGER && expressionType != ExpressionType.BITMASK))
            throw new ParserException(this, "Integer or bitmask expressions expected!");

        if ((type == ZserioParser.LSHIFT || type == ZserioParser.RSHIFT) &&
                expressionType != ExpressionType.INTEGER)
            throw new ParserException(this, "Integer expressions expected!");

        switch (type)
        {
        case ZserioParser.LSHIFT:
            expressionIntegerValue = operand1.expressionIntegerValue.shiftLeft(operand2.expressionIntegerValue);
            break;

        case ZserioParser.RSHIFT:
            expressionIntegerValue =
                    operand1.expressionIntegerValue.shiftRight(operand2.expressionIntegerValue);
            break;

        case ZserioParser.AND:
            expressionIntegerValue = operand1.expressionIntegerValue.and(operand2.expressionIntegerValue);
            break;

        case ZserioParser.OR:
            expressionIntegerValue = operand1.expressionIntegerValue.or(operand2.expressionIntegerValue);
            break;

        case ZserioParser.XOR:
            expressionIntegerValue = operand1.expressionIntegerValue.xor(operand2.expressionIntegerValue);
            break;

        default:
            throw new ParserException(this, "Illegal expression type '" + type + "'!");
        }

        if (type != ZserioParser.LSHIFT && type != ZserioParser.RSHIFT &&
                expressionIntegerValue.needsBigInteger())
        {
            operand1.propagateNeedsBigInteger();
            operand2.propagateNeedsBigInteger();
        }

        // needed for bitmask expressions
        zserioType = operand1.zserioType;
    }

    private void evaluateRelationalExpression()
    {
        if (operand1.expressionType == ExpressionType.UNKNOWN ||
                operand1.expressionType != operand2.expressionType)
            throw new ParserException(this,
                    "Incompatible expression types (" + operand1.expressionType +
                            " != " + operand2.expressionType + ")!");

        if (operand1.expressionType == ExpressionType.FLOAT && type != ZserioParser.LT &&
                type != ZserioParser.GT)
            throw new ParserException(this, "Equality operator is not allowed for floats!");

        if (operand1.expressionType == ExpressionType.STRING && type != ZserioParser.EQ &&
                type != ZserioParser.NE)
            throw new ParserException(
                    this, "'Greater than' and 'less than' comparison is not allowed for strings!");

        if (operand1.expressionType == ExpressionType.STRING)
            throw new ParserException(this, "String comparison is not implemented!");

        expressionType = ExpressionType.BOOLEAN;
        if (operand1.expressionType == ExpressionType.INTEGER)
        {
            expressionIntegerValue =
                    operand1.expressionIntegerValue.relationalOperator(operand2.expressionIntegerValue);
            if (expressionIntegerValue.needsBigInteger())
            {
                operand1.propagateNeedsBigInteger();
                operand2.propagateNeedsBigInteger();
            }
        }
    }

    private void evaluateLogicalExpression()
    {
        if (operand1.expressionType != ExpressionType.BOOLEAN ||
                operand2.expressionType != ExpressionType.BOOLEAN)
            throw new ParserException(this, "Boolean expressions expected!");

        expressionType = ExpressionType.BOOLEAN;
    }

    private void evaluateConditionalExpression()
    {
        if (operand1.expressionType != ExpressionType.BOOLEAN)
            throw new ParserException(operand1, "Boolean expression expected!");

        if (operand2.expressionType == ExpressionType.UNKNOWN ||
                operand2.expressionType != operand3.expressionType)
            throw new ParserException(this,
                    "Incompatible expression types (" + operand2.expressionType +
                            " != " + operand3.expressionType + ")!");

        if (operand2.expressionType == ExpressionType.COMPOUND ||
                operand2.expressionType == ExpressionType.BITMASK ||
                operand2.expressionType == ExpressionType.ENUM)
        {
            if (operand2.zserioType != operand3.zserioType)
            {
                throw new ParserException(this,
                        "Incompatible expression types ('" + operand2.zserioType.getName() + "' != '" +
                                operand3.zserioType.getName() + "')!");
            }
        }

        expressionType = operand2.expressionType;
        zserioType = operand2.zserioType;
        if (expressionType == ExpressionType.INTEGER)
        {
            expressionIntegerValue =
                    operand2.expressionIntegerValue.conditional(operand3.expressionIntegerValue);
            if (expressionIntegerValue.needsBigInteger())
            {
                operand2.propagateNeedsBigInteger();
                operand3.propagateNeedsBigInteger();
            }
        }
    }

    private void evaluateIndexExpression()
    {
        expressionType = ExpressionType.INTEGER;
        // array index has default expressionIntegerValue
    }

    private void evaluateIdentifier(Scope forcedEvaluationScope)
    {
        // identifier on right side of dot operator cannot be evaluated because the identifier without left
        // side (package) can be found wrongly in local scope
        if (expressionFlag != ExpressionFlag.IS_DOT_RIGHT_OPERAND_ID)
        {
            // explicit identifier does not have to be evaluated
            if (expressionFlag != ExpressionFlag.IS_EXPLICIT)
            {
                final FoundSymbol identifierFoundSymbol = forcedEvaluationScope.findSymbol(text);
                if (identifierFoundSymbol != null)
                {
                    // scope symbol
                    evaluateIdentifierScopeSymbol(identifierFoundSymbol);
                }
                else
                {
                    final PackageSymbol identifierPackageSymbol =
                            pkg.getVisibleSymbol(this, PackageName.EMPTY, text);
                    if (identifierPackageSymbol != null)
                    {
                        // package symbol
                        evaluateIdentifierPackageSymbol(identifierPackageSymbol);
                    }
                    else
                    {
                        // identifier not found
                        if (expressionFlag != ExpressionFlag.IS_DOT_LEFT_OPERAND_ID)
                        {
                            // and expression is not in dot expression
                            throw new ParserException(
                                    this, "Unresolved symbol '" + text + "' within expression scope!");
                        }

                        // this can happened for a long package name, we must wait for dot
                        unresolvedIdentifiers.add(this);
                    }
                }
            }
        }
    }

    private void evaluateIdentifierScopeSymbol(FoundSymbol identifierFoundSymbol)
    {
        final ScopeSymbol identifierSymbol = identifierFoundSymbol.getSymbol();
        final ZserioType identifierSymbolOwner = identifierFoundSymbol.getOwner();

        symbolObject = identifierSymbol;
        if (identifierSymbol instanceof Field)
        {
            evaluateExpressionType(((Field)identifierSymbol).getTypeInstantiation());
            expressionOwner = identifierSymbolOwner;
        }
        else if (identifierSymbol instanceof Parameter)
        {
            evaluateExpressionType(((Parameter)identifierSymbol).getTypeReference());
            expressionOwner = identifierSymbolOwner;
        }
        else if (identifierSymbol instanceof Function)
        {
            // function type, we must wait for "()"
            expressionOwner = identifierSymbolOwner;
        }
        else if (identifierSymbol instanceof EnumItem && identifierSymbolOwner instanceof EnumType)
        {
            // this can happen for enums when they are resolved from additional scopes - e.g. in enum choice
            evaluateExpressionType((EnumType)identifierSymbolOwner);
        }
        else if (identifierSymbol instanceof BitmaskValue && identifierSymbolOwner instanceof BitmaskType)
        {
            // this can happen for bitmasks when they are resolved from additional scopes
            // e.g. in bitmask choice or within the isset operator
            evaluateExpressionType((BitmaskType)identifierSymbolOwner);
        }
        else
        {
            throw new ParserException(this,
                    "Symbol '" + identifierSymbol.getName() + "' (" + identifierSymbol.getClass() +
                            ") is not allowed here!");
        }
    }

    private void evaluateIdentifierPackageSymbol(PackageSymbol identifierSymbol)
    {
        symbolObject = identifierSymbol;
        if (identifierSymbol instanceof Constant)
        {
            evaluateConstant((Constant)identifierSymbol);
        }
        else if (identifierSymbol instanceof ZserioType)
        {
            final ZserioType baseType = getBaseType((ZserioType)identifierSymbol);
            if (baseType instanceof EnumType || baseType instanceof BitmaskType)
            {
                // enumeration or bitmask type, we must wait for enumeration item or bitmask value and dot
                zserioType = baseType;
            }
            else
            {
                throw new ParserException(this,
                        "Type '" + baseType.getName() + "' (" + baseType.getClass() + ") is not allowed here!");
            }
        }
        else
        {
            throw new ParserException(this,
                    "Symbol '" + identifierSymbol.getName() + "' (" + identifierSymbol.getClass() +
                            ") is not allowed here!");
        }
    }

    private void evaluateConstant(Constant constant)
    {
        // constant type
        evaluateExpressionType(constant.getTypeInstantiation());

        // call evaluation explicitly because this const does not have to be evaluated yet
        final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
        constant.accept(evaluator);

        final Expression constValueExpression = constant.getValueExpression();
        expressionIntegerValue = constValueExpression.expressionIntegerValue;
        expressionStringValue = constValueExpression.expressionStringValue;
    }

    private void evaluateExpressionType(TypeInstantiation typeInstantiation)
    {
        // call evaluation explicitly because this type instantiation does not have to be evaluated yet
        final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
        typeInstantiation.accept(evaluator);
        symbolInstantiation = typeInstantiation;
        evaluateExpressionType(typeInstantiation.getType());
    }

    private void evaluateExpressionType(TypeReference typeReference)
    {
        evaluateExpressionType(typeReference.getType());
    }

    private void evaluateExpressionType(ZserioType type)
    {
        final ZserioType baseType = getBaseType(type);
        if (baseType instanceof EnumType)
        {
            expressionType = ExpressionType.ENUM;
            if (symbolObject instanceof EnumItem)
            {
                // call evaluation explicitly because this enumeration item does not have to be evaluated yet
                final EnumItem enumItem = (EnumItem)symbolObject;
                final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
                ((EnumType)baseType).accept(evaluator);

                // set integer value according to this enumeration item
                expressionIntegerValue = new ExpressionIntegerValue(enumItem.getValue());
            }
        }
        else if (baseType instanceof BitmaskType)
        {
            expressionType = ExpressionType.BITMASK;
            if (symbolObject instanceof BitmaskValue)
            {
                // call evaluation explicitly because this bitmask value does not have to be evaluated yet
                final BitmaskValue bitmaskValue = (BitmaskValue)symbolObject;
                final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
                ((BitmaskType)baseType).accept(evaluator);

                // set integer value according to this bitmask value
                expressionIntegerValue = new ExpressionIntegerValue(bitmaskValue.getValue());
            }
        }
        else if (baseType instanceof IntegerType)
        {
            expressionType = ExpressionType.INTEGER;
            final IntegerType integerType = (IntegerType)baseType;
            if (symbolInstantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)symbolInstantiation;

                // call evaluation explicitly because this length does not have to be evaluated yet
                final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
                dynamicBitFieldInstantiation.accept(evaluator);
                final BigInteger lowerBound = dynamicBitFieldInstantiation.getLowerBound();
                final BigInteger upperBound = dynamicBitFieldInstantiation.getUpperBound();
                expressionIntegerValue = new ExpressionIntegerValue(lowerBound, upperBound);
            }
            else
            {
                // unknown instantiation, get the type's fixed limits
                final BigInteger lowerBound = integerType.getLowerBound();
                final BigInteger upperBound = integerType.getUpperBound();
                expressionIntegerValue = new ExpressionIntegerValue(lowerBound, upperBound);
            }
        }
        else if (baseType instanceof FloatType)
        {
            expressionType = ExpressionType.FLOAT;
        }
        else if (baseType instanceof StringType)
        {
            expressionType = ExpressionType.STRING;
        }
        else if (baseType instanceof BooleanType)
        {
            expressionType = ExpressionType.BOOLEAN;
        }
        else if (baseType instanceof CompoundType)
        {
            expressionType = ExpressionType.COMPOUND;
        }
        else if (baseType instanceof ExternType)
        {
            expressionType = ExpressionType.EXTERN;
        }
        else if (baseType instanceof BytesType)
        {
            expressionType = ExpressionType.BYTES;
        }
        else
        {
            expressionType = ExpressionType.UNKNOWN;
        }

        zserioType = baseType;
    }

    private ZserioType getBaseType(ZserioType type)
    {
        if (type instanceof Subtype)
            return ((Subtype)type).getBaseTypeReference().getType();
        else if (type instanceof InstantiateType)
            return ((InstantiateType)type).getTypeReference().getType();
        else
            return type;
    }

    private void initialize()
    {
        evaluationState = EvaluationState.NOT_EVALUATED;
        expressionType = ExpressionType.UNKNOWN;
        expressionIntegerValue = new ExpressionIntegerValue();
        expressionStringValue = null;
        zserioType = null;
        symbolObject = null;
        symbolInstantiation = null;
        unresolvedIdentifiers = new ArrayList<Expression>();
        needsBigIntegerCastingToNative = false;
    }

    private enum EvaluationState
    {
        NOT_EVALUATED,
        IN_EVALUATION,
        EVALUATED
    }
    ;

    private static String stripExpressionText(int expressionType, String expressionText)
    {
        switch (expressionType)
        {
        case ZserioParser.BINARY_LITERAL:
            expressionText = stripBinaryLiteral(expressionText);
            break;

        case ZserioParser.OCTAL_LITERAL:
            expressionText = stripOctalLiteral(expressionText);
            break;

        case ZserioParser.HEXADECIMAL_LITERAL:
            expressionText = stripHexadecimalLiteral(expressionText);
            break;

        case ZserioParser.FLOAT_LITERAL:
            expressionText = stripFloatLiteral(expressionText);
            break;

        default:
            break;
        }

        return expressionText;
    }

    private static String stripBinaryLiteral(String binaryLiteral)
    {
        final int postfixPos = findChars(binaryLiteral, 'b', 'B');

        return (postfixPos == -1) ? binaryLiteral : binaryLiteral.substring(0, postfixPos);
    }

    private static String stripOctalLiteral(String octalLiteral)
    {
        final int prefixPos = octalLiteral.indexOf('0');

        return (prefixPos == -1) ? octalLiteral : octalLiteral.substring(prefixPos + 1);
    }

    private static String stripHexadecimalLiteral(String hexadecimalLiteral)
    {
        final int prefixPos = findChars(hexadecimalLiteral, 'x', 'X');

        return (prefixPos == -1) ? hexadecimalLiteral : hexadecimalLiteral.substring(prefixPos + 1);
    }

    private static String stripFloatLiteral(String floatLiteral)
    {
        final int postfixPos = findChars(floatLiteral, 'f', 'F');

        return (postfixPos == -1) ? floatLiteral : floatLiteral.substring(0, postfixPos);
    }

    private static String stripStringLiteral(String stringLiteral)
    {
        final int prefixPos = stringLiteral.indexOf('"');
        final String strippedStringLiteral =
                (prefixPos == -1) ? stringLiteral : stringLiteral.substring(prefixPos + 1);

        final int postfixPos = strippedStringLiteral.lastIndexOf('"');

        return (postfixPos == -1) ? strippedStringLiteral : strippedStringLiteral.substring(0, postfixPos);
    }

    private static int findChars(String text, char firstChar, char secondChar)
    {
        int pos = text.indexOf(firstChar);
        if (pos == -1)
            pos = text.indexOf(secondChar);

        return pos;
    }

    private final Package pkg;

    private final int type;
    private final String text;

    private final ExpressionFlag expressionFlag;

    private final Expression operand1;
    private final Expression operand2;
    private final Expression operand3;

    private Scope evaluationScope;

    private EvaluationState evaluationState;

    private ExpressionType expressionType;
    private ExpressionIntegerValue expressionIntegerValue;
    private String expressionStringValue;
    private ZserioType zserioType;
    private AstNode symbolObject;
    private TypeInstantiation symbolInstantiation;
    private ZserioType expressionOwner;
    private List<Expression> unresolvedIdentifiers;

    private boolean needsBigIntegerCastingToNative;
}
