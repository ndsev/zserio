package zserio.emit.cpp;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import zserio.ast.ConstType;
import zserio.ast.ZserioType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Package;
import zserio.ast.Parameter;
import zserio.emit.common.DefaultExpressionFormattingPolicy;
import zserio.emit.common.StringEscapeConverter;
import zserio.emit.cpp.types.CppNativeType;

/**
 * Default expressions formatting policy for C++ expressions.
 *
 * TODO Please note that literal formatters are duplicitly implemented by native types as well. The correct
 * solution would be to pass formatted type to Expression Formatter to be able to call literal formatters from
 * native types.
 */
public abstract class CppDefaultExpressionFormattingPolicy extends DefaultExpressionFormattingPolicy
{
    public CppDefaultExpressionFormattingPolicy(CppNativeTypeMapper cppNativeTypeMapper,
            IncludeCollector includeCollector)
    {
        this.cppNativeTypeMapper = cppNativeTypeMapper;
        this.includeCollector = includeCollector;
    }

    @Override
    public String getDecimalLiteral(Expression expr, boolean isNegative)
    {
        // decimal literals in C++ are the same but append "(U)L(L)" for (unsigned) long (long) values
        String decLiteral = expr.getText();
        final BigInteger decInBigInteger = new BigInteger(decLiteral);

        // special work around for INT64_MIN for INT32_MIN on 32-bit machines; these decimal literals can't be
        // written as a single decimal number without a warning (at least with gcc 4.4.3)
        if (isNegative && (decLiteral.equals(DECIMAL_LITERAL_ABS_INT64_MIN) ||
                decLiteral.equals(DECIMAL_LITERAL_ABS_INT32_MIN)))
            decLiteral = CPP_HEXADECIMAL_LITERAL_PREFIX + decInBigInteger.toString(16);

        return decLiteral + getIntegerLiteralSuffix(decInBigInteger, isNegative);
    }

    @Override
    public String getBinaryLiteral(Expression expr, boolean isNegative)
    {
        // binary literals are not supported by C++ => use hexadecimal instead of it
        String binaryLiteral = expr.getText();
        if (!binaryLiteral.isEmpty())
        {
            final String strippedBinaryLiteral = binaryLiteral.substring(0, binaryLiteral.length() - 1);
            final BigInteger binaryInBigInteger = new BigInteger(strippedBinaryLiteral, 2);
            binaryLiteral = CPP_HEXADECIMAL_LITERAL_PREFIX + binaryInBigInteger.toString(16);
        }

        return binaryLiteral;
    }

    @Override
    public String getHexadecimalLiteral(Expression expr, boolean isNegative)
    {
        // hexadecimal literals in C++ are the same but append "(U)L(L)" for (unsigned) long (long) values
        String hexLiteral = expr.getText();
        if (hexLiteral.length() > 2)
        {
            final BigInteger hexInBigInteger = new BigInteger(hexLiteral.substring(2, hexLiteral.length()), 16);
            hexLiteral += getIntegerLiteralSuffix(hexInBigInteger, isNegative);
        }

        return hexLiteral;
    }

    @Override
    public String getOctalLiteral(Expression expr, boolean isNegative)
    {
        // octal literals in C++ are the same
        return expr.getText();
    }

    @Override
    public String getFloatLiteral(Expression expr, boolean isNegative)
    {
        // float literals in C++ are the same (with postfix "f")
        return expr.getText();
    }

    @Override
    public String getDoubleLiteral(Expression expr, boolean isNegative)
    {
        // double literals in C++ are the same (without postfix)
        return expr.getText();
    }

    @Override
    public String getBoolLiteral(Expression expr)
    {
        // bool literals in C++ are the same
        return expr.getText();
    }

    @Override
    public String getStringLiteral(Expression expr)
    {
        // string literals in C++ does not support unicode escapes from interval <'\u0000', '\u0031'>
        return StringEscapeConverter.convertUnicodeToHexEscapes(expr.getText());
    }

    @Override
    public String getIndex(Expression expr)
    {
        return "_index";
    }

    @Override
    public String getIdentifier(Expression expr, boolean isLast, boolean isSetter)
    {
        // first try Zserio types then try identifier symbol objects
        final StringBuilder result = new StringBuilder();
        final String symbol = expr.getText();
        final Object resolvedSymbol = expr.getExprSymbolObject();
        final boolean isFirst = (expr.getExprZserioType() != null);
        if (resolvedSymbol instanceof ZserioType)
            formatIdentifierForType(result, symbol, isFirst, (ZserioType)resolvedSymbol);
        else if (!(resolvedSymbol instanceof Package))
            formatIdentifierForSymbol(result, symbol, isFirst, resolvedSymbol, isSetter);

        // ignore package identifiers, they will be a part of the following Zserio type

        return result.toString();
    }

    @Override
    public UnaryExpressionFormatting getFunctionCall(Expression expr)
    {
        return new UnaryExpressionFormatting(getAccessPrefix(), "()");
    }

    @Override
    public UnaryExpressionFormatting getLengthOf(Expression expr)
    {
        return new UnaryExpressionFormatting("", ".size()");
    }

    @Override
    public UnaryExpressionFormatting getSum(Expression expr)
    {
        return new UnaryExpressionFormatting("", ".sum()");
    }

    @Override
    public UnaryExpressionFormatting getExplicit(Expression expr)
    {
        return new UnaryExpressionFormatting("");
    }

    @Override
    public UnaryExpressionFormatting getNumBits(Expression expr)
    {
        final List<String> systemIncludes = Arrays.asList(BUILD_IN_OPERATORS_HEADER);
        includeCollector.addCppSystemIncludes(systemIncludes);

        return new UnaryExpressionFormatting("zserio::getNumBits(", ")");
    }

    @Override
    public BinaryExpressionFormatting getArrayElement(Expression expr, boolean isSetter)
    {
        return new BinaryExpressionFormatting("", ".at(", (isSetter) ? ") = _value" : ")");
    }

    @Override
    public BinaryExpressionFormatting getDot(Expression expr)
    {
        // ignore dots between package identifiers
        if (expr.op1().getExprZserioType() == null)
            return new BinaryExpressionFormatting("");

        // do special handling for enumeration items
        if (expr.op2().getExprSymbolObject() instanceof EnumItem)
            return new BinaryExpressionFormatting("::");

        return new BinaryExpressionFormatting(".");
    }

    protected abstract String getAccessPrefixForCompoundType();

    private void formatIdentifierForType(StringBuilder result, String symbol, boolean isFirst,
            ZserioType resolvedType)
    {
        if (resolvedType instanceof EnumType)
        {
            // [EnumType].ENUM_ITEM
            final EnumType enumType = (EnumType)resolvedType;
            final CppNativeType nativeEnumType = cppNativeTypeMapper.getCppType(enumType);
            result.append(nativeEnumType.getFullName());
        }
        else if (resolvedType instanceof ConstType)
        {
            // [ConstName]
            final ConstType constantType = (ConstType)resolvedType;
            final CppNativeType nativeConstType = cppNativeTypeMapper.getCppType(constantType);
            result.append(nativeConstType.getFullName());
        }
        else
        {
            // [functionCall]()
            result.append(symbol);
        }
    }

    private void formatIdentifierForSymbol(StringBuilder result, String symbol, boolean isFirst,
            Object resolvedSymbol, boolean isSetter)
    {
        if (resolvedSymbol instanceof Parameter)
        {
            final Parameter param = (Parameter)resolvedSymbol;
            formatParameterAccessor(result, isFirst, param, isSetter);
        }
        else if (resolvedSymbol instanceof Field)
        {
            final Field field = (Field)resolvedSymbol;
            formatFieldAccessor(result, isFirst, field, isSetter);
        }
        else if (resolvedSymbol instanceof EnumItem)
        {
            // EnumType.[ENUM_ITEM]
            // emit the whole name if this is the first symbol in this dot subtree, otherwise emit only the
            // enum short name
            final EnumItem item = (EnumItem)resolvedSymbol;
            if (isFirst)
            {
                final EnumType enumType = item.getEnumType();
                final CppNativeType nativeEnumType = cppNativeTypeMapper.getCppType(enumType);
                result.append(nativeEnumType.getFullName());
                result.append("::");
            }
            result.append(item.getName());
        }
        else
        {
            result.append(symbol);
        }
    }

    private String getIntegerLiteralSuffix(BigInteger literalValue, boolean isNegative)
    {
        String literalSuffix = "";
        final long maxAbsIntValue = (isNegative) ? -(long)Integer.MIN_VALUE : 0xFFFFFFFFL;
        if (literalValue.compareTo(BigInteger.valueOf(maxAbsIntValue)) > 0)
        {
            // long long value
            literalSuffix = (isNegative) ? CPP_SIGNED_LONG_LONG_LITERAL_SUFFIX :
                CPP_UNSIGNED_LONG_LONG_LITERAL_SUFFIX;
        }
        else
        {
            final long maxAbsShortValue = (isNegative) ? -(long)Short.MIN_VALUE : 0xFFFFL;
            if (literalValue.compareTo(BigInteger.valueOf(maxAbsShortValue)) > 0)
            {
                // long value
                literalSuffix = (isNegative) ? CPP_SIGNED_LONG_LITERAL_SUFFIX :
                    CPP_UNSIGNED_LONG_LITERAL_SUFFIX;
            }
        }

        return literalSuffix;
    }

    private String getAccessPrefix()
    {
        final String accessPrefix = getAccessPrefixForCompoundType();

        return (accessPrefix.isEmpty()) ? accessPrefix : accessPrefix + ".";
    }

    private void formatParameterAccessor(StringBuilder result, boolean isFirst, Parameter param,
            boolean isSetter)
    {
        if (isFirst)
            result.append(getAccessPrefix());

        if (isSetter)
        {
            result.append(AccessorNameFormatter.getSetterName(param));
            result.append(CPP_SETTER_FUNCTION_CALL);
        }
        else
        {
            result.append(AccessorNameFormatter.getGetterName(param));
            result.append(CPP_GETTER_FUNCTION_CALL);
        }
    }

    private void formatFieldAccessor(StringBuilder result, boolean isFirst, Field field, boolean isSetter)
    {
        if (isFirst)
            result.append(getAccessPrefix());

        if (isSetter)
        {
            result.append(AccessorNameFormatter.getSetterName(field));
            result.append(CPP_SETTER_FUNCTION_CALL);
        }
        else
        {
            result.append(AccessorNameFormatter.getGetterName(field));
            result.append(CPP_GETTER_FUNCTION_CALL);
        }
    }

    private final CppNativeTypeMapper cppNativeTypeMapper;
    private final IncludeCollector includeCollector;

    private final static String CPP_GETTER_FUNCTION_CALL = "()";
    private final static String CPP_SETTER_FUNCTION_CALL = "(_value)";

    private final static String BUILD_IN_OPERATORS_HEADER = "zserio/BuildInOperators.h";

    private final static String CPP_SIGNED_LONG_LITERAL_SUFFIX = "L";
    private final static String CPP_UNSIGNED_LONG_LITERAL_SUFFIX = "UL";
    private final static String CPP_SIGNED_LONG_LONG_LITERAL_SUFFIX = "LL";
    private final static String CPP_UNSIGNED_LONG_LONG_LITERAL_SUFFIX = "ULL";
    private final static String CPP_HEXADECIMAL_LITERAL_PREFIX = "0x";

    private final static String DECIMAL_LITERAL_ABS_INT64_MIN = "9223372036854775808";
    private final static String DECIMAL_LITERAL_ABS_INT32_MIN = "2147483648";
}
