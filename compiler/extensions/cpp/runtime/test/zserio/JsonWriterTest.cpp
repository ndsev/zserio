#include <sstream>
#include <vector>

#include "gtest/gtest.h"
#include "zserio/JsonWriter.h"
#include "zserio/Reflectable.h"
#include "zserio/TypeInfo.h"

namespace zserio
{

class JsonWriterTest : public ::testing::Test
{
public:
    JsonWriterTest() :
            BOOL_FIELD_INFO{"boolField"_sv, BuiltinTypeInfo<>::getBool(), {}, false, {}, {}, {}, false, {}, {},
                    false, {}, false, false},
            INT8_FIELD_INFO{"int8Field"_sv, BuiltinTypeInfo<>::getInt8(), {}, false, {}, {}, {}, false, {}, {},
                    false, {}, false, false},
            INT16_FIELD_INFO{"int16Field"_sv, BuiltinTypeInfo<>::getInt16(), {}, false, {}, {}, {}, false, {},
                    {}, false, {}, false, false},
            INT32_FIELD_INFO{"int32Field"_sv, BuiltinTypeInfo<>::getInt32(), {}, false, {}, {}, {}, false, {},
                    {}, false, {}, false, false},
            INT64_FIELD_INFO{"int64Field"_sv, BuiltinTypeInfo<>::getInt64(), {}, false, {}, {}, {}, false, {},
                    {}, false, {}, false, false},
            UINT8_FIELD_INFO{"uint8Field"_sv, BuiltinTypeInfo<>::getUInt8(), {}, false, {}, {}, {}, false, {},
                    {}, false, {}, false, false},
            UINT16_FIELD_INFO{"uint16Field"_sv, BuiltinTypeInfo<>::getUInt16(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            UINT32_FIELD_INFO{"uint32Field"_sv, BuiltinTypeInfo<>::getUInt32(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            UINT64_FIELD_INFO{"uint64Field"_sv, BuiltinTypeInfo<>::getUInt64(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            FLOAT_FIELD_INFO{"floatField"_sv, BuiltinTypeInfo<>::getFloat32(), {}, false, {}, {}, {}, false, {},
                    {}, false, {}, false, false},
            DOUBLE_FIELD_INFO{"doubleField"_sv, BuiltinTypeInfo<>::getFloat64(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            BYTES_DATA_FIELD_INFO{"bytesData"_sv, BuiltinTypeInfo<>::getBytes(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            STRING_FIELD_INFO{"stringField"_sv, BuiltinTypeInfo<>::getString(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            EXTERN_DATA_FIELD_INFO{"externData"_sv, BuiltinTypeInfo<>::getBitBuffer(), {}, false, {}, {}, {},
                    false, {}, {}, false, {}, false, false},
            IDENTIFIER_FIELD_INFO{"identifier", BuiltinTypeInfo<>::getUInt32(), {}, false, {}, {}, {}, false,
                    {}, {}, false, {}, false, false},
            DUMMY_TYPE_INFO{"Dummy"_sv, nullptr, {}, {}, {}, {}, {}},
            NESTED_FIELD_INFO{"nested"_sv, DUMMY_TYPE_INFO, {}, false, {}, {}, {}, false, {}, {}, false, {},
                    false, false},
            ARRAY_FIELD_INFO{"array", BuiltinTypeInfo<>::getUInt32(), {}, false, {}, {}, {}, false, {}, {},
                    true, {}, false, false},
            INT8_ENUM_ITEMS{ItemInfo{"ZERO"_sv, static_cast<uint64_t>(0), false, false},
                    ItemInfo{"One"_sv, static_cast<uint64_t>(1), false, false},
                    ItemInfo{"MINUS_ONE"_sv, static_cast<uint64_t>(-1), false, false}},
            INT8_ENUM_TYPE_INFO{"DummyEnum"_sv, BuiltinTypeInfo<>::getInt8(), {}, INT8_ENUM_ITEMS},
            INT8_ENUM_FIELD_INFO{"enumField"_sv, INT8_ENUM_TYPE_INFO, {}, false, {}, {}, {}, false, {}, {},
                    false, {}, false, false},
            UINT8_ENUM_ITEMS{ItemInfo{"ZERO"_sv, static_cast<uint64_t>(0), false, false},
                    ItemInfo{"One"_sv, static_cast<uint64_t>(1), false, false}},
            UINT8_ENUM_TYPE_INFO{"DummyEnum"_sv, BuiltinTypeInfo<>::getUInt8(), {}, UINT8_ENUM_ITEMS},
            UINT8_ENUM_FIELD_INFO{"enumField"_sv, UINT8_ENUM_TYPE_INFO, {}, false, {}, {}, {}, false, {}, {},
                    false, {}, false, false},
            BITMASK_ITEMS{ItemInfo{"ZERO"_sv, static_cast<uint64_t>(0), false, false},
                    ItemInfo{"One"_sv, static_cast<uint64_t>(1), false, false},
                    ItemInfo{"TWO"_sv, static_cast<uint64_t>(2), false, false}},
            BITMASK_TYPE_INFO{"DummyBitmask"_sv, BuiltinTypeInfo<>::getUInt32(), {}, BITMASK_ITEMS},
            BITMASK_FIELD_INFO{"bitmaskField"_sv, BITMASK_TYPE_INFO, {}, false, {}, {}, {}, false, {}, {},
                    false, {}, false, false}
    {}

protected:
    void walkNested(IWalkObserver& observer)
    {
        observer.beginRoot(nullptr);
        observer.visitValue(ReflectableFactory::getUInt32(13), IDENTIFIER_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.beginCompound(nullptr, NESTED_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(ReflectableFactory::getString("test"_sv), STRING_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.endCompound(nullptr, NESTED_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.endRoot(nullptr);
    }

    void walkArray(IWalkObserver& observer)
    {
        observer.beginRoot(nullptr);
        observer.beginArray(nullptr, ARRAY_FIELD_INFO);
        observer.visitValue(ReflectableFactory::getUInt32(1), ARRAY_FIELD_INFO, 0);
        observer.visitValue(ReflectableFactory::getUInt32(2), ARRAY_FIELD_INFO, 1);
        observer.endArray(nullptr, ARRAY_FIELD_INFO);
        observer.endRoot(nullptr);
    }

    const FieldInfo BOOL_FIELD_INFO;
    const FieldInfo INT8_FIELD_INFO;
    const FieldInfo INT16_FIELD_INFO;
    const FieldInfo INT32_FIELD_INFO;
    const FieldInfo INT64_FIELD_INFO;
    const FieldInfo UINT8_FIELD_INFO;
    const FieldInfo UINT16_FIELD_INFO;
    const FieldInfo UINT32_FIELD_INFO;
    const FieldInfo UINT64_FIELD_INFO;
    const FieldInfo FLOAT_FIELD_INFO;
    const FieldInfo DOUBLE_FIELD_INFO;
    const FieldInfo BYTES_DATA_FIELD_INFO;
    const FieldInfo STRING_FIELD_INFO;
    const FieldInfo EXTERN_DATA_FIELD_INFO;
    const FieldInfo IDENTIFIER_FIELD_INFO;
    const StructTypeInfo<std::allocator<uint8_t>> DUMMY_TYPE_INFO;
    const FieldInfo NESTED_FIELD_INFO;
    const FieldInfo ARRAY_FIELD_INFO;
    const std::array<ItemInfo, 3> INT8_ENUM_ITEMS;
    const EnumTypeInfo<std::allocator<uint8_t>> INT8_ENUM_TYPE_INFO;
    const FieldInfo INT8_ENUM_FIELD_INFO;
    const std::array<ItemInfo, 2> UINT8_ENUM_ITEMS;
    const EnumTypeInfo<std::allocator<uint8_t>> UINT8_ENUM_TYPE_INFO;
    const FieldInfo UINT8_ENUM_FIELD_INFO;
    const std::array<ItemInfo, 3> BITMASK_ITEMS;
    const BitmaskTypeInfo<std::allocator<uint8_t>> BITMASK_TYPE_INFO;
    const FieldInfo BITMASK_FIELD_INFO;
};

TEST_F(JsonWriterTest, empty)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);

    ASSERT_EQ("", os.str());
}

TEST_F(JsonWriterTest, nullValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(nullptr, STRING_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"stringField\": null", os.str());
}

TEST_F(JsonWriterTest, boolValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getBool(true), BOOL_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"boolField\": true", os.str());
}

TEST_F(JsonWriterTest, int8Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getInt8(INT8_MIN), INT8_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"int8Field\": " + std::to_string(INT8_MIN), os.str());
}

TEST_F(JsonWriterTest, int16Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getInt16(INT16_MIN), INT16_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"int16Field\": " + std::to_string(INT16_MIN), os.str());
}

TEST_F(JsonWriterTest, int32Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getInt32(INT32_MIN), INT32_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"int32Field\": " + std::to_string(INT32_MIN), os.str());
}

TEST_F(JsonWriterTest, int64Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getInt64(INT64_MIN), INT64_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"int64Field\": " + std::to_string(INT64_MIN), os.str());
}

TEST_F(JsonWriterTest, uint8Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getUInt8(UINT8_MAX), UINT8_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"uint8Field\": " + std::to_string(UINT8_MAX), os.str());
}

TEST_F(JsonWriterTest, uint16Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getUInt16(UINT16_MAX), UINT16_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"uint16Field\": " + std::to_string(UINT16_MAX), os.str());
}

TEST_F(JsonWriterTest, uint32Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getUInt32(UINT32_MAX), UINT32_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"uint32Field\": " + std::to_string(UINT32_MAX), os.str());
}

TEST_F(JsonWriterTest, uint64Value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getUInt64(UINT64_MAX), UINT64_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"uint64Field\": " + std::to_string(UINT64_MAX), os.str());
}

TEST_F(JsonWriterTest, floatValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getFloat32(3.5F), FLOAT_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"floatField\": 3.5", os.str());
}

TEST_F(JsonWriterTest, doubleValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getFloat64(9.875), DOUBLE_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"doubleField\": 9.875", os.str());
}

TEST_F(JsonWriterTest, bytesValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    vector<uint8_t> bytesData{{0xCA, 0xFE}};
    observer.visitValue(ReflectableFactory::getBytes(bytesData), BYTES_DATA_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"bytesData\": {\"buffer\": [202, 254]}", os.str());
}

TEST_F(JsonWriterTest, stringValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getString("test"_sv), STRING_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"stringField\": \"test\"", os.str());
}

TEST_F(JsonWriterTest, externValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    BitBuffer bitBuffer({0xFF, 0x1F}, 13);
    observer.visitValue(
            ReflectableFactory::getBitBuffer(bitBuffer), EXTERN_DATA_FIELD_INFO, WALKER_NOT_ELEMENT);

    // note that this is not valid json
    ASSERT_EQ("\"externData\": {\"buffer\": [255, 31], \"bitSize\": 13}", os.str());
}

TEST_F(JsonWriterTest, signedEnumValue)
{
    class DummyEnumReflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        explicit DummyEnumReflectable(int8_t value, const EnumTypeInfo<std::allocator<uint8_t>>& typeInfo) :
                ReflectableBase<std::allocator<uint8_t>>(typeInfo),
                m_value(value)
        {}

        int64_t toInt() const override
        {
            return m_value;
        }

    private:
        int8_t m_value;
    };

    IReflectablePtr reflectableZero = std::make_shared<DummyEnumReflectable>(0, INT8_ENUM_TYPE_INFO);
    IReflectablePtr reflectableOne = std::make_shared<DummyEnumReflectable>(1, INT8_ENUM_TYPE_INFO);
    IReflectablePtr reflectableTwo = std::make_shared<DummyEnumReflectable>(2, INT8_ENUM_TYPE_INFO);
    IReflectablePtr reflectableMinusOne = std::make_shared<DummyEnumReflectable>(-1, INT8_ENUM_TYPE_INFO);

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableOne, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableTwo, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableMinusOne, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);

        // note that this is not valid json
        ASSERT_EQ("\"enumField\": \"ZERO\", "
                  "\"enumField\": \"One\", "
                  "\"enumField\": \"2 /* no match */\", "
                  "\"enumField\": \"MINUS_ONE\"",
                os.str());
    }

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        jsonWriter.setEnumerableFormat(JsonWriter::EnumerableFormat::NUMBER);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableTwo, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableMinusOne, INT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);

        // note that this is not valid json
        ASSERT_EQ("\"enumField\": 0, \"enumField\": 2, \"enumField\": -1", os.str());
    }
}

TEST_F(JsonWriterTest, unsignedEnumValue)
{
    class DummyEnumReflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        explicit DummyEnumReflectable(uint8_t value, const EnumTypeInfo<std::allocator<uint8_t>>& typeInfo) :
                ReflectableBase<std::allocator<uint8_t>>(typeInfo),
                m_value(value)
        {}

        uint64_t toUInt() const override
        {
            return m_value;
        }

    private:
        uint8_t m_value;
    };

    IReflectablePtr reflectableZero = std::make_shared<DummyEnumReflectable>(0, UINT8_ENUM_TYPE_INFO);
    IReflectablePtr reflectableOne = std::make_shared<DummyEnumReflectable>(1, UINT8_ENUM_TYPE_INFO);
    IReflectablePtr reflectableTwo = std::make_shared<DummyEnumReflectable>(2, UINT8_ENUM_TYPE_INFO);

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, UINT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableOne, UINT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableTwo, UINT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);

        // note that this is not valid json
        ASSERT_EQ("\"enumField\": \"ZERO\", "
                  "\"enumField\": \"One\", "
                  "\"enumField\": \"2 /* no match */\"",
                os.str());
    }

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        jsonWriter.setEnumerableFormat(JsonWriter::EnumerableFormat::NUMBER);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, UINT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableTwo, UINT8_ENUM_FIELD_INFO, WALKER_NOT_ELEMENT);

        // note that this is not valid json
        ASSERT_EQ("\"enumField\": 0, \"enumField\": 2", os.str());
    }
}

TEST_F(JsonWriterTest, bitmaskValue)
{
    class DummyBitmaskReflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        explicit DummyBitmaskReflectable(
                uint8_t value, const BitmaskTypeInfo<std::allocator<uint8_t>>& typeInfo) :
                ReflectableBase<std::allocator<uint8_t>>(typeInfo),
                m_value(value)
        {}

        uint64_t toUInt() const override
        {
            return m_value;
        }

    private:
        uint8_t m_value;
    };

    IReflectablePtr reflectableZero = std::make_shared<DummyBitmaskReflectable>(0, BITMASK_TYPE_INFO);
    IReflectablePtr reflectableTwo = std::make_shared<DummyBitmaskReflectable>(2, BITMASK_TYPE_INFO);
    IReflectablePtr reflectableThree = std::make_shared<DummyBitmaskReflectable>(3, BITMASK_TYPE_INFO);
    IReflectablePtr reflectableFour = std::make_shared<DummyBitmaskReflectable>(4, BITMASK_TYPE_INFO);
    IReflectablePtr reflectableSeven = std::make_shared<DummyBitmaskReflectable>(7, BITMASK_TYPE_INFO);

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableTwo, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableThree, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableFour, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableSeven, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);

        // note that this is not valid json
        ASSERT_EQ("\"bitmaskField\": \"ZERO\", "
                  "\"bitmaskField\": \"TWO\", "
                  "\"bitmaskField\": \"One | TWO\", "
                  "\"bitmaskField\": \"4 /* no match */\", "
                  "\"bitmaskField\": \"7 /* partial match: One | TWO */\"",
                os.str());
    }

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        jsonWriter.setEnumerableFormat(JsonWriter::EnumerableFormat::NUMBER);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);
        observer.visitValue(reflectableSeven, BITMASK_FIELD_INFO, WALKER_NOT_ELEMENT);

        // note that this is not valid json
        ASSERT_EQ("\"bitmaskField\": 0, \"bitmaskField\": 7", os.str());
    }
}

TEST_F(JsonWriterTest, unexpectedValue)
{
    using allocator_type = ::std::allocator<uint8_t>;

    const StructTypeInfo<allocator_type> structTypeInfo("Struct"_sv, nullptr, ""_sv, {}, {}, {}, {});
    class Reflectable : public ::zserio::ReflectableAllocatorHolderBase<allocator_type>
    {
    public:
        explicit Reflectable(
                const StructTypeInfo<allocator_type>& structTypeInfo, const allocator_type& allocator) :
                ::zserio::ReflectableAllocatorHolderBase<allocator_type>(structTypeInfo, allocator)
        {}
    };
    auto reflectable = std::allocate_shared<Reflectable>(allocator_type(), structTypeInfo, allocator_type());

    ASSERT_THROW(
            {
                try
                {
                    std::ostringstream os;
                    JsonWriter jsonWriter(os);
                    IWalkObserver& observer = jsonWriter;
                    observer.visitValue(reflectable, BOOL_FIELD_INFO, WALKER_NOT_ELEMENT);
                }
                catch (const CppRuntimeException& e)
                {
                    ASSERT_STREQ("JsonWriter: Unexpected not-null value of type 'Struct'!", e.what());
                    throw;
                }
            },
            CppRuntimeException);
}

TEST_F(JsonWriterTest, compound)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.beginRoot(nullptr);
    observer.visitValue(ReflectableFactory::getUInt32(13), IDENTIFIER_FIELD_INFO, WALKER_NOT_ELEMENT);
    observer.visitValue(ReflectableFactory::getString("test"_sv), STRING_FIELD_INFO, WALKER_NOT_ELEMENT);
    BitBuffer bitBuffer({0xFF, 0x1F}, 13);
    observer.visitValue(
            ReflectableFactory::getBitBuffer(bitBuffer), EXTERN_DATA_FIELD_INFO, WALKER_NOT_ELEMENT);
    vector<uint8_t> bytesData{{0xCA, 0xFE}};
    observer.visitValue(ReflectableFactory::getBytes(bytesData), BYTES_DATA_FIELD_INFO, WALKER_NOT_ELEMENT);
    observer.endRoot(nullptr);

    ASSERT_EQ("{\"identifier\": 13, \"stringField\": \"test\", "
              "\"externData\": {\"buffer\": [255, 31], \"bitSize\": 13}, "
              "\"bytesData\": {\"buffer\": [202, 254]}}",
            os.str());
}

TEST_F(JsonWriterTest, compoundArray)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.beginRoot(nullptr);
    observer.beginArray(nullptr, ARRAY_FIELD_INFO);

    observer.beginCompound(nullptr, NESTED_FIELD_INFO, 0);
    observer.visitValue(ReflectableFactory::getString("test"_sv), STRING_FIELD_INFO, WALKER_NOT_ELEMENT);
    observer.endCompound(nullptr, NESTED_FIELD_INFO, 0);

    observer.endArray(nullptr, ARRAY_FIELD_INFO);
    observer.endRoot(nullptr);

    ASSERT_EQ("{\"array\": [{\"stringField\": \"test\"}]}", os.str());
}

TEST_F(JsonWriterTest, nestedCompound)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);

    walkNested(jsonWriter);

    ASSERT_EQ("{\"identifier\": 13, \"nested\": {\"stringField\": \"test\"}}", os.str());
}

TEST_F(JsonWriterTest, array)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);

    walkArray(jsonWriter);

    ASSERT_EQ("{\"array\": [1, 2]}", os.str());
}

TEST_F(JsonWriterTest, arrayWithIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, 2);

    walkArray(jsonWriter);

    ASSERT_EQ("{\n  \"array\": [\n    1,\n    2\n  ]\n}", os.str());
}

TEST_F(JsonWriterTest, emptyIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, "");

    walkNested(jsonWriter);

    ASSERT_EQ("{\n\"identifier\": 13,\n\"nested\": {\n\"stringField\": \"test\"\n}\n}", os.str());
}

TEST_F(JsonWriterTest, strIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, "  ");

    walkNested(jsonWriter);

    ASSERT_EQ("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"stringField\": \"test\"\n  }\n}", os.str());
}

TEST_F(JsonWriterTest, intIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, 2);

    walkNested(jsonWriter);

    ASSERT_EQ("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"stringField\": \"test\"\n  }\n}", os.str());
}

TEST_F(JsonWriterTest, compactSeparators)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    jsonWriter.setItemSeparator(",");
    jsonWriter.setKeySeparator(":");

    walkNested(jsonWriter);

    ASSERT_EQ("{\"identifier\":13,\"nested\":{\"stringField\":\"test\"}}", os.str());
}

} // namespace zserio
