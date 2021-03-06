package zserio.extension.cpp;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.InstantiateType;
import zserio.ast.PackageName;
import zserio.ast.PubsubType;
import zserio.ast.TypeInstantiation;
import zserio.ast.UnionType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.ExternType;
import zserio.ast.FloatType;
import zserio.ast.ServiceType;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.Subtype;
import zserio.ast.TypeReference;
import zserio.ast.VarIntegerType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.symbols.CppNativeSymbol;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeAnyHolderType;
import zserio.extension.cpp.types.NativeArrayType;
import zserio.extension.cpp.types.NativeBitBufferType;
import zserio.extension.cpp.types.NativeBitFieldArrayType;
import zserio.extension.cpp.types.NativeBlobBufferType;
import zserio.extension.cpp.types.NativeBooleanType;
import zserio.extension.cpp.types.NativeCompoundType;
import zserio.extension.cpp.types.NativeDoubleType;
import zserio.extension.cpp.types.NativeFloatType;
import zserio.extension.cpp.types.NativeHeapOptionalHolderType;
import zserio.extension.cpp.types.NativeInplaceOptionalHolderType;
import zserio.extension.cpp.types.NativeIntegralType;
import zserio.extension.cpp.types.NativeMapType;
import zserio.extension.cpp.types.NativeObjectArrayType;
import zserio.extension.cpp.types.NativeSetType;
import zserio.extension.cpp.types.NativeStringType;
import zserio.extension.cpp.types.NativeStringViewType;
import zserio.extension.cpp.types.NativeUniquePtrType;
import zserio.extension.cpp.types.NativeUserType;
import zserio.extension.cpp.types.NativeVectorType;

public class CppNativeMapper
{
    /**
     * Constructor from types context.
     *
     * @param typesContext Types context to construct from.
     */
    public CppNativeMapper(TypesContext typesContext)
    {
        this.typesContext = typesContext;

        anyHolderType = new NativeAnyHolderType(typesContext, uint8Type);
        uniquePtrType = new NativeUniquePtrType(typesContext);
        heapOptionalHolderType = new NativeHeapOptionalHolderType(typesContext);
        inplaceOptionalHolderType = new NativeInplaceOptionalHolderType();

        stringType = new NativeStringType(typesContext);
        stringViewType = new NativeStringViewType();
        vectorType = new NativeVectorType(typesContext);
        mapType = new NativeMapType(typesContext);
        setType = new NativeSetType(typesContext);
        bitBufferType = new NativeBitBufferType(typesContext, uint8Type);
        blobBufferType = new NativeBlobBufferType(typesContext, uint8Type);

        booleanArrayType = new NativeArrayType(booleanType, "BoolArrayTraits", false, typesContext, vectorType);
        stringArrayType = new NativeArrayType(stringType,
                "StringArrayTraits<" + typesContext.getAllocatorDefinition().getAllocatorType() + ">",
                false, typesContext, vectorType);

        float16ArrayType = new NativeArrayType(floatType, "Float16ArrayTraits", false,
                typesContext, vectorType);
        float32ArrayType = new NativeArrayType(floatType, "Float32ArrayTraits", false,
                typesContext, vectorType);
        float64ArrayType = new NativeArrayType(doubleType, "Float64ArrayTraits", false,
                typesContext, vectorType);

        bitBufferArrayType = new NativeArrayType(bitBufferType,
                "BitBufferArrayTraits<" + typesContext.getAllocatorDefinition().getAllocatorType() + ">",
                false, typesContext, vectorType);

        int8ArrayType = new NativeArrayType(int8Type, "StdIntArrayTraits", true, typesContext, vectorType);
        int16ArrayType = new NativeArrayType(int16Type, "StdIntArrayTraits", true, typesContext, vectorType);
        int32ArrayType = new NativeArrayType(int32Type, "StdIntArrayTraits", true, typesContext, vectorType);
        int64ArrayType = new NativeArrayType(int64Type, "StdIntArrayTraits", true, typesContext, vectorType);

        uint8ArrayType = new NativeArrayType(uint8Type, "StdIntArrayTraits", true, typesContext, vectorType);
        uint16ArrayType = new NativeArrayType(uint16Type, "StdIntArrayTraits", true, typesContext, vectorType);
        uint32ArrayType = new NativeArrayType(uint32Type, "StdIntArrayTraits", true, typesContext, vectorType);
        uint64ArrayType = new NativeArrayType(uint64Type, "StdIntArrayTraits", true, typesContext, vectorType);

        varInt16ArrayType = new NativeArrayType(int16Type, "VarIntNNArrayTraits", true,
                typesContext, vectorType);
        varInt32ArrayType = new NativeArrayType(int32Type, "VarIntNNArrayTraits", true,
                typesContext, vectorType);
        varInt64ArrayType = new NativeArrayType(int64Type, "VarIntNNArrayTraits", true,
                typesContext, vectorType);
        varIntArrayType = new NativeArrayType(int64Type, "VarIntArrayTraits", true, typesContext, vectorType);

        varUInt16ArrayType = new NativeArrayType(uint16Type, "VarIntNNArrayTraits", true,
                typesContext, vectorType);
        varUInt32ArrayType = new NativeArrayType(uint32Type, "VarIntNNArrayTraits", true,
                typesContext, vectorType);
        varUInt64ArrayType = new NativeArrayType(uint64Type, "VarIntNNArrayTraits", true,
                typesContext, vectorType);
        varUIntArrayType = new NativeArrayType(uint64Type, "VarIntArrayTraits", true, typesContext, vectorType);

        varSizeArrayType = new NativeArrayType(uint32Type, "VarSizeArrayTraits", false,
                typesContext, vectorType);
    }

    /**
     * Returns a C++ symbol that can hold an instance of Zserio symbol.
     *
     * @param symbol Zserio symbol.
     *
     * @return C++ symbol.
     *
     * @throws ZserioExtensionException If the Zserio symbol cannot be mapped to any C++ symbol.
     */
    public CppNativeSymbol getCppSymbol(AstNode symbol) throws ZserioExtensionException
    {
        if (symbol instanceof Constant)
        {
            final Constant constant = (Constant)symbol;
            final PackageName packageName = constant.getPackage().getPackageName();
            final String name = constant.getName();
            final String includeFileName = getIncludePath(packageName, name);
            return new CppNativeSymbol(packageName, name, includeFileName);
        }
        else
            throw new ZserioExtensionException("Unhandled symbol '" + symbol.getClass().getName() +
                    "' in CppNativeMapper!");
    }

    /**
     * Returns a C++ type that can hold an instance of the Zserio type.
     *
     * @param typeInstantiation Instantiation of the Zserio type.
     *
     * @return C++ type which can hold the Zserio type.
     *
     * @throws ZserioExtensionException If the Zserio type cannot be mapped to any C++ type.
     */
    public CppNativeType getCppType(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return mapDynamicBitField((DynamicBitFieldInstantiation)typeInstantiation);

        // don't resolve subtypes so that the subtype name (C++ typedef) will be used
        return getCppType(typeInstantiation.getType());
    }

    /**
     * Returns a C++ type that can hold an instance of referenced Zserio type.
     *
     * @param typeReference Reference to the Zserio type.
     *
     * @return C++ type which can hold the Zserio type.
     *
     * @throws ZserioExtensionException If the Zserio type cannot be mapped to any C++ type.
     */
    public CppNativeType getCppType(TypeReference typeReference) throws ZserioExtensionException
    {
        // don't resolve subtypes so that the subtype name (C++ typedef) will be used
        return getCppType(typeReference.getType());
    }

    /**
     * Returns a C++ type that can hold an instance of given Zserio type.
     *
     * @param type Zserio type for mapping to C++ type.
     *
     * @return C++ type which can hold a Zserio type.
     *
     * @throws ZserioExtensionException If the Zserio type cannot be mapped to any C++ type.
     */
    public CppNativeType getCppType(ZserioType type) throws ZserioExtensionException
    {
        final ZserioTypeMapperVisitor visitor = new ZserioTypeMapperVisitor();
        type.accept(visitor);

        final ZserioExtensionException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        final CppNativeType nativeType = visitor.getCppType();
        if (nativeType == null)
            throw new ZserioExtensionException("Unhandled type '" + type.getClass().getName() +
                    "' in CppNativeMapper!");

        return nativeType;
    }

    /**
     * Returns a C++ integer type that can hold an instance of given Zserio integer type.
     *
     * @param typeInstantiation Instantiation of Zserio integer type for mapping to C++ integer type.
     *
     * @return C++ type which can hold a Zserio integer type.
     *
     * @throws ZserioExtensionException If the Zserio integer type cannot be mapped to any C++ integer type.
     */
    public NativeIntegralType getCppIntegralType(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        CppNativeType nativeType = null;
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            nativeType = mapDynamicBitField((DynamicBitFieldInstantiation)typeInstantiation);
        }
        else
        {
            // use the base type to get the integral type (i.e. to resolve subtype)
            nativeType = getCppType(typeInstantiation.getBaseType());
        }

        if (!(nativeType instanceof NativeIntegralType))
        {
            throw new ZserioExtensionException("Unhandled integral type '" +
                    typeInstantiation.getBaseType().getName() + "' in CppNativeMapper!");
        }

        return (NativeIntegralType)nativeType;
    }

    public NativeAnyHolderType getAnyHolderType()
    {
        return anyHolderType;
    }

    public NativeUniquePtrType getUniquePtrType()
    {
        return uniquePtrType;
    }

    public NativeHeapOptionalHolderType getHeapOptionalHolderType()
    {
        return heapOptionalHolderType;
    }

    public NativeInplaceOptionalHolderType getInplaceOptionalHolderType()
    {
        return inplaceOptionalHolderType;
    }

    public NativeStringType getStringType()
    {
        return stringType;
    }

    public NativeStringViewType getStringViewType()
    {
        return stringViewType;
    }

    public NativeVectorType getVectorType()
    {
        return vectorType;
    }

    public NativeMapType getMapType()
    {
        return mapType;
    }

    public NativeSetType getSetType()
    {
        return setType;
    }

    public NativeBitBufferType getBitBufferType()
    {
        return bitBufferType;
    }

    public NativeBlobBufferType getBlobBufferType()
    {
        return blobBufferType;
    }

    public NativeIntegralType getUInt64Type()
    {
        return uint64Type;
    }

    public NativeIntegralType getInt64Type()
    {
        return int64Type;
    }

    private CppNativeType mapArray(ArrayInstantiation instantiation) throws ZserioExtensionException
    {
        final TypeInstantiation elementInstantiation = instantiation.getElementTypeInstantiation();
        final ArrayElementTypeMapperVisitor arrayVisitor =
                new ArrayElementTypeMapperVisitor(elementInstantiation);

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
        elementInstantiation.getBaseType().accept(arrayVisitor);

        final ZserioExtensionException thrownException = arrayVisitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        final CppNativeType nativeType = arrayVisitor.getCppType();
        if (nativeType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" +
                    elementInstantiation.getBaseType().getClass().getName() + "' in CppNativeMapper!");
        }

        return nativeType;
    }

    private static CppNativeType mapDynamicBitField(DynamicBitFieldInstantiation instantiation)
            throws ZserioExtensionException
    {
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int numBits = instantiation.getMaxBitSize();
        return isSigned ? mapSignedIntegralType(numBits) : mapUnsignedIntegralType(numBits);
    }

    private static CppNativeType mapSignedIntegralType(int numBits)
    {
        if (numBits <= 8)
            return int8Type;
        else if (numBits <= 16)
            return int16Type;
        else if (numBits <= 32)
            return int32Type;
        else
            return int64Type;
    }

    private static CppNativeType mapUnsignedIntegralType(int numBits)
    {
        if (numBits <= 8)
            return uint8Type;
        else if (numBits <= 16)
            return uint16Type;
        else if (numBits <= 32)
            return uint32Type;
        else
            return uint64Type;
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
        public void visitStdIntegerType(StdIntegerType type)
        {
            mapIntegralType(type.getBitSize(), type.isSigned(), false);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), true);
        }

        protected void mapIntegralType(int numBits, boolean signed, boolean isVarInteger)
        {
            if (signed)
                mapSignedIntegralType(numBits, isVarInteger);
            else
                mapUnsignedIntegralType(numBits, isVarInteger);
        }

        protected abstract void mapSignedIntegralType(int numBits, boolean isVarInteger);
        protected abstract void mapUnsignedIntegralType(int numBits, boolean isVarInteger);
    }

    private class ArrayElementTypeMapperVisitor extends TypeMapperVisitor
    {
        public ArrayElementTypeMapperVisitor(TypeInstantiation originalInstantiation)
        {
            this.originalInstantiation = originalInstantiation;
        }

        public CppNativeType getCppType()
        {
            return cppType;
        }

        public ZserioExtensionException getThrownException()
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
            try
            {
                // use the original instantiation so that subtype is kept
                final CppNativeType nativeElementType = CppNativeMapper.this.getCppType(originalInstantiation);
                cppType = new NativeArrayType(nativeElementType, "EnumArrayTraits", true,
                        typesContext, vectorType);
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitBitmaskType(BitmaskType type)
        {
            try
            {
                // use the original instantiation so that subtype is kept
                final CppNativeType nativeElementType = CppNativeMapper.this.getCppType(originalInstantiation);
                cppType = new NativeArrayType(nativeElementType, "BitmaskArrayTraits", true,
                        typesContext, vectorType);
            }
            catch (ZserioExtensionException exception)
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
        public void visitExternType(ExternType externType)
        {
            cppType = bitBufferArrayType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            cppType = stringArrayType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapObjectArray();
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            mapBitFieldArray();
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            mapBitFieldArray();
        }

        @Override
        protected void mapSignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                switch (numBits)
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
                if (numBits <= 8)
                    cppType = int8ArrayType;
                else if (numBits <= 16)
                    cppType = int16ArrayType;
                else if (numBits <= 32)
                    cppType = int32ArrayType;
                else
                    cppType = int64ArrayType;
            }
        }

        @Override
        protected void mapUnsignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                switch (numBits)
                {
                case 16:
                    cppType = varUInt16ArrayType;
                    break;

                case 32:
                    cppType = varUInt32ArrayType;
                    break;

                case 40:
                    cppType = varSizeArrayType;
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
                if (numBits <= 8)
                    cppType = uint8ArrayType;
                else if (numBits <= 16)
                    cppType = uint16ArrayType;
                else if (numBits <= 32)
                    cppType = uint32ArrayType;
                else
                    cppType = uint64ArrayType;
            }
        }

        private void mapObjectArray()
        {
            try
            {
                // use the original instantiation so that subtype is kept
                cppType = new NativeObjectArrayType(CppNativeMapper.this.getCppType(originalInstantiation),
                        CppNativeMapper.this.typesContext, CppNativeMapper.this.vectorType);
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        private void mapBitFieldArray()
        {
            try
            {
                // use the original instantiation so that subtype is kept
                cppType = new NativeBitFieldArrayType(CppNativeMapper.this.getCppType(originalInstantiation),
                        CppNativeMapper.this.typesContext, CppNativeMapper.this.vectorType);
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        private final TypeInstantiation originalInstantiation;

        private CppNativeType cppType = null;
        private ZserioExtensionException thrownException = null;
    }

    private class ZserioTypeMapperVisitor extends TypeMapperVisitor
    {
        public CppNativeType getCppType()
        {
            return cppType;
        }

        public ZserioExtensionException getThrownException()
        {
            return thrownException;
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
        public void visitEnumType(EnumType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, true);
        }

        @Override
        public void visitBitmaskType(BitmaskType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, true);
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
        public void visitExternType(ExternType type)
        {
            cppType = bitBufferType;
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, false);
        }

        @Override
        public void visitPubsubType(PubsubType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, false);
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
            try
            {
                final CppNativeType nativeTargetType =
                        CppNativeMapper.this.getCppType(type.getTypeReference());
                final PackageName packageName = type.getPackage().getPackageName();
                final String name = type.getName();
                final String includeFileName = getIncludePath(packageName, name);
                cppType = new NativeUserType(packageName, name, includeFileName,
                        nativeTargetType.isSimpleType());
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitInstantiateType(InstantiateType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName(); // note that name is same as the referenced type name
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, false);
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            mapIntegralType(type.getBitSize(), type.isSigned(), false);
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), false);
        }

        @Override
        protected void mapSignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                switch (numBits)
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
                    // shall not occur!
                    break;
                }
            }
            else
            {
                cppType = CppNativeMapper.mapSignedIntegralType(numBits);
            }
        }

        @Override
        protected void mapUnsignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                switch (numBits)
                {
                case 16:
                    cppType = uint16Type;
                    break;

                case 32:
                case 40:
                    cppType = uint32Type;
                    break;

                case 64:
                case 72:
                    cppType = uint64Type;
                    break;

                default:
                    // shall not occur!
                    break;
                }
            }
            else
            {
                cppType = CppNativeMapper.mapUnsignedIntegralType(numBits);
            }
        }

        private void mapCompoundType(CompoundType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeCompoundType(packageName, name, includeFileName);
        }

        private CppNativeType cppType = null;
        private ZserioExtensionException thrownException = null;
    }

    private final TypesContext typesContext;

    private final NativeAnyHolderType anyHolderType;
    private final NativeUniquePtrType uniquePtrType;
    private final NativeHeapOptionalHolderType heapOptionalHolderType;
    private final NativeInplaceOptionalHolderType inplaceOptionalHolderType;

    private final static String INCLUDE_DIR_SEPARATOR = "/";
    private final static String HEADER_SUFFIX = ".h";

    private final static NativeBooleanType booleanType = new NativeBooleanType();
    private final NativeStringType stringType;
    private final NativeStringViewType stringViewType;
    private final NativeVectorType vectorType;
    private final NativeMapType mapType;
    private final NativeSetType setType;
    private final NativeBitBufferType bitBufferType;
    private final NativeBlobBufferType blobBufferType;

    private final static NativeFloatType floatType = new NativeFloatType();
    private final static NativeDoubleType doubleType = new NativeDoubleType();

    private final static NativeIntegralType uint8Type = new NativeIntegralType(8, false);
    private final static NativeIntegralType uint16Type = new NativeIntegralType(16, false);
    private final static NativeIntegralType uint32Type = new NativeIntegralType(32, false);
    private final static NativeIntegralType uint64Type = new NativeIntegralType(64, false);

    private final static NativeIntegralType int8Type = new NativeIntegralType(8, true);
    private final static NativeIntegralType int16Type = new NativeIntegralType(16, true);
    private final static NativeIntegralType int32Type = new NativeIntegralType(32, true);
    private final static NativeIntegralType int64Type = new NativeIntegralType(64, true);

    private final NativeArrayType booleanArrayType;
    private final NativeArrayType stringArrayType;

    private final NativeArrayType float16ArrayType;
    private final NativeArrayType float32ArrayType;
    private final NativeArrayType float64ArrayType;

    private final NativeArrayType bitBufferArrayType;

    private final NativeArrayType int8ArrayType;
    private final NativeArrayType int16ArrayType;
    private final NativeArrayType int32ArrayType;
    private final NativeArrayType int64ArrayType;

    private final NativeArrayType uint8ArrayType;
    private final NativeArrayType uint16ArrayType;
    private final NativeArrayType uint32ArrayType;
    private final NativeArrayType uint64ArrayType;

    private final NativeArrayType varInt16ArrayType;
    private final NativeArrayType varInt32ArrayType;
    private final NativeArrayType varInt64ArrayType;
    private final NativeArrayType varIntArrayType;

    private final NativeArrayType varUInt16ArrayType;
    private final NativeArrayType varUInt32ArrayType;
    private final NativeArrayType varUInt64ArrayType;
    private final NativeArrayType varUIntArrayType;

    private final NativeArrayType varSizeArrayType;
}
