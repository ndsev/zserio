package zserio.extension.java;

import zserio.ast.ArrayInstantiation;
import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.BytesType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.PackageName;
import zserio.ast.PackageSymbol;
import zserio.ast.PubsubType;
import zserio.ast.ServiceType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.symbols.JavaNativeSymbol;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeArrayTraits;
import zserio.extension.java.types.NativeArrayType;
import zserio.extension.java.types.NativeArrayableType;
import zserio.extension.java.types.NativeBigIntegerType;
import zserio.extension.java.types.NativeBitBufferType;
import zserio.extension.java.types.NativeBitmaskType;
import zserio.extension.java.types.NativeBooleanType;
import zserio.extension.java.types.NativeByteType;
import zserio.extension.java.types.NativeBytesType;
import zserio.extension.java.types.NativeCompoundType;
import zserio.extension.java.types.NativeDoubleType;
import zserio.extension.java.types.NativeEnumType;
import zserio.extension.java.types.NativeFloatType;
import zserio.extension.java.types.NativeBitFieldArrayTraits;
import zserio.extension.java.types.NativeIntType;
import zserio.extension.java.types.NativeIntegralType;
import zserio.extension.java.types.NativeLongType;
import zserio.extension.java.types.NativePubsubType;
import zserio.extension.java.types.NativeServiceType;
import zserio.extension.java.types.NativeShortType;
import zserio.extension.java.types.NativeSqlDatabaseType;
import zserio.extension.java.types.NativeSqlTableType;
import zserio.extension.java.types.NativeStringType;

/**
 * Java native mapper.
 *
 * Provides mapping of types and symbols from Zserio package symbols to Java native types and symbols.
 */
final class JavaNativeMapper
{
    public JavaNativeMapper(boolean withWriterCode, PackedTypesCollector packedTypesCollector)
    {
        this.withWriterCode = withWriterCode;
        this.packedTypesCollector = packedTypesCollector;
    }

    public JavaNativeSymbol getJavaSymbol(PackageSymbol packageSymbol) throws ZserioExtensionException
    {
        if (packageSymbol instanceof Constant)
        {
            return getJavaSymbol((Constant)packageSymbol);
        }
        else if (packageSymbol instanceof ZserioType)
        {
            return getJavaType((ZserioType)packageSymbol);
        }
        else
        {
            throw new ZserioExtensionException("Unhandled package symbol '" +
                    packageSymbol.getClass().getName() + "' in JavaNativeMapper!");
        }
    }

    public JavaNativeSymbol getJavaSymbol(Constant constant) throws ZserioExtensionException
    {
        final PackageName packageName = constant.getPackage().getPackageName();
        final String name = constant.getName();
        return new JavaNativeSymbol(packageName, name);
    }

    public JavaNativeType getJavaType(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeInstantiation);
        final JavaNativeType nativeType = javaTypes != null ? javaTypes.getType() : null;
        if (nativeType == null)
        {
            final ZserioType referencedType = typeInstantiation.getTypeReference().getType();
            throw new ZserioExtensionException("Unhandled type '" + referencedType.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeType;
    }

    public JavaNativeType getJavaType(TypeReference typeReference) throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeReference);
        final JavaNativeType nativeType = javaTypes != null ? javaTypes.getType() : null;
        if (nativeType == null)
        {
            final ZserioType referencedType = typeReference.getType();
            throw new ZserioExtensionException("Unhandled type '" + referencedType.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeType;
    }

    public JavaNativeType getJavaType(ZserioType type) throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(type);
        final JavaNativeType nativeType = javaTypes != null ? javaTypes.getType() : null;
        if (nativeType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" + type.getClass().getName() +
                    "' in JavaNativeMapper!" + type.getLocation());
        }

        return nativeType;
    }

    public NativeIntegralType getJavaIntegralType(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeInstantiation);
        final JavaNativeType nativeType = javaTypes != null ? javaTypes.getType() : null;
        if (!(nativeType instanceof NativeIntegralType))
        {
            throw new ZserioExtensionException("Unhandled integral type '" +
                    typeInstantiation.getClass().getName() + "' in JavaNativeMapper!");
        }

        return (NativeIntegralType)nativeType;
    }

    public JavaNativeType getNullableJavaType(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeInstantiation);
        final JavaNativeType nativeNullableType = javaTypes != null ? javaTypes.getNullableType() : null;
        if (nativeNullableType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" + typeInstantiation.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeNullableType;
    }

    public JavaNativeTypes getJavaTypes(TypeReference typeReference) throws ZserioExtensionException
    {
        // always resolve subtypes
        return getJavaTypes(typeReference.getBaseTypeReference().getType());
    }

    public JavaNativeTypes getJavaTypes(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return mapDynamicBitField((DynamicBitFieldInstantiation)typeInstantiation);

        // always resolve subtypes
        return getJavaTypes(typeInstantiation.getBaseType());
    }

    private JavaNativeTypes getJavaTypes(ZserioType type) throws ZserioExtensionException
    {
        final TypeMapperVisitor visitor = new TypeMapperVisitor();
        type.accept(visitor);

        final ZserioExtensionException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        return visitor.getJavaTypes();
    }

    private static final class JavaNativeTypes
    {
        public JavaNativeTypes(JavaNativeType type, JavaNativeType nullableType)
        {
            this.type = type;
            this.nullableType = nullableType;
        }

        public JavaNativeTypes(JavaNativeType type)
        {
            this(type, type);
        }

        public JavaNativeType getType()
        {
            return type;
        }

        public JavaNativeType getNullableType()
        {
            return nullableType;
        }

        private final JavaNativeType type;
        private final JavaNativeType nullableType;
    }

    private JavaNativeTypes mapArray(ArrayInstantiation instantiation) throws ZserioExtensionException
    {
        final TypeInstantiation elementInstantiation = instantiation.getElementTypeInstantiation();
        final JavaNativeTypes nativeElementTypes = getJavaTypes(elementInstantiation);
        final JavaNativeType nativeElementType = nativeElementTypes.getType();
        if (!(nativeElementType instanceof NativeArrayableType))
        {
            throw new ZserioExtensionException("Unhandled arrayable type '" +
                    elementInstantiation.getClass().getName() + "' in JavaNativeMapper!");
        }

        final JavaNativeType javaType = new NativeArrayType((NativeArrayableType)nativeElementType);

        return new JavaNativeTypes(javaType);
    }

    private JavaNativeTypes mapDynamicBitField(DynamicBitFieldInstantiation instantiation)
            throws ZserioExtensionException
    {
        return mapFixedInteger(instantiation.getMaxBitSize(), instantiation.getBaseType().isSigned());
    }

    private JavaNativeTypes mapFixedInteger(int numBits, boolean isSigned)
    {
        JavaNativeType javaType = null;
        JavaNativeType javaNullableType = null;
        if (isSigned)
        {
            if (numBits <= Byte.SIZE)
            {
                javaType = signedBitFieldByteType;
                javaNullableType = signedBitFieldByteNullableType;
            }
            else if (numBits <= Short.SIZE)
            {
                javaType = signedBitFieldShortType;
                javaNullableType = signedBitFieldShortNullableType;
            }
            else if (numBits <= Integer.SIZE)
            {
                javaType = signedBitFieldIntType;
                javaNullableType = signedBitFieldIntNullableType;
            }
            else if (numBits <= Long.SIZE)
            {
                javaType = signedBitFieldLongType;
                javaNullableType = signedBitFieldLongNullableType;
            }
        }
        else
        {
            if (numBits < Byte.SIZE)
            {
                javaType = bitFieldByteType;
                javaNullableType = bitFieldByteNullableType;
            }
            else if (numBits < Short.SIZE)
            {
                javaType = bitFieldShortType;
                javaNullableType = bitFieldShortNullableType;
            }
            else if (numBits < Integer.SIZE)
            {
                javaType = bitFieldIntType;
                javaNullableType = bitFieldIntNullableType;
            }
            else if (numBits < Long.SIZE)
            {
                javaType = bitFieldLongType;
                javaNullableType = bitFieldLongNullableType;
            }
            else if (numBits == Long.SIZE)
            {
                javaType = bitFieldBigIntegerType;
                javaNullableType = javaType;
            }
        }

        return new JavaNativeTypes(javaType, javaNullableType);
    }

    private final class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        public JavaNativeTypes getJavaTypes()
        {
            return javaTypes;
        }

        public ZserioExtensionException getThrownException()
        {
            return thrownException;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
           javaTypes = mapCompoundType(type);
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            javaTypes = mapCompoundType(type);
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            javaTypes = mapCompoundType(type);
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            try
            {
                final NativeIntegralType nativeBaseType = getJavaIntegralType(type.getTypeInstantiation());
                final PackageName packageName = type.getPackage().getPackageName();
                final String name = type.getName();
                final boolean usedInPackedArray = packedTypesCollector.isUsedInPackedArray(type);
                final JavaNativeType javaType = new NativeEnumType(
                        packageName, name, nativeBaseType, withWriterCode, usedInPackedArray);
                javaTypes = new JavaNativeTypes(javaType);
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
                final NativeIntegralType nativeBaseType = getJavaIntegralType(type.getTypeInstantiation());
                final PackageName packageName = type.getPackage().getPackageName();
                final String name = type.getName();
                final boolean usedInPackedArray = packedTypesCollector.isUsedInPackedArray(type);
                final JavaNativeType javaType = new NativeBitmaskType(
                        packageName, name, nativeBaseType, withWriterCode, usedInPackedArray);
                javaTypes = new JavaNativeTypes(javaType);
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final JavaNativeType javaType = new NativeSqlTableType(packageName, name);
            javaTypes = new JavaNativeTypes(javaType);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final JavaNativeType javaType = new NativeSqlDatabaseType(packageName, name);
            javaTypes = new JavaNativeTypes(javaType);
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final JavaNativeType javaType = new NativeServiceType(packageName, name);
            javaTypes = new JavaNativeTypes(javaType);
        }

        @Override
        public void visitPubsubType(PubsubType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            final JavaNativeType javaType = new NativePubsubType(packageName, name);
            javaTypes = new JavaNativeTypes(javaType);
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            javaTypes = mapFixedInteger(type.getBitSize(), type.isSigned());
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            javaTypes = mapVariableInteger(type.getMaxBitSize(), type.isSigned());
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            javaTypes = mapFixedInteger(type.getBitSize(), type.isSigned());
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            // this is only for reference to dynamic bit field type (e.g. when used as compound parameter)
            javaTypes = mapFixedInteger(DynamicBitFieldType.MAX_BIT_SIZE, type.isSigned());
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            javaTypes = new JavaNativeTypes(booleanType, booleanNullableType);
        }

        @Override
        public void visitBytesType(BytesType type)
        {
            javaTypes = new JavaNativeTypes(bytesType);
        }

        @Override
        public void visitStringType(StringType type)
        {
            javaTypes = new JavaNativeTypes(stringType);
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
                javaTypes = new JavaNativeTypes(float16Type, float16NullableType);
                break;

            case 32:
                javaTypes = new JavaNativeTypes(float32Type, float32NullableType);
                break;

            case 64:
                javaTypes = new JavaNativeTypes(doubleType, doubleNullableType);
                break;

            default:
                break;
            }
        }

        @Override
        public void visitExternType(ExternType externType)
        {
            javaTypes = new JavaNativeTypes(bitBufferType);
        }

        private JavaNativeTypes mapVariableInteger(int numBits, boolean isSigned)
        {
            JavaNativeType javaType = null;
            JavaNativeType javaNullableType = null;
            if (isSigned)
            {
                switch (numBits)
                {
                case 16:
                    javaType = varInt16Type;
                    javaNullableType = varInt16NullableType;
                    break;

                case 32:
                    javaType = varInt32Type;
                    javaNullableType = varInt32NullableType;
                    break;

                case 64:
                    javaType = varInt64Type;
                    javaNullableType = varInt64NullableType;
                    break;

                case 72:
                    javaType = varIntType;
                    javaNullableType = varIntNullableType;
                    break;

                default:
                    break;
                }
            }
            else
            {
                switch (numBits)
                {
                case 16:
                    javaType = varUInt16Type;
                    javaNullableType = varUInt16NullableType;
                    break;

                case 32:
                    javaType = varUInt32Type;
                    javaNullableType = varUInt32NullableType;
                    break;

                case 40:
                    javaType = varSizeType;
                    javaNullableType = varSizeNullableType;
                    break;

                case 64:
                    javaType = varUInt64Type;
                    javaNullableType = varUInt64NullableType;
                    break;

                case 72:
                    javaType = varUIntType;
                    javaNullableType = javaType;
                    break;

                default:
                    break;
                }
            }

            return new JavaNativeTypes(javaType, javaNullableType);
        }

        private JavaNativeTypes mapCompoundType(CompoundType compoundType)
        {
            final PackageName packageName = compoundType.getPackage().getPackageName();
            final String name = compoundType.getName();
            final JavaNativeType javaType = new NativeCompoundType(packageName, name, withWriterCode,
                    compoundType.isPackable() && packedTypesCollector.isUsedInPackedArray(compoundType));

            return new JavaNativeTypes(javaType);
        }

        private JavaNativeTypes javaTypes = null;
        private ZserioExtensionException thrownException = null;
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeBooleanType booleanNullableType = new NativeBooleanType(true);

    private final static NativeBytesType bytesType = new NativeBytesType();
    private final static NativeStringType stringType = new NativeStringType();

    private final static NativeArrayTraits float16ArrayTraits = new NativeArrayTraits("Float16ArrayTraits");
    private final static NativeFloatType float16Type = new NativeFloatType(false, float16ArrayTraits);
    private final static NativeFloatType float16NullableType = new NativeFloatType(true, float16ArrayTraits);

    private final static NativeArrayTraits float32ArrayTraits = new NativeArrayTraits("Float32ArrayTraits");
    private final static NativeFloatType float32Type = new NativeFloatType(false, float32ArrayTraits);
    private final static NativeFloatType float32NullableType = new NativeFloatType(true, float32ArrayTraits);

    private final static NativeDoubleType doubleType = new NativeDoubleType(false);
    private final static NativeDoubleType doubleNullableType = new NativeDoubleType(true);

    private final static NativeBitBufferType bitBufferType = new NativeBitBufferType();

    // integral arrays
    private final static NativeBitFieldArrayTraits signedBitFieldByteArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldByteArrayTraits");
    private final static NativeByteType signedBitFieldByteType =
            new NativeByteType(false, signedBitFieldByteArrayTraits);
    private final static NativeByteType signedBitFieldByteNullableType =
            new NativeByteType(true, signedBitFieldByteArrayTraits);

    private final static NativeBitFieldArrayTraits signedBitFieldShortArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldShortArrayTraits");
    private final static NativeShortType signedBitFieldShortType =
            new NativeShortType(false, signedBitFieldShortArrayTraits);
    private final static NativeShortType signedBitFieldShortNullableType =
            new NativeShortType(true, signedBitFieldShortArrayTraits);

    private final static NativeBitFieldArrayTraits signedBitFieldIntArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldIntArrayTraits");
    private final static NativeIntType signedBitFieldIntType =
            new NativeIntType(false, signedBitFieldIntArrayTraits);
    private final static NativeIntType signedBitFieldIntNullableType =
            new NativeIntType(true, signedBitFieldIntArrayTraits);

    private final static NativeBitFieldArrayTraits signedBitFieldLongArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldLongArrayTraits");
    private final static NativeLongType signedBitFieldLongType =
            new NativeLongType(false, signedBitFieldLongArrayTraits);
    private final static NativeLongType signedBitFieldLongNullableType =
            new NativeLongType(true, signedBitFieldLongArrayTraits);

    private final static NativeBitFieldArrayTraits bitFieldByteArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldByteArrayTraits");
    private final static NativeByteType bitFieldByteType =
            new NativeByteType(false, bitFieldByteArrayTraits);
    private final static NativeByteType bitFieldByteNullableType =
            new NativeByteType(true, bitFieldByteArrayTraits);

    private final static NativeBitFieldArrayTraits bitFieldShortArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldShortArrayTraits");
    private final static NativeShortType bitFieldShortType =
            new NativeShortType(false, bitFieldShortArrayTraits);
    private final static NativeShortType bitFieldShortNullableType =
            new NativeShortType(true, bitFieldShortArrayTraits);

    private final static NativeBitFieldArrayTraits bitFieldIntArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldIntArrayTraits");
    private final static NativeIntType bitFieldIntType =
            new NativeIntType(false, bitFieldIntArrayTraits);
    private final static NativeIntType bitFieldIntNullableType =
            new NativeIntType(true, bitFieldIntArrayTraits);

    private final static NativeBitFieldArrayTraits bitFieldLongArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldLongArrayTraits");
    private final static NativeLongType bitFieldLongType =
            new NativeLongType(false, bitFieldLongArrayTraits);
    private final static NativeLongType bitFieldLongNullableType =
            new NativeLongType(true, bitFieldLongArrayTraits);

    private final static NativeArrayTraits bitFieldBigIntegerArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldBigIntegerArrayTraits");
    private final static NativeBigIntegerType bitFieldBigIntegerType =
            new NativeBigIntegerType(bitFieldBigIntegerArrayTraits);

    private final static NativeArrayTraits varInt16ArrayTraits =
            new NativeArrayTraits("VarInt16ArrayTraits");
    private final static NativeShortType varInt16Type = new NativeShortType(false, varInt16ArrayTraits);
    private final static NativeShortType varInt16NullableType = new NativeShortType(true, varInt16ArrayTraits);

    private final static NativeArrayTraits varInt32ArrayTraits =
            new NativeArrayTraits("VarInt32ArrayTraits");
    private final static NativeIntType varInt32Type = new NativeIntType(false, varInt32ArrayTraits);
    private final static NativeIntType varInt32NullableType = new NativeIntType(true, varInt32ArrayTraits);

    private final static NativeArrayTraits varInt64ArrayTraits =
            new NativeArrayTraits("VarInt64ArrayTraits");
    private final static NativeLongType varInt64Type = new NativeLongType(false, varInt64ArrayTraits);
    private final static NativeLongType varInt64NullableType = new NativeLongType(true, varInt64ArrayTraits);

    private final static NativeArrayTraits varIntArrayTraits =
            new NativeArrayTraits("VarIntArrayTraits");
    private final static NativeLongType varIntType = new NativeLongType(false, varIntArrayTraits);
    private final static NativeLongType varIntNullableType = new NativeLongType(true, varIntArrayTraits);

    private final static NativeArrayTraits varUInt16ArrayTraits =
            new NativeArrayTraits("VarUInt16ArrayTraits");
    private final static NativeShortType varUInt16Type = new NativeShortType(false, varUInt16ArrayTraits);
    private final static NativeShortType varUInt16NullableType =
            new NativeShortType(true, varUInt16ArrayTraits);

    private final static NativeArrayTraits varUInt32ArrayTraits =
            new NativeArrayTraits("VarUInt32ArrayTraits");
    private final static NativeIntType varUInt32Type = new NativeIntType(false, varUInt32ArrayTraits);
    private final static NativeIntType varUInt32NullableType = new NativeIntType(true, varUInt32ArrayTraits);

    private final static NativeArrayTraits varUInt64ArrayTraits =
            new NativeArrayTraits("VarUInt64ArrayTraits");
    private final static NativeLongType varUInt64Type = new NativeLongType(false, varUInt64ArrayTraits);
    private final static NativeLongType varUInt64NullableType = new NativeLongType(true, varUInt64ArrayTraits);

    private final static NativeArrayTraits varUIntArrayTraits =
            new NativeArrayTraits("VarUIntArrayTraits");
    private final static NativeBigIntegerType varUIntType = new NativeBigIntegerType(varUIntArrayTraits);

    private final static NativeArrayTraits varSizeArrayTraits =
            new NativeArrayTraits("VarSizeArrayTraits");
    private final static NativeIntType varSizeType = new NativeIntType(false, varSizeArrayTraits);
    private final static NativeIntType varSizeNullableType = new NativeIntType(true, varSizeArrayTraits);

    private final PackedTypesCollector packedTypesCollector;
    private final boolean withWriterCode;
}
