package zserio.emit.java;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.ExternType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.PackageName;
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
import zserio.emit.common.NativeType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.symbols.JavaNativeSymbol;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeArrayType;
import zserio.emit.java.types.NativeBigIntegerArrayType;
import zserio.emit.java.types.NativeBitBufferType;
import zserio.emit.java.types.NativeBooleanType;
import zserio.emit.java.types.NativeByteArrayType;
import zserio.emit.java.types.NativeByteType;
import zserio.emit.java.types.NativeCompoundType;
import zserio.emit.java.types.NativeDoubleType;
import zserio.emit.java.types.NativeEnumType;
import zserio.emit.java.types.NativeFloatType;
import zserio.emit.java.types.NativeIntArrayType;
import zserio.emit.java.types.NativeIntType;
import zserio.emit.java.types.NativeIntegralType;
import zserio.emit.java.types.NativeLongArrayType;
import zserio.emit.java.types.NativeLongType;
import zserio.emit.java.types.NativeObjectArrayType;
import zserio.emit.java.types.NativeServiceType;
import zserio.emit.java.types.NativeShortArrayType;
import zserio.emit.java.types.NativeShortType;
import zserio.emit.java.types.NativeStringType;
import zserio.emit.java.types.NativeUnsignedByteArrayType;
import zserio.emit.java.types.NativeUnsignedIntArrayType;
import zserio.emit.java.types.NativeUnsignedLongArrayType;
import zserio.emit.java.types.NativeUnsignedLongType;
import zserio.emit.java.types.NativeUnsignedShortArrayType;

final class JavaNativeMapper
{
    /**
     * Constructor.
     *
     * @param javaPackageMapper The Java package mapper to construct from.
     */
    public JavaNativeMapper(PackageMapper javaPackageMapper)
    {
        this.javaPackageMapper = javaPackageMapper;
    }

    /**
     * Returns a Java symbol that can hold an instance of Zserio symbol.
     *
     * @param symbol Zserio symbol.
     *
     * @return Java symbol.
     *
     * @throws ZserioEmitException If the Zserio symbol cannot be mapped to any Java symbol.
     */
    public JavaNativeSymbol getJavaSymbol(AstNode symbol) throws ZserioEmitException
    {
        if (symbol instanceof Constant)
        {
            final Constant constant = (Constant)symbol;
            final PackageName packageName = javaPackageMapper.getPackageName(constant.getPackage());
            final String name = constant.getName();
            return new JavaNativeSymbol(packageName, name);
        }
        else
            throw new ZserioEmitException("Unhandled symbol '" + symbol.getClass().getName() +
                    "' in JavaNativeMapper!");
    }

    /**
     * Returns a Java type that can hold an instance of the Zserio type.
     *
     * @param typeInstantiation Instantiation of Zserio type.
     *
     * @return  Java type which can hold the Zserio type.
     *
     * @throws ZserioEmitException If the the Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getJavaType(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return mapDynamicBitField((DynamicBitFieldInstantiation)typeInstantiation);

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
     * @throws ZserioEmitException If the referenced Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getJavaType(TypeReference typeReference) throws ZserioEmitException
    {
        // always resolve subtypes
        return getJavaType(typeReference.getBaseTypeReference().getType());
    }

    /**
     * Returns a Java type that can hold an instance of given Zserio type.
     *
     * This can be a primitive type (e.g. byte) if it suffices.
     *
     * @param javaPackageMapper Package mapper to use for Java package mapping.
     * @param type              Zserio type.
     *
     * @return JavaNativeType that can hold values of the given Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getJavaType(ZserioType type) throws ZserioEmitException
    {
        final ZserioTypeMapperVisitor visitor = visitType(javaPackageMapper, type);

        final JavaNativeType nativeType = visitor.getJavaType();
        if (nativeType == null)
        {
            throw new ZserioEmitException("Unhandled type '" + type.getClass().getName() +
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
     * @param javaPackageMapper Package mapper to use for Java package mapping.
     * @param typeInstantiation Instantiation of Zserio type.
     *
     * @return JavaNativeType that is derived from Object and can hold values of the Zserio type.
     *
     * @throws ZserioEmitException If the zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getNullableJavaType(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        if (typeInstantiation instanceof ArrayInstantiation)
            return mapArray((ArrayInstantiation)typeInstantiation);
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return mapDynamicBitFieldToNullableType((DynamicBitFieldInstantiation)typeInstantiation);

        final ZserioType type = typeInstantiation.getBaseType();
        final ZserioTypeMapperVisitor visitor = visitType(javaPackageMapper, type);

        final JavaNativeType nativeNullableType = visitor.getJavaNullableType();
        if (nativeNullableType == null)
            throw new ZserioEmitException("Unhandled type '" + type.getClass().getName() +
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
     * @param javaPackageMapper Package mapper to use for Java package mapping.
     * @param typeInstantiation Instantiation of Zserio Integer Type to map.
     *
     * @return NativeIntegralType that can hold values of the given Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any Java type.
     */
    public NativeIntegralType getJavaIntegralType(TypeInstantiation typeInstantiation)
            throws ZserioEmitException
    {
        final NativeType javaType = getJavaType(typeInstantiation);

        if (!(javaType instanceof NativeIntegralType))
        {
            throw new ZserioEmitException("Unhandled integral type '" + typeInstantiation.getClass().getName() +
                    "' in JavaNativeMapper!");
        }

        return (NativeIntegralType)javaType;
    }

    private ZserioTypeMapperVisitor visitType(PackageMapper javaPackageMapper, ZserioType type)
            throws ZserioEmitException
    {
        final ZserioTypeMapperVisitor visitor = new ZserioTypeMapperVisitor(javaPackageMapper);
        type.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        return visitor;
    }

    private JavaNativeType mapArray(ArrayInstantiation instantiation) throws ZserioEmitException
    {
        final TypeInstantiation elementInstantiation = instantiation.getElementTypeInstantiation();
        if (elementInstantiation instanceof DynamicBitFieldInstantiation)
            return mapDynamicBitFieldArray((DynamicBitFieldInstantiation)elementInstantiation);

        final ZserioType elementBaseType = elementInstantiation.getBaseType();
        final ArrayElementTypeMapperVisitor arrayVisitor = new ArrayElementTypeMapperVisitor();

        elementBaseType.accept(arrayVisitor);

        final ZserioEmitException thrownException = arrayVisitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        final JavaNativeType nativeType = arrayVisitor.getJavaNullableType();
        if (nativeType == null)
        {
            throw new ZserioEmitException("Unhandled type '" +
                    elementInstantiation.getBaseType().getClass().getName() + "' in JavaNativeMapper!");
        }

        return nativeType;
    }

    private static JavaNativeType mapDynamicBitField(DynamicBitFieldInstantiation instantiation)
    {
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int numBits = instantiation.getMaxBitSize();
        return isSigned ? mapSignedIntegralType(numBits) : mapUnsignedIntegralType(numBits);
    }

    private static JavaNativeType mapDynamicBitFieldToNullableType(DynamicBitFieldInstantiation instantiation)
    {
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int numBits = instantiation.getMaxBitSize();
        return isSigned ? mapSignedIntegralNullableType(numBits) : mapUnsignedIntegralNullableType(numBits);
    }

    private static JavaNativeType mapDynamicBitFieldArray(DynamicBitFieldInstantiation instantiation)
    {
        final boolean isSigned = instantiation.getBaseType().isSigned();
        final int numBits = instantiation.getMaxBitSize();
        return isSigned ? mapSignedIntegralArray(numBits) : mapUnsignedIntegralArray(numBits);
    }

    private static JavaNativeType mapSignedIntegralType(int numBits)
    {
        if (numBits <= Byte.SIZE)
            return byteType;
        else if (numBits <= Short.SIZE)
            return shortType;
        else if (numBits <= Integer.SIZE)
            return intType;
        else
            return longType;
    }

    private static JavaNativeType mapSignedIntegralNullableType(int numBits)
    {
        if (numBits <= Byte.SIZE)
            return byteNullableType;
        else if (numBits <= Short.SIZE)
            return shortNullableType;
        else if (numBits <= Integer.SIZE)
            return intNullableType;
        else
            return longNullableType;
    }

    private static JavaNativeType mapUnsignedIntegralType(int numBits)
    {
        if (numBits < Byte.SIZE)
            return byteType;
        else if (numBits < Short.SIZE)
            return shortType;
        else if (numBits < Integer.SIZE)
            return intType;
        else if (numBits < Long.SIZE)
            return longType;
        else
            return unsignedLongType;
    }

    private static JavaNativeType mapUnsignedIntegralNullableType(int numBits)
    {
        if (numBits < Byte.SIZE)
            return byteNullableType;
        else if (numBits < Short.SIZE)
            return shortNullableType;
        else if (numBits < Integer.SIZE)
            return intNullableType;
        else if (numBits < Long.SIZE)
            return longNullableType;
        else
            return unsignedLongType;
    }

    private static JavaNativeType mapSignedIntegralArray(int numBits)
    {
        if (numBits <= Byte.SIZE)
            return byteArrayType;
        else if (numBits <= Short.SIZE)
            return shortArrayType;
        else if (numBits <= Integer.SIZE)
            return intArrayType;
        else // this could be > 64 (int8 foo; int<foo> a;) but if we're above 64, we explode at runtime
            return longArrayType;
    }

    private static JavaNativeType mapUnsignedIntegralArray(int numBits)
    {
        // always keep the MSB clear
        if (numBits <= Byte.SIZE)
            return unsignedByteArrayType;
        else if (numBits <= Short.SIZE)
            return unsignedShortArrayType;
        else if (numBits <= Integer.SIZE)
            return unsignedIntArrayType;
        else if (numBits < Long.SIZE)
            return unsignedLongArrayType;
        else
            return bigIntegerArrayType;
    }

    private abstract class TypeMapperVisitor extends ZserioAstDefaultVisitor
    {
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
        public void visitStdIntegerType(StdIntegerType type)
        {
            mapIntegralType(type.getBitSize(), type.isSigned(), false);
        }

        @Override
        public void visitVarIntegerType(VarIntegerType type)
        {
            mapIntegralType(type.getMaxBitSize(), type.isSigned(), true);
        }

        protected void mapIntegralType(int numBits, boolean isSigned, boolean isVarInteger)
        {
            if (isSigned)
                mapSignedIntegralType(numBits, isVarInteger);
            else
                mapUnsignedIntegralType(numBits, isVarInteger);
        }

        protected abstract void mapSignedIntegralType(int numBits, boolean isVarInteger);
        protected abstract void mapUnsignedIntegralType(int numBits, boolean isVarInteger);
    }

    private class ArrayElementTypeMapperVisitor extends TypeMapperVisitor
    {
        public JavaNativeType getJavaNullableType()
        {
            return javaNullableType;
        }

        public ZserioEmitException getThrownException()
        {
            return thrownException;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            javaNullableType = boolArrayType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            mapObjectArray(type);
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            mapObjectArray(type);
        }

        @Override
        public void visitFloatType(FloatType type)
        {
            switch (type.getBitSize())
            {
            case 16:
                javaNullableType = float16ArrayType;
                break;

            case 32:
                javaNullableType = float32ArrayType;
                break;

            case 64:
                javaNullableType = float64ArrayType;
                break;

            default:
                break;
            }
        }

        @Override
        public void visitExternType(ExternType externType)
        {
            javaNullableType = bitBufferArrayType;
        }

        @Override
        public void visitStringType(StringType type)
        {
            javaNullableType = stdStringArrayType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapObjectArray(type);
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapObjectArray(type);
        }

        @Override
        protected void mapSignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                switch (numBits)
                {
                case 16:
                    javaNullableType = varInt16ArrayType;
                    break;

                case 32:
                    javaNullableType = varInt32ArrayType;
                    break;

                case 64:
                    javaNullableType = varInt64ArrayType;
                    break;

                case 72:
                    javaNullableType = varIntArrayType;
                    break;

                default:
                    break;
                }
            }
            else
            {
                javaNullableType = JavaNativeMapper.mapSignedIntegralArray(numBits);
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
                    javaNullableType = varUInt16ArrayType;
                    break;

                case 32:
                    javaNullableType = varUInt32ArrayType;
                    break;

                case 64:
                    javaNullableType = varUInt64ArrayType;
                    break;

                case 72:
                    javaNullableType = varUIntArrayType;
                    break;

                default:
                    break;
                }
            }
            else
            {
                javaNullableType = JavaNativeMapper.mapUnsignedIntegralArray(numBits);
            }
        }

        private void mapObjectArray(ZserioType type)
        {
            try
            {
                final JavaNativeType nativeElementType = getJavaType(type);
                javaNullableType = new NativeObjectArrayType(nativeElementType);
            }
            catch (ZserioEmitException exception)
            {
                thrownException = exception;
            }
        }

        private JavaNativeType javaNullableType = null;
        private ZserioEmitException thrownException = null;
    }

    private class ZserioTypeMapperVisitor extends TypeMapperVisitor
    {
        public ZserioTypeMapperVisitor(PackageMapper javaPackageMapper)
        {
            this.javaPackageMapper = javaPackageMapper;
        }

        public JavaNativeType getJavaType()
        {
            return javaType;
        }

        public JavaNativeType getJavaNullableType()
        {
            return javaNullableType;
        }

        public ZserioEmitException getThrownException()
        {
            return thrownException;
        }

        @Override
        public void visitBooleanType(BooleanType type)
        {
            javaType = booleanType;
            javaNullableType = booleanNullableType;
        }

        @Override
        public void visitChoiceType(ChoiceType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitEnumType(EnumType type)
        {
            try
            {
                final NativeIntegralType nativeBaseType = getJavaIntegralType(type.getTypeInstantiation());
                final PackageName packageName = javaPackageMapper.getPackageName(type);
                final String name = type.getName();
                javaType = new NativeEnumType(packageName, name, nativeBaseType);
                javaNullableType = javaType;
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

        @Override
        public void visitServiceType(ServiceType type)
        {
            final PackageName packageName = javaPackageMapper.getPackageName(type);
            final String name = type.getName();
            javaType = new NativeServiceType(packageName, name);
            javaNullableType = javaType;
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
            javaType = stringType;
            javaNullableType = stringType;
        }

        @Override
        public void visitStructureType(StructureType type)
        {
            mapCompoundType(type);
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapCompoundType(type);
        }

        @Override
        protected void mapSignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                mapVariableInteger(numBits, true);
            }
            else
            {
                javaType = JavaNativeMapper.mapSignedIntegralType(numBits);
                javaNullableType = JavaNativeMapper.mapSignedIntegralNullableType(numBits);
            }
        }

        @Override
        protected void mapUnsignedIntegralType(int numBits, boolean isVarInteger)
        {
            if (isVarInteger)
            {
                mapVariableInteger(numBits, false);
            }
            else
            {
                javaType = JavaNativeMapper.mapUnsignedIntegralType(numBits);
                javaNullableType = JavaNativeMapper.mapUnsignedIntegralNullableType(numBits);
            }
        }

        private void mapVariableInteger(int numBits, boolean isSigned)
        {
            /*
             * In Java, varintN and varuintN always fit in the same native type.
             * (varuintN always uses less than N bits, so it fits w/o setting the MSB, and neither of varuintN
             * and varintN fits in the next smaller native type.)
             */
            switch (numBits)
            {
            case 16:
                javaType = shortType;
                javaNullableType = shortNullableType;
                break;

            case 32:
                javaType = intType;
                javaNullableType = intNullableType;
                break;

            case 64:
                javaType = longType;
                javaNullableType = longNullableType;
                break;

            case 72:
                if (isSigned)
                {
                    javaType = longType;
                    javaNullableType = longNullableType;
                }
                else
                {
                    javaType = unsignedLongType;
                    javaNullableType = unsignedLongType;
                }
                break;

            default:
                break;
            }
        }

        private void mapCompoundType(CompoundType compoundType)
        {
            final PackageName packageName = javaPackageMapper.getPackageName(compoundType);
            final String name = compoundType.getName();
            javaType = new NativeCompoundType(packageName, name);
            javaNullableType = javaType;
        }

        private final PackageMapper javaPackageMapper;

        private JavaNativeType javaType = null;
        private JavaNativeType javaNullableType = null;
        private ZserioEmitException thrownException = null;
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeBooleanType booleanNullableType = new NativeBooleanType(true);

    private final static NativeStringType stringType = new NativeStringType();

    private final static NativeFloatType floatType = new NativeFloatType(false);
    private final static NativeFloatType floatNullableType = new NativeFloatType(true);
    private final static NativeDoubleType doubleType = new NativeDoubleType(false);
    private final static NativeDoubleType doubleNullableType = new NativeDoubleType(true);

    private final static NativeBitBufferType bitBufferType = new NativeBitBufferType();

    // integral types
    private final static NativeByteType byteType = new NativeByteType(false);
    private final static NativeByteType byteNullableType = new NativeByteType(true);
    private final static NativeShortType shortType = new NativeShortType(false);
    private final static NativeShortType shortNullableType = new NativeShortType(true);
    private final static NativeIntType intType = new NativeIntType(false);
    private final static NativeIntType intNullableType = new NativeIntType(true);
    private final static NativeLongType longType = new NativeLongType(false);
    private final static NativeLongType longNullableType = new NativeLongType(true);
    private final static NativeUnsignedLongType unsignedLongType = new NativeUnsignedLongType();

    // zserio.runtime arrays
    private final static NativeArrayType boolArrayType = new NativeArrayType("BoolArray");

    private final static NativeArrayType stdStringArrayType = new NativeArrayType("StringArray");

    private final static NativeArrayType float16ArrayType = new NativeArrayType("Float16Array");
    private final static NativeArrayType float32ArrayType = new NativeArrayType("Float32Array");
    private final static NativeArrayType float64ArrayType = new NativeArrayType("Float64Array");

    private final static NativeArrayType bitBufferArrayType = new NativeArrayType("BitBufferArray");

    private final static NativeByteArrayType byteArrayType = new NativeByteArrayType();
    private final static NativeUnsignedByteArrayType unsignedByteArrayType = new NativeUnsignedByteArrayType();

    private final static NativeShortArrayType shortArrayType = new NativeShortArrayType();
    private final static NativeUnsignedShortArrayType unsignedShortArrayType =
            new NativeUnsignedShortArrayType();

    private final static NativeIntArrayType intArrayType = new NativeIntArrayType();
    private final static NativeUnsignedIntArrayType unsignedIntArrayType = new NativeUnsignedIntArrayType();

    private final static NativeLongArrayType longArrayType = new NativeLongArrayType();
    private final static NativeUnsignedLongArrayType unsignedLongArrayType = new NativeUnsignedLongArrayType();

    private final static NativeBigIntegerArrayType bigIntegerArrayType = new NativeBigIntegerArrayType();

    private final static NativeArrayType varInt16ArrayType = new NativeArrayType("VarInt16Array");
    private final static NativeArrayType varUInt16ArrayType = new NativeArrayType("VarUInt16Array");

    private final static NativeArrayType varInt32ArrayType = new NativeArrayType("VarInt32Array");
    private final static NativeArrayType varUInt32ArrayType = new NativeArrayType("VarUInt32Array");

    private final static NativeArrayType varInt64ArrayType = new NativeArrayType("VarInt64Array");
    private final static NativeArrayType varUInt64ArrayType = new NativeArrayType("VarUInt64Array");

    private final static NativeArrayType varIntArrayType = new NativeArrayType("VarIntArray");
    private final static NativeArrayType varUIntArrayType = new NativeArrayType("VarUIntArray");

    private final PackageMapper javaPackageMapper;
}
