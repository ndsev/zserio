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
import zserio.ast.BytesType;
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
import zserio.extension.cpp.types.NativeArrayTraits;
import zserio.extension.cpp.types.NativeArrayType;
import zserio.extension.cpp.types.CppNativeArrayableType;
import zserio.extension.cpp.types.NativeBitFieldArrayTraits;
import zserio.extension.cpp.types.NativeBuiltinType;
import zserio.extension.cpp.types.NativeBytesType;
import zserio.extension.cpp.types.NativeCompoundType;
import zserio.extension.cpp.types.NativeDynamicBitFieldArrayTraits;
import zserio.extension.cpp.types.NativeIntegralType;
import zserio.extension.cpp.types.NativeRuntimeAllocArrayableType;
import zserio.extension.cpp.types.NativeRuntimeAllocType;
import zserio.extension.cpp.types.NativeRuntimeType;
import zserio.extension.cpp.types.NativeStringViewType;
import zserio.extension.cpp.types.NativeTemplatedArrayTraits;
import zserio.extension.cpp.types.NativeUserArrayableType;
import zserio.extension.cpp.types.NativeUserType;

/**
 * C++ native mapper.
 *
 * Provides mapping of types and symbols from Zserio package symbols to C++ native types and symbols.
 */
public class CppNativeMapper
{
    public CppNativeMapper(TypesContext typesContext)
    {
        final TypesContext.AllocatorDefinition allocatorDefinition = typesContext.getAllocatorDefinition();
        anyHolderType = new NativeRuntimeAllocType(typesContext.getAnyHolder(), allocatorDefinition,
                stdUInt8Type);
        uniquePtrType = new NativeRuntimeAllocType(typesContext.getUniquePtr(), allocatorDefinition);
        heapOptionalHolderType = new NativeRuntimeAllocType(typesContext.getHeapOptionalHolder(),
                allocatorDefinition);
        inplaceOptionalHolderType = new NativeRuntimeType("InplaceOptionalHolder", "zserio/OptionalHolder.h");

        stringType = new NativeRuntimeAllocArrayableType(typesContext.getString(), allocatorDefinition,
                "char", typesContext.getStringArrayTraits());
        stringViewType = new NativeStringViewType();
        vectorType = new NativeRuntimeAllocType(typesContext.getVector(), allocatorDefinition);
        mapType = new NativeRuntimeAllocType(typesContext.getMap(), allocatorDefinition);
        setType = new NativeRuntimeAllocType(typesContext.getSet(), allocatorDefinition);
        bytesType = new NativeBytesType(typesContext, stdUInt8Type);
        bitBufferType = new NativeRuntimeAllocArrayableType(typesContext.getBitBuffer(),
                allocatorDefinition, stdUInt8Type, typesContext.getBitBufferArrayTraits());
        packingContextNodeType = new NativeRuntimeAllocType(typesContext.getPackingContextNode(),
                allocatorDefinition, stdUInt8Type);
        typeInfoType = new NativeRuntimeAllocType(typesContext.getTypeInfo(),
                allocatorDefinition, stdUInt8Type);
        reflectableFactoryType = new NativeRuntimeAllocType(typesContext.getRelectableFactory(),
                allocatorDefinition, stdUInt8Type);
        reflectablePtrType = new NativeRuntimeAllocType(typesContext.getReflectablePtr(),
                allocatorDefinition, stdUInt8Type);
        reflectableConstPtrType = new NativeRuntimeAllocType(typesContext.getReflectableConstPtr(),
                allocatorDefinition, stdUInt8Type);
        serviceType = new NativeRuntimeAllocType(typesContext.getService(),
                allocatorDefinition, stdUInt8Type);
        serviceClientType = new NativeRuntimeAllocType(typesContext.getServiceClient(),
                allocatorDefinition, stdUInt8Type);
        serviceDataPtrType = new NativeRuntimeAllocType(typesContext.getServiceDataPtr(),
                allocatorDefinition, stdUInt8Type);
        reflectableServiceDataType = new NativeRuntimeAllocType(typesContext.getReflectableServiceData(),
                allocatorDefinition, stdUInt8Type);
        objectServiceDataType = new NativeRuntimeAllocType(typesContext.getObjectServiceData(),
                allocatorDefinition, stdUInt8Type);
        rawServiceDataHolderType = new NativeRuntimeAllocType(typesContext.getRawServiceDataHolder(),
                allocatorDefinition, stdUInt8Type);
        rawServiceDataViewType = new NativeRuntimeAllocType(typesContext.getRawServiceDataView(),
                allocatorDefinition, stdUInt8Type);
    }

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

    public CppNativeType getCppType(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return mapDynamicBitFieldInstantiation((DynamicBitFieldInstantiation)typeInstantiation);

        // don't resolve subtypes so that the subtype name (C++ typedef) will be used
        return getCppType(typeInstantiation.getType());
    }

    public CppNativeType getCppType(TypeReference typeReference) throws ZserioExtensionException
    {
        // don't resolve subtypes so that the subtype name (C++ typedef) will be used
        return getCppType(typeReference.getType());
    }

    public CppNativeType getCppType(ZserioType type) throws ZserioExtensionException
    {
        final TypeMapperVisitor visitor = new TypeMapperVisitor();
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

    public NativeIntegralType getCppIntegralType(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        CppNativeType nativeType = null;
        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            nativeType = mapDynamicBitFieldInstantiation((DynamicBitFieldInstantiation)typeInstantiation);
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

    public NativeRuntimeAllocType getAnyHolderType()
    {
        return anyHolderType;
    }

    public NativeRuntimeAllocType getUniquePtrType()
    {
        return uniquePtrType;
    }

    public NativeRuntimeAllocType getHeapOptionalHolderType()
    {
        return heapOptionalHolderType;
    }

    public NativeRuntimeType getInplaceOptionalHolderType()
    {
        return inplaceOptionalHolderType;
    }

    public NativeRuntimeAllocArrayableType getStringType()
    {
        return stringType;
    }

    public NativeStringViewType getStringViewType()
    {
        return stringViewType;
    }

    public NativeRuntimeAllocType getVectorType()
    {
        return vectorType;
    }

    public NativeRuntimeAllocType getMapType()
    {
        return mapType;
    }

    public NativeRuntimeAllocType getSetType()
    {
        return setType;
    }

    public NativeRuntimeAllocArrayableType getBitBufferType()
    {
        return bitBufferType;
    }

    public NativeRuntimeAllocType getPackingContextNodeType()
    {
        return packingContextNodeType;
    }

    public NativeRuntimeAllocType getTypeInfoType()
    {
        return typeInfoType;
    }

    public NativeRuntimeAllocType getReflectableFactoryType()
    {
        return reflectableFactoryType;
    }

    public NativeRuntimeAllocType getReflectablePtrType()
    {
        return reflectablePtrType;
    }

    public NativeRuntimeAllocType getReflectableConstPtrType()
    {
        return reflectableConstPtrType;
    }

    public NativeRuntimeAllocType getServiceType()
    {
        return serviceType;
    }

    public NativeRuntimeAllocType getServiceClientType()
    {
        return serviceClientType;
    }

    public NativeRuntimeAllocType getServiceDataPtrType()
    {
        return serviceDataPtrType;
    }

    public NativeRuntimeAllocType getReflectableServiceDataType()
    {
        return reflectableServiceDataType;
    }

    public NativeRuntimeAllocType getObjectServiceDataType()
    {
        return objectServiceDataType;
    }

    public NativeRuntimeAllocType getRawServiceDataHolderType()
    {
        return rawServiceDataHolderType;
    }

    public NativeRuntimeAllocType getRawServiceDataViewType()
    {
        return rawServiceDataViewType;
    }

    public NativeIntegralType getUInt64Type()
    {
        return stdUInt64Type;
    }

    public NativeIntegralType getInt64Type()
    {
        return stdInt64Type;
    }

    private CppNativeType mapArray(ArrayInstantiation instantiation) throws ZserioExtensionException
    {
        final TypeInstantiation elementInstantiation = instantiation.getElementTypeInstantiation();

        final CppNativeType nativeType = getCppType(elementInstantiation);
        if (!(nativeType instanceof CppNativeArrayableType))
        {
            throw new ZserioExtensionException("Unhandled arrayable type '" +
                    elementInstantiation.getClass().getName() + "' in CppNativeMapper!");
        }

        return new NativeArrayType((CppNativeArrayableType)nativeType, vectorType);
    }

    private static CppNativeType mapDynamicBitFieldInstantiation(DynamicBitFieldInstantiation instantiation)
            throws ZserioExtensionException
    {
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int maxBitSize = instantiation.getMaxBitSize();
        return mapDynamicBitFieldType(isSigned, maxBitSize);
    }

    private static CppNativeType mapBitFieldType(boolean isSigned, int numBits)
    {
        if (numBits <= 8)
            return isSigned ? int8Type : bit8Type;
        else if (numBits <= 16)
            return isSigned ? int16Type : bit16Type;
        else if (numBits <= 32)
            return isSigned ? int32Type : bit32Type;
        else
            return isSigned ? int64Type : bit64Type;
    }

    private static CppNativeType mapDynamicBitFieldType(boolean isSigned, int maxBitSize)
    {
        if (maxBitSize <= 8)
            return isSigned ? dynamicInt8Type : dynamicBit8Type;
        else if (maxBitSize <= 16)
            return isSigned ? dynamicInt16Type : dynamicBit16Type;
        else if (maxBitSize <= 32)
            return isSigned ? dynamicInt32Type : dynamicBit32Type;
        else
            return isSigned ? dynamicInt64Type : dynamicBit64Type;
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

    private class TypeMapperVisitor extends ZserioAstDefaultVisitor
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
        public void visitStdIntegerType(StdIntegerType type)
        {
            final int numBits = type.getBitSize();
            final boolean isSigned = type.isSigned();
            if (numBits <= 8)
                cppType = isSigned ? stdInt8Type : stdUInt8Type;
            else if (numBits <= 16)
                cppType = isSigned ? stdInt16Type : stdUInt16Type;
            else if (numBits <= 32)
                cppType = isSigned ? stdInt32Type : stdUInt32Type;
            else
                cppType = isSigned ? stdInt64Type : stdUInt64Type;
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            switch (type.getMaxBitSize())
            {
            case 16:
                cppType = (type.isSigned()) ? varInt16Type : varUInt16Type;
                break;

            case 32:
                cppType = (type.isSigned()) ? varInt32Type : varUInt32Type;
                break;

            case 40:
                if (!type.isSigned())
                    cppType = varSizeType;
                break;

            case 64:
                cppType = (type.isSigned()) ? varInt64Type : varUInt64Type;
                break;

            case 72:
                cppType = (type.isSigned()) ? varIntType : varUIntType;
                break;

            default:
                // not supported
                break;
            }
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            cppType = booleanType;
        }

        @Override
        public void visitBytesType(BytesType type)
        {
            cppType = bytesType;
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
            cppType = new NativeUserArrayableType(packageName, name, includeFileName, true,
                    new NativeTemplatedArrayTraits("EnumArrayTraits"));
        }

        @Override
        public void visitBitmaskType(BitmaskType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserArrayableType(packageName, name, includeFileName, true,
                    new NativeTemplatedArrayTraits("BitmaskArrayTraits"));
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
                cppType = float16Type;
                break;

            case 32:
                cppType = float32Type;
                break;

            case 64:
                cppType = float64Type;
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
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, false);
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserType(packageName, name, includeFileName, false);
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
            mapAliasType(type, type.getTypeReference());
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitInstantiateType(InstantiateType type)
        {
            mapAliasType(type, type.getTypeReference());
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            cppType = CppNativeMapper.mapBitFieldType(type.isSigned(), type.getBitSize());
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            // this is only for reference to dynamic bit field type (e.g. when used as compound parameter)
            cppType = CppNativeMapper.mapDynamicBitFieldType(type.isSigned(), DynamicBitFieldType.MAX_BIT_SIZE);
        }

        private void mapCompoundType(CompoundType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeCompoundType(packageName, name, includeFileName);
        }

        private void mapAliasType(ZserioType aliasType, TypeReference referencedType)
        {
            try
            {
                final CppNativeType nativeReferencedType = CppNativeMapper.this.getCppType(referencedType);
                final PackageName packageName = aliasType.getPackage().getPackageName();
                final String name = aliasType.getName();
                final String includeFileName = getIncludePath(packageName, name);
                if (nativeReferencedType instanceof CppNativeArrayableType)
                {
                    cppType = new NativeUserArrayableType(packageName, name, includeFileName,
                            nativeReferencedType.isSimple(),
                            ((CppNativeArrayableType)nativeReferencedType).getArrayTraits());
                }
                else
                {
                    cppType = new NativeUserType(packageName, name, includeFileName,
                            nativeReferencedType.isSimple());
                }
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        private CppNativeType cppType = null;
        private ZserioExtensionException thrownException = null;
    }

    private final static String INCLUDE_DIR_SEPARATOR = "/";
    private final static String HEADER_SUFFIX = ".h";

    private final NativeRuntimeAllocType anyHolderType;
    private final NativeRuntimeAllocType uniquePtrType;
    private final NativeRuntimeAllocType heapOptionalHolderType;
    private final NativeRuntimeType inplaceOptionalHolderType;

    private final NativeRuntimeAllocArrayableType stringType;
    private final NativeStringViewType stringViewType;
    private final NativeRuntimeAllocType vectorType;
    private final NativeRuntimeAllocType mapType;
    private final NativeRuntimeAllocType setType;
    private final CppNativeArrayableType bytesType;
    private final NativeRuntimeAllocArrayableType bitBufferType;
    private final NativeRuntimeAllocType packingContextNodeType;
    private final NativeRuntimeAllocType typeInfoType;
    private final NativeRuntimeAllocType reflectableFactoryType;
    private final NativeRuntimeAllocType reflectablePtrType;
    private final NativeRuntimeAllocType reflectableConstPtrType;
    private final NativeRuntimeAllocType serviceType;
    private final NativeRuntimeAllocType serviceClientType;
    private final NativeRuntimeAllocType serviceDataPtrType;
    private final NativeRuntimeAllocType reflectableServiceDataType;
    private final NativeRuntimeAllocType objectServiceDataType;
    private final NativeRuntimeAllocType rawServiceDataHolderType;
    private final NativeRuntimeAllocType rawServiceDataViewType;

    private final static NativeBuiltinType booleanType =
            new NativeBuiltinType("bool", new NativeArrayTraits("BoolArrayTraits"));
    private final static NativeBuiltinType float16Type =
            new NativeBuiltinType("float", new NativeArrayTraits("Float16ArrayTraits"));
    private final static NativeBuiltinType float32Type =
            new NativeBuiltinType("float", new NativeArrayTraits("Float32ArrayTraits"));
    private final static NativeBuiltinType float64Type =
            new NativeBuiltinType("double", new NativeArrayTraits("Float64ArrayTraits"));

    private final static NativeIntegralType stdInt8Type =
            new NativeIntegralType(8, true, new NativeTemplatedArrayTraits("StdIntArrayTraits"));
    private final static NativeIntegralType stdInt16Type =
            new NativeIntegralType(16, true, new NativeTemplatedArrayTraits("StdIntArrayTraits"));
    private final static NativeIntegralType stdInt32Type =
            new NativeIntegralType(32, true, new NativeTemplatedArrayTraits("StdIntArrayTraits"));
    private final static NativeIntegralType stdInt64Type =
            new NativeIntegralType(64, true, new NativeTemplatedArrayTraits("StdIntArrayTraits"));

    private final static NativeIntegralType stdUInt8Type =
            new NativeIntegralType(8, false, new NativeTemplatedArrayTraits("StdIntArrayTraits"));
    private final static NativeIntegralType stdUInt16Type =
            new NativeIntegralType(16, false, new NativeTemplatedArrayTraits("StdIntArrayTraits"));
    private final static NativeIntegralType stdUInt32Type =
            new NativeIntegralType(32, false, new NativeTemplatedArrayTraits("StdIntArrayTraits"));
    private final static NativeIntegralType stdUInt64Type =
            new NativeIntegralType(64, false, new NativeTemplatedArrayTraits("StdIntArrayTraits"));

    private final static NativeIntegralType int8Type =
            new NativeIntegralType(8, true, new NativeBitFieldArrayTraits());
    private final static NativeIntegralType int16Type =
            new NativeIntegralType(16, true, new NativeBitFieldArrayTraits());
    private final static NativeIntegralType int32Type =
            new NativeIntegralType(32, true, new NativeBitFieldArrayTraits());
    private final static NativeIntegralType int64Type =
            new NativeIntegralType(64, true, new NativeBitFieldArrayTraits());

    private final static NativeIntegralType bit8Type =
            new NativeIntegralType(8, false, new NativeBitFieldArrayTraits());
    private final static NativeIntegralType bit16Type =
            new NativeIntegralType(16, false, new NativeBitFieldArrayTraits());
    private final static NativeIntegralType bit32Type =
            new NativeIntegralType(32, false, new NativeBitFieldArrayTraits());
    private final static NativeIntegralType bit64Type =
            new NativeIntegralType(64, false, new NativeBitFieldArrayTraits());

    private final static NativeIntegralType dynamicInt8Type =
            new NativeIntegralType(8, true, new NativeDynamicBitFieldArrayTraits());
    private final static NativeIntegralType dynamicInt16Type =
            new NativeIntegralType(16, true, new NativeDynamicBitFieldArrayTraits());
    private final static NativeIntegralType dynamicInt32Type =
            new NativeIntegralType(32, true, new NativeDynamicBitFieldArrayTraits());
    private final static NativeIntegralType dynamicInt64Type =
            new NativeIntegralType(64, true, new NativeDynamicBitFieldArrayTraits());

    private final static NativeIntegralType dynamicBit8Type =
            new NativeIntegralType(8, false, new NativeDynamicBitFieldArrayTraits());
    private final static NativeIntegralType dynamicBit16Type =
            new NativeIntegralType(16, false, new NativeDynamicBitFieldArrayTraits());
    private final static NativeIntegralType dynamicBit32Type =
            new NativeIntegralType(32, false, new NativeDynamicBitFieldArrayTraits());
    private final static NativeIntegralType dynamicBit64Type =
            new NativeIntegralType(64, false, new NativeDynamicBitFieldArrayTraits());

    private final static NativeIntegralType varUInt16Type =
            new NativeIntegralType(16, false, new NativeTemplatedArrayTraits("VarIntNNArrayTraits"));
    private final static NativeIntegralType varUInt32Type =
            new NativeIntegralType(32, false, new NativeTemplatedArrayTraits("VarIntNNArrayTraits"));
    private final static NativeIntegralType varUInt64Type =
            new NativeIntegralType(64, false, new NativeTemplatedArrayTraits("VarIntNNArrayTraits"));
    private final static NativeIntegralType varUIntType =
            new NativeIntegralType(64, false, new NativeTemplatedArrayTraits("VarIntArrayTraits"));

    private final static NativeIntegralType varInt16Type =
            new NativeIntegralType(16, true, new NativeTemplatedArrayTraits("VarIntNNArrayTraits"));
    private final static NativeIntegralType varInt32Type =
            new NativeIntegralType(32, true, new NativeTemplatedArrayTraits("VarIntNNArrayTraits"));
    private final static NativeIntegralType varInt64Type =
            new NativeIntegralType(64, true, new NativeTemplatedArrayTraits("VarIntNNArrayTraits"));
    private final static NativeIntegralType varIntType =
            new NativeIntegralType(64, true, new NativeTemplatedArrayTraits("VarIntArrayTraits"));

    private final static NativeIntegralType varSizeType =
            new NativeIntegralType(32, false, new NativeArrayTraits("VarSizeArrayTraits"));
}
