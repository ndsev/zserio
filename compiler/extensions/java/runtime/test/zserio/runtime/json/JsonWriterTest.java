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

        assertEquals("", stringWriter.toString());
    }

    @Test
    public void nullValue()
    {
        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(null, TEXT_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertEquals("\"text\": null", stringWriter.toString());
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
            assertEquals("\"text\": \"test\"", stringWriter.toString());
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
            assertEquals("\"boolField\": true", stringWriter.toString());
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
            assertEquals("\"int32Field\": " + Integer.toString(Integer.MIN_VALUE), stringWriter.toString());
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
            assertEquals("\"uint64Field\": " + uint64Max.toString(), stringWriter.toString());
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
            assertEquals("\"floatField\": 3.5", stringWriter.toString());
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
            assertEquals("\"doubleField\": 9.875", stringWriter.toString());
        }
    }

    @Test
    public void enumValue()
    {
        class ZeroDummyEnum implements ZserioEnum
        {
            @Override
            public Number getGenericValue()
            {
                return 0;
            }
        }

        final StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(new ZeroDummyEnum(), ENUM_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertEquals("\"enumField\": 0", stringWriter.toString());
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
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(new UInt64MaxDummyEnum(), ENUM64_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertEquals("\"enum64Field\": " + uint64Max.toString(), stringWriter.toString());
        }
    }

    @Test
    public void bitmaskValue()
    {
        class ZeroDummyBitmask implements ZserioBitmask
        {
            @Override
            public Number getGenericValue()
            {
                return 0;
            }
        }

        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(new ZeroDummyBitmask(), BITMASK_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertEquals("\"bitmaskField\": 0", stringWriter.toString());
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

        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            jsonWriter.visitValue(new UInt64MaxDummyBitmask(), BITMASK64_FIELD_INFO, WalkerConst.NOT_ELEMENT);

            // note that this is not valid json
            assertEquals("\"bitmask64Field\": " + uint64Max.toString(), stringWriter.toString());
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
            jsonWriter.endRoot(null);

            assertEquals("{\"identifier\": 13, \"text\": \"test\", \"data\": " +
                    "{\"buffer\": [255, 31], \"bitSize\": 13}}", stringWriter.toString());
        }
    }

    @Test
    public void nestedCompound()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            walkNested(jsonWriter);

            assertEquals("{\"identifier\": 13, \"nested\": {\"text\": \"test\"}}", stringWriter.toString());
        }
    }

    @Test
    public void array()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter))
        {
            walkArray(jsonWriter);

            assertEquals("{\"array\": [1, 2]}", stringWriter.toString());
        }
    }

    @Test
    public void arrayWithIndnet()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter, 2))
        {
            walkArray(jsonWriter);

            assertEquals("{\n  \"array\": [\n    1,\n    2\n  ]\n}", stringWriter.toString());
        }
    }

    @Test
    public void emptyIndent()
    {
        StringWriter stringWriter = new StringWriter();
        try (final JsonWriter jsonWriter = new JsonWriter(stringWriter, ""))
        {
            walkNested(jsonWriter);

            assertEquals("{\n\"identifier\": 13,\n\"nested\": {\n\"text\": \"test\"\n}\n}",
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

            assertEquals("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}",
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

            assertEquals("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}",
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

            assertEquals("{\"identifier\":13,\"nested\":{\"text\":\"test\"}}", stringWriter.toString());
        }
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

    private static class DummyEnum implements ZserioEnum
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
                    DummyEnum.class, // javaClass
                    BuiltinTypeInfo.getInt8(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList(new ItemInfo("ZERO", "0")) // enumItems
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
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
                    DummyEnum.class, // javaClass
                    BuiltinTypeInfo.getUInt64(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList(new ItemInfo("UINT64_MAX", "18446744073709551615")) // enumItems
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
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

    private static class DummyBitmask implements ZserioBitmask
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
                    DummyBitmask.class, // javaClass
                    BuiltinTypeInfo.getInt8(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList(new ItemInfo("ZERO", "0")) // bitmaskValues
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
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
                    DummyBitmask.class, // javaClass
                    BuiltinTypeInfo.getUInt64(), // underlyingType
                    new ArrayList<java.util.function.Supplier<Object>>(), // underlyingTypeArguments
                    Arrays.asList(new ItemInfo("UINT64_MAX", "18446744073709551615")) // bitmaskValues
            ),
            new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
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
