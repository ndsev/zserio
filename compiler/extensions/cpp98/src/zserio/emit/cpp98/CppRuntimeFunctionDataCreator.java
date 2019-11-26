package zserio.emit.cpp98;

import zserio.ast.BooleanType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioAstDefaultVisitor;
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
    public static RuntimeFunctionTemplateData createData(TypeInstantiation typeInstantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            return mapDynamicBitField((DynamicBitFieldInstantiation)typeInstantiation, cppExpressionFormatter);
        }
        else
        {
            final Visitor visitor = new Visitor();
            typeInstantiation.getBaseType().accept(visitor);

            final ZserioEmitException thrownException = visitor.getThrownException();
            if (thrownException != null)
                throw thrownException;

            // template data can be null, this need to be handled specially in template
            return visitor.getTemplateData();
        }
    }

    private static RuntimeFunctionTemplateData mapDynamicBitField(DynamicBitFieldInstantiation instantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioEmitException
    {
        final DynamicBitFieldType type = instantiation.getBaseType();
        final String suffix = getSuffixForIntegralType(instantiation.getMaxBitSize(), type.isSigned());
        final String arg = cppExpressionFormatter.formatGetter(instantiation.getLengthExpression());
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

        private void handleFixedIntegerType(int bitSize, boolean isSigned)
        {
            final String suffix = getSuffixForIntegralType(bitSize, isSigned);
            try
            {
                final String arg = CppLiteralFormatter.formatUInt8Literal(bitSize);
                templateData = new RuntimeFunctionTemplateData(suffix, arg);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        private RuntimeFunctionTemplateData templateData = null;
        private ZserioEmitException thrownException = null;
    }
}
