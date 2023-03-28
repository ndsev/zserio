#include "gtest/gtest.h"

#include <sstream>

#include "zserio/JsonWriter.h"
#include "zserio/TypeInfo.h"
#include "zserio/Reflectable.h"

namespace zserio
{

namespace
{

const FieldInfo BOOL_FIELD_INFO{
    "boolField"_sv, BuiltinTypeInfo<>::getBool(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo INT_FIELD_INFO{
    "intField"_sv, BuiltinTypeInfo<>::getInt32(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo FLOAT_FIELD_INFO{
    "floatField"_sv, BuiltinTypeInfo<>::getFloat32(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo DOUBLE_FIELD_INFO{
    "doubleField"_sv, BuiltinTypeInfo<>::getFloat64(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo TEXT_FIELD_INFO{
    "text"_sv, BuiltinTypeInfo<>::getString(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo IDENTIFIER_FIELD_INFO{
    "identifier", BuiltinTypeInfo<>::getUInt32(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo EXTERN_DATA_FIELD_INFO{
    "externData"_sv, BuiltinTypeInfo<>::getBitBuffer(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo BYTES_DATA_FIELD_INFO{
    "bytesData"_sv, BuiltinTypeInfo<>::getBytes(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const StructTypeInfo<std::allocator<uint8_t>> DUMMY_TYPE_INFO{
    "Dummy"_sv, nullptr, {}, {}, {}, {}, {}
};

const FieldInfo NESTED_FIELD_INFO{
    "nested"_sv, DUMMY_TYPE_INFO,
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo ARRAY_FIELD_INFO{
    "array", BuiltinTypeInfo<>::getUInt32(),
    {}, {}, {}, {}, false, {}, {}, true, {}, false, false
};

const std::array<ItemInfo, 3> ENUM_ITEMS{
    ItemInfo{"ZERO"_sv, static_cast<uint64_t>(0)},
    ItemInfo{"One"_sv, static_cast<uint64_t>(1)},
    ItemInfo{"MINUS_ONE"_sv, static_cast<uint64_t>(-1)}
};

const EnumTypeInfo<std::allocator<uint8_t>> ENUM_TYPE_INFO{
    "DummyEnum"_sv, BuiltinTypeInfo<>::getInt8(), {}, ENUM_ITEMS
};

const FieldInfo ENUM_FIELD_INFO{
    "enumField"_sv, ENUM_TYPE_INFO,
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const std::array<ItemInfo, 3> BITMASK_ITEMS{
    ItemInfo{"ZERO"_sv, static_cast<uint64_t>(0)},
    ItemInfo{"One"_sv, static_cast<uint64_t>(1)},
    ItemInfo{"TWO"_sv, static_cast<uint64_t>(2)}
};

const BitmaskTypeInfo<std::allocator<uint8_t>> BITMASK_TYPE_INFO{
    "DummyBitmask"_sv, BuiltinTypeInfo<>::getUInt32(), {}, BITMASK_ITEMS
};

const FieldInfo BITMASK_FIELD_INFO{
    "bitmaskField"_sv, BITMASK_TYPE_INFO,
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

void walkNested(IWalkObserver& observer)
{
    observer.beginRoot(nullptr);
    observer.visitValue(ReflectableFactory::getUInt32(13), IDENTIFIER_FIELD_INFO);
    observer.beginCompound(nullptr, NESTED_FIELD_INFO);
    observer.visitValue(ReflectableFactory::getString("test"_sv), TEXT_FIELD_INFO);
    observer.endCompound(nullptr, NESTED_FIELD_INFO);
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

} // namespace

TEST(JsonWriterTest, empty)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);

    ASSERT_EQ("", os.str());
}

TEST(JsonWriterTest, nullValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(nullptr, TEXT_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"text\": null", os.str());
}

TEST(JsonWriterTest, textValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getString("test"_sv), TEXT_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"text\": \"test\"", os.str());
}

TEST(JsonWriterTest, boolValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getBool(true), BOOL_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"boolField\": true", os.str());
}

TEST(JsonWriterTest, intValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getInt32(INT32_MIN), INT_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"intField\": " + std::to_string(INT32_MIN), os.str());
}

TEST(JsonWriterTest, floatValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getFloat32(3.5F), FLOAT_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"floatField\": 3.5", os.str());
}

TEST(JsonWriterTest, doubleValue)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getFloat64(9.875), DOUBLE_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"doubleField\": 9.875", os.str());
}

TEST(JsonWriterTest, enumValue)
{
    class DummyEnumReflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        explicit DummyEnumReflectable(int8_t value) :
                ReflectableBase<std::allocator<uint8_t>>(ENUM_TYPE_INFO),
                m_value(value)
        {}

        virtual size_t bitSizeOf(size_t) const override
        {
            return 0;
        }

        virtual void write(BitStreamWriter&) const override
        {
        }

        virtual AnyHolder<> getAnyValue(const std::allocator<uint8_t>& allocator) const override
        {
            return AnyHolder<>(m_value, allocator);
        }

        virtual AnyHolder<> getAnyValue(const std::allocator<uint8_t>& allocator) override
        {
            return AnyHolder<>(m_value, allocator);
        }

        virtual int64_t toInt() const override
        {
            return m_value;
        }

    private:
        int8_t m_value;
    };

    IReflectableConstPtr reflectableZeroConst = std::make_shared<DummyEnumReflectable>(0);
    ASSERT_EQ(0, reflectableZeroConst->getAnyValue().get<int8_t>()); // improve coverage
    IReflectablePtr reflectableZero = std::make_shared<DummyEnumReflectable>(0);
    ASSERT_EQ(0, reflectableZero->getAnyValue().get<int8_t>()); // improve coverage
    IReflectablePtr reflectableOne = std::make_shared<DummyEnumReflectable>(1);
    IReflectablePtr reflectableTwo = std::make_shared<DummyEnumReflectable>(2);
    IReflectablePtr reflectableMinusOne = std::make_shared<DummyEnumReflectable>(-1);

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, ENUM_FIELD_INFO);
        observer.visitValue(reflectableOne, ENUM_FIELD_INFO);
        observer.visitValue(reflectableTwo, ENUM_FIELD_INFO);
        observer.visitValue(reflectableMinusOne, ENUM_FIELD_INFO);

        // note that this is not valid json
        ASSERT_EQ(
                "\"enumField\": \"ZERO\", "
                "\"enumField\": \"One\", "
                "\"enumField\": \"2 /* no match */\", "
                "\"enumField\": \"MINUS_ONE\"", os.str());


        // just improve coverage
        ASSERT_EQ(0, reflectableZero->bitSizeOf());
        BitBuffer bitBuffer(0);
        BitStreamWriter writer(bitBuffer);
        ASSERT_NO_THROW(reflectableZero->write(writer));
    }

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        jsonWriter.setEnumerableFormat(JsonWriter::EnumerableFormat::NUMBER);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, ENUM_FIELD_INFO);
        observer.visitValue(reflectableTwo, ENUM_FIELD_INFO);
        observer.visitValue(reflectableMinusOne, ENUM_FIELD_INFO);

        // note that this is not valid json
        ASSERT_EQ("\"enumField\": 0, \"enumField\": 2, \"enumField\": -1", os.str());
    }
}

TEST(JsonWriterTest, bitmaskValue)
{
    class DummyBitmaskReflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        explicit DummyBitmaskReflectable(uint8_t value) :
                ReflectableBase<std::allocator<uint8_t>>(BITMASK_TYPE_INFO),
                m_value(value)
        {}

        virtual size_t bitSizeOf(size_t) const override
        {
            return 0;
        }

        virtual void write(BitStreamWriter&) const override
        {
        }

        virtual AnyHolder<> getAnyValue(const std::allocator<uint8_t>& allocator) const override
        {
            return AnyHolder<>(m_value, allocator);
        }

        virtual AnyHolder<> getAnyValue(const std::allocator<uint8_t>& allocator) override
        {
            return AnyHolder<>(m_value, allocator);
        }

        virtual uint64_t toUInt() const override
        {
            return m_value;
        }

    private:
        uint8_t m_value;
    };

    IReflectableConstPtr reflectableZeroConst = std::make_shared<DummyBitmaskReflectable>(0);
    ASSERT_EQ(0, reflectableZeroConst->getAnyValue().get<uint8_t>()); // improve coverage
    IReflectablePtr reflectableZero = std::make_shared<DummyBitmaskReflectable>(0);
    ASSERT_EQ(0, reflectableZero->getAnyValue().get<uint8_t>()); // improve coverage
    IReflectablePtr reflectableTwo = std::make_shared<DummyBitmaskReflectable>(2);
    IReflectablePtr reflectableThree = std::make_shared<DummyBitmaskReflectable>(3);
    IReflectablePtr reflectableFour = std::make_shared<DummyBitmaskReflectable>(4);
    IReflectablePtr reflectableSeven = std::make_shared<DummyBitmaskReflectable>(7);

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, BITMASK_FIELD_INFO);
        observer.visitValue(reflectableTwo, BITMASK_FIELD_INFO);
        observer.visitValue(reflectableThree, BITMASK_FIELD_INFO);
        observer.visitValue(reflectableFour, BITMASK_FIELD_INFO);
        observer.visitValue(reflectableSeven, BITMASK_FIELD_INFO);

        // note that this is not valid json
        ASSERT_EQ(
                "\"bitmaskField\": \"ZERO\", "
                "\"bitmaskField\": \"TWO\", "
                "\"bitmaskField\": \"One | TWO\", "
                "\"bitmaskField\": \"4 /* no match */\", "
                "\"bitmaskField\": \"7 /* partial match: One | TWO */\"", os.str());

        // just improve coverage
        ASSERT_EQ(0, reflectableZero->bitSizeOf());
        BitBuffer bitBuffer(0);
        BitStreamWriter writer(bitBuffer);
        ASSERT_NO_THROW(reflectableZero->write(writer));
    }

    {
        std::ostringstream os;
        JsonWriter jsonWriter(os);
        jsonWriter.setEnumerableFormat(JsonWriter::EnumerableFormat::NUMBER);
        IWalkObserver& observer = jsonWriter;
        observer.visitValue(reflectableZero, BITMASK_FIELD_INFO);
        observer.visitValue(reflectableSeven, BITMASK_FIELD_INFO);

        // note that this is not valid json
        ASSERT_EQ("\"bitmaskField\": 0, \"bitmaskField\": 7", os.str());
    }
}

TEST(JsonWriterTest, compound)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.beginRoot(nullptr);
    observer.visitValue(ReflectableFactory::getUInt32(13), IDENTIFIER_FIELD_INFO);
    observer.visitValue(ReflectableFactory::getString("test"_sv), TEXT_FIELD_INFO);
    BitBuffer bitBuffer({0xFF, 0x1F}, 13);
    observer.visitValue(ReflectableFactory::getBitBuffer(bitBuffer), EXTERN_DATA_FIELD_INFO);
    vector<uint8_t> bytesData{{0xCA, 0xFE}};
    observer.visitValue(ReflectableFactory::getBytes(bytesData), BYTES_DATA_FIELD_INFO);
    observer.endRoot(nullptr);

    ASSERT_EQ("{\"identifier\": 13, \"text\": \"test\", "
            "\"externData\": {\"buffer\": [255, 31], \"bitSize\": 13}, "
            "\"bytesData\": {\"buffer\": [202, 254]}}", os.str());
}

TEST(JsonWriterTest, nestedCompound)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);

    walkNested(jsonWriter);

    ASSERT_EQ("{\"identifier\": 13, \"nested\": {\"text\": \"test\"}}", os.str());
}

TEST(JsonWriterTest, array)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);

    walkArray(jsonWriter);

    ASSERT_EQ("{\"array\": [1, 2]}", os.str());
}

TEST(JsonWriterTest, arrayWithIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, 2);

    walkArray(jsonWriter);

    ASSERT_EQ("{\n  \"array\": [\n    1,\n    2\n  ]\n}", os.str());
}

TEST(JsonWriterTest, emptyIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, "");

    walkNested(jsonWriter);

    ASSERT_EQ("{\n\"identifier\": 13,\n\"nested\": {\n\"text\": \"test\"\n}\n}", os.str());
}

TEST(JsonWriterTest, strIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, "  ");

    walkNested(jsonWriter);

    ASSERT_EQ("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}", os.str());
}

TEST(JsonWriterTest, intIndent)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os, 2);

    walkNested(jsonWriter);

    ASSERT_EQ("{\n  \"identifier\": 13,\n  \"nested\": {\n    \"text\": \"test\"\n  }\n}", os.str());
}

TEST(JsonWriterTest, compactSeparators)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    jsonWriter.setItemSeparator(",");
    jsonWriter.setKeySeparator(":");

    walkNested(jsonWriter);

    ASSERT_EQ("{\"identifier\":13,\"nested\":{\"text\":\"test\"}}", os.str());
}

} // namespace zserio
