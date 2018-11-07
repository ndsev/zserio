package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.FunctionType;
import zserio.ast.ServiceType;
import zserio.ast.SignedBitFieldType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.UnsignedBitFieldType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeVisitor;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.NativeArrayType;
import zserio.emit.python.types.NativeCompoundType;
import zserio.emit.python.types.NativeEnumType;
import zserio.emit.python.types.PythonNativeType;


public class PythonNativeTypeMapper
{
    /**
     * Constructor from package mapper.
     *
     * @param PythonPackageMapper Package mapper to construct from.
     */
    public PythonNativeTypeMapper(PackageMapper pythonPackageMapper)
    {
        this.pythonPackageMapper = pythonPackageMapper;
    }

    /**
     * Returns a Python type that can hold an instance of given Zserio type.
     *
     * @param type Zserio type for mapping to Python type.
     *
     * @return Python type which can hold a Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any Python type.
     */
    public PythonNativeType getPythonType(ZserioType type) throws ZserioEmitException
    {
        // resolve to base type, python doesn't have subtypes
        final ZserioType resolvedBaseType = TypeReference.resolveBaseType(type);

        final TypeMapperVisitor visitor = new TypeMapperVisitor();
        resolvedBaseType.callVisitor(visitor);

        /*final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;*/

        final PythonNativeType nativeType = visitor.getPythonType();
        if (nativeType == null)
            throw new ZserioEmitException("Unhandled type '" + resolvedBaseType.getClass().getName() +
                    "' in PythonNativeTypeMapper!");

        return nativeType;
    }

    private class TypeMapperVisitor implements ZserioTypeVisitor
    {
        public PythonNativeType getPythonType()
        {
            return pythonType;
        }

        /*public ZserioEmitException getThrownException()
        {
            return thrownException;
        }*/

        @Override
        public void visitArrayType(ArrayType type)
        {
            ZserioType resolvedElementBaseType = TypeReference.resolveBaseType(type.getElementType());
            if (resolvedElementBaseType instanceof CompoundType)
                pythonType = new NativeArrayType(mapCompoundType((CompoundType)resolvedElementBaseType));
            else
                pythonType = dynamicArrayType;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            pythonType = dynamicType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            pythonType = mapCompoundType(type);
        }

        @Override
        public void visitConstType(ConstType type)
        {
            // zserio doesn't allow to instantiate compound types
            pythonType = dynamicType;
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            final List<String> packagePath = pythonPackageMapper.getPackagePath(type);
            pythonType = new NativeEnumType(packagePath, type.getName());
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            pythonType = dynamicType;
        }

        @Override
        public void visitFunctionType(FunctionType type)
        {
            // not supported
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            // not supported // TODO: Add when gRPC for python is implemented!
        }

        @Override
        public void visitSignedBitFieldType(SignedBitFieldType type)
        {
            pythonType = dynamicType;
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            pythonType = mapCompoundType(type);
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            pythonType = mapCompoundType(type);
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            pythonType = dynamicType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            pythonType = dynamicType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            pythonType = mapCompoundType(type);
        }

        @Override
        public void visitSubtype(Subtype type)
        {
            // not supported
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            pythonType = mapCompoundType(type.getBaseType());
        }

        @Override
        public void visitTypeReference(TypeReference type)
        {
            // not supported
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            pythonType = mapCompoundType(type);
        }

        @Override
        public void visitUnsignedBitFieldType(UnsignedBitFieldType type)
        {
            pythonType = dynamicType;
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            pythonType = dynamicType;
        }

        private PythonNativeType mapCompoundType(CompoundType type)
        {
            final List<String> packagePath = pythonPackageMapper.getPackagePath(type);
            return new NativeCompoundType(packagePath, type.getName());
        }

        private PythonNativeType pythonType;
        /*private ZserioEmitException thrownException = null;*/
    }

    private final PackageMapper pythonPackageMapper;
    private final static PythonNativeType dynamicType = new PythonNativeType();
    private final static NativeArrayType dynamicArrayType = new NativeArrayType(dynamicType);
}
