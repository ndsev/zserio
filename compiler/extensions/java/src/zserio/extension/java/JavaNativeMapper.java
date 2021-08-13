package zserio.extension.java;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.PackageName;
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
import zserio.extension.java.types.NativeStringType;

final class JavaNativeMapper
{
    public JavaNativeMapper(boolean withWriterCode)
    {
        this.withWriterCode = withWriterCode;
    }

    /**
     * Returns a Java symbol that can hold an instance of Zserio symbol.
     *
     * @param symbol Zserio symbol.
     *
     * @return Java symbol.
     *
     * @throws ZserioExtensionException If the Zserio symbol cannot be mapped to any Java symbol.
     */
    public JavaNativeSymbol getJavaSymbol(AstNode symbol) throws ZserioExtensionException
    {
        if (symbol instanceof Constant)
        {
            final Constant constant = (Constant)symbol;
            final PackageName packageName = constant.getPackage().getPackageName();
            final String name = constant.getName();
            return new JavaNativeSymbol(packageName, name);
        }
        else
            throw new ZserioExtensionException("Unhandled symbol '" + symbol.getClass().getName() +
                    "' in JavaNativeMapper!");
    }

    /**
     * Returns a Java type that can hold an instance of the Zserio type.
     *
     * @param typeInstantiation Instantiation of Zserio type.
     *
     * @return  Java type which can hold the Zserio type.
     *
     * @throws ZserioExtensionException If the the Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getJavaType(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeInstantiation);
        final JavaNativeType nativeType = javaTypes.getType();
        if (nativeType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" + typeInstantiation.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeType;
    }

    /**
     * Returns a Java type that can hold an instance of referenced Zserio type.
     *
     * @param typeReference Reference to the Zserio type.
     *
     * @return  Java type which can hold referenced Zserio type.
     *
     * @throws ZserioExtensionException If the referenced Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getJavaType(TypeReference typeReference) throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeReference);
        final JavaNativeType nativeType = javaTypes.getType();
        if (nativeType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" + typeReference.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeType;
    }

    /**
     * Returns a Java type that can hold an instance of given Zserio type.
     *
     * This can be a primitive type (e.g. byte) if it suffices.
     *
     * @param type Zserio type.
     *
     * @return JavaNativeType that can hold values of the given Zserio type.
     *
     * @throws ZserioExtensionException If the Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getJavaType(ZserioType type) throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(type);
        final JavaNativeType nativeType = javaTypes.getType();
        if (nativeType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" + type.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeType;
    }

    /**
     * Return a Java integral type for given Zserio integral type.
     *
     * This is working exactly like getJavaType() does but it guarantees an (Zserio) IntegerType
     * is mapped to NativeIntegralType, not only to the base JavaNativeType. Use this when the native
     * integral interface is required.
     *
     * @param typeInstantiation Instantiation of Zserio Integer Type to map.
     *
     * @return NativeIntegralType that can hold values of the given Zserio type.
     *
     * @throws ZserioExtensionException If the Zserio type cannot be mapped to any Java type.
     */
    public NativeIntegralType getJavaIntegralType(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeInstantiation);
        final JavaNativeType nativeType = javaTypes.getType();
        if (!(nativeType instanceof NativeIntegralType))
        {
            throw new ZserioExtensionException("Unhandled integral type '" +
                    typeInstantiation.getClass().getName() + "' in JavaNativeMapper!");
        }

        return (NativeIntegralType)nativeType;
    }

    /**
     * Return a Java type for given Zserio type that is suitable to be stored as an Object.
     *
     * In other words it never returns a primitive data type (e.g. byte), but
     * the wrapper class (e.g. Byte).
     *
     * @param typeInstantiation Instantiation of Zserio type.
     *
     * @return JavaNativeType that is derived from Object and can hold values of the Zserio type.
     *
     * @throws ZserioExtensionException If the zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getNullableJavaType(TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        final JavaNativeTypes javaTypes = getJavaTypes(typeInstantiation);
        final JavaNativeType nativeNullableType = javaTypes.getNullableType();
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

    private static class JavaNativeTypes
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
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int numBits = instantiation.getMaxBitSize();

        return mapFixedInteger(numBits, isSigned);
    }

    private JavaNativeTypes mapFixedInteger(int numBits, boolean isSigned)
    {
        JavaNativeType javaType = null;
        JavaNativeType javaNullableType = null;
        if (isSigned)
        {
            if (numBits <= Byte.SIZE)
            {
                javaType = new NativeByteType(false, signedBitFieldByteArrayTraits);
                javaNullableType = new NativeByteType(true, signedBitFieldByteArrayTraits);
            }
            else if (numBits <= Short.SIZE)
            {
                javaType = new NativeShortType(false, signedBitFieldShortArrayTraits);
                javaNullableType = new NativeShortType(true, signedBitFieldShortArrayTraits);
            }
            else if (numBits <= Integer.SIZE)
            {
                javaType = new NativeIntType(false, signedBitFieldIntArrayTraits);
                javaNullableType = new NativeIntType(true, signedBitFieldIntArrayTraits);
            }
            else if (numBits <= Long.SIZE)
            {
                javaType = new NativeLongType(false, signedBitFieldLongArrayTraits);
                javaNullableType = new NativeLongType(true, signedBitFieldLongArrayTraits);
            }
        }
        else
        {
            if (numBits < Byte.SIZE)
            {
                javaType = new NativeByteType(false, bitFieldByteArrayTraits);
                javaNullableType = new NativeByteType(true, bitFieldByteArrayTraits);
            }
            else if (numBits < Short.SIZE)
            {
                javaType = new NativeShortType(false, bitFieldShortArrayTraits);
                javaNullableType = new NativeShortType(true, bitFieldShortArrayTraits);
            }
            else if (numBits < Integer.SIZE)
            {
                javaType = new NativeIntType(false, bitFieldIntArrayTraits);
                javaNullableType = new NativeIntType(true, bitFieldIntArrayTraits);
            }
            else if (numBits < Long.SIZE)
            {
                javaType = new NativeLongType(false, bitFieldLongArrayTraits);
                javaNullableType = new NativeLongType(true, bitFieldLongArrayTraits);
            }
            else if (numBits == Long.SIZE)
            {
                javaType = new NativeBigIntegerType(bitFieldBigIntegerArrayTraits);
                javaNullableType = javaType;
            }
        }

        return new JavaNativeTypes(javaType, javaNullableType);
    }

    private class TypeMapperVisitor extends ZserioAstDefaultVisitor
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
                final JavaNativeType javaType =
                        new NativeEnumType(packageName, name, nativeBaseType, withWriterCode);
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
                final JavaNativeType javaType =
                        new NativeBitmaskType(packageName, name, nativeBaseType, withWriterCode);
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
            javaTypes = mapCompoundType(type);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            javaTypes = mapCompoundType(type);
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
            javaTypes = mapFixedInteger(type.getMaxBitSize(), type.isSigned());
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            javaTypes = new JavaNativeTypes(booleanType, booleanNullableType);
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
                    javaType = new NativeShortType(false, varInt16ArrayTraits);
                    javaNullableType = new NativeShortType(true, varInt16ArrayTraits);
                    break;

                case 32:
                    javaType = new NativeIntType(false, varInt32ArrayTraits);
                    javaNullableType = new NativeIntType(true, varInt32ArrayTraits);
                    break;

                case 64:
                    javaType = new NativeLongType(false, varInt64ArrayTraits);
                    javaNullableType = new NativeLongType(true, varInt64ArrayTraits);
                    break;

                case 72:
                    javaType = new NativeLongType(false, varIntArrayTraits);
                    javaNullableType = new NativeLongType(true, varIntArrayTraits);
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
                    javaType = new NativeShortType(false, varUInt16ArrayTraits);
                    javaNullableType = new NativeShortType(true, varUInt16ArrayTraits);
                    break;

                case 32:
                    javaType = new NativeIntType(false, varUInt32ArrayTraits);
                    javaNullableType = new NativeIntType(true, varUInt32ArrayTraits);
                    break;

                case 40:
                    javaType = new NativeIntType(false, varSizeArrayTraits);
                    javaNullableType = new NativeIntType(true, varSizeArrayTraits);
                    break;

                case 64:
                    javaType = new NativeLongType(false, varUInt64ArrayTraits);
                    javaNullableType = new NativeLongType(true, varUInt64ArrayTraits);
                    break;

                case 72:
                    javaType = new NativeBigIntegerType(varUIntArrayTraits);
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
            final JavaNativeType javaType = new NativeCompoundType(packageName, name, withWriterCode);

            return new JavaNativeTypes(javaType);
        }

        private JavaNativeTypes javaTypes = null;
        private ZserioExtensionException thrownException = null;
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeBooleanType booleanNullableType = new NativeBooleanType(true);

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

    // integral array traits
    private final static NativeBitFieldArrayTraits signedBitFieldByteArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldByteArrayTraits");
    private final static NativeBitFieldArrayTraits signedBitFieldShortArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldShortArrayTraits");
    private final static NativeBitFieldArrayTraits signedBitFieldIntArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldIntArrayTraits");
    private final static NativeBitFieldArrayTraits signedBitFieldLongArrayTraits =
            new NativeBitFieldArrayTraits("SignedBitFieldLongArrayTraits");

    private final static NativeBitFieldArrayTraits bitFieldByteArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldByteArrayTraits");
    private final static NativeBitFieldArrayTraits bitFieldShortArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldShortArrayTraits");
    private final static NativeBitFieldArrayTraits bitFieldIntArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldIntArrayTraits");
    private final static NativeBitFieldArrayTraits bitFieldLongArrayTraits =
            new NativeBitFieldArrayTraits("BitFieldLongArrayTraits");
    private final static NativeArrayTraits bitFieldBigIntegerArrayTraits =
            new NativeArrayTraits("BitFieldBigIntegerArrayTraits");

    private final static NativeArrayTraits varInt16ArrayTraits = new NativeArrayTraits("VarInt16ArrayTraits");
    private final static NativeArrayTraits varInt32ArrayTraits = new NativeArrayTraits("VarInt32ArrayTraits");
    private final static NativeArrayTraits varInt64ArrayTraits = new NativeArrayTraits("VarInt64ArrayTraits");
    private final static NativeArrayTraits varIntArrayTraits = new NativeArrayTraits("VarIntArrayTraits");

    private final static NativeArrayTraits varUInt16ArrayTraits = new NativeArrayTraits("VarUInt16ArrayTraits");
    private final static NativeArrayTraits varUInt32ArrayTraits = new NativeArrayTraits("VarUInt32ArrayTraits");
    private final static NativeArrayTraits varUInt64ArrayTraits = new NativeArrayTraits("VarUInt64ArrayTraits");
    private final static NativeArrayTraits varUIntArrayTraits = new NativeArrayTraits("VarUIntArrayTraits");

    private final static NativeArrayTraits varSizeArrayTraits = new NativeArrayTraits("VarSizeArrayTraits");

    private final boolean withWriterCode;
}
