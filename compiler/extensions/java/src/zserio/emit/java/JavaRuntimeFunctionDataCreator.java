package zserio.emit.java;

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

public class JavaRuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(ZserioType type,
            ExpressionFormatter javaExpressionFormatter, JavaNativeTypeMapper javaNativeTypeMapper)
                    throws ZserioEmitException
    {
        final Visitor visitor = new Visitor(javaExpressionFormatter, javaNativeTypeMapper);
        type.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        // template data can be null, this need to be handled specially in template
        return visitor.getTemplateData();
    }

    private static class Visitor extends ZserioAstDefaultVisitor
    {
        public Visitor(ExpressionFormatter javaExpressionFormatter, JavaNativeTypeMapper javaNativeTypeMapper)
        {
            this.javaExpressionFormatter = javaExpressionFormatter;
            this.javaNativeTypeMapper = javaNativeTypeMapper;
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
            if (type.isSigned())
            {
                switch (bitCount)
                {
                case 8:
                    templateData = new RuntimeFunctionTemplateData("Byte");
                    break;

                case 16:
                    templateData = new RuntimeFunctionTemplateData("Short");
                    break;

                case 32:
                    templateData = new RuntimeFunctionTemplateData("Int");
                    break;

                case 64:
                    templateData = new RuntimeFunctionTemplateData("Long");
                    break;

                default:
                    break;
                }
            }
            else
            {
                switch (bitCount)
                {
                case 8:
                    try
                    {
                        templateData = new RuntimeFunctionTemplateData("UnsignedByte", null,
                                getJavaReadTypeName(type));
                    }
                    catch (ZserioEmitException exception)
                    {
                        thrownException = exception;
                    }
                    break;

                case 16:
                    templateData = new RuntimeFunctionTemplateData("UnsignedShort");
                    break;

                case 32:
                    templateData = new RuntimeFunctionTemplateData("UnsignedInt");
                    break;

                case 64:
                    try
                    {
                        templateData = new RuntimeFunctionTemplateData("BigInteger",
                                JavaLiteralFormatter.formatDecimalLiteral(bitCount));
                    }
                    catch (ZserioEmitException exception)
                    {
                        thrownException = exception;
                    }
                    break;

                default:
                    break;
                }
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
            final StringBuilder suffix = new StringBuilder();
            suffix.append("Var");
            if (!type.isSigned())
                suffix.append("U");
            suffix.append("Int");
            final int maxBitSize = type.getMaxBitSize();
            if (maxBitSize < 72) // Var(U)Int takes up to 9 bytes
                suffix.append(maxBitSize);

            templateData = new RuntimeFunctionTemplateData(suffix.toString());
        }

        private void handleBitFieldType(BitFieldType type)
        {
            try
            {
                final String suffix = type.isSigned() ? "SignedBits" : "Bits";
                final String arg = javaExpressionFormatter.formatGetter(type.getLengthExpression());
                templateData = new RuntimeFunctionTemplateData(suffix, arg, getJavaReadTypeName(type));
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        private String getJavaReadTypeName(ZserioType type) throws ZserioEmitException
        {
            return javaNativeTypeMapper.getJavaType(type).getFullName();
        }

        private final ExpressionFormatter javaExpressionFormatter;
        private final JavaNativeTypeMapper javaNativeTypeMapper;

        private RuntimeFunctionTemplateData templateData = null;
        private ZserioEmitException thrownException = null;
    }
}
