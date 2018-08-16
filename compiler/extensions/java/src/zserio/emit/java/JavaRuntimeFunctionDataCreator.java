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

public class JavaRuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(ZserioType type,
            ExpressionFormatter javaExpressionFormatter, JavaNativeTypeMapper javaNativeTypeMapper)
    {
        final Visitor visitor = new Visitor(javaExpressionFormatter, javaNativeTypeMapper);
        type.callVisitor(visitor);

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

        @Override
        public void visitUnsignedBitFieldType(UnsignedBitFieldType type)
        {
            handleBitFieldType(type);
        }

        @Override
        public void visitSignedBitFieldType(SignedBitFieldType type)
        {
            handleBitFieldType(type);
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            final int bitCount = type.getBitSize();
            String javaReadTypeName = null;
            String arg = null;
            String suffix = null;
            if (type.isSigned())
            {
                switch (bitCount)
                {
                case 8:
                    suffix = "Byte";
                    break;

                case 16:
                    suffix = "Short";
                    break;

                case 32:
                    suffix = "Int";
                    break;

                case 64:
                    suffix = "Long";
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
                    suffix =  "UnsignedByte";
                    javaReadTypeName = getJavaReadTypeName(type);
                    break;

                case 16:
                    suffix =  "UnsignedShort";
                    break;

                case 32:
                    suffix =  "UnsignedInt";
                    break;

                case 64:
                    suffix =  "BigInteger";
                    arg = JavaLiteralFormatter.formatIntegerLiteral(bitCount);
                    break;

                default:
                    break;
                }
            }

            if (suffix != null)
                templateData = new RuntimeFunctionTemplateData(suffix, arg, javaReadTypeName);
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            handleBool();
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            templateData = new RuntimeFunctionTemplateData("Float" + type.getBitSize());
        }

        @Override
        public void visitArrayType(ArrayType type)
        {
            // arrays need to be handled specially in template
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
        public void visitStructureType(StructureType type)
        {
            // do nothing
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            // do nothing
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            // do nothing
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

        private void handleBool()
        {
            templateData = new RuntimeFunctionTemplateData("Bool");
        }

        private void handleBitFieldType(BitFieldType type)
        {
            final String suffix = type.isSigned() ? "SignedBits" : "Bits";
            final String arg = javaExpressionFormatter.formatGetter(type.getLengthExpression());
            templateData = new RuntimeFunctionTemplateData(suffix, arg, getJavaReadTypeName(type));
        }

        private String getJavaReadTypeName(ZserioType type)
        {
            return javaNativeTypeMapper.getJavaType(type).getFullName();
        }

        private final ExpressionFormatter   javaExpressionFormatter;
        private final JavaNativeTypeMapper  javaNativeTypeMapper;

        private RuntimeFunctionTemplateData templateData;
    }
}
