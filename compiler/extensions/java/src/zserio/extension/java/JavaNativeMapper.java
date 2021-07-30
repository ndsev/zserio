package zserio.extension.java;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
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
import zserio.extension.java.types.NativeIntArrayTraits;
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
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);

        // always resolve subtypes
        return getJavaType(typeInstantiation.getBaseType());
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
        // always resolve subtypes
        return getJavaType(typeReference.getBaseTypeReference().getType());
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
        final TypeMapperVisitor visitor = visitType(type);

        final JavaNativeType nativeType = visitor.getJavaType();
        if (nativeType == null)
        {
            throw new ZserioExtensionException("Unhandled type '" + type.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return nativeType;
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
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);

        final ZserioType type = typeInstantiation.getBaseType();
        final TypeMapperVisitor visitor = visitType(type);

        final JavaNativeType nativeNullableType = visitor.getJavaNullableType();
        if (nativeNullableType == null)
            throw new ZserioExtensionException("Unhandled type '" + type.getClass().getName() +
                    "' in JavaNativeMapper!");

        return nativeNullableType;
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
        final JavaNativeType nativeType = getJavaType(typeInstantiation);

        if (!(nativeType instanceof NativeIntegralType))
        {
            throw new ZserioExtensionException("Unhandled integral type '" +
                    typeInstantiation.getClass().getName() + "' in JavaNativeMapper!");
        }

        return (NativeIntegralType)nativeType;
    }

    private TypeMapperVisitor visitType(ZserioType type) throws ZserioExtensionException
    {
        final TypeMapperVisitor visitor = new TypeMapperVisitor();
        type.accept(visitor);

        final ZserioExtensionException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        return visitor;
    }

    private JavaNativeType mapArray(ArrayInstantiation instantiation) throws ZserioExtensionException
    {
        final TypeInstantiation elementInstantiation = instantiation.getElementTypeInstantiation();
        final ZserioType elementBaseType = elementInstantiation.getBaseType();

        final JavaNativeType nativeType = getJavaType(elementBaseType);
        if (!(nativeType instanceof NativeArrayableType))
        {
            throw new ZserioExtensionException("Unhandled arrayable type '" +
                    elementInstantiation.getClass().getName() + "' in JavaNativeMapper!");
        }

        return new NativeArrayType((NativeArrayableType)nativeType);
    }

    private class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
        public JavaNativeType getJavaType()
        {
            return javaType;
        }

        public JavaNativeType getJavaNullableType()
        {
            return javaNullableType;
        }

        public ZserioExtensionException getThrownException()
        {
            return thrownException;
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
        public void visitEnumType(EnumType type)
        {
            try
            {
                final NativeIntegralType nativeBaseType = getJavaIntegralType(type.getTypeInstantiation());
                final PackageName packageName = type.getPackage().getPackageName();
                final String name = type.getName();
                javaType = new NativeEnumType(packageName, name, nativeBaseType, withWriterCode);
                javaNullableType = javaType;
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
                javaType = new NativeBitmaskType(packageName, name, nativeBaseType, withWriterCode);
                javaNullableType = javaType;
            }
            catch (ZserioExtensionException exception)
            {
                thrownException = exception;
            }
        }

        @Override
        public void visitSqlTableType(SqlTableType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitServiceType(ServiceType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            javaType = new NativeServiceType(packageName, name);
            javaNullableType = javaType;
        }

        @Override
        public void visitPubsubType(PubsubType type)
        {
            final PackageName packageName = type.getPackage().getPackageName();
            final String name = type.getName();
            javaType = new NativePubsubType(packageName, name);
            javaNullableType = javaType;
        }

        @Override
        public void visitStdIntegerType(StdIntegerType type)
        {
            mapFixedInteger(type.getBitSize(), type.isSigned());
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            mapVariableInteger(type.getMaxBitSize(), type.isSigned());
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType type)
        {
            mapFixedInteger(type.getBitSize(), type.isSigned());
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType type)
        {
            mapFixedInteger(type.getMaxBitSize(), type.isSigned());
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            javaType = booleanType;
            javaNullableType = booleanNullableType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            javaType = stringType;
            javaNullableType = stringType;
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
            case 32:
                javaType = floatType;
                javaNullableType = floatNullableType;
                break;

            case 64:
                javaType = doubleType;
                javaNullableType = doubleNullableType;
                break;

            default:
                break;
            }
        }

        @Override
        public void visitExternType(ExternType externType)
        {
            javaType = bitBufferType;
            javaNullableType = bitBufferType;
        }

        private void mapFixedInteger(int numBits, boolean isSigned)
        {
            if (isSigned)
            {
                if (numBits <= Byte.SIZE)
                {
                    // !@# TODO[mikir] make it static or NativeNullableByteType
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
        }

        private void mapVariableInteger(int numBits, boolean isSigned)
        {
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
        }

        private void mapCompoundType(CompoundType compoundType)
        {
            final PackageName packageName = compoundType.getPackage().getPackageName();
            final String name = compoundType.getName();
            javaType = new NativeCompoundType(packageName, name, withWriterCode);
            javaNullableType = javaType;
        }

        private JavaNativeType javaType = null;
        private JavaNativeType javaNullableType = null;
        private ZserioExtensionException thrownException = null;
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeBooleanType booleanNullableType = new NativeBooleanType(true);

    private final static NativeStringType stringType = new NativeStringType();

    private final static NativeFloatType floatType = new NativeFloatType(false);
    private final static NativeFloatType floatNullableType = new NativeFloatType(true);
    private final static NativeDoubleType doubleType = new NativeDoubleType(false);
    private final static NativeDoubleType doubleNullableType = new NativeDoubleType(true);

    private final static NativeBitBufferType bitBufferType = new NativeBitBufferType();

    // integral array traits
    private final static NativeIntArrayTraits signedBitFieldByteArrayTraits =
            new NativeIntArrayTraits("SignedBitFieldByteArray");
    private final static NativeIntArrayTraits signedBitFieldShortArrayTraits =
            new NativeIntArrayTraits("SignedBitFieldShortArray");
    private final static NativeIntArrayTraits signedBitFieldIntArrayTraits =
            new NativeIntArrayTraits("SignedBitFieldIntArray");
    private final static NativeIntArrayTraits signedBitFieldLongArrayTraits =
            new NativeIntArrayTraits("SignedBitFieldLongArray");

    private final static NativeIntArrayTraits bitFieldByteArrayTraits =
            new NativeIntArrayTraits("BitFieldByteArray");
    private final static NativeIntArrayTraits bitFieldShortArrayTraits =
            new NativeIntArrayTraits("BitFieldShortArray");
    private final static NativeIntArrayTraits bitFieldIntArrayTraits =
            new NativeIntArrayTraits("BitFieldIntArray");
    private final static NativeIntArrayTraits bitFieldLongArrayTraits =
            new NativeIntArrayTraits("BitFieldLongArray");
    private final static NativeArrayTraits bitFieldBigIntegerArrayTraits =
            new NativeIntArrayTraits("BitFieldBigIntegerArray");

    private final static NativeIntArrayTraits varInt16ArrayTraits = new NativeIntArrayTraits("VarInt16Array");
    private final static NativeIntArrayTraits varInt32ArrayTraits = new NativeIntArrayTraits("VarInt32Array");
    private final static NativeIntArrayTraits varInt64ArrayTraits = new NativeIntArrayTraits("VarInt64Array");
    private final static NativeIntArrayTraits varIntArrayTraits = new NativeIntArrayTraits("VarIntArray");

    private final static NativeIntArrayTraits varUInt16ArrayTraits = new NativeIntArrayTraits("VarUInt16Array");
    private final static NativeIntArrayTraits varUInt32ArrayTraits = new NativeIntArrayTraits("VarUInt32Array");
    private final static NativeIntArrayTraits varUInt64ArrayTraits = new NativeIntArrayTraits("VarUInt64Array");
    private final static NativeArrayTraits varUIntArrayTraits = new NativeArrayTraits("VarUIntArray");

    private final static NativeIntArrayTraits varSizeArrayTraits = new NativeIntArrayTraits("VarSizeArray");

    private final boolean withWriterCode;
}
