package zserio.emit.python;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.PackageName;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.NativeArrayType;
import zserio.emit.python.types.NativeBuiltinType;
import zserio.emit.python.types.NativeFixedSizeIntArrayType;
import zserio.emit.python.types.NativeObjectArrayType;
import zserio.emit.python.types.NativeUserType;
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
        // don't resolve subtypes so that the subtype name (Python imports) will be used
        final ZserioType resolvedType = TypeReference.resolveType(type);

        final TypeMapperVisitor visitor = new TypeMapperVisitor(pythonPackageMapper);
        resolvedType.accept(visitor);

        final PythonNativeType nativeType = visitor.getPythonType();
        if (nativeType == null)
            throw new ZserioEmitException("Unhandled type '" + resolvedType.getClass().getName() +
                    "' in PythonNativeTypeMapper!");

        return nativeType;
    }

    private static class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        public TypeMapperVisitor(PackageMapper pythonPackageMapper)
        {
            this.pythonPackageMapper = pythonPackageMapper;
        }

        public PythonNativeType getPythonType()
        {
            return pythonType;
        }

        @Override
        public void visitArrayType(ArrayType type)
        {
            final ZserioType resolvedElementBaseType = TypeReference.resolveBaseType(type.getElementType());
            final ArrayTypeMapperVisitor visitor = new ArrayTypeMapperVisitor();
            resolvedElementBaseType.accept(visitor);
            pythonType = visitor.getPythonArrayType();
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            pythonType = boolType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitConstType(ConstType type)
        {
            final PackageName packageName = pythonPackageMapper.getPackageName(type);
            pythonType = new NativeUserType(packageName, type.getName());
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            final PackageName packageName = pythonPackageMapper.getPackageName(type);
            pythonType = new NativeUserType(packageName, type.getName());
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            pythonType = floatType;
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitBitFieldType(BitFieldType type)
        {
            pythonType = intType;
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            pythonType = intType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            pythonType = strType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitSubtype(Subtype type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            pythonType = mapUserType(type.getBaseType());
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            pythonType = intType;
        }

        private PythonNativeType mapUserType(ZserioType type)
        {
            final PackageName packageName = pythonPackageMapper.getPackageName(type);
            return new NativeUserType(packageName, type.getName());
        }

        private PythonNativeType pythonType;
        private final PackageMapper pythonPackageMapper;
    }

    private static class ArrayTypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        public NativeArrayType getPythonArrayType()
        {
            return pythonArrayType;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            pythonArrayType = new NativeArrayType("BoolArrayTraits");
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            pythonArrayType = new NativeObjectArrayType("ObjectArrayTraits");
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            pythonArrayType = new NativeObjectArrayType("ObjectArrayTraits");
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
                pythonArrayType = new NativeArrayType("Float16ArrayTraits");
                break;

            case 32:
                pythonArrayType = new NativeArrayType("Float32ArrayTraits");
                break;

            case 64:
                pythonArrayType = new NativeArrayType("Float64ArrayTraits");
                break;

            default:
                // not supported
                break;
            }
        }

        @Override
        public void visitBitFieldType(BitFieldType type)
        {
            if (type.isSigned())
                pythonArrayType = new NativeFixedSizeIntArrayType("SignedBitFieldArrayTraits");
            else
                pythonArrayType = new NativeFixedSizeIntArrayType("BitFieldArrayTraits");
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            pythonArrayType = new NativeFixedSizeIntArrayType((type.isSigned()) ? "SignedBitFieldArrayTraits"
                    : "BitFieldArrayTraits");
        }

        @Override
        public void visitStringType(StringType type)
        {
            pythonArrayType = new NativeArrayType("StringArrayTraits");
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            pythonArrayType = new NativeObjectArrayType("ObjectArrayTraits");
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            pythonArrayType = new NativeObjectArrayType("ObjectArrayTraits");
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            pythonArrayType = new NativeObjectArrayType("ObjectArrayTraits");
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            final String unsignedPrefix = type.isSigned() ? "" : "U";
            switch (type.getMaxBitSize())
            {
            case 16:
                pythonArrayType = new NativeArrayType("Var" + unsignedPrefix + "Int16ArrayTraits");
                break;

            case 32:
                pythonArrayType = new NativeArrayType("Var" + unsignedPrefix + "Int32ArrayTraits");
                break;

            case 64:
                pythonArrayType = new NativeArrayType("Var" + unsignedPrefix + "Int64ArrayTraits");
                break;

            case 72:
                pythonArrayType = new NativeArrayType("Var" + unsignedPrefix + "IntArrayTraits");
                break;

            default:
                // not supported
                break;
            }
        }

        private NativeArrayType pythonArrayType = null;
    }

    private final PackageMapper pythonPackageMapper;
    private final static PythonNativeType boolType = new NativeBuiltinType("bool");
    private final static PythonNativeType intType = new NativeBuiltinType("int");
    private final static PythonNativeType strType = new NativeBuiltinType("str");
    private final static PythonNativeType floatType = new NativeBuiltinType("float");
}
