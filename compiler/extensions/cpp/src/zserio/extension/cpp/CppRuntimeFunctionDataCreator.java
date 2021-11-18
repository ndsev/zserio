package zserio.extension.cpp;

import zserio.ast.BooleanType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.VarIntegerType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

public class CppRuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(TypeInstantiation typeInstantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
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

        private RuntimeFunctionTemplateData templateData = null;
        private ZserioExtensionException thrownException = null;
    }
}
