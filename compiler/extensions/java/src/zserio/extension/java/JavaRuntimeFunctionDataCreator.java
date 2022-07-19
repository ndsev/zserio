package zserio.extension.java;

import zserio.ast.BooleanType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.VarIntegerType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * Create RuntimeFunctionTemplateData needed for generating of calls to Zserio runtime
 * (e.g. for reading and writing of built-in types).
 */
public class JavaRuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(TemplateDataContext context,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
            return RuntimeFunctionSuffixVisitor.mapDynamicBitField(
                    (DynamicBitFieldInstantiation)typeInstantiation, javaExpressionFormatter, javaNativeMapper);
        }
        else
        {
            final RuntimeFunctionSuffixVisitor visitor = new RuntimeFunctionSuffixVisitor(javaNativeMapper);
            typeInstantiation.getBaseType().accept(visitor);

            final ZserioExtensionException thrownException = visitor.getThrownException();
            if (thrownException != null)
                throw thrownException;

            // template data can be null, this need to be handled specially in template
            return visitor.getTemplateData();
        }
    }

    public static RuntimeFunctionTemplateData createTypeInfoData(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            return BuiltinTypeInfoSuffixVisitor.mapDynamicBitFieldType(
                    (DynamicBitFieldInstantiation)typeInstantiation);
        }
        else
        {
            return createTypeInfoData(typeInstantiation.getTypeReference());
        }
    }

    public static RuntimeFunctionTemplateData createTypeInfoData(TypeReference typeReference)
            throws ZserioExtensionException
    {
        final BuiltinTypeInfoSuffixVisitor visitor = new BuiltinTypeInfoSuffixVisitor();
        typeReference.getBaseTypeReference().getType().accept(visitor);

        final ZserioExtensionException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        final RuntimeFunctionTemplateData templateData = visitor.getTemplateData();
        if (templateData == null)
        {
            throw new ZserioExtensionException("Cannot map type '" + typeReference.getType().getName() +
                    "' in createTypeInfoData!");
        }

        return templateData;
    }

    private static abstract class VisitorBase extends ZserioAstDefaultVisitor
    {
        public RuntimeFunctionTemplateData getTemplateData()
        {
            return templateData;
        }

        public ZserioExtensionException getThrownException()
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
        public void visitExternType(ExternType type)
        {
            templateData = new RuntimeFunctionTemplateData("BitBuffer");
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
            final int maxBitSize = type.getMaxBitSize();
            suffix.append("Var");
            if (maxBitSize == 40) // VarSize
            {
                suffix.append("Size");
            }
            else
            {
                if (!type.isSigned())
                    suffix.append("U");
                suffix.append("Int");
                if (maxBitSize != 72) // Var(U)Int takes up to 9 bytes
                    suffix.append(maxBitSize);
            }

            templateData = new RuntimeFunctionTemplateData(suffix.toString());
        }

        protected RuntimeFunctionTemplateData templateData = null;
        protected ZserioExtensionException thrownException = null;
    }

    private static class RuntimeFunctionSuffixVisitor extends VisitorBase
    {
        public RuntimeFunctionSuffixVisitor(JavaNativeMapper javaNativeMapper)
        {
            this.javaNativeMapper = javaNativeMapper;
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            handleFixedIntegerType(type, type.getBitSize());
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            handleFixedIntegerType(type, type.getBitSize());
        }

        private void handleFixedIntegerType(IntegerType type, int bitSize)
        {
            if (type.isSigned())
            {
                switch (bitSize)
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
                    try
                    {
                        final String suffix = "SignedBits";
                        final String arg = JavaLiteralFormatter.formatIntLiteral(bitSize);
                        templateData = new RuntimeFunctionTemplateData(
                                suffix, arg, javaNativeMapper.getJavaType(type).getFullName());
                    }
                    catch (ZserioExtensionException exception)
                    {
                        thrownException = exception;
                    }
                    break;
                }
            }
            else
            {
                switch (bitSize)
                {
                case 8:
                    templateData = new RuntimeFunctionTemplateData("UnsignedByte");
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
                                JavaLiteralFormatter.formatIntLiteral(bitSize));
                    }
                    catch (ZserioExtensionException exception)
                    {
                        thrownException = exception;
                    }
                    break;

                default:
                    try
                    {
                        final String suffix = "Bits";
                        final String arg = JavaLiteralFormatter.formatIntLiteral(bitSize);
                        templateData = new RuntimeFunctionTemplateData(
                                suffix, arg, javaNativeMapper.getJavaType(type).getFullName());
                    }
                    catch (ZserioExtensionException exception)
                    {
                        thrownException = exception;
                    }
                    break;
                }
            }
        }

        public static RuntimeFunctionTemplateData mapDynamicBitField(DynamicBitFieldInstantiation instantiation,
                ExpressionFormatter javaExpressionFormatter, JavaNativeMapper javaNativeMapper)
                        throws ZserioExtensionException
        {
            final String suffix = (instantiation.getBaseType().isSigned()) ? "SignedBits" :
                (instantiation.getMaxBitSize() > 63) ? "BigInteger" : "Bits";
            // this int cast is necessary because length can be bigger than integer (uint64, uint32)
            final String arg = "(int)" + "(" +
                    javaExpressionFormatter.formatGetter(instantiation.getLengthExpression()) + ")";
            return new RuntimeFunctionTemplateData(
                    suffix, arg, javaNativeMapper.getJavaType(instantiation).getFullName());
        }

        private final JavaNativeMapper javaNativeMapper;
    }

    private static class BuiltinTypeInfoSuffixVisitor extends VisitorBase
    {
        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            try
            {
                templateData = mapFixedBitFieldType(type.isSigned(), type.getBitSize());
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            try
            {
                templateData = mapDynamicBitFieldType(type.isSigned(), DynamicBitFieldType.MAX_BIT_SIZE);
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            final StringBuilder suffix = new StringBuilder();
            if (!type.isSigned())
                suffix.append("U");
            suffix.append("Int");
            suffix.append(type.getBitSize());

            templateData = new RuntimeFunctionTemplateData(suffix.toString());
        }

        public static RuntimeFunctionTemplateData mapDynamicBitFieldType(
                DynamicBitFieldInstantiation instantiation) throws ZserioExtensionException
        {
            return mapDynamicBitFieldType(instantiation.getBaseType().isSigned(),
                    instantiation.getMaxBitSize());
        }

        private static RuntimeFunctionTemplateData mapDynamicBitFieldType(boolean isSigned, int bitSize)
                throws ZserioExtensionException
        {
            return mapBitFieldType("Dynamic", isSigned, bitSize);
        }

        private static RuntimeFunctionTemplateData mapFixedBitFieldType(boolean isSigned, int bitSize)
                throws ZserioExtensionException
        {
            return mapBitFieldType("Fixed", isSigned, bitSize);
        }
        private static RuntimeFunctionTemplateData mapBitFieldType(String prefix, boolean isSigned, int bitSize)
                throws ZserioExtensionException
        {
            final StringBuilder suffix = new StringBuilder(prefix);
            if (isSigned)
                suffix.append("Signed");
            else
                suffix.append("Unsigned");
            suffix.append("BitField");

            return new RuntimeFunctionTemplateData(suffix.toString(),
                    JavaLiteralFormatter.formatIntLiteral(bitSize));
        }
    }
}
