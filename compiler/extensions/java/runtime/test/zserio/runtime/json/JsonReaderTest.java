package zserio.runtime.json;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;

import test_object.DummyBitmask;
import test_object.DummyEnum;
import test_object.DummyObject;

public class JsonReaderTest
{
    @Test
    public void readObject() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ],\n" +
                "             \"bitSize\": 12\n" +
                "        },\n" +
                "        \"bytesData\": {\n" +
                "           \"buffer\": [\n" +
                "               202,\n" +
                "               254\n" +
                "           ]\n" +
                "        },\n" +
                "        \"dummyEnum\": 0,\n" +
                "        \"dummyBitmask\": 1\n" +
                "    },\n" +
                "    \"text\": \"test\",\n" +
                "    \"nestedArray\": [\n" +
                "        {\n" +
                "            \"value\": 5,\n" +
                "            \"text\": \"nestedArray\",\n" +
                "            \"externData\": {\n" +
                "                 \"buffer\": [\n" +
                "                     202,\n" +
                "                     254\n" +
                "                 ]," +
                "                 \"bitSize\": 15\n" +
                "            },\n" +
                "            \"bytesData\": {\n" +
                "               \"buffer\": [\n" +
                "                   203,\n" +
                "                   240\n" +
                "               ]\n" +
                "            },\n" +
                "            \"dummyEnum\": 1,\n" +
                "            \"dummyBitmask\": 2\n" +
                "        }\n" +
                "    ],\n" +
                "    \"textArray\": [\n" +
                "        \"this\",\n" +
                "        \"is\",\n" +
                "        \"text\",\n" +
                "        \"array\"\n" +
                "    ],\n" +
                "    \"externArray\": [\n" +
                "        {\n" +
                "            \"buffer\": [\n" +
                "                222,\n" +
                "                209\n" +
                "            ]," +
                "            \"bitSize\": 13\n" +
                "        }\n" +
                "    ],\n" +
                "    \"bytesArray\": [\n"  +
                "        {\n" +
                "           \"buffer\": [\n" +
                "               0\n" +
                "           ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"optionalBool\": null\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject = jsonReader.read(DummyObject.typeInfo());
        jsonReader.close();
        assertTrue(zserioObject != null);
        assertTrue(zserioObject instanceof DummyObject);
        final DummyObject dummyObject = (DummyObject)zserioObject;

        assertEquals(13, dummyObject.getValue());
        assertEquals(13, dummyObject.getNested().getParam());
        assertEquals(10, dummyObject.getNested().getValue());
        assertEquals("nested", dummyObject.getNested().getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12),
                dummyObject.getNested().getExternData());
        assertArrayEquals(new byte[] {(byte)0xCA, (byte)0xFE}, dummyObject.getNested().getBytesData());
        assertEquals(DummyEnum.ONE, dummyObject.getNested().getDummyEnum());
        assertEquals(DummyBitmask.Values.READ, dummyObject.getNested().getDummyBitmask());
        assertEquals("test", dummyObject.getText());
        assertEquals(1, dummyObject.getNestedArray().length);
        assertEquals(5, dummyObject.getNestedArray()[0].getValue());
        assertEquals("nestedArray", dummyObject.getNestedArray()[0].getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 15),
                dummyObject.getNestedArray()[0].getExternData());
        assertArrayEquals(new byte[] {(byte)0xCB, (byte)0xF0}, dummyObject.getNestedArray()[0].getBytesData());
        assertEquals(DummyEnum.TWO, dummyObject.getNestedArray()[0].getDummyEnum());
        assertEquals(DummyBitmask.Values.WRITE, dummyObject.getNestedArray()[0].getDummyBitmask());
        assertEquals(4, dummyObject.getTextArray().length);
        assertEquals("this", dummyObject.getTextArray()[0]);
        assertEquals("is", dummyObject.getTextArray()[1]);
        assertEquals("text", dummyObject.getTextArray()[2]);
        assertEquals("array", dummyObject.getTextArray()[3]);
        assertEquals(1, dummyObject.getExternArray().length);
        assertEquals(new BitBuffer(new byte[] {(byte)0xDE, (byte)0xD1}, 13), dummyObject.getExternArray()[0]);
        assertEquals(1, dummyObject.getBytesArray().length);
        assertArrayEquals(new byte[] {0}, dummyObject.getBytesArray()[0]);
        assertEquals(null, dummyObject.getOptionalBool());
        assertEquals(null, dummyObject.getOptionalNested()); // not present in json
    }

    @Test
    public void readTwoObjects() throws IOException
    {
        final Reader reader = new StringReader(
                "{\"value\": 13}\n" +
                "{\"value\": 42, \"text\": \"test\"}\n"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject1 = jsonReader.read(DummyObject.typeInfo());
        assertTrue(zserioObject1 != null);
        assertTrue(zserioObject1 instanceof DummyObject);
        final DummyObject dummyObject1 = (DummyObject)zserioObject1;

        assertEquals(13, dummyObject1.getValue());
        assertEquals(null, dummyObject1.getText());

        final Object zserioObject2 = jsonReader.read(DummyObject.typeInfo());
        assertTrue(zserioObject2 != null);
        assertTrue(zserioObject2 instanceof DummyObject);
        final DummyObject dummyObject2 = (DummyObject)zserioObject2;

        assertEquals(42, dummyObject2.getValue());
        assertEquals("test", dummyObject2.getText());

        jsonReader.close();
    }

    @Test
    public void readUnonrderedBitBuffer() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"externData\": {\n" +
                "             \"bitSize\": 12,\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ]\n" +
                "        },\n" +
                "        \"bytesData\": {\n" +
                "           \"buffer\": [\n" +
                "               202,\n" +
                "               254\n" +
                "           ]\n" +
                "        },\n" +
                "        \"dummyEnum\": -1,\n" +
                "        \"dummyBitmask\": 1\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject = jsonReader.read(DummyObject.typeInfo());
        assertTrue(zserioObject != null);
        assertTrue(zserioObject instanceof DummyObject);
        final DummyObject dummyObject = (DummyObject)zserioObject;

        assertEquals(13, dummyObject.getValue());
        assertEquals(13, dummyObject.getNested().getParam());
        assertEquals(10, dummyObject.getNested().getValue());
        assertEquals("nested", dummyObject.getNested().getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12),
                dummyObject.getNested().getExternData());
        assertArrayEquals(new byte[] {(byte)0xCA, (byte)0xFE}, dummyObject.getNested().getBytesData());
        assertEquals(DummyEnum.MinusOne, dummyObject.getNested().getDummyEnum());
        assertEquals(DummyBitmask.Values.READ, dummyObject.getNested().getDummyBitmask());

        jsonReader.close();
    }

    @Test
    public void readStringifiedEnum() throws IOException
    {
        checkReadStringifiedEnum("ONE", DummyEnum.ONE);
        checkReadStringifiedEnum("MinusOne", DummyEnum.MinusOne);
        checkReadStringifiedEnumThrows("NONEXISTING",
                "JsonReader: Cannot create enum 'test_object.DummyEnum' " +
                "from string value 'NONEXISTING'! (JsonParser:3:22)");
        checkReadStringifiedEnumThrows("***",
                "JsonReader: Cannot create enum 'test_object.DummyEnum' " +
                "from string value '***'! (JsonParser:3:22)");
        checkReadStringifiedEnumThrows("10 /* no match */",
                "JsonReader: Cannot create enum 'test_object.DummyEnum' " +
                "from string value '10 /* no match */'! (JsonParser:3:22)");
        checkReadStringifiedEnumThrows("-10 /* no match */",
                "JsonReader: Cannot create enum 'test_object.DummyEnum' " +
                "from string value '-10 /* no match */'! (JsonParser:3:22)");
        checkReadStringifiedEnumThrows("",
                "JsonReader: Cannot create enum 'test_object.DummyEnum' " +
                "from string value ''! (JsonParser:3:22)");
    }

    @Test
    public void readStringifiedBitmask() throws IOException
    {
        checkReadStringifiedBitmask("READ", DummyBitmask.Values.READ);
        checkReadStringifiedBitmask("READ | WRITE", DummyBitmask.Values.READ.or(DummyBitmask.Values.WRITE));
        checkReadStringifiedBitmaskThrows("NONEXISTING",
                "JsonReader: Cannot create bitmask 'test_object.DummyBitmask' " +
                "from string value 'NONEXISTING'! (JsonParser:3:25)");
        checkReadStringifiedBitmaskThrows("READ | NONEXISTING",
                "JsonReader: Cannot create bitmask 'test_object.DummyBitmask' " +
                "from string value 'READ | NONEXISTING'! (JsonParser:3:25)");
        checkReadStringifiedBitmaskThrows("READ * NONEXISTING",
                "JsonReader: Cannot create bitmask 'test_object.DummyBitmask' " +
                "from string value 'READ * NONEXISTING'! (JsonParser:3:25)");
        checkReadStringifiedBitmask("7 /* READ | WRITE */", new DummyBitmask((short)7));
        checkReadStringifiedBitmask("15 /* READ | WRITE */", new DummyBitmask((short)15));
        checkReadStringifiedBitmask("4 /* no match */", new DummyBitmask((short)4));
        checkReadStringifiedBitmaskThrows("",
                "JsonReader: Cannot create bitmask 'test_object.DummyBitmask' " +
                "from string value ''! (JsonParser:3:25)");
        checkReadStringifiedBitmaskThrows(" ",
                "JsonReader: Cannot create bitmask 'test_object.DummyBitmask' " +
                "from string value ' '! (JsonParser:3:25)");
        checkReadStringifiedBitmaskThrows(" | ",
                "JsonReader: Cannot create bitmask 'test_object.DummyBitmask' " +
                "from string value ' | '! (JsonParser:3:25)");
    }

    @Test
    public void jsonParserException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\"value\"\n\"value\""
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final JsonParserError exception = assertThrows(JsonParserError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonParser:2:1: Unexpected token: VALUE ('value'), expecting KEY_SEPARATOR!",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void wrongKeyException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\"value\": 13,\n\"nonexisting\": 10}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("ZserioTreeCreator: Field 'nonexisting' not found in " +
                "'test_object.DummyObject'! (JsonParser:2:16)", exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void wrongValueTypeException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n  \"value\": \"13\"\n}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("ZserioTreeCreator: Unexpected value type 'class java.lang.String', expecting 'long'! " +
                "(JsonParser:2:12)", exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void wrongBitBufferException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ],\n" +
                "             \"bitSize\": {\n" +
                "             }\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Unexpected begin object in Bit Buffer! (JsonParser:11:25)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void partialBitBufferException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ]\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Unexpected end in Bit Buffer! (JsonParser:12:5)", exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void wrongBytesException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 13,\n" +
                "    \"nested\": {\n" +
                "        \"value\": 10,\n" +
                "        \"text\": \"nested\",\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ],\n" +
                "             \"bitSize\": 12\n" +
                "        },\n" +
                "        \"bytesData\": {\n" +
                "            \"buffer\": {}\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Unexpected begin object in bytes! (JsonParser:14:23)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void jsonArrayException() throws IOException
    {
        final Reader reader = new StringReader(
                "[1, 2]"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: ZserioTreeCreator expects json object! (JsonParser:1:1)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void jsonValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "\"text\""
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: ZserioTreeCreator expects json object! (JsonParser:1:1)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void bigLongValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 9223372036854775808\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Cannot create long 'uint32' from value '9223372036854775808'! " +
                "(JsonParser:2:14)", exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void floatLongValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"value\": 1.234\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("ZserioTreeCreator: Unexpected value type 'class java.lang.Double', expecting 'long'! " +
                "(JsonParser:2:14)", exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void bigBitBufferByteValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 256\n" +
                "             ],\n" +
                "             \"bitSize\": 7\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Cannot create byte for Bit Buffer from value '256'! (JsonParser:5:18)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void negativeBitBufferByteValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 -1\n" +
                "             ],\n" +
                "             \"bitSize\": 7\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Cannot create byte for Bit Buffer from value '-1'! (JsonParser:5:18)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void wrongBitBufferSizeValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"externData\": {\n" +
                "             \"buffer\": [\n" +
                "                 255\n" +
                "             ],\n" +
                "             \"bitSize\": 9223372036854775808\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Cannot create long for Bit Buffer size from value '9223372036854775808'! " +
                "(JsonParser:7:25)", exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void bigBytesByteValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"bytesData\": {\n" +
                "             \"buffer\": [\n" +
                "                 256\n" +
                "             ]\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Cannot create byte for bytes from value '256'! (JsonParser:5:18)",
                exception.getMessage());
        jsonReader.close();
    }

    @Test
    public void negativeBytesByteValueException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"bytesData\": {\n" +
                "             \"buffer\": [\n" +
                "                 -1\n" +
                "             ]\n" +
                "        }\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals("JsonReader: Cannot create byte for bytes from value '-1'! (JsonParser:5:18)",
                exception.getMessage());
        jsonReader.close();
    }

    private void checkReadStringifiedEnum(String stringValue, DummyEnum expectedValue) throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"dummyEnum\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        try (final JsonReader jsonReader = new JsonReader(reader))
        {
            final Object zserioObject = jsonReader.read(DummyObject.typeInfo());
            assertTrue(zserioObject != null);
            assertTrue(zserioObject instanceof DummyObject);
            final DummyObject dummyObject = (DummyObject)zserioObject;

            assertEquals(expectedValue, dummyObject.getNested().getDummyEnum());
        }
    }

    private void checkReadStringifiedEnumThrows(String stringValue, String expectedMessage) throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"dummyEnum\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals(expectedMessage, exception.getMessage());
        jsonReader.close();
    }

    private void checkReadStringifiedBitmask(String stringValue, DummyBitmask expectedValue) throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"dummyBitmask\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        try (final JsonReader jsonReader = new JsonReader(reader))
        {
            final Object zserioObject = jsonReader.read(DummyObject.typeInfo());
            assertTrue(zserioObject != null);
            assertTrue(zserioObject instanceof DummyObject);
            final DummyObject dummyObject = (DummyObject)zserioObject;

            assertEquals(expectedValue, dummyObject.getNested().getDummyBitmask());
        }
    }

    private void checkReadStringifiedBitmaskThrows(String stringValue, String expectedMessage)
            throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"dummyBitmask\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(DummyObject.typeInfo()));
        assertEquals(expectedMessage, exception.getMessage());
        jsonReader.close();
    }
}
