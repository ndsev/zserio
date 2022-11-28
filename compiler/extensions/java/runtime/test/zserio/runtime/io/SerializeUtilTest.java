package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

public class SerializeUtilTest
{
    @Test
    public void serialize()
    {
        final DummyObject dummyObject = new DummyObject(true, 0xAD);
        final BitBuffer bitBuffer = SerializeUtil.serialize(dummyObject);
        final int expectedBitsize = 31;
        assertEquals(expectedBitsize, bitBuffer.getBitSize());
        assertEquals((byte)0x00, bitBuffer.getBuffer()[0]);
        assertEquals((byte)0x00, bitBuffer.getBuffer()[1]);
        assertEquals((byte)0x01, bitBuffer.getBuffer()[2]);
        assertEquals((byte)0x5A, bitBuffer.getBuffer()[3]);
    }

    @Test
    public void deserialize()
    {
        final int bitSize = 31;
        final BitBuffer bitBuffer = new BitBuffer(new byte[] {
                (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x5A}, bitSize);
        final DummyObject dummyObject = SerializeUtil.deserialize(DummyObject.class, bitBuffer, true);
        assertEquals(true, dummyObject.getParam());
        assertEquals(0xAD, dummyObject.getOptionalValue());
    }

    @Test
    public void serializeToBytes()
    {
        final DummyObject dummyObject = new DummyObject(true, 0xAD);
        final byte[] buffer = SerializeUtil.serializeToBytes(dummyObject);
        final int expectedBitsize = 31;
        assertEquals((expectedBitsize + 7) / 8, buffer.length);
        assertEquals((byte)0x00, buffer[0]);
        assertEquals((byte)0x00, buffer[1]);
        assertEquals((byte)0x01, buffer[2]);
        assertEquals((byte)0x5A, buffer[3]);
    }

    @Test
    public void deserializeFromBytes()
    {
        final byte[] buffer = new byte[] {(byte)0x00, (byte)0x00, (byte)0x01, (byte)0x5A};
        final DummyObject dummyObject = SerializeUtil.deserializeFromBytes(DummyObject.class, buffer, true);
        assertEquals(true, dummyObject.getParam());
        assertEquals(0xAD, dummyObject.getOptionalValue());
    }

    @Test
    public void serializeToFileFromFile()
    {
        final DummyObject dummyObject = new DummyObject(true, 0xAD);
        final String fileName = "SerializationTest1.bin";
        SerializeUtil.serializeToFile(dummyObject, fileName);
        final DummyObject readDummyObject1 = SerializeUtil.deserializeFromFile(DummyObject.class, fileName,
                true);
        assertEquals(true, readDummyObject1.getParam());
        assertEquals(0xAD, readDummyObject1.getOptionalValue());
        assertEquals(readDummyObject1, dummyObject);

        final File file = new File("SerializationTest2.bin");
        SerializeUtil.serializeToFile(dummyObject, file);
        final DummyObject readDummyObject2 = SerializeUtil.deserializeFromFile(DummyObject.class, file,
                true);
        assertEquals(true, readDummyObject2.getParam());
        assertEquals(0xAD, readDummyObject2.getOptionalValue());
        assertEquals(readDummyObject2, dummyObject);
    }

    public static class DummyObject implements zserio.runtime.io.Writer, zserio.runtime.SizeOf
    {
        public DummyObject(
                boolean param_)
        {
            this.param_ = param_;
        }

        public DummyObject(zserio.runtime.io.BitStreamReader in,
                boolean param_)
                throws java.io.IOException
        {
            this.param_ = param_;

            read(in);
        }

        public DummyObject(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in,
                boolean param_)
                throws java.io.IOException
        {
            this.param_ = param_;

            read(contextNode, in);
        }

        public DummyObject(
                boolean param_,
                java.lang.Integer optionalValue_)
        {
            this(param_);

            setOptionalValue(optionalValue_);
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createChild().createContext();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            if (isOptionalValueUsed())
            {
                contextNode.getChildren().get(0).getContext().init(
                        new zserio.runtime.array.ArrayTraits.BitFieldIntArrayTraits(31),
                        new zserio.runtime.array.ArrayElement.IntArrayElement(optionalValue_));
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

            if (isOptionalValueUsed())
            {
                endBitPosition += 31;
            }

            return (int)(endBitPosition - bitPosition);
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            if (isOptionalValueUsed())
            {
                endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                        new zserio.runtime.array.ArrayTraits.BitFieldIntArrayTraits(31),
                        new zserio.runtime.array.ArrayElement.IntArrayElement(optionalValue_));
            }

            return (int)(endBitPosition - bitPosition);
        }

        public boolean getParam()
        {
            return this.param_;
        }

        public java.lang.Integer getOptionalValue()
        {
            return optionalValue_;
        }

        public void setOptionalValue(java.lang.Integer optionalValue_)
        {
            this.optionalValue_ = optionalValue_;
        }

        public boolean isOptionalValueUsed()
        {
            return (getParam());
        }

        public boolean isOptionalValueSet()
        {
            return (optionalValue_ != null);
        }

        public void resetOptionalValue()
        {
            optionalValue_ = null;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof DummyObject)
            {
                final DummyObject that = (DummyObject)obj;

                return
                        this.param_ == that.param_ &&
                        ((!isOptionalValueUsed()) ? !that.isOptionalValueUsed() :
                            ((optionalValue_ == null) ? that.optionalValue_ == null : optionalValue_.equals(that.optionalValue_)));
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.HashCodeUtil.HASH_SEED;

            result = zserio.runtime.HashCodeUtil.calcHashCode(result, getParam());
            if (isOptionalValueUsed())
                result = zserio.runtime.HashCodeUtil.calcHashCode(result, optionalValue_);

            return result;
        }

        public void read(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            if (getParam())
            {
                optionalValue_ = (int)in.readBits(31);
            }
        }

        public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            if (getParam())
            {
                optionalValue_ = ((zserio.runtime.array.ArrayElement.IntArrayElement)
                        contextNode.getChildren().get(0).getContext().read(
                                new zserio.runtime.array.ArrayTraits.BitFieldIntArrayTraits(31), in)).get();
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

            if (isOptionalValueUsed())
            {
                endBitPosition += 31;
            }

            return endBitPosition;
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            if (isOptionalValueUsed())
            {
                endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                        new zserio.runtime.array.ArrayTraits.BitFieldIntArrayTraits(31),
                        new zserio.runtime.array.ArrayElement.IntArrayElement(optionalValue_));
            }

            return endBitPosition;
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            if (isOptionalValueUsed())
            {
                out.writeBits(optionalValue_, 31);
            }
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            if (isOptionalValueUsed())
            {
                contextNode.getChildren().get(0).getContext().write(
                        new zserio.runtime.array.ArrayTraits.BitFieldIntArrayTraits(31), out,
                        new zserio.runtime.array.ArrayElement.IntArrayElement(optionalValue_));
            }
        }

        private final boolean param_;
        private java.lang.Integer optionalValue_;
    }
}
