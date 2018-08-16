package zserio.emit.cpp;

import zserio.ast.ArrayType;
import zserio.ast.UnionType;
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
import zserio.ast.UnsignedBitFieldType;
import zserio.ast.VarIntegerType;
import zserio.emit.common.ExpressionFormatter;

public class CppRuntimeFunctionDataCreator
{
    /**
     * This function generates the suffix for Zserio C++ runtime functions based on the actual type passed.
     */
    public static RuntimeFunctionTemplateData createData(ZserioType type,
            ExpressionFormatter cppExpressionFormatter)
    {
        final Visitor visitor = new Visitor(cppExpressionFormatter);
        type.callVisitor(visitor);

        final String suffix = visitor.getSuffix();
        final String arg = visitor.getArg();

        return (suffix != null) ? new RuntimeFunctionTemplateData(suffix, arg) : null;
    }

    private static class Visitor implements ZserioTypeVisitor
    {
        public Visitor(ExpressionFormatter cppExpressionFormatter)
        {
            this.cppExpressionFormatter = cppExpressionFormatter;
        }

        public String getSuffix()
        {
            return suffix;
        }

        public String getArg()
        {
            return arg;
        }

        @Override
        public void visitStringType(StringType type)
        {
            suffix = "String";
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

            suffix = sb.toString();
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
        public void visitStdIntegerType(StdIntegerType type) throws ZserioEmitCppException
        {
            final int bitCount = type.getBitSize();
            suffix = getSuffixForIntegralType(bitCount, type.isSigned());
            arg = CppLiteralFormatter.formatUInt8Literal(bitCount);
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            handleBool();
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            suffix = "Float" + type.getBitSize();
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
            suffix = "Bool";
        }

        private void handleBitFieldType(BitFieldType type)
        {
            suffix = getSuffixForIntegralType(type.getMaxBitSize(), type.isSigned());
            arg = cppExpressionFormatter.formatGetter(type.getLengthExpression());
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

        private final ExpressionFormatter cppExpressionFormatter;

        private String suffix;
        private String arg;
    }
}
