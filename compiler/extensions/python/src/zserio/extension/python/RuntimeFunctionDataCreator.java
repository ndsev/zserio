package zserio.extension.python;

import zserio.ast.ArrayType;
import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.BytesType;
import zserio.ast.ChoiceType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FixedSizeType;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * Create RuntimeFunctionTemplateData needed for generating of calls to Zserio runtime for reading and writing
 * of built-in types.
 */
class RuntimeFunctionDataCreator
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

    public static RuntimeFunctionTemplateData createHashCodeData(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            if (((DynamicBitFieldInstantiation)typeInstantiation).getMaxBitSize() > 32)
                return new RuntimeFunctionTemplateData("int64");
            else
                return new RuntimeFunctionTemplateData("int32");
        }
        else
        {
            return createHashCodeData(typeInstantiation.getTypeReference());
        }
    }

    public static RuntimeFunctionTemplateData createHashCodeData(TypeReference typeReference)
            throws ZserioExtensionException
    {
        final HashCodeSuffixVisitor visitor = new HashCodeSuffixVisitor();
        typeReference.getBaseTypeReference().getType().accept(visitor);

        final RuntimeFunctionTemplateData templateData = visitor.getTemplateData();
        if (templateData == null)
        {
            throw new ZserioExtensionException("Cannot map type '" + typeReference.getType().getName() +
                    "' in createHashCodeData!");
        }

        return templateData;
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
        return signed ? "signed_bits" : "bits";
    }

    private static final class Visitor extends ZserioAstDefaultVisitor
    {
        public RuntimeFunctionTemplateData getTemplateData()
        {
            return templateData;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            templateData = new RuntimeFunctionTemplateData("bool");
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            templateData = new RuntimeFunctionTemplateData("float" + type.getBitSize());
        }

        @Override
        public void visitExternType(ExternType type)
        {
            templateData = new RuntimeFunctionTemplateData("bitbuffer");
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
        public void visitBytesType(BytesType type)
        {
            templateData = new RuntimeFunctionTemplateData("bytes");
        }

        @Override
        public void visitStringType(StringType type)
        {
            templateData = new RuntimeFunctionTemplateData("string");
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            final StringBuilder suffix = new StringBuilder();
            final int maxBitSize = type.getMaxBitSize();
            suffix.append("var");
            if (maxBitSize == 40) // VarSize
            {
                suffix.append("size");
            }
            else
            {
                if (!type.isSigned())
                    suffix.append("u");
                suffix.append("int");
                if (maxBitSize != 72) // var(u)int takes up to 9 bytes
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

    private static final class HashCodeSuffixVisitor extends ZserioAstDefaultVisitor
    {
        public RuntimeFunctionTemplateData getTemplateData()
        {
            return templateData;
        }

        @Override
        public void visitArrayType(ArrayType type)
        {
            // python uses Array wrapper which implements __hash__ method
            templateData = new RuntimeFunctionTemplateData("object");
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            templateData = new RuntimeFunctionTemplateData("bool");
        }

        @Override
        public void visitStdIntegerType(StdIntegerType stdIntegerType)
        {
            final String suffix = (stdIntegerType.getBitSize() > 32) ? "int64" : "int32";
            templateData = new RuntimeFunctionTemplateData(suffix);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType varIntegerType)
        {
            final String suffix = (varIntegerType.getMaxBitSize() > 32) ? "int64" : "int32";
            templateData = new RuntimeFunctionTemplateData(suffix);
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType fixedBitFieldType)
        {
            final String suffix = (fixedBitFieldType.getBitSize() > 32) ? "int64" : "int32";
            templateData = new RuntimeFunctionTemplateData(suffix);
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType dynamicBitFieldType)
        {
            templateData = new RuntimeFunctionTemplateData("int64");
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            templateData = new RuntimeFunctionTemplateData("float" + (type.getBitSize() > 32 ? "64" : "32"));
        }

        @Override
        public void visitBytesType(BytesType type)
        {
            templateData = new RuntimeFunctionTemplateData("bytes");
        }

        @Override
        public void visitStringType(StringType type)
        {
            templateData = new RuntimeFunctionTemplateData("string");
        }

        @Override
        public void visitExternType(ExternType type)
        {
            templateData = new RuntimeFunctionTemplateData("object");
        }

        @Override
        public void visitStructureType(StructureType structureType)
        {
            templateData = new RuntimeFunctionTemplateData("object");
        }

        @Override
        public void visitChoiceType(ChoiceType choiceType)
        {
            templateData = new RuntimeFunctionTemplateData("object");
        }

        @Override
        public void visitUnionType(UnionType unionType)
        {
            templateData = new RuntimeFunctionTemplateData("object");
        }

        @Override
        public void visitEnumType(EnumType enumType)
        {
            templateData = new RuntimeFunctionTemplateData("object");
        }

        @Override
        public void visitBitmaskType(BitmaskType bitmaskType)
        {
            templateData = new RuntimeFunctionTemplateData("object");
        }

        private RuntimeFunctionTemplateData templateData = null;
    }
}
