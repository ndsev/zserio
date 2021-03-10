package zserio.extension.python;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.Constant;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Package;
import zserio.ast.Parameter;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.extension.python.types.PythonNativeType;
import zserio.extension.common.ExpressionFormattingPolicy;

public class PythonExpressionFormattingPolicy implements ExpressionFormattingPolicy
{
    public PythonExpressionFormattingPolicy(TemplateDataContext context, ImportCollector importCollector)
    {
        this.context = context;
        this.importCollector = importCollector;
    }

    // atom expressions formatting

    @Override
    public String getDecimalLiteral(Expression expr, boolean isNegative)
    {
        // decimal literals in Python are the same
        return expr.getText();
    }

    @Override
    public String getBinaryLiteral(Expression expr, boolean isNegative)
    {
        // binary literals in Python are the same (with prefix "0b")
        return PYTHON_BINARY_LITERAL_PREFIX + expr.getText();
    }

    @Override
    public String getHexadecimalLiteral(Expression expr, boolean isNegative)
    {
        // hexadecimal literals in Python are the same (with prefix "0x")
        return PYTHON_HEXADECIMAL_LITERAL_PREFIX + expr.getText();
    }

    @Override
    public String getOctalLiteral(Expression expr, boolean isNegative)
    {
        // octal literals in Python are the same (with prefix "0o")
        return PYTHON_OCTAL_LITERAL_PREFIX + expr.getText();
    }

    @Override
    public String getFloatLiteral(Expression expr, boolean isNegative)
    {
        // Python doesn't have float literals, use double
        return getDoubleLiteral(expr, isNegative);
    }

    @Override
    public String getDoubleLiteral(Expression expr, boolean isNegative)
    {
        // double literals in Python are the same (no suffix)
        return expr.getText();
    }

    @Override
    public String getBoolLiteral(Expression expr)
    {
        final String boolLiteral = expr.getText();
        return PythonLiteralFormatter.formatBooleanLiteral(boolLiteral.equals("true"));
    }

    @Override
    public String getStringLiteral(Expression expr)
    {
        // string literals supports both unicode ('\u0000') and hexadecimal ('\x42') escapes
        return expr.getText();
    }

    @Override
    public String getIndex(Expression expr)
    {
        return "index";
    }

    @Override
    public String getIdentifier(Expression expr, boolean isLastInDot, boolean isSetter)
            throws ZserioExtensionException
    {
        final StringBuilder result = new StringBuilder();
        final String symbol = expr.getText();
        final Object resolvedSymbol = expr.getExprSymbolObject();
        if (resolvedSymbol instanceof ZserioType)
        {
            // package identifiers are part of this Zserio type
            formatTypeIdentifier(result, (ZserioType)resolvedSymbol);
        }
        else if (!(resolvedSymbol instanceof Package))
        {
            formatSymbolIdentifier(result, symbol, expr.isMostLeftId(), resolvedSymbol,
                    expr.getExprZserioType(), isSetter);
        }

        return result.toString();
    }

    // unary expressions formatting

    @Override
    public UnaryExpressionFormatting getBigIntegerCastingToNative(Expression expr)
    {
        return new UnaryExpressionFormatting("");
    }

    @Override
    public UnaryExpressionFormatting getUnaryPlus(Expression expr)
    {
        return new UnaryExpressionFormatting("+");
    }

    @Override
    public UnaryExpressionFormatting getUnaryMinus(Expression expr)
    {
        return new UnaryExpressionFormatting("-");
    }

    @Override
    public UnaryExpressionFormatting getTilde(Expression expr)
    {
        return new UnaryExpressionFormatting("~");
    }

    @Override
    public UnaryExpressionFormatting getBang(Expression expr)
    {
        return new UnaryExpressionFormatting("not ");
    }

    @Override
    public UnaryExpressionFormatting getLeftParenthesis(Expression expr)
    {
        return new UnaryExpressionFormatting("(", ")");
    }

    @Override
    public UnaryExpressionFormatting getFunctionCall(Expression expr)
    {
        return new UnaryExpressionFormatting("", "()");
    }

    @Override
    public UnaryExpressionFormatting getLengthOf(Expression expr)
    {
        return new UnaryExpressionFormatting("len(", ")");
    }

    @Override
    public UnaryExpressionFormatting getValueOf(Expression expr) throws ZserioExtensionException
    {
        if (expr.op1().getExprType() == Expression.ExpressionType.ENUM)
        {
            return new UnaryExpressionFormatting("", ".value");
        }
        else if (expr.op1().getExprType() == Expression.ExpressionType.BITMASK)
        {
            // TODO[Mi-L@]: Use parentheses only when needed (i.e when applied on another expression)
            return new UnaryExpressionFormatting("(", ").value");
        }
        else
        {
            throw new ZserioExtensionException("Unexpected expression in valueof operator!");
        }
    }

    @Override
    public UnaryExpressionFormatting getNumBits(Expression expr)
    {
        importCollector.importPackage("zserio");
        return new UnaryExpressionFormatting("zserio.builtin.numbits(", ")");
    }

    // binary expressions formatting

    @Override
    public BinaryExpressionFormatting getComma(Expression expr)
    {
        return new BinaryExpressionFormatting(", ");
    }

    @Override
    public BinaryExpressionFormatting getLogicalOr(Expression expr)
    {
        return new BinaryExpressionFormatting(" or ");
    }

    @Override
    public BinaryExpressionFormatting getLogicalAnd(Expression expr)
    {
        return new BinaryExpressionFormatting(" and ");
    }

    @Override
    public BinaryExpressionFormatting getOr(Expression expr)
    {
        return new BinaryExpressionFormatting(" | ");
    }

    @Override
    public BinaryExpressionFormatting getXor(Expression expr)
    {
        return new BinaryExpressionFormatting(" ^ ");
    }

    @Override
    public BinaryExpressionFormatting getAnd(Expression expr)
    {
        return new BinaryExpressionFormatting(" & ");
    }

    @Override
    public BinaryExpressionFormatting getEq(Expression expr)
    {
        return new BinaryExpressionFormatting(" == ");
    }

    @Override
    public BinaryExpressionFormatting getNe(Expression expr)
    {
        return new BinaryExpressionFormatting(" != ");
    }

    @Override
    public BinaryExpressionFormatting getLt(Expression expr)
    {
        return new BinaryExpressionFormatting(" < ");
    }

    @Override
    public BinaryExpressionFormatting getLe(Expression expr)
    {
        return new BinaryExpressionFormatting(" <= ");
    }

    @Override
    public BinaryExpressionFormatting getGe(Expression expr)
    {
        return new BinaryExpressionFormatting(" >= ");
    }

    @Override
    public BinaryExpressionFormatting getGt(Expression expr)
    {
        return new BinaryExpressionFormatting(" > ");
    }

    @Override
    public BinaryExpressionFormatting getLeftShift(Expression expr)
    {
        return new BinaryExpressionFormatting(" << ");
    }

    @Override
    public BinaryExpressionFormatting getRightShift(Expression expr)
    {
        return new BinaryExpressionFormatting(" >> ");
    }

    @Override
    public BinaryExpressionFormatting getPlus(Expression expr)
    {
        return new BinaryExpressionFormatting(" + ");
    }

    @Override
    public BinaryExpressionFormatting getMinus(Expression expr)
    {
        return new BinaryExpressionFormatting(" - ");
    }

    @Override
    public BinaryExpressionFormatting getMultiply(Expression expr)
    {
        return new BinaryExpressionFormatting(" * ");
    }

    @Override
    public BinaryExpressionFormatting getDivide(Expression expr)
    {
        return new BinaryExpressionFormatting(" // ");
    }

    @Override
    public BinaryExpressionFormatting getModulo(Expression expr)
    {
        return new BinaryExpressionFormatting(" % ");
    }

    @Override
    public BinaryExpressionFormatting getArrayElement(Expression expr, boolean isSetter)
    {
        return new BinaryExpressionFormatting("", "[", (isSetter) ? "] = value" : "]");
    }

    @Override
    public BinaryExpressionFormatting getDot(Expression expr)
    {
        // ignore dots between package identifiers
        if (expr.op1().getExprZserioType() == null)
            return new BinaryExpressionFormatting("");

        return new BinaryExpressionFormatting(".");
    }

    // ternary expressions formatting

    @Override
    public TernaryExpressionFormatting getQuestionMark(Expression expr)
    {
        return new TernaryExpressionFormattingPython(expr, "(", ") if (", ") else (", ")");
    }

    protected void formatFieldAccessor(StringBuilder result, boolean isMostLeftId, Field field,
            boolean isSetter)
    {
        if (isMostLeftId)
            result.append(PYTHON_FUNCTION_CALL_PREFIX);

        if (isSetter)
        {
            result.append(AccessorNameFormatter.getPropertyName(field));
            result.append(PYTHON_PROPERTY_ASSIGNMENT);
        }
        else
        {
            result.append(AccessorNameFormatter.getPropertyName(field));
        }
    }

    private void formatParameterAccessor(StringBuilder result, boolean isMostLeftId, Parameter param)
    {
        if (isMostLeftId)
            result.append(PYTHON_FUNCTION_CALL_PREFIX);

        result.append(AccessorNameFormatter.getPropertyName(param));
    }

    private void formatEnumItem(StringBuilder result, boolean isMostLeftId, EnumItem enumItem,
            ZserioType exprType) throws ZserioExtensionException
    {
        // emit whole name if this is the first symbol in dot subtree, otherwise emit only enum short name
        if (isMostLeftId && exprType instanceof EnumType)
        {
            final EnumType enumType = (EnumType)exprType;
            final PythonNativeType nativeEnumType = context.getPythonNativeMapper().getPythonType(enumType);
            importCollector.importType(nativeEnumType);
            result.append(nativeEnumType.getFullName());
            result.append(".");
        }
        result.append(PythonSymbolConverter.enumItemToSymbol(enumItem.getName()));
    }

    private void formatBitmaskValue(StringBuilder result, boolean isMostLeftId, BitmaskValue bitmaskValue,
            ZserioType exprType) throws ZserioExtensionException
    {
        // emit whole name if this is the first symbol in dot subtree, otherwise emit only bitmask name
        if (isMostLeftId && exprType instanceof BitmaskType)
        {
            final BitmaskType bitmaskType = (BitmaskType)exprType;
            final PythonNativeType nativeBitmaskType =
                    context.getPythonNativeMapper().getPythonType(bitmaskType);
            importCollector.importType(nativeBitmaskType);
            result.append(nativeBitmaskType.getFullName());
            result.append(".");
        }
        result.append("Values.");
        result.append(PythonSymbolConverter.bitmaskValueToSymbol(bitmaskValue.getName()));
    }

    private void formatTypeIdentifier(StringBuilder result, ZserioType resolvedType)
            throws ZserioExtensionException
    {
        final PythonNativeType resolvedNativeType = context.getPythonNativeMapper().getPythonType(resolvedType);
        importCollector.importType(resolvedNativeType);
        result.append(resolvedNativeType.getFullName());
    }

    private void formatSymbolIdentifier(StringBuilder result, String symbol, boolean isMostLeftId,
            Object resolvedSymbol, ZserioType exprType, boolean isSetter) throws ZserioExtensionException
    {
        if (resolvedSymbol instanceof Parameter)
        {
            // [Parameter]
            final Parameter param = (Parameter)resolvedSymbol;
            formatParameterAccessor(result, isMostLeftId, param);
        }
        else if (resolvedSymbol instanceof Field)
        {
            // [Field]
            final Field field = (Field)resolvedSymbol;
            formatFieldAccessor(result, isMostLeftId, field, isSetter);
        }
        else if (resolvedSymbol instanceof EnumItem)
        {
            // EnumType.[ENUM_ITEM]
            final EnumItem enumItem = (EnumItem)resolvedSymbol;
            formatEnumItem(result, isMostLeftId, enumItem, exprType);
        }
        else if (resolvedSymbol instanceof BitmaskValue)
        {
            // BitmaskType.[BITMASK_VALUE]
            final BitmaskValue bitmaskValue = (BitmaskValue)resolvedSymbol;
            formatBitmaskValue(result, isMostLeftId, bitmaskValue, exprType);
        }
        else if (resolvedSymbol instanceof Function)
        {
            // [functionCall]()
            final Function function = (Function)resolvedSymbol;
            if (isMostLeftId)
                result.append(PYTHON_FUNCTION_CALL_PREFIX);
            result.append(AccessorNameFormatter.getFunctionName(function));
        }
        else if (resolvedSymbol instanceof Constant)
        {
            final Constant constant = (Constant)resolvedSymbol;
            formatConstant(result, constant);
        }
        else
        {
            // this could happen for "explicit identifier" expressions
            result.append(PythonSymbolConverter.toLowerSnakeCase(symbol));
        }
    }

    private void formatConstant(StringBuilder result, Constant constant) throws ZserioExtensionException
    {
        final PythonNativeSymbol nativeSymbol = context.getPythonNativeMapper().getPythonSymbol(constant);
        result.append(nativeSymbol.getFullName());
        importCollector.importSymbol(nativeSymbol);
    }

    private static class TernaryExpressionFormattingPython extends TernaryExpressionFormatting
    {
        public TernaryExpressionFormattingPython(Expression expression,
                String beforeOperand1, String afterOperand1, String afterOperand2, String afterOperand3)
        {
            super(expression, beforeOperand1, afterOperand1, afterOperand2, afterOperand3);
        }

        @Override
        public Expression getOperand1()
        {
            return super.getOperand2();
        }

        @Override
        public Expression getOperand2()
        {
            return super.getOperand1();
        }
    }

    private final TemplateDataContext context;
    private final ImportCollector importCollector;

    private final static String PYTHON_BINARY_LITERAL_PREFIX = "0b";
    private final static String PYTHON_HEXADECIMAL_LITERAL_PREFIX = "0x";
    private final static String PYTHON_OCTAL_LITERAL_PREFIX = "0o";

    private final static String PYTHON_PROPERTY_ASSIGNMENT = " = value";

    private final static String PYTHON_FUNCTION_CALL_PREFIX = "self.";
}
