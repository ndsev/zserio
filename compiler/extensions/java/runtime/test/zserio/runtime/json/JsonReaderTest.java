package zserio.runtime.json;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.creator.ZserioTreeCreatorTestObject;
import zserio.runtime.io.BitBuffer;

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
                "        \"data\": {\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ],\n" +
                "             \"bitSize\": 12\n" +
                "        },\n" +
                "        \"dummyEnum\": 0,\n" +
                "        \"dummyBitmask\": 1\n" +
                "    },\n" +
                "    \"text\": \"test\",\n" +
                "    \"nestedArray\": [\n" +
                "        {\n" +
                "            \"value\": 5,\n" +
                "            \"text\": \"nestedArray\",\n" +
                "            \"data\": {\n" +
                "                 \"buffer\": [\n" +
                "                     202,\n" +
                "                     254\n" +
                "                 ]," +
                "                 \"bitSize\": 15\n" +
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
                "    \"optionalBool\": null\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject = jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo());
        jsonReader.close();
        assertTrue(zserioObject != null);
        assertTrue(zserioObject instanceof ZserioTreeCreatorTestObject.DummyObject);
        final ZserioTreeCreatorTestObject.DummyObject dummyObject =
                (ZserioTreeCreatorTestObject.DummyObject)zserioObject;

        assertEquals(13, dummyObject.getValue());
        assertEquals(13, dummyObject.getNested().getParam());
        assertEquals(10, dummyObject.getNested().getValue());
        assertEquals("nested", dummyObject.getNested().getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12), dummyObject.getNested().getData());
        assertEquals(ZserioTreeCreatorTestObject.DummyEnum.ONE, dummyObject.getNested().getDummyEnum());
        assertEquals(ZserioTreeCreatorTestObject.DummyBitmask.READ, dummyObject.getNested().getDummyBitmask());
        assertEquals("test", dummyObject.getText());
        assertEquals(1, dummyObject.getNestedArray().length);
        assertEquals(5, dummyObject.getNestedArray()[0].getValue());
        assertEquals("nestedArray", dummyObject.getNestedArray()[0].getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 15),
                dummyObject.getNestedArray()[0].getData());
        assertEquals(ZserioTreeCreatorTestObject.DummyEnum.TWO, dummyObject.getNestedArray()[0].getDummyEnum());
        assertEquals(ZserioTreeCreatorTestObject.DummyBitmask.WRITE,
                dummyObject.getNestedArray()[0].getDummyBitmask());
        assertEquals(4, dummyObject.getTextArray().length);
        assertEquals("this", dummyObject.getTextArray()[0]);
        assertEquals("is", dummyObject.getTextArray()[1]);
        assertEquals("text", dummyObject.getTextArray()[2]);
        assertEquals("array", dummyObject.getTextArray()[3]);
        assertEquals(1, dummyObject.getExternArray().length);
        assertEquals(new BitBuffer(new byte[] {(byte)0xDE, (byte)0xD1}, 13), dummyObject.getExternArray()[0]);
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
        final Object zserioObject1 = jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo());
        assertTrue(zserioObject1 != null);
        assertTrue(zserioObject1 instanceof ZserioTreeCreatorTestObject.DummyObject);
        final ZserioTreeCreatorTestObject.DummyObject dummyObject1 =
                (ZserioTreeCreatorTestObject.DummyObject)zserioObject1;

        assertEquals(13, dummyObject1.getValue());
        assertEquals(null, dummyObject1.getText());

        final Object zserioObject2 = jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo());
        assertTrue(zserioObject2 != null);
        assertTrue(zserioObject2 instanceof ZserioTreeCreatorTestObject.DummyObject);
        final ZserioTreeCreatorTestObject.DummyObject dummyObject2 =
                (ZserioTreeCreatorTestObject.DummyObject)zserioObject2;

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
                "        \"data\": {\n" +
                "             \"bitSize\": 12,\n" +
                "             \"buffer\": [\n" +
                "                 203,\n" +
                "                 240\n" +
                "             ]\n" +
                "        },\n" +
                "        \"dummyEnum\": 0,\n" +
                "        \"dummyBitmask\": 1\n" +
                "    }\n" +
                "}"
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final Object zserioObject = jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo());
        assertTrue(zserioObject != null);
        assertTrue(zserioObject instanceof ZserioTreeCreatorTestObject.DummyObject);
        final ZserioTreeCreatorTestObject.DummyObject dummyObject =
                (ZserioTreeCreatorTestObject.DummyObject)zserioObject;

        assertEquals(13, dummyObject.getValue());
        assertEquals(13, dummyObject.getNested().getParam());
        assertEquals(10, dummyObject.getNested().getValue());
        assertEquals("nested", dummyObject.getNested().getText());
        assertEquals(new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12), dummyObject.getNested().getData());
        assertEquals(ZserioTreeCreatorTestObject.DummyEnum.ONE, dummyObject.getNested().getDummyEnum());
        assertEquals(ZserioTreeCreatorTestObject.DummyBitmask.READ, dummyObject.getNested().getDummyBitmask());

        jsonReader.close();
    }

    @Test
    public void jsonParserException() throws IOException
    {
        final Reader reader = new StringReader(
                "{\"value\"\n\"value\""
                );

        final JsonReader jsonReader = new JsonReader(reader);
        final JsonParserError exception = assertThrows(JsonParserError.class,
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
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
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
        assertEquals("ZserioTreeCreator: Field 'nonexisting' not found in 'DummyObject'! (JsonParser:2:16)",
                exception.getMessage());
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
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
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
                "        \"data\": {\n" +
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
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
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
                "        \"data\": {\n" +
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
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
        assertEquals("JsonReader: Unexpected end in Bit Buffer! (JsonParser:12:5)", exception.getMessage());
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
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
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
                () -> jsonReader.read(ZserioTreeCreatorTestObject.DummyObject.typeInfo()));
        assertEquals("JsonReader: ZserioTreeCreator expects json object! (JsonParser:1:1)",
                exception.getMessage());
        jsonReader.close();
    }
}