package zserio.emit.cpp;

import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.VarIntegerType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class CppRuntimeFunctionDataCreator
{
    /**
     * This function generates the suffix for Zserio C++ runtime functions based on the actual type passed.
     *
     * @throws ZserioEmitException Throws in case of wrong passed Zserio type.
     */
    public static RuntimeFunctionTemplateData createData(ZserioType type,
            ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        final Visitor visitor = new Visitor(cppExpressionFormatter);
        type.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        // template data can be null, this need to be handled specially in template
        return visitor.getTemplateData();
    }

    private static class Visitor extends ZserioAstDefaultVisitor
    {
        public Visitor(ExpressionFormatter cppExpressionFormatter)
        {
            this.cppExpressionFormatter = cppExpressionFormatter;
        }

        public RuntimeFunctionTemplateData getTemplateData()
        {
            return templateData;
        }

        public ZserioEmitException getThrownException()
        {
            return thrownException;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            templateData = new RuntimeFunctionTemplateData("Bool");
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            templateData = new RuntimeFunctionTemplateData("Float" + type.getBitSize());
        }

        @Override
        public void visitBitFieldType(BitFieldType type)
        {
            handleBitFieldType(type);
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            final int bitCount = type.getBitSize();
            final String suffix = getSuffixForIntegralType(bitCount, type.isSigned());
            try
            {
                final String arg = CppLiteralFormatter.formatUInt8Literal(bitCount);
                templateData = new RuntimeFunctionTemplateData(suffix, arg);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitStringType(StringType type)
        {
            templateData = new RuntimeFunctionTemplateData("String");
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            final StringBuilder sb = new StringBuilder();

            sb.append("Var");
            if (!type.isSigned())
                sb.append("U");
            sb.append("Int");
            final int maxBitSize = type.getMaxBitSize();
            if (maxBitSize < 72) // Var(U)Int takes up to 9 bytes
                sb.append(maxBitSize);

            templateData = new RuntimeFunctionTemplateData(sb.toString());
        }

        private void handleBitFieldType(BitFieldType type)
        {
            final String suffix = getSuffixForIntegralType(type.getMaxBitSize(), type.isSigned());
            try
            {
                final String arg = cppExpressionFormatter.formatGetter(type.getLengthExpression());
                templateData = new RuntimeFunctionTemplateData(suffix, arg);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        private static String getSuffixForIntegralType(int maxBitCount, boolean signed)
        {
            if (maxBitCount <= 32)
            {
                return signed ? "SignedBits" : "Bits";
            }
            else
            {
                return signed ? "SignedBits64" : "Bits64";
            }
        }

        private final ExpressionFormatter cppExpressionFormatter;

        private RuntimeFunctionTemplateData templateData = null;
        private ZserioEmitException thrownException = null;
    }
}
