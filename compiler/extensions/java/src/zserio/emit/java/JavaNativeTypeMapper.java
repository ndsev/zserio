package zserio.emit.java;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.PackageName;
import zserio.ast.ServiceType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.IntegerType;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.emit.common.NativeType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeArrayType;
import zserio.emit.java.types.NativeBigIntegerArrayType;
import zserio.emit.java.types.NativeBooleanType;
import zserio.emit.java.types.NativeByteArrayType;
import zserio.emit.java.types.NativeByteType;
import zserio.emit.java.types.NativeCompoundType;
import zserio.emit.java.types.NativeConstType;
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

final class JavaNativeTypeMapper
{
    /**
     * Constructor.
     *
     * @param javaPackageMapper The Java package mapper to construct from.
     */
    public JavaNativeTypeMapper(PackageMapper javaPackageMapper)
    {
        this.javaPackageMapper = javaPackageMapper;
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
            throw new ZserioEmitException("Unhandled type '" + type.getClass().getName() +
                    "' in JavaNativeTypeMapper!");

        return nativeType;
    }

    /**
     * Return a Java type for given Zserio type that is suitable to be stored as an Object.
     *
     * In other words it never returns a primitive data type (e.g. byte), but
     * the wrapper class (e.g. Byte).
     *
     * @param javaPackageMapper Package mapper to use for Java package mapping.
     * @param type              Zserio type.
     *
     * @return JavaNativeType that is derived from Object and can hold values of the given Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any Java type.
     */
    public JavaNativeType getNullableJavaType(ZserioType type) throws ZserioEmitException
    {
        final ZserioTypeMapperVisitor visitor = visitType(javaPackageMapper, type);

        final JavaNativeType nativeNullableType = visitor.getJavaNullableType();
        if (nativeNullableType == null)
            throw new ZserioEmitException("Unhandled type '" + type.getClass().getName() +
                    "' in JavaNativeTypeMapper!");

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
     * @param type              Zserio IntegerType to map.
     *
     * @return NativeIntegralType that can hold values of the given Zserio type.
     *
     * @throws ZserioEmitException If the Zserio type cannot be mapped to any Java type.
     */
    public NativeIntegralType getJavaIntegralType(IntegerType type) throws ZserioEmitException
    {
        final NativeType javaType = getJavaType(type);

        if (!(javaType instanceof NativeIntegralType))
            throw new ZserioEmitException("Unhandled integral type '" + type.getClass().getName() +
                    "' in JavaNativeTypeMapper!");

        return (NativeIntegralType)javaType;
    }

    private ZserioTypeMapperVisitor visitType(PackageMapper javaPackageMapper, ZserioType type)
            throws ZserioEmitException
    {
        type = TypeReference.resolveBaseType(type);
        final ZserioTypeMapperVisitor visitor = new ZserioTypeMapperVisitor(javaPackageMapper);
        type.accept(visitor);

        final ZserioEmitException thrownException = visitor.getThrownException();
        if (thrownException != null)
            throw thrownException;

        return visitor;
    }

    private abstract class TypeMapperVisitor extends ZserioAstDefaultVisitor
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
        public void visitTypeInstantiation(TypeInstantiation type)
        {
            final CompoundType baseType = type.getBaseType();
            mapObjectArray(baseType);
        }

        @Override
        public void visitUnionType(UnionType type)
        {
            mapObjectArray(type);
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
                if (nBits <= Byte.SIZE)
                {
                    javaNullableType = byteArrayType;
                }
                else if (nBits <= Short.SIZE)
                {
                    javaNullableType = shortArrayType;
                }
                else if (nBits <= Integer.SIZE)
                {
                    javaNullableType = intArrayType;
                }
                else // this could be > 64 (int8 foo; int<foo> a;) but if we're above 64, we explode at runtime
                {
                    javaNullableType = longArrayType;
                }
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
                // always keep the MSB clear
                if (nBits <= Byte.SIZE)
                {
                    javaNullableType = unsignedByteArrayType;
                }
                else if (nBits <= Short.SIZE)
                {
                    javaNullableType = unsignedShortArrayType;
                }
                else if (nBits <= Integer.SIZE)
                {
                    javaNullableType = unsignedIntArrayType;
                }
                else if (nBits < Long.SIZE)
                {
                    javaNullableType = unsignedLongArrayType;
                }
                else // this could be >= 64 (int8 foo; bit<foo> a;) but if we're above 64, we explode at runtime
                {
                    javaNullableType = bigIntegerArrayType;
                }
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
        public void visitArrayType(ArrayType type)
        {
            final ZserioType elementType = TypeReference.resolveBaseType(type.getElementType());
            final ArrayElementTypeMapperVisitor arrayVisitor = new ArrayElementTypeMapperVisitor();

            TypeReference.resolveBaseType(elementType).accept(arrayVisitor);
            javaType = arrayVisitor.getJavaNullableType();
            javaNullableType = javaType;
            thrownException = arrayVisitor.getThrownException();
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
        public void visitConstType(ConstType type)
        {
            try
            {
                final JavaNativeType nativeTargetType =
                        JavaNativeTypeMapper.this.getJavaType(type.getConstType());
                final PackageName packageName = javaPackageMapper.getPackageName(type);
                final String name = type.getName();
                javaType = new NativeConstType(packageName, name, nativeTargetType);
                javaNullableType = javaType;
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
                final NativeIntegralType nativeBaseType = getJavaIntegralType(type.getIntegerBaseType());
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
        public void visitTypeInstantiation(TypeInstantiation type)
        {
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
                mapVariableInteger(nBits, true);
            }
            else
            {
                if (nBits <= Byte.SIZE)
                {
                    javaType = byteType;
                    javaNullableType = byteNullableType;
                }
                else if (nBits <= Short.SIZE)
                {
                    javaType = shortType;
                    javaNullableType = shortNullableType;
                }
                else if (nBits <= Integer.SIZE)
                {
                    javaType = intType;
                    javaNullableType = intNullableType;
                }
                else // this could be > 64 (int8 foo; bit<foo> a;) but if we're above 64, we explode at runtime
                {
                    javaType = longType;
                    javaNullableType = longNullableType;
                }
            }
        }

        @Override
        protected void mapUnsignedIntegralType(int nBits, boolean variable)
        {
            if (variable)
            {
                mapVariableInteger(nBits, false);
            }
            else
            {
                // always keep the MSB clear
                if (nBits < Byte.SIZE)
                {
                    javaType = byteType;
                    javaNullableType = byteNullableType;
                }
                else if (nBits < Short.SIZE)
                {
                    javaType = shortType;
                    javaNullableType = shortNullableType;
                }
                else if (nBits < Integer.SIZE)
                {
                    javaType = intType;
                    javaNullableType = intNullableType;
                }
                else if (nBits < Long.SIZE)
                {
                    javaType = longType;
                    javaNullableType = longNullableType;
                }
                else // this could be >= 64 (int8 foo; bit<foo> a;) but if we're above 64, we explode at runtime
                {
                    javaType = unsignedLongType;
                    javaNullableType = unsignedLongType;
                }
            }
        }

        private void mapVariableInteger(int nBits, boolean signed)
        {
            /*
             * In Java, varintN and varuintN always fit in the same native type.
             * (varuintN always uses less than N bits, so it fits w/o setting the MSB, and neither of varuintN
             * and varintN fits in the next smaller native type.)
             */
            switch (nBits)
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
                if (signed)
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
