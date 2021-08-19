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
import zserio.extension.cpp.types.NativeArrayTraits;
import zserio.extension.cpp.types.NativeArrayType;
import zserio.extension.cpp.types.NativeArrayableType;
import zserio.extension.cpp.types.NativeBitBufferType;
import zserio.extension.cpp.types.NativeBitFieldArrayTraits;
import zserio.extension.cpp.types.NativeBlobBufferType;
import zserio.extension.cpp.types.NativeBuiltinType;
import zserio.extension.cpp.types.NativeCompoundType;
import zserio.extension.cpp.types.NativeHeapOptionalHolderType;
import zserio.extension.cpp.types.NativeInplaceOptionalHolderType;
import zserio.extension.cpp.types.NativeIntegralType;
import zserio.extension.cpp.types.NativeMapType;
import zserio.extension.cpp.types.NativePackingContextNodeType;
import zserio.extension.cpp.types.NativeSetType;
import zserio.extension.cpp.types.NativeStringType;
import zserio.extension.cpp.types.NativeStringViewType;
import zserio.extension.cpp.types.NativeUniquePtrType;
import zserio.extension.cpp.types.NativeUserArrayableType;
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
        anyHolderType = new NativeAnyHolderType(typesContext, stdUInt8Type);
        uniquePtrType = new NativeUniquePtrType(typesContext);
        heapOptionalHolderType = new NativeHeapOptionalHolderType(typesContext);
        inplaceOptionalHolderType = new NativeInplaceOptionalHolderType();

        stringType = new NativeStringType(typesContext);
        stringViewType = new NativeStringViewType();
        vectorType = new NativeVectorType(typesContext);
        mapType = new NativeMapType(typesContext);
        setType = new NativeSetType(typesContext);
        bitBufferType = new NativeBitBufferType(typesContext, stdUInt8Type);
        blobBufferType = new NativeBlobBufferType(typesContext, stdUInt8Type);
        packingContextNodeType = new NativePackingContextNodeType(typesContext, stdUInt8Type);
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

    public NativePackingContextNodeType getPackingContextNodeType()
    {
        return packingContextNodeType;
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
        final ZserioType elementBaseType = elementInstantiation.getBaseType();

        final CppNativeType nativeType = getCppType(elementBaseType);
        if (!(nativeType instanceof NativeArrayableType))
        {
            throw new ZserioExtensionException("Unhandled arrayable type '" +
                    elementBaseType.getClass().getName() + "' in CppNativeMapper!");
        }

        return new NativeArrayType((NativeArrayableType)nativeType, vectorType);
    }

    private static CppNativeType mapDynamicBitField(DynamicBitFieldInstantiation instantiation)
            throws ZserioExtensionException
    {
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int numBits = instantiation.getMaxBitSize();
        return mapBitFieldType(isSigned, numBits);
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
                    new NativeArrayTraits("EnumArrayTraits", true));
        }

        @Override
        public void visitBitmaskType(BitmaskType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final String includeFileName = getIncludePath(packageName, name);
            cppType = new NativeUserArrayableType(packageName, name, includeFileName, true,
                    new NativeArrayTraits("BitmaskArrayTraits", true));
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
            try
            {
                final CppNativeType nativeTargetType =
                        CppNativeMapper.this.getCppType(type.getTypeReference());
                final PackageName packageName = type.getPackage().getPackageName();
                final String name = type.getName();
                final String includeFileName = getIncludePath(packageName, name);
                if (nativeTargetType instanceof NativeArrayableType)
                {
                    cppType = new NativeUserArrayableType(packageName, name, includeFileName,
                            nativeTargetType.isSimpleType(),
                            ((NativeArrayableType)nativeTargetType).getArrayTraits());
                }
                else
                {
                    cppType = new NativeUserType(packageName, name, includeFileName,
                            nativeTargetType.isSimpleType());
                }
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
            cppType = CppNativeMapper.mapBitFieldType(type.isSigned(), type.getBitSize());
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            cppType = CppNativeMapper.mapBitFieldType(type.isSigned(), type.getMaxBitSize());
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

    private final static String INCLUDE_DIR_SEPARATOR = "/";
    private final static String HEADER_SUFFIX = ".h";

    private final NativeAnyHolderType anyHolderType;
    private final NativeUniquePtrType uniquePtrType;
    private final NativeHeapOptionalHolderType heapOptionalHolderType;
    private final NativeInplaceOptionalHolderType inplaceOptionalHolderType;

    private final NativeStringType stringType;
    private final NativeStringViewType stringViewType;
    private final NativeVectorType vectorType;
    private final NativeMapType mapType;
    private final NativeSetType setType;
    private final NativeBitBufferType bitBufferType;
    private final NativeBlobBufferType blobBufferType;
    private final NativePackingContextNodeType packingContextNodeType;

    private final static NativeBuiltinType booleanType =
            new NativeBuiltinType("bool", new NativeArrayTraits("BoolArrayTraits"));
    private final static NativeBuiltinType float16Type =
            new NativeBuiltinType("float", new NativeArrayTraits("Float16ArrayTraits"));
    private final static NativeBuiltinType float32Type =
            new NativeBuiltinType("float", new NativeArrayTraits("Float32ArrayTraits"));
    private final static NativeBuiltinType float64Type =
            new NativeBuiltinType("double", new NativeArrayTraits("Float64ArrayTraits"));

    private final static NativeIntegralType stdInt8Type =
            new NativeIntegralType(8, true, new NativeArrayTraits("StdIntArrayTraits", true));
    private final static NativeIntegralType stdInt16Type =
            new NativeIntegralType(16, true, new NativeArrayTraits("StdIntArrayTraits", true));
    private final static NativeIntegralType stdInt32Type =
            new NativeIntegralType(32, true, new NativeArrayTraits("StdIntArrayTraits", true));
    private final static NativeIntegralType stdInt64Type =
            new NativeIntegralType(64, true, new NativeArrayTraits("StdIntArrayTraits", true));

    private final static NativeIntegralType stdUInt8Type =
            new NativeIntegralType(8, false, new NativeArrayTraits("StdIntArrayTraits", true));
    private final static NativeIntegralType stdUInt16Type =
            new NativeIntegralType(16, false, new NativeArrayTraits("StdIntArrayTraits", true));
    private final static NativeIntegralType stdUInt32Type =
            new NativeIntegralType(32, false, new NativeArrayTraits("StdIntArrayTraits", true));
    private final static NativeIntegralType stdUInt64Type =
            new NativeIntegralType(64, false, new NativeArrayTraits("StdIntArrayTraits", true));

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

    private final static NativeIntegralType varUInt16Type =
            new NativeIntegralType(16, false, new NativeArrayTraits("VarIntNNArrayTraits", true));
    private final static NativeIntegralType varUInt32Type =
            new NativeIntegralType(32, false, new NativeArrayTraits("VarIntNNArrayTraits", true));
    private final static NativeIntegralType varUInt64Type =
            new NativeIntegralType(64, false, new NativeArrayTraits("VarIntNNArrayTraits", true));
    private final static NativeIntegralType varUIntType =
            new NativeIntegralType(64, false, new NativeArrayTraits("VarIntArrayTraits", true));

    private final static NativeIntegralType varInt16Type =
            new NativeIntegralType(16, true, new NativeArrayTraits("VarIntNNArrayTraits", true));
    private final static NativeIntegralType varInt32Type =
            new NativeIntegralType(32, true, new NativeArrayTraits("VarIntNNArrayTraits", true));
    private final static NativeIntegralType varInt64Type =
            new NativeIntegralType(64, true, new NativeArrayTraits("VarIntNNArrayTraits", true));
    private final static NativeIntegralType varIntType =
            new NativeIntegralType(64, true, new NativeArrayTraits("VarIntArrayTraits", true));

    private final static NativeIntegralType varSizeType =
            new NativeIntegralType(32, false, new NativeArrayTraits("VarSizeArrayTraits"));
}
