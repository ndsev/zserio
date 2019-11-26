package zserio.emit.common.sql;

import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FloatType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.sql.types.NativeBlobType;
import zserio.emit.common.sql.types.NativeIntegerType;
import zserio.emit.common.sql.types.NativeRealType;
import zserio.emit.common.sql.types.NativeTextType;
import zserio.emit.common.sql.types.SqlNativeType;

/**
 * Mapper from Zserio types to SQLite3 types.
 *
 * For the types supported by SQLite, see https://www.sqlite.org/datatype3.html
 */
public class SqlNativeTypeMapper
{
    /**
     * Gets SQLite3 native type from Zserio type.
     *
     * @param type Zserio type to map to SQLite3 native type.
     *
     * @return Mapped Zserio type to SQLite3 native type.
     *
     * @throws ZserioEmitException Throws in case of internal error.
     */
    public SqlNativeType getSqlType(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        // resolve all the way through subtypes to the base type
        final ZserioType baseType = typeInstantiation.getBaseType();

        final TypeMapperVisitor visitor = new TypeMapperVisitor();
        baseType.accept(visitor);
        final SqlNativeType nativeType = visitor.getSqlType();

        if (nativeType == null)
        {
            throw new ZserioEmitException("Unexpected element '" +
                    ZserioTypeUtil.getFullName(typeInstantiation.getType()) +
                    "' of type '" + baseType.getClass() + "' in SqlNativeTypeMapper!");
        }

        return nativeType;
    }

    private static class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        /**
         * Gets the SQL type mapped from Zserio type.
         *
         * @return Mapped SQL type.
         */
        SqlNativeType getSqlType()
        {
            return sqlType;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            sqlType = integerType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            sqlType = blobType;
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            sqlType = integerType;
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            sqlType = realType;
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            sqlType = integerType;
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            sqlType = integerType;
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType fixedFieldTypeType)
        {
            sqlType = integerType;
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType dynamicBitFieldType)
        {
            sqlType = integerType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            sqlType = textType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            sqlType = blobType;
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            sqlType = blobType;
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            sqlType = blobType;
        }

        private SqlNativeType sqlType = null;
    }

    private final static NativeIntegerType integerType = new NativeIntegerType();
    private final static NativeRealType realType = new NativeRealType();
    private final static NativeTextType textType = new NativeTextType();
    private final static NativeBlobType blobType = new NativeBlobType();
}
