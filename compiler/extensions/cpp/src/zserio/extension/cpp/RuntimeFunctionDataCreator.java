package zserio.extension.cpp;

import zserio.ast.BooleanType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
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
public class RuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(TemplateDataContext context,
            TypeInstantiation typeInstantiation, IncludeCollector includeCollector)
                    throws ZserioExtensionException
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            return mapDynamicBitField((DynamicBitFieldInstantiation)typeInstantiation, cppExpressionFormatter);
        }
        else
        {
            final Visitor visitor = new Visitor();
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

    private static RuntimeFunctionTemplateData mapDynamicBitField(DynamicBitFieldInstantiation instantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        final DynamicBitFieldType type = instantiation.getBaseType();
        final String suffix = getSuffixForIntegralType(instantiation.getMaxBitSize(), type.isSigned());
        final String arg = "static_cast<uint8_t>(" +
                    cppExpressionFormatter.formatGetter(instantiation.getLengthExpression()) + ")";
        return new RuntimeFunctionTemplateData(suffix, arg);
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

    private static class Visitor extends ZserioAstDefaultVisitor
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
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            handleFixedIntegerType(type.getBitSize(), type.isSigned());
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            handleFixedIntegerType(type.getBitSize(), type.isSigned());
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

        private void handleFixedIntegerType(int bitSize, boolean isSigned)
        {
            final String suffix = getSuffixForIntegralType(bitSize, isSigned);
            try
            {
                final String arg = CppLiteralFormatter.formatUInt8Literal(bitSize);
                templateData = new RuntimeFunctionTemplateData(suffix, arg);
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        protected RuntimeFunctionTemplateData templateData = null;
        protected ZserioExtensionException thrownException = null;
    }

    private static class BuiltinTypeInfoSuffixVisitor extends Visitor
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

        private static RuntimeFunctionTemplateData mapDynamicBitFieldType(boolean isSigned, int numBits)
                throws ZserioExtensionException
        {
            return mapBitFieldType("Dynamic", isSigned, numBits);
        }

        private static RuntimeFunctionTemplateData mapFixedBitFieldType(boolean isSigned, int numBits)
                throws ZserioExtensionException
        {
            return mapBitFieldType("Fixed", isSigned, numBits);
        }

        private static RuntimeFunctionTemplateData mapBitFieldType(String prefix, boolean isSigned, int numBits)
                throws ZserioExtensionException
        {
            final StringBuilder suffix = new StringBuilder(prefix);
            if (isSigned)
                suffix.append("Signed");
            else
                suffix.append("Unsigned");
            suffix.append("BitField");

            return new RuntimeFunctionTemplateData(suffix.toString(),
                    CppLiteralFormatter.formatUInt8Literal(numBits));
        }
    }
}
