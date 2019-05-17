package zserio.emit.python;

import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

final class PythonRuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(ZserioType type,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioEmitException
    {
        final Visitor visitor = new Visitor(pythonExpressionFormatter);
        type.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        // template data can be null, this need to be handled specially in template
        return visitor.getTemplateData();
    }

    private static class Visitor extends ZserioAstDefaultVisitor
    {
        public Visitor(ExpressionFormatter pythonExpressionFormatter)
        {
            this.pythonExpressionFormatter = pythonExpressionFormatter;
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
            final String suffix = getSuffixForIntegralType(type.isSigned());
            final String arg = PythonLiteralFormatter.formatDecimalLiteral(bitCount);
            templateData = new RuntimeFunctionTemplateData(suffix, arg);
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
            final String suffix = getSuffixForIntegralType(type.isSigned());
            try
            {
                final String arg = pythonExpressionFormatter.formatGetter(type.getLengthExpression());
                templateData = new RuntimeFunctionTemplateData(suffix, arg);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        private static String getSuffixForIntegralType(boolean signed)
        {
            return signed ? "SignedBits" : "Bits";
        }

        private final ExpressionFormatter pythonExpressionFormatter;
        private RuntimeFunctionTemplateData templateData = null;
        private ZserioEmitException thrownException = null;
    }
}
