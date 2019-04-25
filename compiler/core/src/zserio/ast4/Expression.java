package zserio.ast4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.antlr.Zserio4Parser;

/**
 *
 * TODO
 * It would be probably better to use internal visitors instead of if (operandX) construction.
 *
 */
/**
 * AST node for expressions defined in the language.
 */
public class Expression extends AstNodeBase
{
    /**
     * TODO
     * @param expressionToken
     * @param pkg
     */
    public Expression(Token expressionToken, Package pkg)
    {
        this(expressionToken, pkg, expressionToken, false, null, null, null);
    }

    /**
     * TODO
     * @param locationToken
     * @param pkg
     * @param expressionToken
     * @param isExplicit
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken)
    {
        this(locationToken, pkg, expressionToken, false, null, null, null);
    }

    /**
     * TODO
     * @param locationToken
     * @param pkg
     * @param expressionToken
     * @param isExplicit
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, boolean isExplicit)
    {
        this(locationToken, pkg, expressionToken, isExplicit, null, null, null);
    }

    /**
     * TODO
     * @param locationToken
     * @param pkg
     * @param expressionToken
     * @param operand1
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, Expression operand1)
    {
        this(locationToken, pkg, expressionToken, false, operand1, null, null);
    }

    /**
     * TODO
     * @param locationToken
     * @param pkg
     * @param expressionToken
     * @param operand1
     * @param operand2
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, Expression operand1,
            Expression operand2)
    {
        this(locationToken, pkg, expressionToken, false, operand1, operand2, null);
    }

    /**
     * TODO
     * @param locationToken
     * @param pkg
     * @param expressionToken
     * @param operand1
     * @param operand2
     * @param operand3
     */
    public Expression(Token locationToken, Package pkg, Token expressionToken, Expression operand1,
            Expression operand2, Expression operand3)
    {
        this(locationToken, pkg, expressionToken, false, operand1, operand2, operand3);
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
     * Sets lexical scope for the expression.
     *
     * This method is called by ScopeEvaluator.
     *
     * @param scope Lexical scope to set.
     */
    public void setScope(Scope scope)
    {
        this.scope = scope;
    }

    /**
     * TODO
     */
    public String getExpressionString()
    {
        return text;
    }

    /**
     * Gets the expression type given by the parser.
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
     * @return Expression text given by ANTLR4.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Checks if the expression contains explicit variable.
     *
     * @return Returns true if expression is explicit otherwise false.
     */
    public boolean isExplicitVariable()
    {
        return isExplicit;
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
     * Gets lexical scope of the expression.
     *
     * @return Lexical scope of the expression.
     */
    public Scope getScope()
    {
        return scope;
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
        return containsOperand(Zserio4Parser.INDEX);
    }

    /**
     * Checks if expression contains ternary operator 'a ? b : c'.
     *
     * @return Returns true if this expression contains ternary operator.
     */
    public boolean containsTernaryOperator()
    {
        return containsOperand(Zserio4Parser.QUESTIONMARK);
    }

    /**
     * Checks if expression contains function call.
     *
     * @return Returns true if this expression contains function call.
     */
    public boolean containsFunctionCall()
    {
        return containsOperand(Zserio4Parser.RPAREN);
    }

    @Override
    protected void evaluate() throws ParserException
    {
        evaluate(scope);
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
     * If given scope for evaluation is different to expression scope, the method forces evaluation even if
     * the expression has been already evaluated (this is used for function called within owner structure).
     *
     * @param evaluationScope Scope for evaluation.
     *
     * @throws ParserException Throws if expression is not valid.
     */
    protected void evaluate(Scope evaluationScope) throws ParserException
    {
        if (evaluationState == EvaluationState.IN_EVALUATION)
            throw new ParserException(this, "Cyclic dependency detected in expression evaluation!");

        // force evaluation if different scope is specified
        if (evaluationScope != scope && evaluationState != EvaluationState.NOT_EVALUATED)
            initialize();

        if (evaluationState == EvaluationState.NOT_EVALUATED)
        {
            evaluationState = EvaluationState.IN_EVALUATION;

            if (operand1 != null)
            {
                operand1.evaluate(evaluationScope);
                if (operand2 != null)
                {
                    operand2.evaluate(evaluationScope);
                    if (operand3 != null)
                        operand3.evaluate(evaluationScope);
                }
            }

            // check unresolved identifiers if no further DOT or ID is available TODO why?
            if (type != Zserio4Parser.DOT && type != Zserio4Parser.ID)
            {
                if (operand1 != null)
                {
                    operand1.checkUnresolvedIdentifiers();
                    if (operand2 != null)
                    {
                        operand2.checkUnresolvedIdentifiers();
                        if (operand3 != null)
                            operand3.checkUnresolvedIdentifiers();
                    }
                }
            }

            switch (type)
            {
                case Zserio4Parser.LPAREN:              // parenthesizedExpression
                    evaluateParenthesizedExpression();
                    break;

                case Zserio4Parser.RPAREN:              // functionCallExpression
                    evaluateFunctionCallExpression(evaluationScope);
                    break;

                case Zserio4Parser.LBRACKET:            // arrayExpression
                    evaluateArrayElement();
                    break;

                case Zserio4Parser.DOT:                 // dotExpression
                    evaluateDotExpression();
                    break;

                case Zserio4Parser.LENGTHOF:
                    evaluateLengthOfOperator();         // lengthofExpression
                    break;

                case Zserio4Parser.SUM:
                    evaluateSumOperator();              // sumExpression
                    break;

                case Zserio4Parser.VALUEOF:
                    evaluateValueOfOperator();          // valueofExpression
                    break;

                case Zserio4Parser.NUMBITS:
                    evaluateNumBitsOperator();          // numbitsExpression
                    break;

                case Zserio4Parser.PLUS:                // unaryExpression or additiveExpression
                    if (operand2 == null)
                        evaluateUnaryPlusMinus(false);
                    else
                        evaluateArithmeticExpression();
                    break;

                case Zserio4Parser.MINUS:               // unaryExpression or additiveExpression
                    if (operand2 == null)
                        evaluateUnaryPlusMinus(true);
                    else
                        evaluateArithmeticExpression();
                    break;

                case Zserio4Parser.BANG:                // unaryExpression
                    evaluateNegationOperator();
                    break;

                case Zserio4Parser.TILDE:               // unaryExpression
                    evaluateBitNotExpression();
                    break;

                case Zserio4Parser.MULTIPLY:            // multiplicativeExpression
                case Zserio4Parser.DIVIDE:
                case Zserio4Parser.MODULO:
                    evaluateArithmeticExpression();
                    break;

                case Zserio4Parser.LSHIFT:              // shiftExpression
                case Zserio4Parser.RSHIFT:
                case Zserio4Parser.AND:                 // bitwiseAndExpression
                case Zserio4Parser.XOR:                 // bitwiseXorExpression
                case Zserio4Parser.OR:                  // bitwiseOrExpression
                    evaluateBitExpression();
                    break;

                case Zserio4Parser.LT:                  // relationalExpression
                case Zserio4Parser.LE:
                case Zserio4Parser.GT:
                case Zserio4Parser.GE:
                case Zserio4Parser.EQ:                  // equalityExpression
                case Zserio4Parser.NE:
                    evaluateRelationalExpression();
                    break;

                case Zserio4Parser.LOGICAL_AND:         // logicalAndExpression
                case Zserio4Parser.LOGICAL_OR:          // logicalOrExpression
                    evaluateLogicalExpression();
                    break;

                case Zserio4Parser.QUESTIONMARK:        // ternaryExpression
                    evaluateConditionalExpression();
                    break;

                case Zserio4Parser.BINARY_LITERAL:      // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText(), 2));
                    break;

                case Zserio4Parser.OCTAL_LITERAL:       // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText(), 8));
                    break;

                case Zserio4Parser.DECIMAL_LITERAL:     // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText()));
                    break;

                case Zserio4Parser.HEXADECIMAL_LITERAL: // literalExpression
                    expressionType = ExpressionType.INTEGER;
                    expressionIntegerValue = new ExpressionIntegerValue(new BigInteger(getText(), 16));
                    break;

                case Zserio4Parser.BOOL_LITERAL:        // literalExpression
                    expressionType = ExpressionType.BOOLEAN;
                    break;

                case Zserio4Parser.STRING_LITERAL:      // literalExpression
                    expressionType = ExpressionType.STRING;
                    break;

                case Zserio4Parser.FLOAT_LITERAL:       // literalExpression
                case Zserio4Parser.DOUBLE_LITERAL:
                    expressionType = ExpressionType.FLOAT;
                    break;

                case Zserio4Parser.INDEX:               // indexExpression
                    evaluateIndexExpression();
                    break;

                case Zserio4Parser.ID:                  // identifierExpression
                    evaluateIdentifier(evaluationScope);
                    break;

                default:
                    throw new ParserException(this, "Illegal expression type '" + type + "'!");
            }

            checkUnresolvedIdentifiers();
            evaluationState = EvaluationState.EVALUATED;
        }
    }

    /**
     * Adds additional lexical scope to the expression scope.
     *
     * @param additionalScope Additional scope to add.
     */
    protected void addScope(Scope additionalScope)
    {
        scope.add(additionalScope);
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
    protected void propagateNeedsBigInteger()
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
    protected void setNeedsBigIntegerCastingNative()
    {
        needsBigIntegerCastingToNative = true;
    }

    private Expression(Token locationToken, Package pkg, Token expressionToken, boolean isExplicit,
            Expression operand1, Expression operand2, Expression operand3)
    {
        super(locationToken);

        this.pkg = pkg;
        type = expressionToken.getType();
        text = expressionToken.getText();
        this.isExplicit = isExplicit;
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

    /**
     * This method checks of evaluation for expression.
     *
     * Method is necessary because of full qualified enumeration names. Example: wrong_package.enum_type.
     *
     * Expression "wrong_package" can be legally unknown identifier during evaluation (we know nothing about
     * enum_type yet). Therefore the evaluation process must continue. Afterwards, expression "wrong_package"
     * will be checked during evaluation of dot expression. However if dot expression is not there, evaluation
     * of expression "wrong_package" will be succeeded even if the expression is not valid.
     *
     * @throws ParserException Throws if expression is not valid.
     */
    private void checkUnresolvedIdentifiers() throws ParserException
    {
        if (!isExplicit && !unresolvedIdentifiers.isEmpty())
        {
            final PackageName.Builder unresolvedSymbolBuilder = new PackageName.Builder();
            for (Expression unresolvedIdentifier : unresolvedIdentifiers)
                unresolvedSymbolBuilder.addId(unresolvedIdentifier.text);
            throw new ParserException(this, "Unresolved symbol '" + unresolvedSymbolBuilder.get().toString() +
                    "' within expression scope!");
        }
    }

    private void evaluateParenthesizedExpression()
    {
        expressionType = operand1.expressionType;
        zserioType = operand1.zserioType;
        expressionIntegerValue = operand1.expressionIntegerValue;
        unresolvedIdentifiers = operand1.unresolvedIdentifiers;
    }

    private void evaluateFunctionCallExpression(Scope evaluationScope) throws ParserException
    {
        if (!(operand1.zserioType instanceof FunctionType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not a function!");

        final FunctionType functionType = (FunctionType)operand1.zserioType;
        final Expression functionResultExpression = functionType.getResultExpression();

        // function expression should know only symbols available on a place of the function call:
        // - if it's called within its owner object, it can see only symbols defined before the call
        try
        {
            if (evaluationScope.getOwner() == functionResultExpression.getScope().getOwner())
                functionResultExpression.evaluate(evaluationScope); // called within the owner
            else
                functionResultExpression.evaluate(scope); // called externally
        }
        catch (ParserException e)
        {
            final AstNodeLocation location = getLocation();
            throw new ParserException(functionResultExpression, e.getMessage() +
                    " Found in function '" + functionType.getName() + "' called from here: " +
                    location.getFileName() + ":" + location.getLine() + ":" + location.getColumn());
        }

        evaluateExpressionType(functionType.getReturnType());
        expressionIntegerValue = functionResultExpression.expressionIntegerValue;
    }

    private void evaluateArrayElement() throws ParserException
    {
        if (!(operand1.zserioType instanceof ArrayType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        if (operand2.expressionType != ExpressionType.INTEGER)
            throw new ParserException(operand2, "Integer expression expected!");

        final ArrayType arrayType = (ArrayType)operand1.zserioType;
        evaluateExpressionType(arrayType.getElementType());
    }

    private void evaluateDotExpression() throws ParserException
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

    private void evaluatePackageDotExpression() throws ParserException
    {
        // try to resolve operand1 as package name and operand2 as type
        final PackageName.Builder op1UnresolvedPackageNameBuilder = new PackageName.Builder();
        for (Expression unresolvedIdentifier : operand1.unresolvedIdentifiers)
            op1UnresolvedPackageNameBuilder.addId(unresolvedIdentifier.text);

        final ZserioType identifierType = pkg.getVisibleType(op1UnresolvedPackageNameBuilder.get(),
                operand2.text);
        if (identifierType == null)
        {
            // identifier still not found, this can happened for long package name, we must wait for dot
            unresolvedIdentifiers.addAll(operand1.unresolvedIdentifiers);
            unresolvedIdentifiers.add(operand2);
        }
        else
        {
            evaluateIdentifierType(identifierType);

            // reset operand2, it could be already found in different package
            operand2.reset(identifierType);

            // set symbolObject to all unresolved identifier expressions (needed for formatters)
            for (Expression unresolvedIdentifier : operand1.unresolvedIdentifiers)
                unresolvedIdentifier.symbolObject = identifierType.getPackage();
        }
    }

    private void evaluateEnumDotExpression() throws ParserException
    {
        final EnumType enumType = (EnumType)(operand1.zserioType);
        final Scope enumScope = enumType.getScope();
        final String dotOperand = operand2.text;
        final Object enumSymbol = enumScope.getSymbol(dotOperand);
        if (!(enumSymbol instanceof EnumItem))
            throw new ParserException(this, "'" + dotOperand + "' undefined in enumeration '" +
                    enumType.getName() + "'!");

        // reset operand2, enumeration items can be already resolved because they are searched globally to
        // support choices with enumeration selector (needed for formatters)
        operand2.reset(enumSymbol);

        symbolObject = enumSymbol;
        evaluateExpressionType(enumType);
    }

    private void evaluateCompoundDotExpression() throws ParserException
    {
        final CompoundType compoundType = (CompoundType)(operand1.zserioType);
        final Scope compoundScope = compoundType.getScope();
        final String dotOperand = operand2.text;
        final Object compoundSymbol = compoundScope.getSymbol(dotOperand);
        if (compoundSymbol == null)
            throw new ParserException(this, "'" + dotOperand + "' undefined in compound '" +
                    compoundType.getName() + "'!");

        // reset operand2, it could be already found in the compound type (needed for formatters)
        operand2.reset(compoundSymbol);

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

    private void evaluateLengthOfOperator() throws ParserException
    {
        if (!(operand1.zserioType instanceof ArrayType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        expressionType = ExpressionType.INTEGER;
        // length of result has default expressionIntegerValue
    }

    private void evaluateSumOperator() throws ParserException
    {
        if (!(operand1.zserioType instanceof ArrayType))
            throw new ParserException(operand1, "'" + operand1.text + "' is not an array!");

        final ArrayType arrayType = (ArrayType)operand1.zserioType;
        evaluateExpressionType(arrayType.getElementType());
    }

    private void evaluateValueOfOperator() throws ParserException
    {
        if (operand1.expressionType != ExpressionType.ENUM)
            throw new ParserException(operand1, "'" + operand1.text + "' is not an enumeration item!");

        expressionType = ExpressionType.INTEGER;
        expressionIntegerValue = operand1.expressionIntegerValue;
    }

    private void evaluateNumBitsOperator() throws ParserException
    {
        if (operand1.expressionType != ExpressionType.INTEGER)
            throw new ParserException(operand1, "Integer expression expected!");

        expressionType = ExpressionType.INTEGER;
        expressionIntegerValue = operand1.expressionIntegerValue.numbits();
    }

    private void evaluateUnaryPlusMinus(boolean isNegate) throws ParserException
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

    private void evaluateNegationOperator() throws ParserException
    {
        final Expression op1 = op1();
        if (op1.expressionType != ExpressionType.BOOLEAN)
            throw new ParserException(this, "Boolean expression expected!");

        expressionType = ExpressionType.BOOLEAN;
    }

    private void evaluateBitNotExpression() throws ParserException
    {
        final Expression op1 = op1();
        if (op1.expressionType != ExpressionType.INTEGER)
            throw new ParserException(this, "Integer expression expected!");

        expressionType = ExpressionType.INTEGER;
        expressionIntegerValue = op1.expressionIntegerValue.not();
    }

    private void evaluateArithmeticExpression() throws ParserException
    {
        if (type == Zserio4Parser.PLUS && operand1.expressionType == ExpressionType.STRING &&
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
                    case Zserio4Parser.PLUS:
                        expressionIntegerValue = operand1.expressionIntegerValue.add(
                                operand2.expressionIntegerValue);
                        break;

                    case Zserio4Parser.MINUS:
                        expressionIntegerValue =
                            operand1.expressionIntegerValue.subtract(operand2.expressionIntegerValue);
                        break;

                    case Zserio4Parser.MULTIPLY:
                        expressionIntegerValue =
                            operand1.expressionIntegerValue.multiply(operand2.expressionIntegerValue);
                        break;

                    case Zserio4Parser.DIVIDE:
                        expressionIntegerValue = operand1.expressionIntegerValue.divide(
                                operand2.expressionIntegerValue);
                        break;

                    case Zserio4Parser.MODULO:
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

    private void evaluateBitExpression() throws ParserException
    {
        if (operand1.expressionType != ExpressionType.INTEGER ||
                operand2.expressionType != ExpressionType.INTEGER)
            throw new ParserException(this, "Integer expressions expected!");

        expressionType = ExpressionType.INTEGER;
        switch (type)
        {
            case Zserio4Parser.LSHIFT:
                expressionIntegerValue = operand1.expressionIntegerValue.shiftLeft(
                        operand2.expressionIntegerValue);
                break;

            case Zserio4Parser.RSHIFT:
                expressionIntegerValue = operand1.expressionIntegerValue.shiftRight(
                        operand2.expressionIntegerValue);
                break;

            case Zserio4Parser.AND:
                expressionIntegerValue = operand1.expressionIntegerValue.and(
                        operand2.expressionIntegerValue);
                break;

            case Zserio4Parser.OR:
                expressionIntegerValue = operand1.expressionIntegerValue.or(operand2.expressionIntegerValue);
                break;

            case Zserio4Parser.XOR:
                expressionIntegerValue = operand1.expressionIntegerValue.xor(operand2.expressionIntegerValue);
                break;

            default:
                throw new ParserException(this, "Illegal expression type '" + type + "'!");
        }

        if (type != Zserio4Parser.LSHIFT && type != Zserio4Parser.RSHIFT &&
                expressionIntegerValue.needsBigInteger())
        {
            operand1.propagateNeedsBigInteger();
            operand2.propagateNeedsBigInteger();
        }
    }

    private void evaluateRelationalExpression() throws ParserException
    {
        if (operand1.expressionType == ExpressionType.UNKNOWN ||
                operand1.expressionType != operand2.expressionType)
            throw new ParserException(this, "Incompatible expression types (" + operand1.expressionType +
                    " != " + operand2.expressionType + ")!");

        if (operand1.expressionType == ExpressionType.FLOAT &&
                type != Zserio4Parser.LT && type != Zserio4Parser.GT)
            throw new ParserException(this, "Equality operator is not allowed for floats!");

        if (operand1.expressionType == ExpressionType.STRING &&
                type != Zserio4Parser.EQ && type != Zserio4Parser.NE)
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

    private void evaluateLogicalExpression() throws ParserException
    {
        if (operand1.expressionType != ExpressionType.BOOLEAN ||
                operand2.expressionType != ExpressionType.BOOLEAN)
            throw new ParserException(this, "Boolean expressions expected!");

        expressionType = ExpressionType.BOOLEAN;
    }

    private void evaluateConditionalExpression() throws ParserException
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

    private void evaluateIndexExpression() throws ParserException
    {
        expressionType = ExpressionType.INTEGER;
        // array index has default expressionIntegerValue
    }

    private void evaluateIdentifier(Scope evaluationScope) throws ParserException
    {
        final Object identifierSymbol = evaluationScope.getSymbol(text);
        if (identifierSymbol == null)
        {
            // it still can be a type
            final ZserioType identifierType = pkg.getVisibleType(PackageName.EMPTY, text);
            if (identifierType == null)
            {
                // identifier not found, this can happened for a package name, we must wait for dot
                unresolvedIdentifiers.add(this);
            }
            else
            {
                evaluateIdentifierType(identifierType);
            }
        }
        else
        {
            evaluateIdentifierSymbol(identifierSymbol, evaluationScope, text);
        }
    }

    private void evaluateIdentifierType(ZserioType identifierType) throws ParserException
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
            final Expression constValueExpression = constType.getValueExpression();

            // call evaluation explicitly because this const does not have to be evaluated yet
            // TODO should be constType.evaluateExpressions()
            // constValueExpression.evaluateTree();

            expressionIntegerValue = constValueExpression.expressionIntegerValue;
        }
        else
        {
            throw new ParserException(this, "Type '" + resolvedType.getName() + "' (" +
                    resolvedType.getClass() + ") is not allowed here!");
        }
    }

    private void evaluateIdentifierSymbol(Object identifierSymbol, Scope evaluationScope, String identifier)
            throws ParserException
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
            final EnumItem enumItem = (EnumItem)identifierSymbol;
            final EnumType enumType = enumItem.getEnumType();

            // if this enumeration item is in own enum, leave it unresolved (we have problem with it because
            // such enumeration items cannot be evaluated yet)
            if (evaluationScope.getOwner() != enumType)
                evaluateExpressionType(enumType);
        }
        else
        {
            throw new ParserException(this, "Symbol '" + identifier + "' (" +
                    identifierSymbol.getClass() + ") is not allowed here!");
        }
    }

    private void evaluateExpressionType(ZserioType type) throws ParserException
    {
        // resolved instantiated type
        ZserioType resolvedType = type;
        if (resolvedType instanceof TypeInstantiation)
        {
            // call evaluation explicitly because this type instantiation does not have to be evaluated yet
            final TypeInstantiation typeInstantiation = (TypeInstantiation)resolvedType;
            typeInstantiation.evaluate();  // TODO
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
                // TODO should be enumType.evaluateExpressions()
                // enumItem.getEnumType().evaluateItemValues();

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
                // TODO should be bitFieldType.evaluateExpressions()
                // bitFieldType.evaluateBitSizes();
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

    private void reset(Object newSymbolObject)
    {
        expressionType = ExpressionType.UNKNOWN;
        zserioType = null;
        expressionIntegerValue = new ExpressionIntegerValue();
        unresolvedIdentifiers = new ArrayList<Expression>();
        symbolObject = newSymbolObject;
    }

    private void initialize()
    {
        evaluationState = EvaluationState.NOT_EVALUATED;
        reset(null);
        needsBigIntegerCastingToNative = false;
    }

    private enum EvaluationState
    {
        NOT_EVALUATED,
        IN_EVALUATION,
        EVALUATED
    };

    private final Package pkg;

    private final int type;
    private final String text;

    private final boolean isExplicit;

    private final Expression operand1;
    private final Expression operand2;
    private final Expression operand3;

    private Scope scope;

    private EvaluationState evaluationState;

    private ExpressionType expressionType;
    private ZserioType zserioType;
    private ExpressionIntegerValue expressionIntegerValue;
    private List<Expression> unresolvedIdentifiers;
    private Object symbolObject;

    private boolean needsBigIntegerCastingToNative;
}
