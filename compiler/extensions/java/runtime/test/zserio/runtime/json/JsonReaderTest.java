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

import test_object.CreatorBitmask;
import test_object.CreatorEnum;
import test_object.CreatorObject;

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
                "        \"creatorEnum\": 0,\n" +
                "        \"creatorBitmask\": 1\n" +
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
                "            \"creatorEnum\": 1,\n" +
                "            \"creatorBitmask\": 2\n" +
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
        final Object zserioObject = jsonReader.read(CreatorObject.typeInfo());
        jsonReader.close();
        assertTrue(zserioObject != null);
        assertTrue(zserioObject instanceof CreatorObject);
        final CreatorObject creatorObject = (CreatorObject)zserioObject;

        assertEquals(13, creatorObject.getValue());
        assertEquals(13, creatorObject.getNested().getParam());
        assertEquals(10, creatorObject.getNested().getValue());
        assertEquals("nested", creatorObject.getNested().getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12),
                creatorObject.getNested().getExternData());
        assertArrayEquals(new byte[] {(byte)0xCA, (byte)0xFE}, creatorObject.getNested().getBytesData());
        assertEquals(CreatorEnum.ONE, creatorObject.getNested().getCreatorEnum());
        assertEquals(CreatorBitmask.Values.READ, creatorObject.getNested().getCreatorBitmask());
        assertEquals("test", creatorObject.getText());
        assertEquals(1, creatorObject.getNestedArray().length);
        assertEquals(5, creatorObject.getNestedArray()[0].getValue());
        assertEquals("nestedArray", creatorObject.getNestedArray()[0].getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 15),
                creatorObject.getNestedArray()[0].getExternData());
        assertArrayEquals(new byte[] {(byte)0xCB, (byte)0xF0}, creatorObject.getNestedArray()[0].getBytesData());
        assertEquals(CreatorEnum.TWO, creatorObject.getNestedArray()[0].getCreatorEnum());
        assertEquals(CreatorBitmask.Values.WRITE, creatorObject.getNestedArray()[0].getCreatorBitmask());
        assertEquals(4, creatorObject.getTextArray().length);
        assertEquals("this", creatorObject.getTextArray()[0]);
        assertEquals("is", creatorObject.getTextArray()[1]);
        assertEquals("text", creatorObject.getTextArray()[2]);
        assertEquals("array", creatorObject.getTextArray()[3]);
        assertEquals(1, creatorObject.getExternArray().length);
        assertEquals(new BitBuffer(new byte[] {(byte)0xDE, (byte)0xD1}, 13), creatorObject.getExternArray()[0]);
        assertEquals(1, creatorObject.getBytesArray().length);
        assertArrayEquals(new byte[] {0}, creatorObject.getBytesArray()[0]);
        assertEquals(null, creatorObject.getOptionalBool());
        assertEquals(null, creatorObject.getOptionalNested()); // not present in json
    }

    @Test
    public void readTwoObjects() throws IOException
    {
        final Reader reader = new StringReader(
                "{\"value\": 13}\n" +
                "{\"value\": 42, \"text\": \"test\"}\n"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject1 = jsonReader.read(CreatorObject.typeInfo());
        assertTrue(zserioObject1 != null);
        assertTrue(zserioObject1 instanceof CreatorObject);
        final CreatorObject creatorObject1 = (CreatorObject)zserioObject1;

        assertEquals(13, creatorObject1.getValue());
        assertEquals(null, creatorObject1.getText());

        final Object zserioObject2 = jsonReader.read(CreatorObject.typeInfo());
        assertTrue(zserioObject2 != null);
        assertTrue(zserioObject2 instanceof CreatorObject);
        final CreatorObject creatorObject2 = (CreatorObject)zserioObject2;

        assertEquals(42, creatorObject2.getValue());
        assertEquals("test", creatorObject2.getText());

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
                "        \"creatorEnum\": -1,\n" +
                "        \"creatorBitmask\": 1\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject = jsonReader.read(CreatorObject.typeInfo());
        assertTrue(zserioObject != null);
        assertTrue(zserioObject instanceof CreatorObject);
        final CreatorObject creatorObject = (CreatorObject)zserioObject;

        assertEquals(13, creatorObject.getValue());
        assertEquals(13, creatorObject.getNested().getParam());
        assertEquals(10, creatorObject.getNested().getValue());
        assertEquals("nested", creatorObject.getNested().getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12),
                creatorObject.getNested().getExternData());
        assertArrayEquals(new byte[] {(byte)0xCA, (byte)0xFE}, creatorObject.getNested().getBytesData());
        assertEquals(CreatorEnum.MinusOne, creatorObject.getNested().getCreatorEnum());
        assertEquals(CreatorBitmask.Values.READ, creatorObject.getNested().getCreatorBitmask());

        jsonReader.close();
    }

    @Test
    public void readStringifiedEnum() throws IOException
    {
        checkReadStringifiedEnum("ONE", CreatorEnum.ONE);
        checkReadStringifiedEnum("MinusOne", CreatorEnum.MinusOne);
        checkReadStringifiedEnumThrows("NONEXISTING",
                "JsonReader: Cannot create enum 'test_object.CreatorEnum' " +
                "from string value 'NONEXISTING'! (JsonParser:3:24)");
        checkReadStringifiedEnumThrows("***",
                "JsonReader: Cannot create enum 'test_object.CreatorEnum' " +
                "from string value '***'! (JsonParser:3:24)");
        checkReadStringifiedEnumThrows("10 /* no match */",
                "JsonReader: Cannot create enum 'test_object.CreatorEnum' " +
                "from string value '10 /* no match */'! (JsonParser:3:24)");
        checkReadStringifiedEnumThrows("-10 /* no match */",
                "JsonReader: Cannot create enum 'test_object.CreatorEnum' " +
                "from string value '-10 /* no match */'! (JsonParser:3:24)");
        checkReadStringifiedEnumThrows("",
                "JsonReader: Cannot create enum 'test_object.CreatorEnum' " +
                "from string value ''! (JsonParser:3:24)");
    }

    @Test
    public void readStringifiedBitmask() throws IOException
    {
        checkReadStringifiedBitmask("READ", CreatorBitmask.Values.READ);
        checkReadStringifiedBitmask("READ | WRITE", CreatorBitmask.Values.READ.or(CreatorBitmask.Values.WRITE));
        checkReadStringifiedBitmaskThrows("NONEXISTING",
                "JsonReader: Cannot create bitmask 'test_object.CreatorBitmask' " +
                "from string value 'NONEXISTING'! (JsonParser:3:27)");
        checkReadStringifiedBitmaskThrows("READ | NONEXISTING",
                "JsonReader: Cannot create bitmask 'test_object.CreatorBitmask' " +
                "from string value 'READ | NONEXISTING'! (JsonParser:3:27)");
        checkReadStringifiedBitmaskThrows("READ * NONEXISTING",
                "JsonReader: Cannot create bitmask 'test_object.CreatorBitmask' " +
                "from string value 'READ * NONEXISTING'! (JsonParser:3:27)");
        checkReadStringifiedBitmask("7 /* READ | WRITE */", new CreatorBitmask((short)7));
        checkReadStringifiedBitmask("15 /* READ | WRITE */", new CreatorBitmask((short)15));
        checkReadStringifiedBitmask("4 /* no match */", new CreatorBitmask((short)4));
        checkReadStringifiedBitmaskThrows("",
                "JsonReader: Cannot create bitmask 'test_object.CreatorBitmask' " +
                "from string value ''! (JsonParser:3:27)");
        checkReadStringifiedBitmaskThrows(" ",
                "JsonReader: Cannot create bitmask 'test_object.CreatorBitmask' " +
                "from string value ' '! (JsonParser:3:27)");
        checkReadStringifiedBitmaskThrows(" | ",
                "JsonReader: Cannot create bitmask 'test_object.CreatorBitmask' " +
                "from string value ' | '! (JsonParser:3:27)");
    }

    @Test
    public void jsonParserException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\"value\"\n\"value\""
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final JsonParserError exception = assertThrows(JsonParserError.class,
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
        assertEquals("ZserioTreeCreator: Field 'nonexisting' not found in " +
                "'test_object.CreatorObject'! (JsonParser:2:16)", exception.getMessage());
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
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
                () -> jsonReader.read(CreatorObject.typeInfo()));
        assertEquals("JsonReader: Cannot create byte for bytes from value '-1'! (JsonParser:5:18)",
                exception.getMessage());
        jsonReader.close();
    }

    private void checkReadStringifiedEnum(String stringValue, CreatorEnum expectedValue) throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"creatorEnum\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        try (final JsonReader jsonReader = new JsonReader(reader))
        {
            final Object zserioObject = jsonReader.read(CreatorObject.typeInfo());
            assertTrue(zserioObject != null);
            assertTrue(zserioObject instanceof CreatorObject);
            final CreatorObject creatorObject = (CreatorObject)zserioObject;

            assertEquals(expectedValue, creatorObject.getNested().getCreatorEnum());
        }
    }

    private void checkReadStringifiedEnumThrows(String stringValue, String expectedMessage) throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"creatorEnum\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(CreatorObject.typeInfo()));
        assertEquals(expectedMessage, exception.getMessage());
        jsonReader.close();
    }

    private void checkReadStringifiedBitmask(String stringValue, CreatorBitmask expectedValue) throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"creatorBitmask\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        try (final JsonReader jsonReader = new JsonReader(reader))
        {
            final Object zserioObject = jsonReader.read(CreatorObject.typeInfo());
            assertTrue(zserioObject != null);
            assertTrue(zserioObject instanceof CreatorObject);
            final CreatorObject creatorObject = (CreatorObject)zserioObject;

            assertEquals(expectedValue, creatorObject.getNested().getCreatorBitmask());
        }
    }

    private void checkReadStringifiedBitmaskThrows(String stringValue, String expectedMessage)
            throws IOException
    {
        final Reader reader = new StringReader(
                "{\n" +
                "    \"nested\": {\n" +
                "        \"creatorBitmask\": \"" + stringValue + "\"\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final ZserioError exception = assertThrows(ZserioError.class,
                () -> jsonReader.read(CreatorObject.typeInfo()));
        assertEquals(expectedMessage, exception.getMessage());
        jsonReader.close();
    }
}
