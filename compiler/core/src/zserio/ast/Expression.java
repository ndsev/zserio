package zserio.ast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.antlr.ZserioParser;
import zserio.antlr.util.ParserException;

/**
 * AST node for expressions defined in the language.
 */
public class Expression extends AstNodeBase
{
    /**
     * Defines expression flag for constructors.
     */
    public enum ExpressionFlag
    {
        NONE,                       /** no flag */
        IS_EXPLICIT,                /** the explicit keyword was before expression in the source */
        IS_TOP_LEVEL_DOT,           /** the expression is top level dot operator */
        IS_DOT_RIGHT_OPERAND_ID,    /** the expression is identifier which is dot right operand */
        IS_DOT_LEFT_OPERAND_ID      /** the expression is identifier which is dot left operand */
    };

    /**
     * Constructor.
     *
     * @param expressionToken Token to construct expression from.
     * @param pkg             Package to which the expression belongs.
     */
    public Expression(Token expressionToken, Package pkg)
    {
        this(expressionToken, pkg, expressionToken, ExpressionFlag.NONE, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param expressionToken Token to construct expression from.
     * @param pkg             Package to which the expression belongs.
     * @param expressionFlag  Flag for the expression.
     */
    public Expression(Token expressionToken, Package pkg, ExpressionFlag expressionFlag)
    {
        this(expressionToken, pkg, expressionToken, expressionFlag, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param locationToken   Token which denotes expression location in the sources.
     * @param pkg             Package to which the expression belongs.
     * @param expressionToken Token to construct expression from.
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken)
    {
        this(locationToken, pkg, expressionToken, ExpressionFlag.NONE, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param locationToken   Token which denotes expression location in the sources.
     * @param pkg             Package to which the expression belongs.
     * @param expressionToken Token to construct expression from.
     * @param expressionFlag  Flag for the expression.
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, ExpressionFlag expressionFlag)
    {
        this(locationToken, pkg, expressionToken, expressionFlag, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param locationToken   Token which denotes expression location in the sources.
     * @param pkg             Package to which the expression belongs.
     * @param expressionToken Token to construct expression from.
     * @param operand1        Left operand of the expression.
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, Expression operand1)
    {
        this(locationToken, pkg, expressionToken, ExpressionFlag.NONE, operand1, null, null);
    }

    /**
     * Constructor.
     *
     * @param locationToken   Token which denotes expression location in the sources.
     * @param pkg             Package to which the expression belongs.
     * @param expressionToken Token to construct expression from.
     * @param operand1        Left operand of the expression.
     * @param operand2        Right operand of the expression.
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, Expression operand1,
            Expression operand2)
    {
        this(locationToken, pkg, expressionToken, ExpressionFlag.NONE, operand1, operand2, null);
    }

    /**
     * Constructor.
     *
     * @param locationToken   Token which denotes expression location in the sources.
     * @param pkg             Package to which the expression belongs.
     * @param expressionToken Token to construct expression from.
     * @param expressionFlag  Flag for the expression.
     * @param operand1        Left operand of the expression.
     * @param operand2        Right operand of the expression.
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, ExpressionFlag expressionFlag,
            Expression operand1, Expression operand2)
    {
        this(locationToken, pkg, expressionToken, expressionFlag, operand1, operand2, null);
    }

    /**
     * Constructor.
     *
     * @param locationToken   Token which denotes expression location in the sources.
     * @param pkg             Package to which the expression belongs.
     * @param expressionToken Token to construct expression from.
     * @param operand1        Left operand of the ternary expression.
     * @param operand2        Middle operand of the ternary expression.
     * @param operand3        Right operand of the ternary expression.
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, Expression operand1,
            Expression operand2, Expression operand3)
    {
        this(locationToken, pkg, expressionToken, ExpressionFlag.NONE, operand1, operand2, operand3);
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

    @Override
    public String toString()
    {
        final StringBuilder stringBuilder = new StringBuilder(text);
        if (operand1 != null)
        {
            stringBuilder.append(operand1.toString());
            if (operand2 != null)
            {
                stringBuilder.append(operand2.toString());
                if (operand3 != null)
                    stringBuilder.append(operand3.toString());
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Gets the expression type given by the parser.
     *
     * This method should not be public but it is used by expression formatters at the moment.
     *
     * @return Expression type given by ANTLR4.
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
     * @return Expression text given by ANTLR4.
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

        /** Expression which result is compound type. */
        COMPOUND
    };

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
    public Object getExprSymbolObject()
    {
        return symbolObject;
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
     * @param clazz Class of which objects should be found.
     *
     * @return Set of objects of given class referenced from the expression.
     */
    public <T extends Object> Set<T> getReferencedSymbolObjects(Class<? extends T> clazz)
    {
        final Set<T> referencedSymbolObjects = new HashSet<T>();
        addReferencedSymbolObject(referencedSymbolObjects, clazz);

        return referencedSymbolObjects;
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
        if (!(getReferencedSymbolObjects(FunctionType.class).isEmpty()))
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
     * Checks if expression contains function call.
     *
     * @return Returns true if this expression contains function call.
     */
    public boolean containsFunctionCall()
    {
        return containsOperand(ZserioParser.RPAREN);
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
     * Method fills up the following expression properties:
     *
     *   expressionType
     *   zserioType
     *   expressionIntegerValue
     *   unresolvedIdentifiers
     *   symbolObject
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
                case ZserioParser.LPAREN:              // parenthesizedExpression
                    evaluateParenthesizedExpression();
                    break;

                case ZserioParser.RPAREN:              // functionCallExpression
                    evaluateFunctionCallExpression(forcedEvaluationScope);
                    break;

                case ZserioParser.LBRACKET:            // arrayExpression
                    evaluateArrayElement();
                    break;

                case ZserioParser.DOT:                 // dotExpression
                    evaluateDotExpression();
                    break;

                case ZserioParser.LENGTHOF:
                    evaluateLengthOfOperator();         // lengthofExpression
                    break;

                case ZserioParser.SUM:
                    evaluateSumOperator();              // sumExpression
                    break;

                case ZserioParser.VALUEOF:
                    evaluateValueOfOperator();          // valueofExpression
                    break;

                case ZserioParser.NUMBITS:
                    evaluateNumBitsOperator();          // numbitsExpression
                    break;

                case ZserioParser.PLUS:                // unaryExpression or additiveExpression
                    if (operand2 == null)
                        evaluateUnaryPlusMinus(false);
                    else
                        evaluateArithmeticExpression();
                    break;

                case ZserioParser.MINUS:               // unaryExpression or additiveExpression
                    if (operand2 == null)
                        evaluateUnaryPlusMinus(true);
                    else
                        evaluateArithmeticExpression();
                    break;

                case ZserioParser.BANG:                // unaryExpression
                    evaluateNegationOperator();
                    break;

                case ZserioParser.TILDE:               // unaryExpression
                    evaluateBitNotExpression();
                    break;

                case ZserioParser.MULTIPLY:            // multiplicativeExpression
                case ZserioParser.DIVIDE:
                case ZserioParser.MODULO:
                    evaluateArithmeticExpression();
                    break;

                case ZserioParser.LSHIFT:              // shiftExpression
                case ZserioParser.RSHIFT:
                case ZserioParser.AND:                 // bitwiseAndExpression
                case ZserioParser.XOR:                 // bitwiseXorExpression
                case ZserioParser.OR:                  // bitwiseOrExpression
                    evaluateBitExpression();
                    break;

                case ZserioParser.LT:                  // relationalExpression
                case ZserioParser.LE:
                case ZserioParser.GT:
                case ZserioParser.GE:
                case ZserioParser.EQ:                  // equalityExpression
                case ZserioParser.NE:
                    evaluateRelationalExpression();
                    break;

                case ZserioParser.LOGICAL_AND:         // logicalAndExpression
                case ZserioParser.LOGICAL_OR:          // logicalOrExpression
                    evaluateLogicalExpression();
                    break;

                case ZserioParser.QUESTIONMARK:        // ternaryExpression
                    evaluateConditionalExpression();
                    break;

                case ZserioParser.BINARY_LITERAL:      // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    final String binText = stripBinaryLiteral(getText());
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(binText, 2));
                    break;

                case ZserioParser.OCTAL_LITERAL:       // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText(), 8));
                    break;

                case ZserioParser.DECIMAL_LITERAL:     // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText()));
                    break;

                case ZserioParser.HEXADECIMAL_LITERAL: // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    final String hexText = stripHexadecimalLiteral(getText());
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(hexText, 16));
                    break;

                case ZserioParser.BOOL_LITERAL:        // literalExpression
                    expressionType = ExpressionType.BOOLEAN;
                    break;

                case ZserioParser.STRING_LITERAL:      // literalExpression
                    expressionType = ExpressionType.STRING;
                    break;

                case ZserioParser.FLOAT_LITERAL:       // literalExpression
                case ZserioParser.DOUBLE_LITERAL:
                    expressionType = ExpressionType.FLOAT;
                    break;

                case ZserioParser.INDEX:               // indexExpression
                    evaluateIndexExpression();
                    break;

                case ZserioParser.ID:                  // identifierExpression
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

    private Expression(Token locationToken, Package pkg, Token expressionToken, ExpressionFlag expressionFlag,
            Expression operand1, Expression operand2, Expression operand3)
    {
        super(locationToken);

        this.pkg = pkg;
        type = expressionToken.getType();
        text = extractTokenText(expressionToken);
        this.expressionFlag = expressionFlag;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;

        initialize();
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> void addReferencedSymbolObject(Set<T> referencedObjectList,
            Class<? extends Object> elementClass)
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
        zserioType = operand1.zserioType;
        expressionIntegerValue = operand1.expressionIntegerValue;
        unresolvedIdentifiers = operand1.unresolvedIdentifiers;
    }

    private void evaluateFunctionCallExpression(Scope forcedEvaluationScope)
    {
        if (!(operand1.zserioType instanceof FunctionType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not a function!");

        final FunctionType functionType = (FunctionType)operand1.zserioType;
        final Expression functionResultExpression = functionType.getResultExpression();

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
            throw new ParserException(functionResultExpression, e.getMessage() +
                    " Found in function '" + functionType.getName() + "' called from here: " +
                    location.getFileName() + ":" + location.getLine() + ":" + location.getColumn());
        }

        evaluateExpressionType(functionType.getReturnType());
        expressionIntegerValue = functionResultExpression.expressionIntegerValue;
    }

    private void evaluateArrayElement()
    {
        if (!(operand1.zserioType instanceof ArrayType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        if (operand2.expressionType != ExpressionType.INTEGER)
            throw new ParserException(operand2, "Integer expression expected!");

        final ArrayType arrayType = (ArrayType)operand1.zserioType;
        evaluateExpressionType(arrayType.getElementType());
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
        for (Expression unresolvedIdentifier : operand1.unresolvedIdentifiers)
            op1UnresolvedPackageNameBuilder.addId(unresolvedIdentifier.text);

        final ZserioType identifierType = pkg.getVisibleType(op1UnresolvedPackageNameBuilder.get(),
                operand2.text);
        if (identifierType == null)
        {
            // identifier still not found
            if (expressionFlag == ExpressionFlag.IS_TOP_LEVEL_DOT)
            {
                // and we are top level dot
                throw new ParserException(this, "Unresolved symbol '" +
                        op1UnresolvedPackageNameBuilder.get().toString() + "' within expression scope!");
            }

            // this can happened for long package name, we must wait for dot
            unresolvedIdentifiers.addAll(operand1.unresolvedIdentifiers);
            unresolvedIdentifiers.add(operand2);
        }
        else
        {
            evaluateIdentifierType(identifierType);

            // set symbolObject to all unresolved identifier expressions (needed for formatters)
            for (Expression unresolvedIdentifier : operand1.unresolvedIdentifiers)
                unresolvedIdentifier.symbolObject = identifierType.getPackage();
            operand2.symbolObject = identifierType; // this is used by formatters (operand2 was not evaluated)
        }
    }

    private void evaluateEnumDotExpression()
    {
        final EnumType enumType = (EnumType)(operand1.zserioType);
        final Scope enumScope = enumType.getScope();
        final String dotOperand = operand2.text;
        final Object enumSymbol = enumScope.getSymbol(dotOperand);
        if (!(enumSymbol instanceof EnumItem))
            throw new ParserException(this, "'" + dotOperand + "' undefined in enumeration '" +
                    enumType.getName() + "'!");

        operand2.symbolObject = enumSymbol; // this is used by formatters (operand2 was not evaluated)
        symbolObject = enumSymbol;
        evaluateExpressionType(enumType);
    }

    private void evaluateCompoundDotExpression()
    {
        final CompoundType compoundType = (CompoundType)(operand1.zserioType);
        final Scope compoundScope = compoundType.getScope();
        final String dotOperand = operand2.text;
        final Object compoundSymbol = compoundScope.getSymbol(dotOperand);
        if (compoundSymbol == null)
            throw new ParserException(this, "'" + dotOperand + "' undefined in compound '" +
                    compoundType.getName() + "'!");

        operand2.symbolObject = compoundSymbol; // this is used by formatter (operand2 was not evaluated)
        symbolObject = compoundSymbol;
        if (compoundSymbol instanceof Field)
        {
            evaluateExpressionType(((Field)compoundSymbol).getFieldType());
        }
        else if (compoundSymbol instanceof Parameter)
        {
            evaluateExpressionType(((Parameter)compoundSymbol).getParameterType());
        }
        else if (compoundSymbol instanceof FunctionType)
        {
            // function type, we must wait for "()"
            zserioType = (FunctionType)compoundSymbol;
        }
        else if (compoundSymbol instanceof CompoundType)
        {
            evaluateExpressionType((CompoundType)compoundSymbol);
        }
        else
        {
            throw new ParserException(this, "'" + dotOperand + "' undefined in compound '" +
                    compoundType.getName() + "'!");
        }
    }

    private void evaluateLengthOfOperator()
    {
        if (!(operand1.zserioType instanceof ArrayType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        expressionType = ExpressionType.INTEGER;
        // length of result has default expressionIntegerValue
    }

    private void evaluateSumOperator()
    {
        if (!(operand1.zserioType instanceof ArrayType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        final ArrayType arrayType = (ArrayType)operand1.zserioType;
        evaluateExpressionType(arrayType.getElementType());
    }

    private void evaluateValueOfOperator()
    {
        if (operand1.expressionType != ExpressionType.ENUM)
            throw new ParserException(operand1, "'" + operand1.text + "' is not an enumeration item!");

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
            expressionIntegerValue = (isNegate) ? operand1.expressionIntegerValue.negate() :
                operand1.expressionIntegerValue;
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
        if (op1.expressionType != ExpressionType.INTEGER)
            throw new ParserException(this, "Integer expression expected!");

        expressionType = ExpressionType.INTEGER;
        expressionIntegerValue = op1.expressionIntegerValue.not();
    }

    private void evaluateArithmeticExpression()
    {
        if (type == ZserioParser.PLUS && operand1.expressionType == ExpressionType.STRING &&
                operand2.expressionType == ExpressionType.STRING)
        {
            expressionType = ExpressionType.STRING;
        }
        else
        {
            if ( (operand1.expressionType != ExpressionType.INTEGER &&
                    operand1.expressionType != ExpressionType.FLOAT) ||
                 (operand2.expressionType != ExpressionType.INTEGER &&
                    operand2.expressionType != ExpressionType.FLOAT) )
                throw new ParserException(this, "Integer or float expressions expected!");

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
                        expressionIntegerValue = operand1.expressionIntegerValue.add(
                                operand2.expressionIntegerValue);
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
                        expressionIntegerValue = operand1.expressionIntegerValue.divide(
                                operand2.expressionIntegerValue);
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
        if (operand1.expressionType != ExpressionType.INTEGER ||
                operand2.expressionType != ExpressionType.INTEGER)
            throw new ParserException(this, "Integer expressions expected!");

        expressionType = ExpressionType.INTEGER;
        switch (type)
        {
            case ZserioParser.LSHIFT:
                expressionIntegerValue = operand1.expressionIntegerValue.shiftLeft(
                        operand2.expressionIntegerValue);
                break;

            case ZserioParser.RSHIFT:
                expressionIntegerValue = operand1.expressionIntegerValue.shiftRight(
                        operand2.expressionIntegerValue);
                break;

            case ZserioParser.AND:
                expressionIntegerValue = operand1.expressionIntegerValue.and(
                        operand2.expressionIntegerValue);
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
    }

    private void evaluateRelationalExpression()
    {
        if (operand1.expressionType == ExpressionType.UNKNOWN ||
                operand1.expressionType != operand2.expressionType)
            throw new ParserException(this, "Incompatible expression types (" + operand1.expressionType +
                    " != " + operand2.expressionType + ")!");

        if (operand1.expressionType == ExpressionType.FLOAT &&
                type != ZserioParser.LT && type != ZserioParser.GT)
            throw new ParserException(this, "Equality operator is not allowed for floats!");

        if (operand1.expressionType == ExpressionType.STRING &&
                type != ZserioParser.EQ && type != ZserioParser.NE)
            throw new ParserException(this,
                    "'Greater than' and 'less than' comparison is not allowed for strings!");

        if (operand1.expressionType == ExpressionType.STRING)
            throw new ParserException(this, "String comparison is not implemented!");

        expressionType = ExpressionType.BOOLEAN;
        if (operand1.expressionType == ExpressionType.INTEGER)
        {
            expressionIntegerValue = operand1.expressionIntegerValue.relationalOperator(
                    operand2.expressionIntegerValue);
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
            throw new ParserException(this, "Incompatible expression types (" + operand2.expressionType +
                    " != " + operand3.expressionType + ")!");

        expressionType = operand2.expressionType;
        zserioType = operand2.zserioType;
        if (expressionType == ExpressionType.INTEGER)
        {
            expressionIntegerValue = operand2.expressionIntegerValue.conditional(
                    operand3.expressionIntegerValue);
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
                final Object identifierSymbol = forcedEvaluationScope.getSymbol(text);
                if (identifierSymbol == null)
                {
                    // it still can be a type
                    final ZserioType identifierType = pkg.getVisibleType(PackageName.EMPTY, text);
                    if (identifierType == null)
                    {
                        // identifier not found
                        if (expressionFlag != ExpressionFlag.IS_DOT_LEFT_OPERAND_ID)
                        {
                            // and expression is not in dot expression
                            throw new ParserException(this, "Unresolved symbol '" + text +
                                    "' within expression scope!");
                        }

                        // this can happened for a long package name, we must wait for dot
                        unresolvedIdentifiers.add(this);
                    }
                    else
                    {
                        evaluateIdentifierType(identifierType);
                    }
                }
                else
                {
                    evaluateIdentifierSymbol(identifierSymbol, forcedEvaluationScope, text);
                }
            }
        }
    }

    private void evaluateIdentifierType(ZserioType identifierType)
    {
        symbolObject = identifierType;

        // resolve type
        final ZserioType resolvedType = TypeReference.resolveBaseType(identifierType);

        if (resolvedType instanceof EnumType)
        {
            // enumeration type, we must wait for field and dot
            zserioType = resolvedType;
        }
        else if (resolvedType instanceof ConstType)
        {
            // constant type
            final ConstType constType = (ConstType)resolvedType;
            evaluateExpressionType(constType.getConstType());

            // call evaluation explicitly because this const does not have to be evaluated yet
            final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
            constType.accept(evaluator);

            final Expression constValueExpression = constType.getValueExpression();
            expressionIntegerValue = constValueExpression.expressionIntegerValue;
        }
        else
        {
            throw new ParserException(this, "Type '" + resolvedType.getName() + "' (" +
                    resolvedType.getClass() + ") is not allowed here!");
        }
    }

    private void evaluateIdentifierSymbol(Object identifierSymbol, Scope forcedEvaluationScope,
            String identifier)
    {
        symbolObject = identifierSymbol;
        if (identifierSymbol instanceof Field)
        {
            evaluateExpressionType(((Field)identifierSymbol).getFieldType());
        }
        else if (identifierSymbol instanceof Parameter)
        {
            evaluateExpressionType(((Parameter)identifierSymbol).getParameterType());
        }
        else if (identifierSymbol instanceof FunctionType)
        {
            // function type, we must wait for "()"
            zserioType = (FunctionType)identifierSymbol;
        }
        else if (identifierSymbol instanceof EnumItem)
        {
            // enumeration item (this can happen for enum choices where enum is visible or for enum itself)
            final ZserioType scopeOwner = forcedEvaluationScope.getOwner();
            if (scopeOwner instanceof ChoiceType)
            {
                // this enumeration item is in choice with enumeration type selector
                final ChoiceType enumChoice = (ChoiceType)scopeOwner;
                final Expression selectorExpression = enumChoice.getSelectorExpression();
                final ZserioType selectorExprZserioType = selectorExpression.getExprZserioType();
                if (selectorExprZserioType instanceof EnumType)
                {
                    final EnumType enumType = (EnumType)selectorExprZserioType;
                    zserioType = enumType;
                    evaluateExpressionType(enumType);
                }
            }
            // if this enumeration item is in own enum, leave it unresolved (we have problem with it because
            // such enumeration items cannot be evaluated yet)
        }
        else
        {
            throw new ParserException(this, "Symbol '" + identifier + "' (" +
                    identifierSymbol.getClass() + ") is not allowed here!");
        }
    }

    private void evaluateExpressionType(ZserioType type)
    {
        // resolved instantiated type
        ZserioType resolvedType = type;
        if (resolvedType instanceof TypeInstantiation)
        {
            // call evaluation explicitly because this type instantiation does not have to be evaluated yet
            final TypeInstantiation typeInstantiation = (TypeInstantiation)resolvedType;
            final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
            typeInstantiation.accept(evaluator);
            resolvedType = typeInstantiation.getReferencedType();
        }

        // resolve type reference
        resolvedType = TypeReference.resolveBaseType(resolvedType);

        if (resolvedType instanceof EnumType)
        {
            expressionType = ExpressionType.ENUM;
            if (symbolObject instanceof EnumItem)
            {
                // call evaluation explicitly because this enumeration item does not have to be evaluated yet
                final EnumItem enumItem = (EnumItem)symbolObject;
                final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
                ((EnumType)resolvedType).accept(evaluator);

                // set integer value according to this enumeration item
                expressionIntegerValue = new ExpressionIntegerValue(enumItem.getValue());
            }
        }
        else if (resolvedType instanceof IntegerType)
        {
            expressionType = ExpressionType.INTEGER;
            final IntegerType integerType = (IntegerType)resolvedType;
            if (resolvedType instanceof BitFieldType)
            {
                // call evaluation explicitly because this length does not have to be evaluated yet
                final BitFieldType bitFieldType = (BitFieldType)resolvedType;
                final ZserioAstEvaluator evaluator = new ZserioAstEvaluator();
                bitFieldType.accept(evaluator);
            }
            final BigInteger lowerBound = integerType.getLowerBound();
            final BigInteger upperBound = integerType.getUpperBound();
            if (lowerBound != null && upperBound != null)
                expressionIntegerValue = new ExpressionIntegerValue(lowerBound, upperBound);
        }
        else if (resolvedType instanceof FloatType)
        {
            expressionType = ExpressionType.FLOAT;
        }
        else if (resolvedType instanceof StringType)
        {
            expressionType = ExpressionType.STRING;
        }
        else if (resolvedType instanceof BooleanType)
        {
            expressionType = ExpressionType.BOOLEAN;
        }
        else if (resolvedType instanceof CompoundType)
        {
            expressionType = ExpressionType.COMPOUND;
        }
        else
        {
            expressionType = ExpressionType.UNKNOWN;
        }

        zserioType = resolvedType;
    }

    private void initialize()
    {
        evaluationState = EvaluationState.NOT_EVALUATED;
        expressionType = ExpressionType.UNKNOWN;
        zserioType = null;
        expressionIntegerValue = new ExpressionIntegerValue();
        unresolvedIdentifiers = new ArrayList<Expression>();
        symbolObject = null;
        needsBigIntegerCastingToNative = false;
    }

    private enum EvaluationState
    {
        NOT_EVALUATED,
        IN_EVALUATION,
        EVALUATED
    };

    private static String extractTokenText(Token expressionToken)
    {
        String tokenText = expressionToken.getText();
        switch (expressionToken.getType())
        {
            case ZserioParser.BINARY_LITERAL:
                tokenText = stripBinaryLiteral(tokenText);
                break;

            case ZserioParser.OCTAL_LITERAL:
                tokenText = stripOctalLiteral(tokenText);
                break;

            case ZserioParser.HEXADECIMAL_LITERAL:
                tokenText = stripHexadecimalLiteral(tokenText);
                break;

            case ZserioParser.FLOAT_LITERAL:
                tokenText = stripFloatLiteral(tokenText);
                break;

            default:
                break;
        }

        return tokenText;
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
    private ZserioType zserioType;
    private ExpressionIntegerValue expressionIntegerValue;
    private List<Expression> unresolvedIdentifiers;
    private Object symbolObject;

    private boolean needsBigIntegerCastingToNative;
}
