package zserio.emit.java;

import java.math.BigInteger;

import zserio.ast.ConstType;
import zserio.ast.FunctionType;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Package;
import zserio.ast.Parameter;
import zserio.emit.common.DefaultExpressionFormattingPolicy;
import zserio.emit.common.StringEscapeConverter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;

/**
 * Default expressions formatting policy for Java expressions.
 *
 * TODO Please note that literal formatters are duplicitly implemented by native types as well. The correct
 * solution would be to pass formatted type to Expression Formatter to be able to call literal formatters from
 * native types.
 */
public abstract class JavaDefaultExpressionFormattingPolicy extends DefaultExpressionFormattingPolicy
{
    public JavaDefaultExpressionFormattingPolicy(JavaNativeTypeMapper javaNativeTypeMapper)
    {
        this.javaNativeTypeMapper = javaNativeTypeMapper;
    }

    // atom expressions formatting

    @Override
    public String getDecimalLiteral(Expression expr, boolean isNegative)
    {
        String decLiteral = expr.getText();
        // special work around for INT64_MIN, this value is not mapped to BigInteger
        if (expr.needsBigInteger() && (!isNegative || !decLiteral.equals(DECIMAL_LITERAL_ABS_INT64_MIN)))
        {
            // special handling for uint64 type
            decLiteral = getBigIntegerLiteral(decLiteral);
        }
        else
        {
            // decimal literals in Java are the same but append "L" for long values
            decLiteral += getIntegerLiteralSuffix(new BigInteger(decLiteral), isNegative);
        }

        return decLiteral;
    }

    @Override
    public String getBinaryLiteral(Expression expr, boolean isNegative)
    {
        String binaryLiteral = expr.getText();
        if (!binaryLiteral.isEmpty())
        {
            if (expr.needsBigInteger())
            {
                // special handling for uint64 type
                binaryLiteral = getBigIntegerLiteral(binaryLiteral, 2);
            }
            else
            {
                // binary literals are in Java only from version 1.7 => use hexadecimal instead of it
                final BigInteger binaryInBigInteger = new BigInteger(binaryLiteral, 2);
                binaryLiteral = JAVA_HEXADECIMAL_LITERAL_PREFIX + binaryInBigInteger.toString(16);
            }
        }

        return binaryLiteral;
    }

    @Override
    public String getHexadecimalLiteral(Expression expr, boolean isNegative)
    {
        // hexadecimal literals in Java are the same (with prefix "0x") but append "L" for long values
        if (expr.needsBigInteger())
        {
            // special handling for uint64 type
            return getBigIntegerLiteral(expr.getText(), 16);
        }

        String hexLiteral = expr.getText();
        final BigInteger hexInBigInteger = new BigInteger(hexLiteral, 16);
        hexLiteral = JAVA_HEXADECIMAL_LITERAL_PREFIX + hexLiteral +
                getIntegerLiteralSuffix(hexInBigInteger, isNegative);

        return hexLiteral;
    }

    @Override
    public String getOctalLiteral(Expression expr, boolean isNegative)
    {
        // octal literals in Java are the same (with prefix '0')
        if (expr.needsBigInteger())
        {
            // special handling for uint64 type
            return getBigIntegerLiteral(expr.getText(), 8);
        }

        return JAVA_OCTAL_LITERAL_PREFIX + expr.getText();
    }

    @Override
    public String getFloatLiteral(Expression expr, boolean isNegative)
    {
        // float literals in Java are the same (with postfix "f")
        return expr.getText() + JAVA_FLOAT_LITERAL_SUFFIX;
    }

    @Override
    public String getDoubleLiteral(Expression expr, boolean isNegative)
    {
        // double literals in Java are the same (without postfix)
        return expr.getText();
    }

    @Override
    public String getBoolLiteral(Expression expr)
    {
        // bool literals in Java are the same
        return expr.getText();
    }

    @Override
    public String getStringLiteral(Expression expr)
    {
        // string literals in Java does not support hexadecimal escapes
        return StringEscapeConverter.convertHexToUnicodeToEscapes(expr.getText());
    }

    @Override
    public String getIndex(Expression expr)
    {
        return "__index";
    }

    @Override
    public String getIdentifier(Expression expr, boolean isLastInDot, boolean isSetter)
            throws ZserioEmitException
    {
        // check if casting to BigInteger is necessary
        final String symbol = expr.getText();
        final Object resolvedSymbol = expr.getExprSymbolObject();
        final boolean isFirstInDot = (expr.getExprZserioType() != null);
        final StringBuilder result = new StringBuilder();
        final BigInteger exprUpperBound = expr.getIntegerUpperBound();
        final boolean isMappedToBigInteger = (exprUpperBound != null &&
                exprUpperBound.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0);
        final boolean needsCastingToBigInteger = (!isMappedToBigInteger && expr.needsBigInteger());
        if (isFirstInDot && needsCastingToBigInteger)
            result.append(BIG_INTEGER + ".valueOf(");

        // ignore package identifiers, they will be a part of the following Zserio type
        if (resolvedSymbol instanceof ZserioType)
        {
            // Zserio types
            formatTypeIdentifier(result, symbol, isFirstInDot, (ZserioType)resolvedSymbol);
        }
        else if (!(resolvedSymbol instanceof Package))
        {
            // identifier symbol objects
            formatSymbolIdentifier(result, symbol, isFirstInDot, resolvedSymbol, isSetter);
        }

        // finish casting to BigInteger
        if (isLastInDot && needsCastingToBigInteger)
            result.append(")");

        return result.toString();
    }

    // unary expressions formatting

    @Override
    public UnaryExpressionFormatting getBigIntegerCastingToNative(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getBigIntegerCastingToNative(expr);

        return new UnaryExpressionFormatting("(", ").longValue()");
    }

    @Override
    public UnaryExpressionFormatting getUnaryPlus(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getUnaryPlus(expr);

        return new UnaryExpressionFormatting("");
    }

    @Override
    public UnaryExpressionFormatting getUnaryMinus(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getUnaryMinus(expr);

        return new UnaryExpressionFormatting("", ".negate()");
    }

    @Override
    public UnaryExpressionFormatting getTilde(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getTilde(expr);

        return new UnaryExpressionFormatting("", ".not()");
    }

    // getBang() is ok from the base class

    // getLeftParenthesis() is ok from the base class

    @Override
    public UnaryExpressionFormatting getFunctionCall(Expression expr)
    {
        return new UnaryExpressionFormatting(getAccessPrefix(), "()");
    }

    @Override
    public UnaryExpressionFormatting getLengthOf(Expression expr)
    {
        return new UnaryExpressionFormatting("", ".length()");
    }

    @Override
    public UnaryExpressionFormatting getSum(Expression expr)
    {
        return new UnaryExpressionFormatting("", ".sum()");
    }

    @Override
    public UnaryExpressionFormatting getValueOf(Expression expr)
    {
        return new UnaryExpressionFormatting("", ".getValue()");
    }

    @Override
    public UnaryExpressionFormatting getNumBits(Expression expr)
    {
        return new UnaryExpressionFormatting("zserio.runtime.BuildInOperators.getNumBits(", ")");
    }

    // binary expressions formatting

    // getComma() is ok from the base class

    // getLogicalOr() is ok from the base class

    // getLogicalAnd() is ok from the base class

    @Override
    public BinaryExpressionFormatting getOr(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getOr(expr);

        return new BinaryExpressionFormatting("", ".or(", ")");
    }

    @Override
    public BinaryExpressionFormatting getXor(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getXor(expr);

        return new BinaryExpressionFormatting("", ".xor(", ")");
    }

    @Override
    public BinaryExpressionFormatting getAnd(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getAnd(expr);

        return new BinaryExpressionFormatting("", ".and(", ")");
    }

    @Override
    public BinaryExpressionFormatting getEq(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getEq(expr);

        return new BinaryExpressionFormatting("", ".compareTo(", ") == 0");
    }

    @Override
    public BinaryExpressionFormatting getNe(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getNe(expr);

        return new BinaryExpressionFormatting("", ".compareTo(", ") != 0");
    }

    @Override
    public BinaryExpressionFormatting getLt(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getLt(expr);

        return new BinaryExpressionFormatting("", ".compareTo(", ") < 0");
    }

    @Override
    public BinaryExpressionFormatting getLe(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getLe(expr);

        return new BinaryExpressionFormatting("", ".compareTo(", ") <= 0");
    }

    @Override
    public BinaryExpressionFormatting getGe(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getGe(expr);

        return new BinaryExpressionFormatting("", ".compareTo(", ") >= 0");
    }

    @Override
    public BinaryExpressionFormatting getGt(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getGt(expr);

        return new BinaryExpressionFormatting("", ".compareTo(", ") > 0");
    }

    @Override
    public BinaryExpressionFormatting getLeftShift(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getLeftShift(expr);

        return new BinaryExpressionFormatting("", ".shiftLeft(", ")");
    }

    @Override
    public BinaryExpressionFormatting getRightShift(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getRightShift(expr);

        return new BinaryExpressionFormatting("", ".shiftRight(", ")");
    }

    @Override
    public BinaryExpressionFormatting getPlus(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getPlus(expr);

        return new BinaryExpressionFormatting("", ".add(", ")");
    }

    @Override
    public BinaryExpressionFormatting getMinus(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getMinus(expr);

        return new BinaryExpressionFormatting("", ".subtract(", ")");
    }

    @Override
    public BinaryExpressionFormatting getMultiply(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getMultiply(expr);

        return new BinaryExpressionFormatting("", ".multiply(", ")");
    }

    @Override
    public BinaryExpressionFormatting getDivide(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getDivide(expr);

        return new BinaryExpressionFormatting("", ".divide(", ")");
    }

    @Override
    public BinaryExpressionFormatting getModulo(Expression expr)
    {
        if (!expr.needsBigInteger())
            return super.getModulo(expr);

        return new BinaryExpressionFormatting("", ".mod(", ")");
    }

    @Override
    public BinaryExpressionFormatting getArrayElement(Expression expr, boolean isSetter)
    {
        return new BinaryExpressionFormatting("", (isSetter) ? ".setElementAt(__value, " : ".elementAt(", ")");
    }

    @Override
    public BinaryExpressionFormatting getDot(Expression expr)
    {
        // ignore dots between package identifiers
        if (expr.op1().getExprZserioType() == null)
            return new BinaryExpressionFormatting("");

        // do special handling for enumeration items
        if (expr.op2().getExprSymbolObject() instanceof EnumItem)
            return new BinaryExpressionFormatting(getDotSeparatorForEnumItem());

        return new BinaryExpressionFormatting(".");
    }

    // ternary expressions formatting

    // getQuestionMark() is ok from the base class

    protected abstract String getIdentifierForTypeEnum(EnumType resolvedType,
            JavaNativeTypeMapper javaNativeTypeMapper) throws ZserioEmitException;
    protected abstract String getIdentifierForEnumItem(EnumItem enumItem);
    protected abstract String getDotSeparatorForEnumItem();
    protected abstract String getAccessPrefixForCompoundType();

    private void formatAccessorCall(StringBuilder result, String accessor, boolean isSetter)
    {
        result.append(accessor);
        if (isSetter)
            result.append("(__value)");
        else
            result.append("()");
    }

    private void formatTypeIdentifier(StringBuilder result, String symbol, boolean isFirstInDot,
            ZserioType identifierType) throws ZserioEmitException
    {
        // we need to resolve subtypes because Java does not support them
        final ZserioType resolvedType = TypeReference.resolveBaseType(identifierType);
        if (resolvedType instanceof EnumType)
        {
            // [EnumType].ENUM_ITEM
            result.append(getIdentifierForTypeEnum((EnumType)resolvedType, javaNativeTypeMapper));
        }
        else if (resolvedType instanceof ConstType)
        {
            // [ConstName]
            final JavaNativeType javaType = javaNativeTypeMapper.getJavaType(resolvedType);
            result.append(javaType.getFullName());
        }
        else if (resolvedType instanceof FunctionType)
        {
            // [functionCall]()
            final FunctionType functionType = (FunctionType)resolvedType;
            result.append(AccessorNameFormatter.getFunctionName(functionType));
        }
        else
        {
            result.append(symbol);
        }
    }

    private String getAccessPrefix()
    {
        final String accessPrefix = getAccessPrefixForCompoundType();

        return (accessPrefix.isEmpty()) ? accessPrefix : accessPrefix + ".";
    }

    private void formatSymbolIdentifier(StringBuilder result, String symbol, boolean isFirstInDot,
            Object resolvedSymbol, boolean isSetter)
    {
        if (resolvedSymbol instanceof Parameter)
        {
            final Parameter param = (Parameter)resolvedSymbol;
            final String accessor = AccessorNameFormatter.getGetterName(param);
            if (isFirstInDot)
                result.append(getAccessPrefix());
            formatAccessorCall(result, accessor, isSetter);
        }
        else if (resolvedSymbol instanceof Field)
        {
            final Field field = (Field)resolvedSymbol;
            final String accessor = isSetter ? AccessorNameFormatter.getSetterName(field) :
                    AccessorNameFormatter.getGetterName(field);
            if (isFirstInDot)
                result.append(getAccessPrefix());
            formatAccessorCall(result, accessor, isSetter);
        }
        else if (resolvedSymbol instanceof EnumItem)
        {
            result.append(getIdentifierForEnumItem((EnumItem)resolvedSymbol));
        }
        else
        {
            // this could happen for "explicit identifier" expressions
            result.append(symbol);
        }
    }

    private String getIntegerLiteralSuffix(BigInteger literalValue, boolean isNegative)
    {
        final long maxAbsIntValue = (isNegative) ? -(long)Integer.MIN_VALUE : (long)Integer.MAX_VALUE;

        return (literalValue.compareTo(BigInteger.valueOf(maxAbsIntValue)) > 0) ? JAVA_LONG_LITERAL_SUFFIX : "";
    }

    private String getBigIntegerLiteral(String value)
    {
        return getPreformattedBigIntegerValue(value) + ")";
    }

    private String getBigIntegerLiteral(String value, int radix)
    {
        return getPreformattedBigIntegerValue(value) + ", " + radix + ")";
    }

    private String getPreformattedBigIntegerValue(String value)
    {
        return "new " + BIG_INTEGER + "(\"" + value + "\"";
    }

    private final static String JAVA_LONG_LITERAL_SUFFIX = "L";
    private final static String JAVA_HEXADECIMAL_LITERAL_PREFIX = "0x";
    private final static String JAVA_OCTAL_LITERAL_PREFIX = "0";
    private final static String JAVA_FLOAT_LITERAL_SUFFIX = "f";

    private final static String DECIMAL_LITERAL_ABS_INT64_MIN = "9223372036854775808";
    private final static String BIG_INTEGER = "java.math.BigInteger";

    private final JavaNativeTypeMapper javaNativeTypeMapper;
}
