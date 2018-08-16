package zserio.emit.common.sql;

import zserio.ast.ArrayType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
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
    public SqlNativeTypeMapper()
    {}

    public SqlNativeType getSqlType(ZserioType type) throws ZserioEmitException
    {
        // resolve all the way through subtypes to the base type
        type = TypeReference.resolveBaseType(type);

        final TypeMapperVisitor visitor = new TypeMapperVisitor();
        type.callVisitor(visitor);
        final SqlNativeType nativeType = visitor.getSqlType();

        if (nativeType == null)
            throw new ZserioEmitException("unhandled type: " + type.getClass().getName());

        return nativeType;
    }

    private static class TypeMapperVisitor implements ZserioTypeVisitor
    {
        SqlNativeType getSqlType()
        {
            return sqlType;
        }

        @Override
        public void visitUnsignedBitFieldType(UnsignedBitFieldType type)
        {
            sqlType = integerType;
        }

        @Override
        public void visitSignedBitFieldType(SignedBitFieldType type)
        {
            sqlType = integerType;
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
        public void visitUnionType(UnionType type)
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
        public void visitServiceType(ServiceType type)
        {
            unexpected(type);
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            sqlType = blobType;
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            sqlType = integerType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            sqlType = textType;
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            sqlType = blobType;
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            // FIXME: does this work now?
            sqlType = integerType;
        }

        @Override
        public void visitArrayType(ArrayType type) throws ZserioEmitException
        {
            // arrays are not allowed in a sql_table
            unexpected(type);
        }

        @Override
        public void visitConstType(ConstType type) throws ZserioEmitException
        {
            unexpected(type);
        }

        @Override
        public void visitFunctionType(FunctionType type) throws ZserioEmitException
        {
            unexpected(type);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type) throws ZserioEmitException
        {
            unexpected(type);
        }

        @Override
        public void visitSqlTableType(SqlTableType type) throws ZserioEmitException
        {
            unexpected(type);
        }

        @Override
        public void visitSubtype(Subtype type) throws ZserioEmitException
        {
            // subtypes should have been resolved by the owner of this instance
            unexpected(type);
        }

        @Override
        public void visitTypeReference(TypeReference type) throws ZserioEmitException
        {
            // type references should have been resolved by the owner of this instance
            unexpected(type);
        }

        private void unexpected(ZserioType type) throws ZserioEmitException
        {
            throw new ZserioEmitException("internal error: unexpected element " +
                    ZserioTypeUtil.getFullName(type) +  " of type " + type.getClass());
        }

        private SqlNativeType sqlType;
    }

    private final static NativeIntegerType integerType = new NativeIntegerType();
    private final static NativeRealType realType = new NativeRealType();
    private final static NativeTextType textType = new NativeTextType();
    private final static NativeBlobType blobType = new NativeBlobType();
}
