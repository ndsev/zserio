package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;

import test_object.SerializeEnum;
import test_object.SerializeNested;
import test_object.SerializeObject;

public class SerializeUtilTest
{
    @Test
    public void serializeEnum()
    {
        final SerializeEnum serializeEnum = SerializeEnum.VALUE3;
        final BitBuffer bitBuffer = SerializeUtil.serialize(serializeEnum);
        final int expectedBitsize = 8;
        assertEquals(expectedBitsize, bitBuffer.getBitSize());
        assertEquals((byte)0x02, bitBuffer.getBuffer()[0]);
    }

    @Test
    public void serializeParameterizedObject()
    {
        final byte param = 0x12;
        final short offset = 0;
        final long optionalValue = 0xDEADCAFEL;
        final SerializeNested serializeNested = new SerializeNested(param, offset, optionalValue);
        final BitBuffer bitBuffer = SerializeUtil.serialize(serializeNested);
        final int expectedBitsize = 40;
        assertEquals(expectedBitsize, bitBuffer.getBitSize());
        assertEquals((byte)0x01, bitBuffer.getBuffer()[0]);
        assertEquals((byte)0xDE, bitBuffer.getBuffer()[1]);
        assertEquals((byte)0xAD, bitBuffer.getBuffer()[2]);
        assertEquals((byte)0xCA, bitBuffer.getBuffer()[3]);
        assertEquals((byte)0xFE, bitBuffer.getBuffer()[4]);
    }

    @Test
    public void serializeObject()
    {
        final byte param = 0x12;
        final short offset = 0;
        final long optionalValue = 0xDEADCAFEL;
        final SerializeNested serializeNested = new SerializeNested(param, offset, optionalValue);
        final SerializeObject serializeObject = new SerializeObject(param, serializeNested);
        final BitBuffer bitBuffer = SerializeUtil.serialize(serializeObject);
        final int expectedBitsize = 48;
        assertEquals(expectedBitsize, bitBuffer.getBitSize());
        assertEquals((byte)0x12, bitBuffer.getBuffer()[0]);
        assertEquals((byte)0x02, bitBuffer.getBuffer()[1]);
        assertEquals((byte)0xDE, bitBuffer.getBuffer()[2]);
        assertEquals((byte)0xAD, bitBuffer.getBuffer()[3]);
        assertEquals((byte)0xCA, bitBuffer.getBuffer()[4]);
        assertEquals((byte)0xFE, bitBuffer.getBuffer()[5]);
    }

    @Test
    public void deserializeEnum()
    {
        final BitBuffer bitBuffer = new BitBuffer(new byte[] {(byte)0x02}, 8);
        final SerializeEnum serializeEnum = SerializeUtil.deserialize(SerializeEnum.class, bitBuffer);
        assertEquals(SerializeEnum.VALUE3, serializeEnum);
    }

    @Test
    public void deserializeParameterizedObject()
    {
        final BitBuffer bitBuffer =
                new BitBuffer(new byte[] {(byte)0x01, (byte)0xDE, (byte)0xAD, (byte)0xCA, (byte)0xFE}, 40);
        final byte param = 0x12;
        assertThrows(ZserioError.class, () -> SerializeUtil.deserialize(SerializeNested.class, bitBuffer));
        final SerializeNested serializeNested =
                SerializeUtil.deserialize(SerializeNested.class, bitBuffer, param);
        assertEquals(param, serializeNested.getParam());
        assertEquals(0x01, serializeNested.getOffset());
        assertEquals(0xDEADCAFEL, serializeNested.getOptionalValue());

        final BitBuffer wrongBitBuffer =
                new BitBuffer(new byte[] {(byte)0x01, (byte)0xDE, (byte)0xAD, (byte)0xCA, (byte)0xFE}, 39);
        assertThrows(ZserioError.class, () -> SerializeUtil.deserialize(SerializeNested.class, wrongBitBuffer));
    }

    @Test
    public void deserializeObject()
    {
        final BitBuffer bitBuffer = new BitBuffer(
                new byte[] {(byte)0x12, (byte)0x02, (byte)0xDE, (byte)0xAD, (byte)0xCA, (byte)0xFE}, 48);
        final SerializeObject serializeObject = SerializeUtil.deserialize(SerializeObject.class, bitBuffer);
        assertEquals(0x12, serializeObject.getParam());
        final SerializeNested serializeNested = serializeObject.getNested();
        assertEquals(0x12, serializeNested.getParam());
        assertEquals(0x02, serializeNested.getOffset());
        assertEquals(0xDEADCAFEL, serializeNested.getOptionalValue());
    }

    @Test
    public void serializeEnumToBytes()
    {
        final SerializeEnum serializeEnum = SerializeEnum.VALUE3;
        final byte[] buffer = SerializeUtil.serializeToBytes(serializeEnum);
        assertEquals(1, buffer.length);
        assertEquals((byte)0x02, buffer[0]);
    }

    @Test
    public void serializeParameterizedObjectToBytes()
    {
        final byte param = 0x12;
        final short offset = 0;
        final long optionalValue = 0xDEADCAFEL;
        final SerializeNested serializeNested = new SerializeNested(param, offset, optionalValue);
        final byte[] buffer = SerializeUtil.serializeToBytes(serializeNested);
        assertEquals(5, buffer.length);
        assertEquals((byte)0x01, buffer[0]);
        assertEquals((byte)0xDE, buffer[1]);
        assertEquals((byte)0xAD, buffer[2]);
        assertEquals((byte)0xCA, buffer[3]);
        assertEquals((byte)0xFE, buffer[4]);
    }

    @Test
    public void serializeObjectToBytes()
    {
        final byte param = 0x12;
        final short offset = 0;
        final long optionalValue = 0xDEADCAFEL;
        final SerializeNested serializeNested = new SerializeNested(param, offset, optionalValue);
        final SerializeObject serializeObject = new SerializeObject(param, serializeNested);
        final byte[] buffer = SerializeUtil.serializeToBytes(serializeObject);
        assertEquals(6, buffer.length);
        assertEquals((byte)0x12, buffer[0]);
        assertEquals((byte)0x02, buffer[1]);
        assertEquals((byte)0xDE, buffer[2]);
        assertEquals((byte)0xAD, buffer[3]);
        assertEquals((byte)0xCA, buffer[4]);
        assertEquals((byte)0xFE, buffer[5]);
    }

    @Test
    public void deserializeEnumFromBytes()
    {
        final byte[] buffer = new byte[] {(byte)0x02};
        final SerializeEnum serializeEnum = SerializeUtil.deserializeFromBytes(SerializeEnum.class, buffer);
        assertEquals(SerializeEnum.VALUE3, serializeEnum);
    }

    @Test
    public void deserializeParameterizedObjectFromBytes()
    {
        final byte[] buffer = new byte[] {(byte)0x01, (byte)0xDE, (byte)0xAD, (byte)0xCA, (byte)0xFE};
        final byte param = 0x12;
        assertThrows(
                ZserioError.class, () -> SerializeUtil.deserializeFromBytes(SerializeNested.class, buffer));
        final SerializeNested serializeNested =
                SerializeUtil.deserializeFromBytes(SerializeNested.class, buffer, param);
        assertEquals(param, serializeNested.getParam());
        assertEquals(0x01, serializeNested.getOffset());
        assertEquals(0xDEADCAFEL, serializeNested.getOptionalValue());

        final byte[] wrongBuffer = new byte[] {(byte)0x00, (byte)0xDE, (byte)0xAD, (byte)0xCA, (byte)0xFE};
        assertThrows(ZserioError.class,
                () -> SerializeUtil.deserializeFromBytes(SerializeNested.class, wrongBuffer));
    }

    @Test
    public void deserializeObjectFromBytes()
    {
        final byte[] buffer =
                new byte[] {(byte)0x12, (byte)0x02, (byte)0xDE, (byte)0xAD, (byte)0xCA, (byte)0xFE};
        final SerializeObject serializeObject =
                SerializeUtil.deserializeFromBytes(SerializeObject.class, buffer);
        assertEquals(0x12, serializeObject.getParam());
        final SerializeNested serializeNested = serializeObject.getNested();
        assertEquals(0x12, serializeNested.getParam());
        assertEquals(0x02, serializeNested.getOffset());
        assertEquals(0xDEADCAFEL, serializeNested.getOptionalValue());
    }

    @Test
    public void serializeToFileFromFile()
    {
        final byte param = 0x12;
        final short offset = 0;
        final long optionalValue = 0xDEADCAFEL;
        final SerializeNested serializeNested = new SerializeNested(param, offset, optionalValue);
        final SerializeObject serializeObject = new SerializeObject(param, serializeNested);
        final String fileName = "SerializationTest.bin";
        SerializeUtil.serializeToFile(serializeObject, fileName);
        final SerializeObject readSerializeObject =
                SerializeUtil.deserializeFromFile(SerializeObject.class, fileName);
        assertEquals(serializeObject, readSerializeObject);
    }
}
