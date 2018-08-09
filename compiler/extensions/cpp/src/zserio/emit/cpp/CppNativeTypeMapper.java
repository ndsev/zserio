package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import zserio.ast.ArrayType;
import zserio.ast.UnionType;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.ZserioTypeVisitor;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.FunctionType;
import zserio.ast.IntegerType;
import zserio.ast.RpcType;
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
import zserio.emit.common.PackageMapper;
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
import zserio.emit.cpp.types.NativeStdIntType;
import zserio.emit.cpp.types.NativeStringType;
import zserio.emit.cpp.types.NativeSubType;
import zserio.tools.StringJoinUtil;

public class CppNativeTypeMapper
{
    public CppNativeTypeMapper(PackageMapper cppPackageMapper)
    {
        this.cppPackageMapper = cppPackageMapper;

        final List<String> rootNamespacePath = cppPackageMapper.getRootPackagePath();
        List<String> path = new ArrayList<String>(rootNamespacePath.size() + 1);
        path.addAll(rootNamespacePath);
        path.add(CONST_TYPE_NAME);
        constPackagePath = Collections.unmodifiableList(path);

        constIncludeFile = getIncludePath(cppPackageMapper.getRootPackagePath(), CONST_TYPE_NAME);
    }

    /**
     * Returns a C++ type that can hold an instance of given Zserio type.
     *
     * @param t Zserio type.
     * @return C++ type.
     */
    public CppNativeType getCppType(ZserioType type) throws ZserioEmitCppException
    {
        // don't resolve subtypes so that the subtype name (C++ typedef) will be used
        type = TypeReference.resolveType(type);

        final ZserioTypeMapperVisitor visitor = new ZserioTypeMapperVisitor();
        type.callVisitor(visitor);
        final CppNativeType nativeType = visitor.getCppType();

        if (nativeType == null)
            throw new ZserioEmitCppException("Unhandled type: " + type.getClass().getName());

        return nativeType;
    }

    public NativeOptionalHolderType getCppOptionalHolderType(ZserioType type, boolean isOptionalField,
            boolean useHeapOptionalHolder) throws ZserioEmitCppException
    {
        CppNativeType rawType = getCppType(type);

        NativeOptionalHolderType nativeOptionalType;
        if (!isOptionalField || rawType.isSimpleType())
            nativeOptionalType = new NativeInPlaceOptionalHolderType(ZSERIO_RUNTIME_NAMESPACE_PATH,
                    ZSERIO_RUNTIME_INCLUDE_PREFIX, rawType);
        else if (useHeapOptionalHolder)
            nativeOptionalType = new NativeHeapOptionalHolderType(ZSERIO_RUNTIME_NAMESPACE_PATH,
                    ZSERIO_RUNTIME_INCLUDE_PREFIX, rawType);
        else
            nativeOptionalType = new NativeOptimizedOptionalHolderType(ZSERIO_RUNTIME_NAMESPACE_PATH,
                    ZSERIO_RUNTIME_INCLUDE_PREFIX, rawType);

        return nativeOptionalType;
    }

    public NativeIntegralType getCppIntegralType(IntegerType type) throws ZserioEmitCppException
    {
        CppNativeType nativeType = getCppType(type);

        if (nativeType instanceof NativeIntegralType)
            return (NativeIntegralType)nativeType;

        throw new ZserioEmitCppException("Internal error: CppNativeTypeMapper returned " +
                "a non-integral native type for integral Zserio type");
    }

    public NativeSubType getCppSubType(Subtype type)
    {
        CppNativeType nativeType = getCppType(type);

        if (nativeType instanceof NativeSubType)
            return (NativeSubType)nativeType;

        throw new ZserioEmitCppException("Internal error: CppNativeTypeMapper returned " +
                "a non-subtype native type for Zserio subtype");
    }

    private List<String> getPathForUserDefinedType(ZserioType type)
    {
        final ArrayList<String> path = new ArrayList<String>();
        for (String component: cppPackageMapper.getPackagePath(type))
            path.add(component);
        path.add(type.getName());
        return path;
    }

    private String getIncludePathForUserDefinedType(ZserioType type)
    {
        return StringJoinUtil.joinStrings(getPathForUserDefinedType(type), INCLUDE_DIR_SEPARATOR)
                + HEADER_SUFFIX;
    }

    private String getIncludePath(List<String> packagePath, String name)
    {
        return StringJoinUtil.joinStrings(packagePath, INCLUDE_DIR_SEPARATOR) + INCLUDE_DIR_SEPARATOR + name
                + HEADER_SUFFIX;
    }

    private static abstract class TypeMapperVisitor implements ZserioTypeVisitor
    {
        @Override
        public void visitStdIntegerType(StdIntegerType type) throws ZserioEmitCppException
        {
            mapIntegralType(type.getBitSize(), type.isSigned(), false);
        }

        @Override
        public void visitUnsignedBitFieldType(UnsignedBitFieldType type)
        {
            mapBitfieldType(type);
        }

        @Override
        public void visitSignedBitFieldType(SignedBitFieldType type)
        {
            mapBitfieldType(type);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type) throws ZserioEmitCppException
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), true);
        }

        @Override
        public void visitTypeReference(TypeReference type) throws ZserioEmitCppException
        {
            unexpected(type);
        }

        @Override
        public void visitFunctionType(FunctionType type) throws ZserioEmitCppException
        {
            unexpected(type);
        }

        protected void mapIntegralType(int nBits, boolean signed, boolean variable)
                throws ZserioEmitCppException
        {
            if (signed)
                mapSignedIntegralType(nBits, variable);
            else
                mapUnsignedIntegralType(nBits, variable);
        }

        protected abstract void mapBitfieldType(BitFieldType type);
        protected abstract void mapSignedIntegralType(int nBits, boolean variable)
                throws ZserioEmitCppException;
        protected abstract void mapUnsignedIntegralType(int nBits, boolean variable)
                throws ZserioEmitCppException;

        protected void unexpected(ZserioType type) throws ZserioEmitCppException
        {
            throw new ZserioEmitCppException("Internal error: unexpected element " +
                    ZserioTypeUtil.getFullName(type) +  " of type " + type.getClass());
        }
    }

    private class ArrayElementTypeMapperVisitor extends TypeMapperVisitor
    {
        /**
         * Create a new instance of the array element mapper visitor.
         *
         * @param originalType The original, unresolved, type. This is used to handle subtypes (typedefs).
         */
        public ArrayElementTypeMapperVisitor(ZserioType originalType)
        {
            // resolve instantiations, but don't resolve subtype
            this.originalType = TypeReference.resolveType(originalType);
        }

        public CppNativeType getCppType()
        {
            return cppType;
        }

        @Override
        public void visitArrayType(ArrayType type) throws ZserioEmitCppException
        {
            // array of arrays is not supported
            unexpected(type);
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            cppType = booleanArrayType;
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
                throw new ZserioEmitCppException("Unexpected bit size of float (" + type.getBitSize() + ")");
            }
        }

        @Override
        public void visitStringType(StringType type)
        {
            cppType = stdStringArrayType;
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitRpcType(RpcType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitSubtype(Subtype type) throws ZserioEmitCppException
        {
            // should be resolved by whoever creates an instance of this visitor
            unexpected(type);
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type) throws ZserioEmitCppException
        {
            mapObjectArray();
        }

        @Override
        public void visitSqlTableType(SqlTableType type) throws ZserioEmitCppException
        {
            // array of sql_tables is not supported
            unexpected(type);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type) throws ZserioEmitCppException
        {
            // array of sql_databases is not supported
            unexpected(type);
        }

        @Override
        public void visitConstType(ConstType type) throws ZserioEmitCppException
        {
            unexpected(type);
        }

        private void mapObjectArray()
        {
            // use the original type so that subtype is kept
            cppType = new NativeObjectArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, ZSERIO_RUNTIME_INCLUDE_PREFIX,
                    CppNativeTypeMapper.this.getCppType(originalType));
        }

        @Override
        protected void mapBitfieldType(BitFieldType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), false);
        }

        @Override
        protected void mapSignedIntegralType(int nBits, boolean variable) throws ZserioEmitCppException
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
                    throw new ZserioEmitCppException("Unexpected size of variable integer (" + nBits + ")");
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
        protected void mapUnsignedIntegralType(int nBits, boolean variable) throws ZserioEmitCppException
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
                    throw new ZserioEmitCppException("Unexpected size of variable integer (" + nBits + ")");
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

        private final ZserioType originalType;
        private CppNativeType cppType;
    }

    private class ZserioTypeMapperVisitor extends TypeMapperVisitor
    {
        public CppNativeType getCppType()
        {
            return cppType;
        }

        @Override
        public void visitArrayType(ArrayType type) throws ZserioEmitCppException
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
            TypeReference.resolveBaseType(elementType).callVisitor(arrayVisitor);
            cppType = arrayVisitor.getCppType();

            if (cppType == null)
                throw new ZserioEmitCppException("Unhandled array element type: " + type.getClass().getName());
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            cppType = booleanType;
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            final NativeIntegralType nativeBaseType = getCppIntegralType(type.getIntegerBaseType());
            final List<String> namespacePath = cppPackageMapper.getPackagePath(type);
            final String name = type.getName();
            final String includeFileName = getIncludePathForUserDefinedType(type);
            cppType = new NativeEnumType(namespacePath, name, includeFileName, nativeBaseType);
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
                throw new ZserioEmitCppException("Unexpected bit size of float (" + type.getBitSize() + ")");
            }
        }

        @Override
        public void visitStringType(StringType type)
        {
            cppType = stringType;
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitRpcType(RpcType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitSubtype(Subtype type)
        {
            final ZserioType targetType = type.getTargetType();
            final CppNativeType nativeTargetType = CppNativeTypeMapper.this.getCppType(targetType);
            final List<String> namespacePath = cppPackageMapper.getPackagePath(type);
            final String name = type.getName();
            final String includeFileName = getIncludePathForUserDefinedType(type);
            cppType = new NativeSubType(namespacePath, name, includeFileName, nativeTargetType);
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation type) throws ZserioEmitCppException
        {
            final ZserioType resolvedReferencedType = TypeReference.resolveType(type.getReferencedType());
            if (resolvedReferencedType instanceof Subtype)
                visitSubtype((Subtype)resolvedReferencedType);
            else
                mapCompoundType(type.getBaseType());
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type) throws ZserioEmitCppException
        {
            mapCompoundType(type);
        }

        @Override
        public void visitSqlTableType(SqlTableType type) throws ZserioEmitCppException
        {
            mapCompoundType(type);
        }

        @Override
        public void visitConstType(ConstType type) throws ZserioEmitCppException
        {
            final CppNativeType nativeTargetType = CppNativeTypeMapper.this.getCppType(type.getConstType());
            cppType = new NativeConstType(constPackagePath, type.getName(), constIncludeFile, nativeTargetType);
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
                    throw new ZserioEmitCppException("Unexpected size of variable integer (" + nBits + ")");
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
                    throw new ZserioEmitCppException("unexpected size of variable integer (" +
                            Integer.toString(nBits) + ")");
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
            final List<String> namespacePath = cppPackageMapper.getPackagePath(type);
            final String name = type.getName();
            final String includeFileName = getIncludePathForUserDefinedType(type);
            cppType = new NativeCompoundType(namespacePath, name, includeFileName);
        }

        private CppNativeType cppType;
    }

    private final PackageMapper cppPackageMapper;
    private final List<String> constPackagePath;
    private final String constIncludeFile;

    private final static String INCLUDE_DIR_SEPARATOR = "/";
    private final static String HEADER_SUFFIX = ".h";

    private final static List<String> ZSERIO_RUNTIME_NAMESPACE_PATH = Arrays.asList("zserio");
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
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "BoolArray", BASIC_ARRAY_H, booleanType);
    private final static NativeArrayType stdStringArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "StringArray", BASIC_ARRAY_H, stringType);

    private final static NativeArrayType float16ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Float16Array", BASIC_ARRAY_H, floatType);
    private final static NativeArrayType float32ArrayType =
            new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Float32Array", BASIC_ARRAY_H, floatType);
    private final static NativeArrayType float64ArrayType =
            new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Float64Array", BASIC_ARRAY_H, doubleType);

    private final static NativeArrayType int8ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Int8Array", BIT_FIELD_ARRAY_H, int8Type);
    private final static NativeArrayType int16ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Int16Array", BIT_FIELD_ARRAY_H, int16Type);
    private final static NativeArrayType int32ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Int32Array", BIT_FIELD_ARRAY_H, int32Type);
    private final static NativeArrayType int64ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "Int64Array", BIT_FIELD_ARRAY_H, int64Type);

    private final static NativeArrayType uint8ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "UInt8Array", BIT_FIELD_ARRAY_H, uint8Type);
    private final static NativeArrayType uint16ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "UInt16Array", BIT_FIELD_ARRAY_H, uint16Type);
    private final static NativeArrayType uint32ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "UInt32Array", BIT_FIELD_ARRAY_H, uint32Type);
    private final static NativeArrayType uint64ArrayType =
        new NativeIntegralArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "UInt64Array", BIT_FIELD_ARRAY_H, uint64Type);

    private final static NativeArrayType varInt16ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarInt16Array", BASIC_ARRAY_H, int16Type);
    private final static NativeArrayType varUInt16ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarUInt16Array", BASIC_ARRAY_H, uint16Type);

    private final static NativeArrayType varInt32ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarInt32Array", BASIC_ARRAY_H, int32Type);
    private final static NativeArrayType varUInt32ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarUInt32Array", BASIC_ARRAY_H, uint32Type);

    private final static NativeArrayType varInt64ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarInt64Array", BASIC_ARRAY_H, int64Type);
    private final static NativeArrayType varUInt64ArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarUInt64Array", BASIC_ARRAY_H, uint64Type);

    private final static NativeArrayType varIntArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarIntArray", BASIC_ARRAY_H, int64Type);
    private final static NativeArrayType varUIntArrayType =
        new NativeArrayType(ZSERIO_RUNTIME_NAMESPACE_PATH, "VarUIntArray", BASIC_ARRAY_H, uint64Type);

    private final static String CONST_TYPE_NAME = "ConstType";
}
