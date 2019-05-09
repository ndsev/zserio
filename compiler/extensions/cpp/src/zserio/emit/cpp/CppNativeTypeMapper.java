package zserio.emit.cpp;

import zserio.ast.ArrayType;
import zserio.ast.PackageName;
import zserio.ast.UnionType;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.IntegerType;
import zserio.ast.ServiceType;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.VarIntegerType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeArrayType;
import zserio.emit.cpp.types.NativeBooleanType;
import zserio.emit.cpp.types.NativeCompoundType;
import zserio.emit.cpp.types.NativeConstType;
import zserio.emit.cpp.types.NativeDoubleType;
import zserio.emit.cpp.types.NativeEnumType;
import zserio.emit.cpp.types.NativeFloatType;
import zserio.emit.cpp.types.NativeHeapOptionalHolderType;
import zserio.emit.cpp.types.NativeInPlaceOptionalHolderType;
import zserio.emit.cpp.types.NativeIntegralArrayType;
import zserio.emit.cpp.types.NativeIntegralType;
import zserio.emit.cpp.types.NativeObjectArrayType;
import zserio.emit.cpp.types.NativeOptimizedOptionalHolderType;
import zserio.emit.cpp.types.NativeOptionalHolderType;
import zserio.emit.cpp.types.NativeServiceType;
import zserio.emit.cpp.types.NativeStdIntType;
import zserio.emit.cpp.types.NativeStringType;
import zserio.emit.cpp.types.NativeSubType;

public class CppNativeTypeMapper
{
    /**
     * Constructor from package mapper.
     *
     * @param cppPackageMapper Package mapper to construct from.
     */
    public CppNativeTypeMapper(PackageMapper cppPackageMapper)
    {
        this.cppPackageMapper = cppPackageMapper;
    }

    /**
     * Returns a C++ type that can hold an instance of given Zserio type.
     *
     * @param type Zserio type for mapping to C++ type.
     *
     * @return C++ type which can hold a Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any C++ type.
     */
    public CppNativeType getCppType(ZserioType type) throws ZserioEmitException
    {
        // don't resolve subtypes so that the subtype name (C++ typedef) will be used
        final ZserioType resolvedType = TypeReference.resolveType(type);

        final ZserioTypeMapperVisitor visitor = new ZserioTypeMapperVisitor();
        resolvedType.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        final CppNativeType nativeType = visitor.getCppType();
        if (nativeType == null)
            throw new ZserioEmitException("Unhandled type '" + resolvedType.getClass().getName() +
                    "' in CppNativeTypeMapper!");

        return nativeType;
    }

    /**
     * Returns a C++ type that can hold an instance of given optional Zserio type or Zserio compound type.
     *
     * @param type                  Zserio type for mapping to C++ type.
     * @param isOptionalField       true if the given Zserio type is optional.
     * @param useHeapOptionalHolder true to force mapping to the heap optional holder.
     *
     * @return C++ optional holder type which can hold a given Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to C++ optional holder type.
     */
    public NativeOptionalHolderType getCppOptionalHolderType(ZserioType type, boolean isOptionalField,
            boolean useHeapOptionalHolder) throws ZserioEmitException
    {
        final CppNativeType rawType = getCppType(type);

        NativeOptionalHolderType nativeOptionalType;
        if (!isOptionalField || rawType.isSimpleType())
            nativeOptionalType = new NativeInPlaceOptionalHolderType(ZSERIO_RUNTIME_PACKAGE_NAME,
                    ZSERIO_RUNTIME_INCLUDE_PREFIX, rawType);
        else if (useHeapOptionalHolder)
            nativeOptionalType = new NativeHeapOptionalHolderType(ZSERIO_RUNTIME_PACKAGE_NAME,
                    ZSERIO_RUNTIME_INCLUDE_PREFIX, rawType);
        else
            nativeOptionalType = new NativeOptimizedOptionalHolderType(ZSERIO_RUNTIME_PACKAGE_NAME,
                    ZSERIO_RUNTIME_INCLUDE_PREFIX, rawType);

        return nativeOptionalType;
    }

    /**
     * Returns a C++ integer type that can hold an instance of given Zserio integer type.
     *
     * @param type Zserio integer type for mapping to C++ integer type.
     *
     * @return C++ integer type which can hold a Zserio integer type.
     *
     * @throws ZserioEmitException If the Zserio integer type cannot be mapped to any C++ integer type.
     */
    public NativeIntegralType getCppIntegralType(IntegerType type) throws ZserioEmitException
    {
        final CppNativeType nativeType = getCppType(type);

        if (!(nativeType instanceof NativeIntegralType))
            throw new ZserioEmitException("Unhandled integral type '" + type.getClass().getName() +
                    "' in CppNativeTypeMapper!");

        return (NativeIntegralType)nativeType;
    }

    /**
     * Returns a C++ subtype that can hold an instance of given Zserio subtype.
     *
     * @param type Zserio subtype for mapping to C++ subtype.
     *
     * @return C++ subtype which can hold a Zserio subtype.
     *
     * @throws ZserioEmitException If the Zserio subtype cannot be mapped to any C++ subtype.
     */
    public NativeSubType getCppSubType(Subtype type) throws ZserioEmitException
    {
        final CppNativeType nativeType = getCppType(type);

        if (!(nativeType instanceof NativeSubType))
            throw new ZserioEmitException("Unhandled subtype '" + type.getClass().getName() +
                    "' in CppNativeTypeMapper!");

        return (NativeSubType)nativeType;
    }

    private String getIncludePathRoot(PackageName packageName)
    {
        if (packageName.isEmpty())
            return "";

        return packageName.toString(INCLUDE_DIR_SEPARATOR) + INCLUDE_DIR_SEPARATOR;
    }

    private String getIncludePath(PackageName packageName, String name)
    {
        return getIncludePathRoot(packageName) + name + HEADER_SUFFIX;
    }

    private static abstract class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        @Override
        public void visitBitFieldType(BitFieldType type)
        {
            mapBitfieldType(type);
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            mapIntegralType(type.getBitSize(), type.isSigned(), false);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), true);
        }

        protected void mapIntegralType(int nBits, boolean signed, boolean variable)
        {
            if (signed)
                mapSignedIntegralType(nBits, variable);
            else
                mapUnsignedIntegralType(nBits, variable);
        }

        protected abstract void mapBitfieldType(BitFieldType type);
        protected abstract void mapSignedIntegralType(int nBits, boolean variable);
        protected abstract void mapUnsignedIntegralType(int nBits, boolean variable);
    }

    private class ArrayElementTypeMapperVisitor extends TypeMapperVisitor
    {
        public ArrayElementTypeMapperVisitor(ZserioType originalType)
        {
            // resolve instantiations, but don't resolve subtype
            this.originalType = TypeReference.resolveType(originalType);
        }

        public CppNativeType getCppType()
        {
            return cppType;
        }

        public ZserioEmitException getThrownException()
        {
            return thrownException;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            cppType = booleanArrayType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
                cppType = float16ArrayType;
                break;

            case 32:
                cppType = float32ArrayType;
                break;

            case 64:
                cppType = float64ArrayType;
                break;

            default:
                break;
            }
        }

        @Override
        public void visitStringType(StringType type)
        {
            cppType = stdStringArrayType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            mapObjectArray();
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapObjectArray();
        }

        @Override
        protected void mapBitfieldType(BitFieldType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), false);
        }

        @Override
        protected void mapSignedIntegralType(int nBits, boolean variable)
        {
            if (variable)
            {
                switch (nBits)
                {
                case 16:
                    cppType = varInt16ArrayType;
                    break;

                case 32:
                    cppType = varInt32ArrayType;
                    break;

                case 64:
                    cppType = varInt64ArrayType;
                    break;

                case 72:
                    cppType = varIntArrayType;
                    break;

                default:
                    break;
                }
            }
            else
            {
                if (nBits <= 8)
                    cppType = int8ArrayType;
                else if (nBits <= 16)
                    cppType = int16ArrayType;
                else if (nBits <= 32)
                    cppType = int32ArrayType;
                else // this could be > 64 (int8 foo; bit<foo> a;) but values above 64 explode at runtime
                    cppType = int64ArrayType;
            }
        }

        @Override
        protected void mapUnsignedIntegralType(int nBits, boolean variable)
        {
            if (variable)
            {
                switch (nBits)
                {
                case 16:
                    cppType = varUInt16ArrayType;
                    break;

                case 32:
                    cppType = varUInt32ArrayType;
                    break;

                case 64:
                    cppType = varUInt64ArrayType;
                    break;

                case 72:
                    cppType = varUIntArrayType;
                    break;

                default:
                    break;
                }
            }
            else
            {
                if (nBits <= 8)
                    cppType = uint8ArrayType;
                else if (nBits <= 16)
                    cppType = uint16ArrayType;
                else if (nBits <= 32)
                    cppType = uint32ArrayType;
                else // this could be > 64 (int8 foo; bit<foo> a;) but values above 64 explode at runtime
                    cppType = uint64ArrayType;
            }
        }

        private void mapObjectArray()
        {
            // use the original type so that subtype is kept
            try
            {
                cppType = new NativeObjectArrayType(ZSERIO_RUNTIME_PACKAGE_NAME,
                        ZSERIO_RUNTIME_INCLUDE_PREFIX, CppNativeTypeMapper.this.getCppType(originalType));
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        private final ZserioType originalType;

        private CppNativeType cppType = null;
        private ZserioEmitException thrownException = null;
    }

    private class ZserioTypeMapperVisitor extends TypeMapperVisitor
    {
        public CppNativeType getCppType()
        {
            return cppType;
        }

        public ZserioEmitException getThrownException()
        {
            return thrownException;
        }

        @Override
        public void visitArrayType(ArrayType type)
        {
            // don't resolve subtype yet so that the element mapper visitor is given the original type
            final ZserioType elementType = TypeReference.resolveType(type.getElementType());

            final ArrayElementTypeMapperVisitor arrayVisitor = new ArrayElementTypeMapperVisitor(elementType);

            /* Call visitor on the resolved type.
             *
             * This is required so that for subtypes of the simple types the correct array class is used,
             * e.g.:
             *
             * subtype uint8 MyID;
             * Compound
             * {
             *     MyID ids[10];
             * };
             *
             * must use UnsignedByteArray for the field ids whereas for the code:
             *
             * Foo
             * {
             *     uint8 blah;
             * };
             * subtype Foo MyID;
             * Compound
             * {
             *     MyID ids[10];
             * }
             *
             * the field ids should be backed by ObjectArray<MyID> (not ObjectArray<Foo>).
             */
            TypeReference.resolveBaseType(elementType).accept(arrayVisitor);
            cppType = arrayVisitor.getCppType();
            thrownException = arrayVisitor.getThrownException();
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            cppType = booleanType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitConstType(ConstType type)
        {
            try
            {
                final CppNativeType nativeTargetType = CppNativeTypeMapper.this.getCppType(type.getConstType());
                final PackageName packageName = cppPackageMapper.getPackageName(type);
                final String name = type.getName();
                final String includeFileName = getIncludePath(packageName, name);
                cppType = new NativeConstType(packageName, type.getName(), includeFileName, nativeTargetType);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            try
            {
                final NativeIntegralType nativeBaseType = getCppIntegralType(type.getIntegerBaseType());
                final PackageName packageName = cppPackageMapper.getPackageName(type);
                final String name = type.getName();
                final String includeFileName = getIncludePath(packageName, name);
                cppType = new NativeEnumType(packageName, name, includeFileName, nativeBaseType);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
            case 32:
                cppType = floatType;
                break;

            case 64:
                cppType = doubleType;
                break;

            default:
                break;
            }
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            final PackageName packageName = cppPackageMapper.getPackageName(type);
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeServiceType(packageName, name, includeFileName);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitStringType(StringType type)
        {
            cppType = stringType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitSubtype(Subtype type)
        {
            final ZserioType targetType = type.getTargetType();
            try
            {
                final CppNativeType nativeTargetType = CppNativeTypeMapper.this.getCppType(targetType);
                final PackageName packageName = cppPackageMapper.getPackageName(type);
                final String name = type.getName();
                final String includeFileName = getIncludePath(packageName, name);
                cppType = new NativeSubType(packageName, name, includeFileName, nativeTargetType);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            final ZserioType resolvedReferencedType = TypeReference.resolveType(type.getReferencedType());
            if (resolvedReferencedType instanceof Subtype)
                visitSubtype((Subtype)resolvedReferencedType);
            else
                mapCompoundType(type.getBaseType());
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapCompoundType(type);
        }

        @Override
        protected void mapBitfieldType(BitFieldType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), false);
        }

        @Override
        protected void mapSignedIntegralType(int nBits, boolean variable)
        {
            if (variable)
            {
                switch (nBits)
                {
                case 16:
                    cppType = int16Type;
                    break;

                case 32:
                    cppType = int32Type;
                    break;

                case 64:
                case 72:
                    cppType = int64Type;
                    break;

                default:
                    break;
                }
            }
            else
            {
                if (nBits <= 8)
                    cppType = int8Type;
                else if (nBits <= 16)
                    cppType = int16Type;
                else if (nBits <= 32)
                    cppType = int32Type;
                else // this could be > 64 (int8 foo; bit<foo> a;) but if we're above 64, we explode at runtime
                    cppType = int64Type;
            }
        }

        @Override
        protected void mapUnsignedIntegralType(int nBits, boolean variable)
        {
            if (variable)
            {
                switch (nBits)
                {
                case 16:
                    cppType = uint16Type;
                    break;

                case 32:
                    cppType = uint32Type;
                    break;

                case 64:
                case 72:
                    cppType = uint64Type;
                    break;

                default:
                    break;
                }
            }
            else
            {
                if (nBits <= 8)
                    cppType = uint8Type;
                else if (nBits <= 16)
                    cppType = uint16Type;
                else if (nBits <= 32)
                    cppType = uint32Type;
                else // this could be > 64 (int8 foo; bit<foo> a;) but if we're above 64, we explode at runtime
                    cppType = uint64Type;
            }
        }

        private void mapCompoundType(CompoundType type)
        {
            final PackageName packageName = cppPackageMapper.getPackageName(type);
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeCompoundType(packageName, name, includeFileName);
        }

        private CppNativeType cppType = null;
        private ZserioEmitException thrownException = null;
    }

    private final PackageMapper cppPackageMapper;

    private final static String INCLUDE_DIR_SEPARATOR = "/";
    private final static String HEADER_SUFFIX = ".h";

    private final static PackageName ZSERIO_RUNTIME_PACKAGE_NAME =
            new PackageName.Builder().addId("zserio").get();
    private final static String ZSERIO_RUNTIME_INCLUDE_PREFIX = "zserio" + INCLUDE_DIR_SEPARATOR;
    private final static String BIT_FIELD_ARRAY_H = ZSERIO_RUNTIME_INCLUDE_PREFIX + "BitFieldArray" +
            HEADER_SUFFIX;
    private final static String BASIC_ARRAY_H = ZSERIO_RUNTIME_INCLUDE_PREFIX + "BasicArray" + HEADER_SUFFIX;

    private final static NativeBooleanType booleanType = new NativeBooleanType();
    private final static NativeStringType stringType = new NativeStringType();

    private final static NativeFloatType floatType = new NativeFloatType();
    private final static NativeDoubleType doubleType = new NativeDoubleType();

    private final static NativeStdIntType uint8Type = new NativeStdIntType(8, false);
    private final static NativeStdIntType uint16Type = new NativeStdIntType(16, false);
    private final static NativeStdIntType uint32Type = new NativeStdIntType(32, false);
    private final static NativeStdIntType uint64Type = new NativeStdIntType(64, false);

    private final static NativeStdIntType int8Type = new NativeStdIntType(8, true);
    private final static NativeStdIntType int16Type = new NativeStdIntType(16, true);
    private final static NativeStdIntType int32Type = new NativeStdIntType(32, true);
    private final static NativeStdIntType int64Type = new NativeStdIntType(64, true);

    private final static NativeArrayType booleanArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "BoolArray", BASIC_ARRAY_H, booleanType);
    private final static NativeArrayType stdStringArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "StringArray", BASIC_ARRAY_H, stringType);

    private final static NativeArrayType float16ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Float16Array", BASIC_ARRAY_H, floatType);
    private final static NativeArrayType float32ArrayType =
            new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Float32Array", BASIC_ARRAY_H, floatType);
    private final static NativeArrayType float64ArrayType =
            new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Float64Array", BASIC_ARRAY_H, doubleType);

    private final static NativeArrayType int8ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Int8Array", BIT_FIELD_ARRAY_H, int8Type);
    private final static NativeArrayType int16ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Int16Array", BIT_FIELD_ARRAY_H, int16Type);
    private final static NativeArrayType int32ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Int32Array", BIT_FIELD_ARRAY_H, int32Type);
    private final static NativeArrayType int64ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "Int64Array", BIT_FIELD_ARRAY_H, int64Type);

    private final static NativeArrayType uint8ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "UInt8Array", BIT_FIELD_ARRAY_H, uint8Type);
    private final static NativeArrayType uint16ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "UInt16Array", BIT_FIELD_ARRAY_H, uint16Type);
    private final static NativeArrayType uint32ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "UInt32Array", BIT_FIELD_ARRAY_H, uint32Type);
    private final static NativeArrayType uint64ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "UInt64Array", BIT_FIELD_ARRAY_H, uint64Type);

    private final static NativeArrayType varInt16ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarInt16Array", BASIC_ARRAY_H, int16Type);
    private final static NativeArrayType varUInt16ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarUInt16Array", BASIC_ARRAY_H, uint16Type);

    private final static NativeArrayType varInt32ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarInt32Array", BASIC_ARRAY_H, int32Type);
    private final static NativeArrayType varUInt32ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarUInt32Array", BASIC_ARRAY_H, uint32Type);

    private final static NativeArrayType varInt64ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarInt64Array", BASIC_ARRAY_H, int64Type);
    private final static NativeArrayType varUInt64ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarUInt64Array", BASIC_ARRAY_H, uint64Type);

    private final static NativeArrayType varIntArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarIntArray", BASIC_ARRAY_H, int64Type);
    private final static NativeArrayType varUIntArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_PACKAGE_NAME, "VarUIntArray", BASIC_ARRAY_H, uint64Type);
}
