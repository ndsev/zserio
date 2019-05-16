package zserio.emit.python;

import zserio.ast.ConstType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.FunctionType;
import zserio.ast.Package;
import zserio.ast.Parameter;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.ExpressionFormattingPolicy;
import zserio.emit.python.types.PythonNativeType;

public class PythonExpressionFormattingPolicy implements ExpressionFormattingPolicy
{
    public PythonExpressionFormattingPolicy(PythonNativeTypeMapper pythonNativeTypeMapper,
            ImportCollector importCollector)
    {
        this.pythonNativeTypeMapper = pythonNativeTypeMapper;
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
            throws ZserioEmitException
    {
        final StringBuilder result = new StringBuilder();
        final String symbol = expr.getText();
        final Object resolvedSymbol = expr.getExprSymbolObject();
        final ZserioType resolvedType = expr.getExprZserioType();
        final boolean isFirstInDot = (expr.getExprZserioType() != null); // first in a dot expression
        if (resolvedSymbol instanceof ZserioType)
        {
            // package identifiers are part of this Zserio type
            formatIdentifierForType(result, symbol, isFirstInDot, (ZserioType)resolvedSymbol);
        }
        else if (!(resolvedSymbol instanceof Package))
        {
            formatIdentifierForSymbol(result, symbol, isFirstInDot, resolvedSymbol, resolvedType, isSetter);
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
    public UnaryExpressionFormatting getSum(Expression expr)
    {
        return new UnaryExpressionFormatting("sum(", ")");
    }

    @Override
    public UnaryExpressionFormatting getValueOf(Expression expr)
    {
        return new UnaryExpressionFormatting("", ".value");
    }

    @Override
    public UnaryExpressionFormatting getNumBits(Expression expr)
    {
        importCollector.importPackage("zserio");
        return new UnaryExpressionFormatting("zserio.builtin.getNumBits(", ")");
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
        return new BinaryExpressionFormatting(" / ");
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

    protected void formatFieldAccessor(StringBuilder result, boolean isFirstInDot, Field field,
            boolean isSetter)
    {
        if (isFirstInDot)
            result.append(PYTHON_FUNCTION_CALL_PREFIX);

        if (isSetter)
        {
            result.append(AccessorNameFormatter.getSetterName(field));
            result.append(PYTHON_SETTER_FUNCTION_CALL);
        }
        else
        {
            result.append(AccessorNameFormatter.getGetterName(field));
            result.append(PYTHON_GETTER_FUNCTION_CALL);
        }
    }

    private void formatIdentifierForType(StringBuilder result, String symbol, boolean isFirstInDot,
            ZserioType identifierType) throws ZserioEmitException
    {
        if (identifierType instanceof Subtype)
        {
            // subtype
            final Subtype subtype = (Subtype)identifierType;
            final PythonNativeType nativeSubtype = pythonNativeTypeMapper.getPythonType(subtype);
            importCollector.importType(nativeSubtype);
            result.append(nativeSubtype.getFullName());
        }
        else if (identifierType instanceof EnumType)
        {
            // [EnumType].ENUM_ITEM
            final EnumType enumType = (EnumType)identifierType;
            final PythonNativeType nativeEnumType = pythonNativeTypeMapper.getPythonType(enumType);
            importCollector.importType(nativeEnumType);
            result.append(nativeEnumType.getFullName());
        }
        else if (identifierType instanceof ConstType)
        {
            // [ConstName]
            final ConstType constType = (ConstType)identifierType;
            final PythonNativeType nativeConstType = pythonNativeTypeMapper.getPythonType(constType);
            importCollector.importType(nativeConstType);
            result.append(nativeConstType.getFullName());
        }
        else if (identifierType instanceof FunctionType)
        {
            // [functionCall]()
            final FunctionType functionType = (FunctionType)identifierType;
            if (isFirstInDot)
                result.append(PYTHON_FUNCTION_CALL_PREFIX);
            result.append(AccessorNameFormatter.getFunctionName(functionType));
        }
        else
        {
            result.append(symbol);
        }
    }

    private void formatIdentifierForSymbol(StringBuilder result, String symbol, boolean isFirstInDot,
            Object resolvedSymbol, ZserioType resolvedType, boolean isSetter) throws ZserioEmitException
    {
        if (resolvedSymbol instanceof Parameter)
        {
            final Parameter param = (Parameter)resolvedSymbol;
            formatParameterAccessor(result, isFirstInDot, param, isSetter);
        }
        else if (resolvedSymbol instanceof Field)
        {
            final Field field = (Field)resolvedSymbol;
            formatFieldAccessor(result, isFirstInDot, field, isSetter);
        }
        else if (resolvedSymbol instanceof EnumItem)
        {
            // EnumType.[ENUM_ITEM]
            // emit the whole name if this is the first symbol in this dot subtree, otherwise emit only the
            // enum short name
            final EnumItem item = (EnumItem)resolvedSymbol;
            if (isFirstInDot && resolvedType instanceof EnumType)
            {
                final EnumType enumType = (EnumType)resolvedType;
                final PythonNativeType nativeEnumType = pythonNativeTypeMapper.getPythonType(enumType);
                importCollector.importType(nativeEnumType);
                result.append(nativeEnumType.getFullName());
                result.append(".");
            }
            result.append(item.getName());
        }
        else
        {
            // this could happen for "explicit identifier" expressions
            result.append(symbol);
        }
    }

    private void formatParameterAccessor(StringBuilder result, boolean isFirstInDot, Parameter param,
            boolean isSetter)
    {
        if (isFirstInDot)
            result.append(PYTHON_FUNCTION_CALL_PREFIX);

        result.append(AccessorNameFormatter.getGetterName(param));
        result.append(PYTHON_GETTER_FUNCTION_CALL);
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

    private final PythonNativeTypeMapper pythonNativeTypeMapper;
    private final ImportCollector importCollector;

    private final static String PYTHON_BINARY_LITERAL_PREFIX = "0b";
    private final static String PYTHON_HEXADECIMAL_LITERAL_PREFIX = "0x";
    private final static String PYTHON_OCTAL_LITERAL_PREFIX = "0o";

    private final static String PYTHON_GETTER_FUNCTION_CALL = "()";
    private final static String PYTHON_SETTER_FUNCTION_CALL = "(value)";

    private final static String PYTHON_FUNCTION_CALL_PREFIX = "self.";
}
