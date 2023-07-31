package zserio.runtime.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioBitmask;
import zserio.runtime.ZserioEnum;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.FunctionInfo;
import zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.EnumTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.BitmaskTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.StructTypeInfo;
import zserio.runtime.typeinfo.ItemInfo;
import zserio.runtime.typeinfo.ParameterInfo;
import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.walker.WalkObserver;
import zserio.runtime.walker.WalkerConst;

public class JsonWriterTest
{
    @Test
    public void empty()
    {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.close();

        assertJsonEquals("", stringWriter.toString());
    }

    @Test
    public void nullValue()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(null, TEXT_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"text\": null", stringWriter.toString());
        }
    }

    @Test
    public void textValue()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue("test", TEXT_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"text\": \"test\"", stringWriter.toString());
        }
    }

    @Test
    public void boolValue()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(true, BOOL_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"boolField\": true", stringWriter.toString());
        }
    }

    @Test
    public void int32Value()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(Integer.MIN_VALUE, INT32_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"int32Field\": " + Integer.toString(Integer.MIN_VALUE), stringWriter.toString());
        }
    }

    @Test
    public void uint64Value()
    {
        // test BigInteger
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            final BigInteger uint64Max = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
            jsonWriter.visitValue(uint64Max, UINT64_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"uint64Field\": " + uint64Max.toString(), stringWriter.toString());
        }
    }

    @Test
    public void floatValue()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(3.5f, FLOAT_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"floatField\": 3.5", stringWriter.toString());
        }
    }

    @Test
    public void doubleValue()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(9.875, DOUBLE_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertJsonEquals("\"doubleField\": 9.875", stringWriter.toString());
        }
    }

    @Test
    public void enumValue()
    {
        class DummyEnum implements ZserioEnum
        {
            public DummyEnum(byte value)
            {
                this.value = value;
            }

            @Override
            public Number getGenericValue()
            {
                return value;
            }

            private final byte value;
        }

        {
            final StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.visitValue(new DummyEnum((byte)0), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyEnum((byte)1), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyEnum((byte)2), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyEnum((byte)-1), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals(
                        "\"enumField\": \"ZERO\", " +
                        "\"enumField\": \"One\", " +
                        "\"enumField\": \"2 /* no match */\", " +
                        "\"enumField\": \"MINUS_ONE\"", stringWriter.toString());
            }
        }

        {
            final StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.setEnumerableFormat(JsonWriter.EnumerableFormat.NUMBER);
                jsonWriter.visitValue(new DummyEnum((byte)0), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyEnum((byte)2), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyEnum((byte)-1), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals("\"enumField\": 0, \"enumField\": 2, \"enumField\": -1",
                        stringWriter.toString());
            }
        }
    }

    @Test
    public void enum64Value()
    {
        final BigInteger uint64Max = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

        class UInt64MaxDummyEnum implements ZserioEnum
        {
            @Override
            public Number getGenericValue()
            {
                return uint64Max;
            }
        }

        // test BigInteger
        {
            StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.visitValue(new UInt64MaxDummyEnum(), ENUM64_FIELD_INFO, WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals("\"enum64Field\": \"UINT64_MAX\"", stringWriter.toString());
            }
        }

        {
            StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.setEnumerableFormat(JsonWriter.EnumerableFormat.NUMBER);
                jsonWriter.visitValue(new UInt64MaxDummyEnum(), ENUM64_FIELD_INFO, WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals("\"enum64Field\": " + uint64Max.toString(), stringWriter.toString());
            }
        }
    }

    @Test
    public void bitmaskValue()
    {
        class DummyBitmask implements ZserioBitmask
        {
            public DummyBitmask(short value)
            {
                this.value = value;
            }

            @Override
            public Number getGenericValue()
            {
                return value;
            }

            private final short value;
        }

        {
            StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.visitValue(new DummyBitmask((short)0), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyBitmask((short)2), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyBitmask((short)3), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyBitmask((short)4), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyBitmask((short)7), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals(
                        "\"bitmaskField\": \"ZERO\", " +
                        "\"bitmaskField\": \"TWO\", " +
                        "\"bitmaskField\": \"One | TWO\", " +
                        "\"bitmaskField\": \"4 /* no match */\", " +
                        "\"bitmaskField\": \"7 /* partial match: One | TWO */\"", stringWriter.toString());
            }
        }

        {
            StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.setEnumerableFormat(JsonWriter.EnumerableFormat.NUMBER);
                jsonWriter.visitValue(new DummyBitmask((short)0), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);
                jsonWriter.visitValue(new DummyBitmask((short)7), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals("\"bitmaskField\": 0, \"bitmaskField\": 7", stringWriter.toString());
            }
        }
    }

    @Test
    public void bitmask64Value()
    {
        final BigInteger uint64Max = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

        class UInt64MaxDummyBitmask implements ZserioBitmask
        {
            @Override
            public Number getGenericValue()
            {
                return uint64Max;
            }
        }

        {
            StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.visitValue(new UInt64MaxDummyBitmask(), BITMASK64_FIELD_INFO,
                        WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals("\"bitmask64Field\": \"UINT64_MAX\"", stringWriter.toString());
            }
        }

        {
            StringWriter stringWriter = new StringWriter();
            try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
            {
                jsonWriter.setEnumerableFormat(JsonWriter.EnumerableFormat.NUMBER);
                jsonWriter.visitValue(new UInt64MaxDummyBitmask(), BITMASK64_FIELD_INFO,
                        WalkerConst.NOT_ELEMENT);

                // note that this is not valid json
                assertJsonEquals("\"bitmask64Field\": " + uint64Max.toString(), stringWriter.toString());
            }
        }
    }

    @Test
    public void compound()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.beginRoot(null);
            jsonWriter.visitValue(13, IDENTIFIER_FIELD_INFO, WalkerConst.NOT_ELEMENT);
            jsonWriter.visitValue("test", TEXT_FIELD_INFO, WalkerConst.NOT_ELEMENT);
            jsonWriter.visitValue(new BitBuffer(new byte[] {(byte)0xFF, 0x1F}, (long)13), DATA_FIELD_INFO,
                    WalkerConst.NOT_ELEMENT);
            jsonWriter.visitValue(new byte[] {(byte)0xCA, (byte)0xFE}, BYTES_DATA_FIELD_INFO,
                    WalkerConst.NOT_ELEMENT);
            jsonWriter.endRoot(null);

            assertJsonEquals("{\"identifier\": 13, \"text\": \"test\", " +
                    "\"data\": {\"buffer\": [255, 31], \"bitSize\": 13}, " +
                    "\"bytesData\": {\"buffer\": [202, 254]}}", stringWriter.toString());
        }
    }

    @Test
    public void nestedCompound()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            walkNested(jsonWriter);

            assertJsonEquals("{\"identifier\": 13, \"nested\": {\"text\": \"test\"}}", stringWriter.toString());
        }
    }

    @Test
    public void array()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            walkArray(jsonWriter);

            assertJsonEquals("{\"array\": [1, 2]}", stringWriter.toString());
        }
    }

    @Test
    public void arrayWithIndent()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter, 2))
        {
            walkArray(jsonWriter);

            assertJsonEquals("{\n  \"array\": [\n    1,\n    2\n  ]\n}", stringWriter.toString());
        }
    }

    @Test
    public void emptyIndent()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter, ""))
        {
            walkNested(jsonWriter);

            assertJsonEquals("{\n\"identifier\": 13,\n\"nested\": {\n\"text\": \"test\"\n}\n}",
                    stringWriter.toString());
        }
    }

    @Test
    public void strIndent()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter, "  "))
        {
            walkNested(jsonWriter);

            assertJsonEquals("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}",
                    stringWriter.toString());
        }
    }

    @Test
    public void intIndent()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter, 2))
        {
            walkNested(jsonWriter);

            assertJsonEquals("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}",
                    stringWriter.toString());
        }
    }

    @Test
    public void compactSeparators()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.setItemSeparator(",");
            jsonWriter.setKeySeparator(":");

            walkNested(jsonWriter);

            assertJsonEquals("{\"identifier\":13,\"nested\":{\"text\":\"test\"}}", stringWriter.toString());
        }
    }

    private void assertJsonEquals(String expectedJson, String providedJson)
    {
        assertEquals(expectedJson.replaceAll("\n", System.lineSeparator()), providedJson);
    }

    private void walkNested(WalkObserver observer)
    {
        observer.beginRoot(null);
        observer.visitValue(13, IDENTIFIER_FIELD_INFO, WalkerConst.NOT_ELEMENT);
        observer.beginCompound(null, NESTED_FIELD_INFO, WalkerConst.NOT_ELEMENT);
        observer.visitValue("test", TEXT_FIELD_INFO, WalkerConst.NOT_ELEMENT);
        observer.endCompound(null, NESTED_FIELD_INFO, WalkerConst.NOT_ELEMENT);
        observer.endRoot(null);
    }

    private void walkArray(WalkObserver observer)
    {
        observer.beginRoot(null);
        observer.beginArray(null, ARRAY_FIELD_INFO);
        observer.visitValue(1, ARRAY_FIELD_INFO, 0);
        observer.visitValue(2, ARRAY_FIELD_INFO, 1);
        observer.endArray(null, ARRAY_FIELD_INFO);
        observer.endRoot(null);
    }

    private static final FieldInfo TEXT_FIELD_INFO = new FieldInfo(
            "text", // schemaName
            "getText", // getterName
            "setText", // setterName
            BuiltinTypeInfo.getString(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo BOOL_FIELD_INFO = new FieldInfo(
            "boolField", // schemaName
            "getBoolField", // getterName
            "setBoolField", // setterName
            BuiltinTypeInfo.getBool(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo INT32_FIELD_INFO = new FieldInfo(
            "int32Field", // schemaName
            "getInt32Field", // getterName
            "setInt32Field", // setterName
            BuiltinTypeInfo.getInt32(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo UINT64_FIELD_INFO = new FieldInfo(
            "uint64Field", // schemaName
            "getUint64Field", // getterName
            "setUint64Field", // setterName
            BuiltinTypeInfo.getUInt64(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo FLOAT_FIELD_INFO = new FieldInfo(
            "floatField", // schemaName
            "getFloatField", // getterName
            "setFloatField", // setterName
            BuiltinTypeInfo.getFloat32(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo DOUBLE_FIELD_INFO = new FieldInfo(
            "doubleField", // schemaName
            "getDoubleField", // getterName
            "setDoubleField", // setterName
            BuiltinTypeInfo.getFloat64(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static class DummyEnumForClass implements ZserioEnum
    {
        @Override
        public Number getGenericValue()
        {
            return 0;
        }
    }

    private static final FieldInfo ENUM_FIELD_INFO = new FieldInfo(
            "enumField", // schemaName
            "getEnumField", // getterName
            "setEnumField", // setterName
            new EnumTypeInfo(
                    "DummyEnum", // schemaName
                    DummyEnumForClass.class, // javaClass
                    BuiltinTypeInfo.getInt8(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList( // enumItems
                            new ItemInfo("ZERO", BigInteger.valueOf(0)),
                            new ItemInfo("One", BigInteger.valueOf(1)),
                            new ItemInfo("MINUS_ONE", BigInteger.valueOf(-1))
                    )
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo ENUM64_FIELD_INFO = new FieldInfo(
            "enum64Field", // schemaName
            "getEnum64Field", // getterName
            "setEnum64Field", // setterName
            new EnumTypeInfo(
                    "DummyEnum", // schemaName
                    DummyEnumForClass.class, // javaClass
                    BuiltinTypeInfo.getUInt64(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList( // enumItems
                            new ItemInfo("UINT64_MAX", new BigInteger("18446744073709551615")))
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static class DummyBitmaskForClass implements ZserioBitmask
    {
        @Override
        public Number getGenericValue()
        {
            return 0;
        }
    }

    private static final FieldInfo BITMASK_FIELD_INFO = new FieldInfo(
            "bitmaskField", // schemaName
            "getBitmaskField", // getterName
            "setBitmaskField", // setterName
            new BitmaskTypeInfo(
                    "DummyBitmask", // schemaName
                    DummyBitmaskForClass.class, // javaClass
                    BuiltinTypeInfo.getInt8(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList( // bitmaskValues
                            new ItemInfo("ZERO", BigInteger.valueOf(0)),
                            new ItemInfo("One", BigInteger.valueOf(1)),
                            new ItemInfo("TWO", BigInteger.valueOf(2))
                    )
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo BITMASK64_FIELD_INFO = new FieldInfo(
            "bitmask64Field", // schemaName
            "getBitmask64Field", // getterName
            "setBitmask64Field", // setterName
            new BitmaskTypeInfo(
                    "DummyBitmask", // schemaName
                    DummyBitmaskForClass.class, // javaClass
                    BuiltinTypeInfo.getUInt64(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList( // bitmaskValues
                            new ItemInfo("UINT64_MAX", new BigInteger("18446744073709551615")))
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo IDENTIFIER_FIELD_INFO = new FieldInfo(
            "identifier", // schemaName
            "getIdentifier", // getterName
            "setIdentifier", // setterName
            BuiltinTypeInfo.getUInt32(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo DATA_FIELD_INFO = new FieldInfo(
            "data", // schemaName
            "getData", // getterName
            "setData", // setterName
            BuiltinTypeInfo.getBitBuffer(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo BYTES_DATA_FIELD_INFO = new FieldInfo(
            "bytesData", // schemaName
            "getBytesData", // getterName
            "setBytesData", // setterName
            BuiltinTypeInfo.getBytes(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static class Dummy
    {
    }

    private static final TypeInfo DUMMY_TYPE_INFO = new StructTypeInfo(
            "Dummy", // schemaName
            Dummy.class, // javaClass
            "", // templateName
            new ArrayList<TypeInfo>(), // templateArguments
            new ArrayList<FieldInfo>(), // fields
            new ArrayList<ParameterInfo>(), // parameters
            new ArrayList<FunctionInfo>() // functions
    );

    private static final FieldInfo NESTED_FIELD_INFO = new FieldInfo(
            "nested", // schemaName
            "getNested", // getterName
            "setNested", // setterName
            DUMMY_TYPE_INFO, // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            false, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );

    private static final FieldInfo ARRAY_FIELD_INFO = new FieldInfo(
            "array", // schemaName
            "getArray", // getterName
            "setArray", // setterName
            BuiltinTypeInfo.getUInt32(), // typeInfo
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
            false, // isExtended
            null, // alignment
            null, // offset
            null, // initializer
            false, // isOptional
            null, // optionalCondition
            "", // isUsedindicatorName
            "", // isSetindicatorName
            null, // constraint
            true, // isArray
            null, // arrayLength
            false, // isPacked
            false // isImplicit
    );
}
