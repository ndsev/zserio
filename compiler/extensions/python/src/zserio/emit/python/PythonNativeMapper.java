package zserio.emit.python;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FloatType;
import zserio.ast.InstantiateType;
import zserio.ast.PackageName;
import zserio.ast.PubsubType;
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
import zserio.emit.python.symbols.PythonNativeSymbol;
import zserio.emit.python.types.NativeArrayType;
import zserio.emit.python.types.NativeBitBufferType;
import zserio.emit.python.types.NativeBuiltinType;
import zserio.emit.python.types.NativeFixedSizeIntArrayType;
import zserio.emit.python.types.NativeObjectArrayType;
import zserio.emit.python.types.NativeSubtype;
import zserio.emit.python.types.NativeUserType;
import zserio.emit.python.types.PythonNativeType;

public class PythonNativeMapper
{
    /**
     * Constructor from package mapper.
     *
     * @param PythonPackageMapper Package mapper to construct from.
     */
    public PythonNativeMapper(PackageMapper pythonPackageMapper)
    {
        this.pythonPackageMapper = pythonPackageMapper;
    }

    /**
     * Returns a Python symbol that can hold an instance of Zserio symbol.
     *
     * @param symbol Zserio symbol.
     *
     * @return Python symbol.
     *
     * @throws ZserioEmitException If the Zserio symbol cannot be mapped to any Python symbol.
     */
    public PythonNativeSymbol getPythonSymbol(AstNode symbol) throws ZserioEmitException
    {
        if (symbol instanceof Constant)
        {
            final Constant constant = (Constant)symbol;
            final PackageName packageName = pythonPackageMapper.getPackageName(constant.getPackage());
            final String name = constant.getName();
            return new PythonNativeSymbol(packageName, name);
        }
        else
        {
            throw new ZserioEmitException("Unhandled symbol '" + symbol.getClass().getName() +
                    "' in PythonNativeMapper!");
        }
    }

    /**
     * Returns a Python type that can hold an instance of the Zserio type.
     *
     * @param typeInstantiation Instantiation of the Zserio type.
     *
     * @return  Python type which can hold the Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any Python type.
     */
    public PythonNativeType getPythonType(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);

        // don't resolve subtypes so that the subtype name (Python imports) will be used
        return getPythonType(typeInstantiation.getType());
    }

    /**
     * Returns a Python type that can hold an instance of referenced Zserio type.
     *
     * @param typeReference Reference to the Zserio type.
     *
     * @return  Python type which can hold referenced Zserio type.
     *
     * @throws ZserioEmitException If the referenced Zserio type cannot be mapped to any Python type.
     */
    public PythonNativeType getPythonType(TypeReference typeReference) throws ZserioEmitException
    {
        // don't resolve subtypes so that the subtype name (Python imports) will be used
        return getPythonType(typeReference.getType());
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
        final TypeMapperVisitor visitor = new TypeMapperVisitor(pythonPackageMapper);
        type.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        final PythonNativeType nativeType = visitor.getPythonType();
        if (nativeType == null)
            throw new ZserioEmitException("Unhandled type '" + type.getClass().getName() +
                    "' in PythonNativeMapper!");

        return nativeType;
    }

    private PythonNativeType mapArray(ArrayInstantiation instantiation) throws ZserioEmitException
    {
        // use base type since we just need to know whether it's an object array or built-in type array
        final TypeInstantiation elementInstantiation = instantiation.getElementTypeInstantiation();
        final ZserioType elementBaseType = elementInstantiation.getBaseType();
        final ArrayTypeMapperVisitor visitor = new ArrayTypeMapperVisitor();
        elementBaseType.accept(visitor);

        final PythonNativeType nativeType = visitor.getPythonArrayType();
        if (nativeType == null)
        {
            throw new ZserioEmitException("Unhandled type '" + elementBaseType.getClass().getName() +
                    "' in PythonNativeMapper!");
        }

        return nativeType;
    }

    private class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        public TypeMapperVisitor(PackageMapper pythonPackageMapper)
        {
            this.pythonPackageMapper = pythonPackageMapper;
        }

        public PythonNativeType getPythonType()
        {
            return pythonType;
        }

        public ZserioEmitException getThrownException()
        {
            return thrownException;
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
        public void visitEnumType(EnumType type)
        {
            final PackageName packageName = pythonPackageMapper.getPackageName(type);
            pythonType = new NativeUserType(packageName, type.getName());
        }

        @Override
        public void visitBitmaskType(BitmaskType type)
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
        public void visitExternType(ExternType type)
        {
            pythonType = bitBufferType;
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitPubsubType(PubsubType type)
        {
            pythonType = mapUserType(type);
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            pythonType = intType;
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
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
            try
            {
                final PackageName packageName = pythonPackageMapper.getPackageName(type);
                final PythonNativeType nativeTargetBaseType =
                        PythonNativeMapper.this.getPythonType(type.getBaseTypeReference());
                pythonType = new NativeSubtype(packageName, type.getName(), nativeTargetBaseType);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitInstantiateType(InstantiateType type)
        {
            pythonType = mapUserType(type);
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
        private ZserioEmitException thrownException = null;
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
            pythonArrayType = boolArrayType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            pythonArrayType = objectArrayType;
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            pythonArrayType = objectArrayType;
        }

        @Override
        public void visitBitmaskType(BitmaskType type)
        {
            pythonArrayType = objectArrayType;
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
                pythonArrayType = float16ArrayType;
                break;

            case 32:
                pythonArrayType = float32ArrayType;
                break;

            case 64:
                pythonArrayType = float64ArrayType;
                break;

            default:
                // not supported
                break;
            }
        }

        @Override
        public void visitExternType(ExternType type)
        {
            pythonArrayType = bitBufferArrayType;
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            if (type.isSigned())
                pythonArrayType = signedBitFieldArrayType;
            else
                pythonArrayType = bitFieldArrayType;
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            if (type.isSigned())
                pythonArrayType = signedBitFieldArrayType;
            else
                pythonArrayType = bitFieldArrayType;
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            pythonArrayType = (type.isSigned()) ? signedBitFieldArrayType : bitFieldArrayType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            pythonArrayType = stringArrayType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            pythonArrayType = objectArrayType;
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            pythonArrayType = objectArrayType;
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            switch (type.getMaxBitSize())
            {
            case 16:
                pythonArrayType = (type.isSigned()) ? varInt16ArrayType : varUInt16ArrayType;
                break;

            case 32:
                pythonArrayType = (type.isSigned()) ? varInt32ArrayType : varUInt32ArrayType;
                break;

            case 64:
                pythonArrayType = (type.isSigned()) ? varInt64ArrayType : varUInt64ArrayType;
                break;

            case 72:
                pythonArrayType = (type.isSigned()) ? varIntArrayType : varUIntArrayType;
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
    private final static PythonNativeType bitBufferType = new NativeBitBufferType();

    private final static NativeArrayType boolArrayType = new NativeArrayType("BoolArrayTraits");
    private final static NativeArrayType objectArrayType = new NativeObjectArrayType("ObjectArrayTraits");
    private final static NativeArrayType float16ArrayType = new NativeArrayType("Float16ArrayTraits");
    private final static NativeArrayType float32ArrayType = new NativeArrayType("Float32ArrayTraits");
    private final static NativeArrayType float64ArrayType = new NativeArrayType("Float64ArrayTraits");
    private final static NativeArrayType bitBufferArrayType = new NativeArrayType("BitBufferArrayTraits");
    private final static NativeArrayType signedBitFieldArrayType =
            new NativeFixedSizeIntArrayType("SignedBitFieldArrayTraits");
    private final static NativeArrayType bitFieldArrayType =
            new NativeFixedSizeIntArrayType("BitFieldArrayTraits");
    private final static NativeArrayType stringArrayType = new NativeArrayType("StringArrayTraits");
    private final static NativeArrayType varInt16ArrayType = new NativeArrayType("VarInt16ArrayTraits");
    private final static NativeArrayType varInt32ArrayType = new NativeArrayType("VarInt32ArrayTraits");
    private final static NativeArrayType varInt64ArrayType = new NativeArrayType("VarInt64ArrayTraits");
    private final static NativeArrayType varIntArrayType = new NativeArrayType("VarIntArrayTraits");
    private final static NativeArrayType varUInt16ArrayType = new NativeArrayType("VarUInt16ArrayTraits");
    private final static NativeArrayType varUInt32ArrayType = new NativeArrayType("VarUInt32ArrayTraits");
    private final static NativeArrayType varUInt64ArrayType = new NativeArrayType("VarUInt64ArrayTraits");
    private final static NativeArrayType varUIntArrayType = new NativeArrayType("VarUIntArrayTraits");
}
