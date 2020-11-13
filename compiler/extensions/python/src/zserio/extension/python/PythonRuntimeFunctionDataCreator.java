package zserio.extension.python;

import zserio.ast.BooleanType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FixedSizeType;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.TypeInstantiation;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

final class PythonRuntimeFunctionDataCreator
{
    public static RuntimeFunctionTemplateData createData(TypeInstantiation typeInstantiation,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            return mapDynamicBitField(
                    (DynamicBitFieldInstantiation)typeInstantiation, pythonExpressionFormatter);
        }
        else
        {
            final Visitor visitor = new Visitor();
            typeInstantiation.getBaseType().accept(visitor);

            // template data can be null, this need to be handled specially in template
            return visitor.getTemplateData();
        }
    }

    private static RuntimeFunctionTemplateData mapDynamicBitField(DynamicBitFieldInstantiation instantiation,
            ExpressionFormatter pythonExpressionFormatter) throws ZserioExtensionException
    {
        final String suffix = getSuffixForIntegralType(instantiation.getBaseType().isSigned());
        final String arg = pythonExpressionFormatter.formatGetter(instantiation.getLengthExpression());
        return new RuntimeFunctionTemplateData(suffix, arg);
    }

    private static String getSuffixForIntegralType(boolean signed)
    {
        return signed ? "SignedBits" : "Bits";
    }

    private static class Visitor extends ZserioAstDefaultVisitor
    {
        public RuntimeFunctionTemplateData getTemplateData()
        {
            return templateData;
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
            handleFixedIntegerType(type, type.isSigned());
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            handleFixedIntegerType(type, type.isSigned());
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

        private void handleFixedIntegerType(FixedSizeType type, boolean isSigned)
        {
            final int bitCount = type.getBitSize();
            final String suffix = getSuffixForIntegralType(isSigned);
            final String arg = PythonLiteralFormatter.formatDecimalLiteral(bitCount);
            templateData = new RuntimeFunctionTemplateData(suffix, arg);
        }

        private RuntimeFunctionTemplateData templateData = null;
    }
}
