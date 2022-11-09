package zserio.runtime.creator;

import java.math.BigInteger;

import zserio.runtime.HashCodeUtil;

/**
 * Corresponds to the following schema:
 *
 * enum int8 DummyEnum
 * {
 *     ONE,
 *     TWO,
 *     MinusOne = -1
 * };
 *
 * bitmask uint8 DummyBitmask
 * {
 *     READ = 1,
 *     WRITE = 2
 * };
 *
 * struct DummyNested(uint32 param)
 * {
 *     uint32 value;
 *     string text;
 *     extern data;
 *     DummyEnum dummyEnum;
 *     DummyBitmask dummyBitmask;
 * };
 *
 * struct DummyObject
 * {
 *     uint32 value;
 *     DummyNested(value) nested;
 *     string text;
 *     DummyNested(value) nestedArray[];
 *     string textArray[];
 *     extern externArray[];
 *     optional bool optionalBool;
 *     optional DummyNested(value) optionalNested;
 * };
 */
public class ZserioTreeCreatorTestObject
{
    public static enum DummyEnum implements zserio.runtime.io.Writer,
            zserio.runtime.SizeOf, zserio.runtime.ZserioEnum
    {
        ONE((byte)0),
        TWO((byte)1),
        MinusOne((byte)-1);

        private DummyEnum(byte value)
        {
            this.value = value;
        }

        public byte getValue()
        {
            return value;
        }

        @Override
        public java.lang.Number getGenericValue()
        {
            return value;
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            return new zserio.runtime.typeinfo.TypeInfo.EnumTypeInfo(
                    "DummyEnum",
                    DummyEnum.class,
                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getInt8(),
                    new java.util.ArrayList<java.util.function.Supplier<java.lang.Object>>(),
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.ItemInfo("ONE", BigInteger.valueOf(0)),
                            new zserio.runtime.typeinfo.ItemInfo("TWO", BigInteger.valueOf(1)),
                            new zserio.runtime.typeinfo.ItemInfo("MinusOne", BigInteger.valueOf(-1))
                    )
                );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createContext();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getContext().init(
                    new zserio.runtime.array.ArrayTraits.BitFieldByteArrayTraits(8),
                    new zserio.runtime.array.ArrayElement.ByteArrayElement(value));
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            return 8;
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            return contextNode.getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldByteArrayTraits(8),
                    new zserio.runtime.array.ArrayElement.ByteArrayElement(value));
        }

        @Override
        public long initializeOffsets()
        {
            return initializeOffsets(0);
        }

        @Override
        public long initializeOffsets(long bitPosition) throws zserio.runtime.ZserioError
        {
            return bitPosition + bitSizeOf(bitPosition);
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            return bitPosition + bitSizeOf(contextNode, bitPosition);
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            out.writeByte(getValue());
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getContext().write(
                    new zserio.runtime.array.ArrayTraits.BitFieldShortArrayTraits(8), out,
                    new zserio.runtime.array.ArrayElement.ByteArrayElement(value));
        }

        public static DummyEnum readEnum(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
        {
            return toEnum(in.readByte());
        }

        public static DummyEnum readEnum(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamReader in) throws java.io.IOException
        {
            return toEnum(((zserio.runtime.array.ArrayElement.ByteArrayElement)
                    contextNode.getContext().read(
                            new zserio.runtime.array.ArrayTraits.BitFieldByteArrayTraits(8), in)).get());
        }

        public static DummyEnum toEnum(byte value)
        {
            switch (value)
            {
                case (byte)0:
                    return ONE;
                case (byte)1:
                    return TWO;
                case (byte)-1:
                    return MinusOne;
                default:
                    throw new java.lang.IllegalArgumentException(
                            "Unknown value for enumeration DummyEnum: " + value + "!");
            }
        }

        private byte value;
    }

    public static class DummyBitmask implements zserio.runtime.io.Writer,
            zserio.runtime.SizeOf, zserio.runtime.ZserioBitmask
    {
        public DummyBitmask()
        {
            this((short)0);
        }

        public DummyBitmask(short value)
        {
            if (value < (short)0 || value > (short)255)
            {
                throw new java.lang.IllegalArgumentException(
                        "Value for bitmask 'DummyBitmask' out of bounds: " + value + "!");
            }
            this.value = value;
        }

        public DummyBitmask(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
        {
            value = in.readUnsignedByte();
        }

        public DummyBitmask(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            value = ((zserio.runtime.array.ArrayElement.ShortArrayElement)
                    contextNode.getContext().read(
                            new zserio.runtime.array.ArrayTraits.BitFieldShortArrayTraits(8), in)).get();
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            return new zserio.runtime.typeinfo.TypeInfo.BitmaskTypeInfo(
                    "DummyBitmask",
                    DummyBitmask.class,
                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt8(),
                    new java.util.ArrayList<java.util.function.Supplier<java.lang.Object>>(),
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.ItemInfo("READ", BigInteger.valueOf(1)),
                            new zserio.runtime.typeinfo.ItemInfo("WRITE", BigInteger.valueOf(2))
                    )
            );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createContext();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getContext().init(
                    new zserio.runtime.array.ArrayTraits.BitFieldShortArrayTraits(8),
                    new zserio.runtime.array.ArrayElement.ShortArrayElement(value));
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            return 8;
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            return contextNode.getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldShortArrayTraits(8),
                    new zserio.runtime.array.ArrayElement.ShortArrayElement(value));
        }

        @Override
        public long initializeOffsets()
        {
            return initializeOffsets(0);
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            return bitPosition + bitSizeOf(bitPosition);
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            return bitPosition + bitSizeOf(contextNode, bitPosition);
        }

        @Override
        public boolean equals(java.lang.Object other)
        {
            if (!(other instanceof DummyBitmask))
                return false;

            final DummyBitmask otherDummyBitmask = (DummyBitmask)other;
            return value == otherDummyBitmask.value;
        }

        @Override
        public int hashCode()
        {
            int result = HashCodeUtil.HASH_SEED;

            result = HashCodeUtil.calcHashCode(result, value);

            return result;
        }

        @Override
        public java.lang.String toString()
        {
            final java.lang.StringBuilder builder = new java.lang.StringBuilder();

            if (this.and(DummyBitmask.Values.READ).equals(DummyBitmask.Values.READ))
                builder.append(builder.length() == 0 ? "READ" : " | READ");
            if (this.and(DummyBitmask.Values.WRITE).equals(DummyBitmask.Values.WRITE))
                builder.append(builder.length() == 0 ? "WRITE" : " | WRITE");

            return java.lang.String.valueOf(value) + "[" + builder.toString() + "]";
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            out.writeUnsignedByte(value);
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getContext().write(
                    new zserio.runtime.array.ArrayTraits.BitFieldShortArrayTraits(8), out,
                    new zserio.runtime.array.ArrayElement.ShortArrayElement(value));
        }

        public short getValue()
        {
            return value;
        }

        @Override
        public java.lang.Number getGenericValue()
        {
            return value;
        }

        public DummyBitmask or(DummyBitmask other)
        {
            return new DummyBitmask((short)(value | other.value));
        }

        public DummyBitmask and(DummyBitmask other)
        {
            return new DummyBitmask((short)(value & other.value));
        }

        public DummyBitmask xor(DummyBitmask other)
        {
            return new DummyBitmask((short)(value ^ other.value));
        }

        public DummyBitmask not()
        {
            return new DummyBitmask((short)(~value & (short)255));
        }

        public static final class Values
        {
            public static final DummyBitmask READ = new DummyBitmask((short)1);
            public static final DummyBitmask WRITE = new DummyBitmask((short)2);
        }

        private short value;
    }

    public static class DummyNested implements zserio.runtime.io.Writer, zserio.runtime.SizeOf
    {
        public DummyNested(
                long param_)
        {
            this.param_ = param_;
        }

        public DummyNested(java.io.File file,
                long param_)
                throws java.io.IOException
        {
            this.param_ = param_;

            try (final zserio.runtime.io.FileBitStreamReader in = new zserio.runtime.io.FileBitStreamReader(file))
            {
                read(in);
            }
        }

        public DummyNested(zserio.runtime.io.BitStreamReader in,
                long param_)
                throws java.io.IOException
        {
            this.param_ = param_;

            read(in);
        }

        public DummyNested(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in,
                long param_)
                throws java.io.IOException
        {
            this.param_ = param_;

            read(contextNode, in);
        }

        public DummyNested(
                long param_,
                long value_,
                java.lang.String text_,
                zserio.runtime.io.BitBuffer data_,
                DummyEnum dummyEnum_,
                DummyBitmask dummyBitmask_)
        {
            this(param_);

            setValue(value_);
            setText(text_);
            setData(data_);
            setDummyEnum(dummyEnum_);
            setDummyBitmask(dummyBitmask_);
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            final java.lang.String templateName = "";
            final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                    new java.util.ArrayList<zserio.runtime.typeinfo.TypeInfo>();
            final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "value", // schemaName
                                    "getValue", // getterName
                                    "setValue", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt32(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "text", // schemaName
                                    "getText", // getterName
                                    "setText", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getString(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "data", // schemaName
                                    "getData", // getterName
                                    "setData", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getBitBuffer(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "dummyEnum", // schemaName
                                    "getDummyEnum", // getterName
                                    "setDummyEnum", // setterName
                                    DummyEnum.typeInfo(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "dummyBitmask", // schemaName
                                    "getDummyBitmask", // getterName
                                    "setDummyBitmask", // setterName
                                    DummyBitmask.typeInfo(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            )
                    );
            final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.ParameterInfo(
                                    "param", // schemaName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt32() // typeInfo
                            )
                    );
            final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.FunctionInfo>();

            return new zserio.runtime.typeinfo.TypeInfo.StructTypeInfo(
                    "DummyNested", DummyNested.class, templateName, templateArguments,
                    fieldList, parameterList, functionList
            );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createChild().createContext();
            contextNode.createChild();
            contextNode.createChild();
            DummyEnum.createPackingContext(contextNode.createChild());
            DummyBitmask.createPackingContext(contextNode.createChild());
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getChildren().get(0).getContext().init(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));
            dummyEnum_.initPackingContext(contextNode.getChildren().get(3));
            dummyBitmask_.initPackingContext(contextNode.getChildren().get(4));
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 32;
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfBitBuffer(data_);
            endBitPosition += dummyEnum_.bitSizeOf(endBitPosition);
            endBitPosition += dummyBitmask_.bitSizeOf(endBitPosition);

            return (int)(endBitPosition - bitPosition);
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfBitBuffer(data_);
            endBitPosition += dummyEnum_.bitSizeOf(contextNode.getChildren().get(3),
                    endBitPosition);
            endBitPosition += dummyBitmask_.bitSizeOf(contextNode.getChildren().get(4),
                    endBitPosition);

            return (int)(endBitPosition - bitPosition);
        }

        public long getParam()
        {
            return this.param_;
        }

        public long getValue()
        {
            return value_;
        }

        public void setValue(long value_)
        {
            this.value_ = value_;
        }

        public java.lang.String getText()
        {
            return text_;
        }

        public void setText(java.lang.String text_)
        {
            this.text_ = text_;
        }

        public zserio.runtime.io.BitBuffer getData()
        {
            return data_;
        }

        public void setData(zserio.runtime.io.BitBuffer data_)
        {
            this.data_ = data_;
        }

        public DummyEnum getDummyEnum()
        {
            return dummyEnum_;
        }

        public void setDummyEnum(DummyEnum dummyEnum_)
        {
            this.dummyEnum_ = dummyEnum_;
        }

        public DummyBitmask getDummyBitmask()
        {
            return dummyBitmask_;
        }

        public void setDummyBitmask(DummyBitmask dummyBitmask_)
        {
            this.dummyBitmask_ = dummyBitmask_;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof DummyNested)
            {
                final DummyNested that = (DummyNested)obj;

                return
                        this.param_ == that.param_ &&
                        value_ == that.value_ &&
                        ((text_ == null) ? that.text_ == null : text_.equals(that.text_)) &&
                        ((data_ == null) ? that.data_ == null : data_.equals(that.data_)) &&
                        ((dummyEnum_ == null) ? that.dummyEnum_ == null : dummyEnum_.getValue() == that.dummyEnum_.getValue()) &&
                        ((dummyBitmask_ == null) ? that.dummyBitmask_ == null : dummyBitmask_.getValue() == that.dummyBitmask_.getValue());
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.HashCodeUtil.HASH_SEED;

            result = HashCodeUtil.calcHashCode(result, getParam());
            result = HashCodeUtil.calcHashCode(result, value_);
            result = HashCodeUtil.calcHashCode(result, text_);
            result = HashCodeUtil.calcHashCode(result, data_);
            result = HashCodeUtil.calcHashCode(result, dummyEnum_);
            result = HashCodeUtil.calcHashCode(result, dummyBitmask_);

            return result;
        }

        public void read(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            value_ = in.readUnsignedInt();

            text_ = in.readString();

            data_ = in.readBitBuffer();

            dummyEnum_ = DummyEnum.readEnum(in);

            dummyBitmask_ = new DummyBitmask(in);
        }

        public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            value_ = ((zserio.runtime.array.ArrayElement.LongArrayElement)
                    contextNode.getChildren().get(0).getContext().read(
                            new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), in)).get();

            text_ = in.readString();

            data_ = in.readBitBuffer();

            dummyEnum_ = DummyEnum.readEnum(contextNode.getChildren().get(3), in);

            dummyBitmask_ = new DummyBitmask(contextNode.getChildren().get(4), in);
        }

        @Override
        public long initializeOffsets()
        {
            return initializeOffsets(0);
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 32;
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfBitBuffer(data_);
            endBitPosition += dummyEnum_.bitSizeOf(endBitPosition);
            endBitPosition += dummyBitmask_.bitSizeOf(endBitPosition);

            return endBitPosition;
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfBitBuffer(data_);
            endBitPosition = dummyEnum_.initializeOffsets(contextNode.getChildren().get(3),
                    endBitPosition);
            endBitPosition = dummyBitmask_.initializeOffsets(contextNode.getChildren().get(4),
                    endBitPosition);

            return endBitPosition;
        }

        public void write(java.io.File file) throws java.io.IOException
        {
            try (final zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file))
            {
                write(out);
            }
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            out.writeUnsignedInt(value_);

            out.writeString(text_);

            out.writeBitBuffer(data_);

            dummyEnum_.write(out);

            dummyBitmask_.write(out);
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getChildren().get(0).getContext().write(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), out,
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));

            out.writeString(text_);

            out.writeBitBuffer(data_);

            dummyEnum_.write(contextNode.getChildren().get(3), out);

            dummyBitmask_.write(contextNode.getChildren().get(4), out);
        }

        private final long param_;
        private long value_;
        private java.lang.String text_;
        private zserio.runtime.io.BitBuffer data_;
        private DummyEnum dummyEnum_;
        private DummyBitmask dummyBitmask_;
    }

    public static class DummyObject implements zserio.runtime.io.Writer, zserio.runtime.SizeOf
    {
        public DummyObject()
        {
        }

        public DummyObject(java.io.File file)
                throws java.io.IOException
        {
            try (final zserio.runtime.io.FileBitStreamReader in = new zserio.runtime.io.FileBitStreamReader(file))
            {
                read(in);
            }
        }

        public DummyObject(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(in);
        }

        public DummyObject(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(contextNode, in);
        }

        public DummyObject(
                long value_,
                DummyNested nested_,
                java.lang.String text_,
                DummyNested[] nestedArray_,
                java.lang.String[] textArray_,
                zserio.runtime.io.BitBuffer[] externArray_,
                java.lang.Boolean optionalBool_,
                DummyNested optionalNested_)
        {
            setValue(value_);
            setNested(nested_);
            setText(text_);
            setNestedArray(nestedArray_);
            setTextArray(textArray_);
            setExternArray(externArray_);
            setOptionalBool(optionalBool_);
            setOptionalNested(optionalNested_);
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            final java.lang.String templateName = "";
            final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                    new java.util.ArrayList<zserio.runtime.typeinfo.TypeInfo>();
            final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "value", // schemaName
                                    "getValue", // getterName
                                    "setValue", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt32(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "nested", // schemaName
                                    "getNested", // getterName
                                    "setNested", // setterName
                                    DummyNested.typeInfo(), // typeInfo
                                    java.util.Arrays.asList((obj, index) -> ((DummyObject)obj).getValue()), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "text", // schemaName
                                    "getText", // getterName
                                    "setText", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getString(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "nestedArray", // schemaName
                                    "getNestedArray", // getterName
                                    "setNestedArray", // setterName
                                    DummyNested.typeInfo(), // typeInfo
                                    java.util.Arrays.asList((obj, index) -> ((DummyObject)obj).getValue()), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    true, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "textArray", // schemaName
                                    "getTextArray", // getterName
                                    "setTextArray", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getString(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    true, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "externArray", // schemaName
                                    "getExternArray", // getterName
                                    "setExternArray", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getBitBuffer(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    true, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "optionalBool", // schemaName
                                    "getOptionalBool", // getterName
                                    "setOptionalBool", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getBool(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<java.lang.Object, java.lang.Integer, java.lang.Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    true, // isOptional
                                    null, // optionalCondition
                                    "isOptionalBoolUsed", // isUsedIndicatorName
                                    "isOptionalBoolSet", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "optionalNested", // schemaName
                                    "getOptionalNested", // getterName
                                    "setOptionalNested", // setterName
                                    DummyNested.typeInfo(), // typeInfo
                                    java.util.Arrays.asList((obj, index) -> ((DummyObject)obj).getValue()), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    true, // isOptional
                                    null, // optionalCondition
                                    "isOptionalNestedUsed", // isUsedIndicatorName
                                    "isOptionalNestedSet", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            )
                    );
            final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.ParameterInfo>();
            final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.FunctionInfo>();

            return new zserio.runtime.typeinfo.TypeInfo.StructTypeInfo(
                    "DummyObject", DummyObject.class, templateName, templateArguments,
                    fieldList, parameterList, functionList
            );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createChild().createContext();
            DummyNested.createPackingContext(contextNode.createChild());
            contextNode.createChild();
            contextNode.createChild();
            contextNode.createChild();
            contextNode.createChild();
            contextNode.createChild();
            DummyNested.createPackingContext(contextNode.createChild());
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getChildren().get(0).getContext().init(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));
            nested_.initPackingContext(contextNode.getChildren().get(1));
            if (isOptionalNestedUsed())
            {
                optionalNested_.initPackingContext(contextNode.getChildren().get(7));
            }
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 32;
            endBitPosition += nested_.bitSizeOf(endBitPosition);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += nestedArray_.bitSizeOf(endBitPosition);
            endBitPosition += textArray_.bitSizeOf(endBitPosition);
            endBitPosition += externArray_.bitSizeOf(endBitPosition);
            endBitPosition += 1;
            if (isOptionalBoolUsed())
            {
                endBitPosition += 1;
            }
            endBitPosition += 1;
            if (isOptionalNestedUsed())
            {
                endBitPosition += optionalNested_.bitSizeOf(endBitPosition);
            }

            return (int)(endBitPosition - bitPosition);
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));
            endBitPosition += nested_.bitSizeOf(contextNode.getChildren().get(1),
                    endBitPosition);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += nestedArray_.bitSizeOfPacked(endBitPosition);
            endBitPosition += textArray_.bitSizeOf(endBitPosition);
            endBitPosition += externArray_.bitSizeOf(endBitPosition);
            endBitPosition += 1;
            if (isOptionalBoolUsed())
            {
                endBitPosition += 1;
            }
            endBitPosition += 1;
            if (isOptionalNestedUsed())
            {
                endBitPosition += optionalNested_.bitSizeOf(contextNode.getChildren().get(7),
                        endBitPosition);
            }

            return (int)(endBitPosition - bitPosition);
        }

        public long getValue()
        {
            return value_;
        }

        public void setValue(long value_)
        {
            this.value_ = value_;
        }

        public DummyNested getNested()
        {
            return nested_;
        }

        public void setNested(DummyNested nested_)
        {
            this.nested_ = nested_;
        }

        public java.lang.String getText()
        {
            return text_;
        }

        public void setText(java.lang.String text_)
        {
            this.text_ = text_;
        }

        public DummyNested[] getNestedArray()
        {
            return nestedArray_.getRawArray();
        }

        public void setNestedArray(DummyNested[] nestedArray_)
        {
            this.nestedArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyNested.class, nestedArray_),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyNested>(new ZserioElementFactory_nestedArray()),
                    zserio.runtime.array.ArrayType.AUTO);
        }

        public java.lang.String[] getTextArray()
        {
            return textArray_.getRawArray();
        }

        public void setTextArray(java.lang.String[] textArray_)
        {
            this.textArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.StringRawArray(textArray_),
                    new zserio.runtime.array.ArrayTraits.StringArrayTraits(),
                    zserio.runtime.array.ArrayType.AUTO);
        }

        public zserio.runtime.io.BitBuffer[] getExternArray()
        {
            return externArray_.getRawArray();
        }

        public void setExternArray(zserio.runtime.io.BitBuffer[] externArray_)
        {
            this.externArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.BitBufferRawArray(externArray_),
                    new zserio.runtime.array.ArrayTraits.BitBufferArrayTraits(),
                    zserio.runtime.array.ArrayType.AUTO);
        }

        public java.lang.Boolean getOptionalBool()
        {
            return optionalBool_;
        }

        public void setOptionalBool(java.lang.Boolean optionalBool_)
        {
            this.optionalBool_ = optionalBool_;
        }

        public boolean isOptionalBoolUsed()
        {
            return isOptionalBoolSet();
        }

        public boolean isOptionalBoolSet()
        {
            return (optionalBool_ != null);
        }

        public void resetOptionalBool()
        {
            optionalBool_ = null;
        }

        public DummyNested getOptionalNested()
        {
            return optionalNested_;
        }

        public void setOptionalNested(DummyNested optionalNested_)
        {
            this.optionalNested_ = optionalNested_;
        }

        public boolean isOptionalNestedUsed()
        {
            return isOptionalNestedSet();
        }

        public boolean isOptionalNestedSet()
        {
            return (optionalNested_ != null);
        }

        public void resetOptionalNested()
        {
            optionalNested_ = null;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof DummyObject)
            {
                final DummyObject that = (DummyObject)obj;

                return
                        value_ == that.value_ &&
                        ((nested_ == null) ? that.nested_ == null : nested_.equals(that.nested_)) &&
                        ((text_ == null) ? that.text_ == null : text_.equals(that.text_)) &&
                        ((nestedArray_ == null) ? that.nestedArray_ == null : nestedArray_.equals(that.nestedArray_)) &&
                        ((textArray_ == null) ? that.textArray_ == null : textArray_.equals(that.textArray_)) &&
                        ((externArray_ == null) ? that.externArray_ == null : externArray_.equals(that.externArray_)) &&
                        ((!isOptionalBoolUsed()) ? !that.isOptionalBoolUsed() :
                            ((optionalBool_ == null) ? that.optionalBool_ == null : optionalBool_.equals(that.optionalBool_))) &&
                        ((!isOptionalNestedUsed()) ? !that.isOptionalNestedUsed() :
                            ((optionalNested_ == null) ? that.optionalNested_ == null : optionalNested_.equals(that.optionalNested_)));
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.HashCodeUtil.HASH_SEED;

            result = HashCodeUtil.calcHashCode(result, value_);
            result = HashCodeUtil.calcHashCode(result, nested_);
            result = HashCodeUtil.calcHashCode(result, text_);
            result = HashCodeUtil.calcHashCode(result, nestedArray_);
            result = HashCodeUtil.calcHashCode(result, textArray_);
            result = HashCodeUtil.calcHashCode(result, externArray_);
            if (isOptionalBoolUsed())
                result = HashCodeUtil.calcHashCode(result, optionalBool_);
            if (isOptionalNestedUsed())
                result = HashCodeUtil.calcHashCode(result, optionalNested_);

            return result;
        }

        public void read(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            value_ = in.readUnsignedInt();

            nested_ = new DummyNested(in, (getValue()));

            text_ = in.readString();

            nestedArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyNested.class),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyNested>(new ZserioElementFactory_nestedArray()),
                    zserio.runtime.array.ArrayType.AUTO);
            nestedArray_.read(in);

            textArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.StringRawArray(),
                    new zserio.runtime.array.ArrayTraits.StringArrayTraits(),
                    zserio.runtime.array.ArrayType.AUTO);
            textArray_.read(in);

            externArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.BitBufferRawArray(),
                    new zserio.runtime.array.ArrayTraits.BitBufferArrayTraits(),
                    zserio.runtime.array.ArrayType.AUTO);
            externArray_.read(in);

            if (in.readBool())
            {
                optionalBool_ = in.readBool();
            }

            if (in.readBool())
            {
                optionalNested_ = new DummyNested(in, (getValue()));
            }
        }

        public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            value_ = ((zserio.runtime.array.ArrayElement.LongArrayElement)
                    contextNode.getChildren().get(0).getContext().read(
                            new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), in)).get();

            nested_ = new DummyNested(contextNode.getChildren().get(1), in, (getValue()));

            text_ = in.readString();

            nestedArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyNested.class),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyNested>(new ZserioElementFactory_nestedArray()),
                    zserio.runtime.array.ArrayType.AUTO);
            nestedArray_.readPacked(in);

            textArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.StringRawArray(),
                    new zserio.runtime.array.ArrayTraits.StringArrayTraits(),
                    zserio.runtime.array.ArrayType.AUTO);
            textArray_.read(in);

            externArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.BitBufferRawArray(),
                    new zserio.runtime.array.ArrayTraits.BitBufferArrayTraits(),
                    zserio.runtime.array.ArrayType.AUTO);
            externArray_.read(in);

            if (in.readBool())
            {
                optionalBool_ = in.readBool();
            }

            if (in.readBool())
            {
                optionalNested_ = new DummyNested(contextNode.getChildren().get(7), in, (getValue()));
            }
        }

        @Override
        public long initializeOffsets()
        {
            return initializeOffsets(0);
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 32;
            endBitPosition = nested_.initializeOffsets(endBitPosition);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition = nestedArray_.initializeOffsets(endBitPosition);
            endBitPosition = textArray_.initializeOffsets(endBitPosition);
            endBitPosition = externArray_.initializeOffsets(endBitPosition);
            endBitPosition += 1;
            if (isOptionalBoolUsed())
            {
                endBitPosition += 1;
            }
            endBitPosition += 1;
            if (isOptionalNestedUsed())
            {
                endBitPosition = optionalNested_.initializeOffsets(endBitPosition);
            }

            return endBitPosition;
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));
            endBitPosition = nested_.initializeOffsets(contextNode.getChildren().get(1),
                    endBitPosition);
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition = nestedArray_.initializeOffsetsPacked(endBitPosition);
            endBitPosition = textArray_.initializeOffsets(endBitPosition);
            endBitPosition = externArray_.initializeOffsets(endBitPosition);
            endBitPosition += 1;
            if (isOptionalBoolUsed())
            {
                endBitPosition += 1;
            }
            endBitPosition += 1;
            if (isOptionalNestedUsed())
            {
                endBitPosition = optionalNested_.initializeOffsets(contextNode.getChildren().get(7),
                        endBitPosition);
            }

            return endBitPosition;
        }

        public void write(java.io.File file) throws java.io.IOException
        {
            try (final zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file))
            {
                write(out);
            }
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            out.writeUnsignedInt(value_);

            // check parameters
            if (nested_.getParam() != (getValue()))
            {
                throw new zserio.runtime.ZserioError("Write: Wrong parameter param for field DummyObject.nested: " +
                        nested_.getParam() + " != " + (getValue()) + "!");
            }
            nested_.write(out);

            out.writeString(text_);

            nestedArray_.write(out);

            textArray_.write(out);

            externArray_.write(out);

            if (isOptionalBoolUsed())
            {
                out.writeBool(true);
                out.writeBool(optionalBool_);
            }
            else
            {
                out.writeBool(false);
            }

            if (isOptionalNestedUsed())
            {
                out.writeBool(true);
                // check parameters
                if (optionalNested_.getParam() != (getValue()))
                {
                    throw new zserio.runtime.ZserioError("Write: Wrong parameter param for field DummyObject.optionalNested: " +
                            optionalNested_.getParam() + " != " + (getValue()) + "!");
                }
                optionalNested_.write(out);
            }
            else
            {
                out.writeBool(false);
            }
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getChildren().get(0).getContext().write(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), out,
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value_));

            nested_.write(contextNode.getChildren().get(1), out);

            out.writeString(text_);

            nestedArray_.writePacked(out);

            textArray_.write(out);

            externArray_.write(out);

            if (isOptionalBoolUsed())
            {
                out.writeBool(true);
                out.writeBool(optionalBool_);
            }
            else
            {
                out.writeBool(false);
            }

            if (isOptionalNestedUsed())
            {
                out.writeBool(true);
                optionalNested_.write(contextNode.getChildren().get(7), out);
            }
            else
            {
                out.writeBool(false);
            }
        }

        private final class ZserioElementFactory_nestedArray implements zserio.runtime.array.ElementFactory<DummyNested>
        {
            @Override
            public DummyNested create(zserio.runtime.io.BitStreamReader in, int index)
                    throws java.io.IOException
            {
                return new DummyNested(in, (getValue()));
            }

            @Override
            public void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
            {
                DummyNested.createPackingContext(contextNode);
            }

            @Override
            public DummyNested create(zserio.runtime.array.PackingContextNode contextNode,
                    zserio.runtime.io.BitStreamReader in, int index) throws java.io.IOException
            {
                return new DummyNested(contextNode, in, (getValue()));
            }
        }

        private long value_;
        private DummyNested nested_;
        private java.lang.String text_;
        private zserio.runtime.array.Array nestedArray_;
        private zserio.runtime.array.Array textArray_;
        private zserio.runtime.array.Array externArray_;
        private java.lang.Boolean optionalBool_;
        private DummyNested optionalNested_;
    }
}
