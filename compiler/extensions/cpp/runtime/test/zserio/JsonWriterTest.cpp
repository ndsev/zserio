#include "gtest/gtest.h"

#include <sstream>

#include "zserio/JsonWriter.h"
#include "zserio/TypeInfo.h"
#include "zserio/Reflectable.h"

using namespace zserio::literals;

namespace zserio
{

namespace
{

const FieldInfo TEXT_FIELD_INFO{
    "text"_sv, BuiltinTypeInfo::getString(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo IDENTIFIER_FIELD_INFO{
    "identifier", BuiltinTypeInfo::getUInt32(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo DATA_FIELD_INFO{
    "data"_sv, BuiltinTypeInfo::getBitBuffer(),
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const StructTypeInfo DUMMY_TYPE_INFO{
    "Dummy"_sv, {}, {}, {}, {}, {}
};

const FieldInfo NESTED_FIELD_INFO{
    "nested"_sv, DUMMY_TYPE_INFO,
    {}, {}, {}, {}, false, {}, {}, false, {}, false, false
};

const FieldInfo ARRAY_FIELD_INFO{
    "array", BuiltinTypeInfo::getUInt32(),
    {}, {}, {}, {}, false, {}, {}, true, {}, false, false
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

TEST(JsonWriterTest, value)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.visitValue(ReflectableFactory::getString("test"_sv), TEXT_FIELD_INFO);

    // note that this is not valid json
    ASSERT_EQ("\"text\": \"test\"", os.str());
}

TEST(JsonWriterTest, compound)
{
    std::ostringstream os;
    JsonWriter jsonWriter(os);
    IWalkObserver& observer = jsonWriter;

    observer.beginRoot(nullptr);
    observer.visitValue(ReflectableFactory::getUInt32(13), IDENTIFIER_FIELD_INFO);
    observer.visitValue(ReflectableFactory::getString("test"_sv), TEXT_FIELD_INFO);
    BitBuffer bitBuffer({0x1F}, 5);
    observer.visitValue(ReflectableFactory::getBitBuffer(bitBuffer), DATA_FIELD_INFO);
    observer.endRoot(nullptr);

    ASSERT_EQ("{\"identifier\": 13, \"text\": \"test\", \"data\": "
            "{\"buffer\": [31], \"bitSize\": 5}}", os.str());
}

TEST(JsonWriterTest, nested_compound)
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
