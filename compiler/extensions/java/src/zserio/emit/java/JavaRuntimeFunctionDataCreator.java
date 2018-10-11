package zserio.emit.java;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeVisitor;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.FunctionType;
import zserio.ast.ServiceType;
import zserio.ast.StructureType;
import zserio.ast.SignedBitFieldType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.UnsignedBitFieldType;
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
        type.callVisitor(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        // template data can be null, this need to be handled specially in template
        return visitor.getTemplateData();
    }

    private static class Visitor implements ZserioTypeVisitor
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
        public void visitArrayType(ArrayType type)
        {
            // do nothing
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            templateData = new RuntimeFunctionTemplateData("Bool");
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            // do nothing
        }

        @Override
        public void visitConstType(ConstType type)
        {
            // do nothing
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            // do nothing
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            templateData = new RuntimeFunctionTemplateData("Float" + type.getBitSize());
        }

        @Override
        public void visitFunctionType(FunctionType type)
        {
            // do nothing
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            // do nothing
        }

        @Override
        public void visitSignedBitFieldType(SignedBitFieldType type)
        {
            handleBitFieldType(type);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            // do nothing
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            // do nothing
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
                                JavaLiteralFormatter.formatIntegerLiteral(bitCount));
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
        public void visitStructureType(StructureType type)
        {
            // do nothing
        }

        @Override
        public void visitSubtype(Subtype type)
        {
            // do nothing
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            // do nothing
        }

        @Override
        public void visitTypeReference(TypeReference type)
        {
            // do nothing
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            // do nothing
        }

        @Override
        public void visitUnsignedBitFieldType(UnsignedBitFieldType type)
        {
            handleBitFieldType(type);
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
