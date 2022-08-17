#include "gtest/gtest.h"

#include "zserio/JsonReader.h"
#include "zserio/ReflectableUtil.h"
#include "zserio/ZserioTreeCreatorTestObject.h"

namespace zserio
{

namespace
{

void checkReadStringifiedEnum(const char* stringValue, DummyEnum expectedValue)
{
    std::stringstream str;
    str <<
            "{\n"
            "    \"nested\": {\n"
            "        \"dummyEnum\": \"" << stringValue << "\"\n"
            "    }\n"
            "}";

    JsonReader jsonReader(str);
    auto reflectable = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ(expectedValue, ReflectableUtil::getValue<DummyEnum>(reflectable->find("nested.dummyEnum")));
}

void checkReadStringifiedEnumThrows(const char* stringValue, const char* expectedMessage)
{
    std::stringstream str;
    str <<
            "{\n"
            "    \"nested\": {\n"
            "        \"dummyEnum\": \"" << stringValue << "\"\n"
            "    }\n"
            "}";

    JsonReader jsonReader(str);
    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ(expectedMessage, e.what());
            throw;
        }
    }, CppRuntimeException);
}

void checkReadStringifiedBitmask(const char* stringValue, DummyBitmask expectedValue)
{
    std::stringstream str;
    str <<
            "{\n"
            "    \"nested\": {\n"
            "        \"dummyBitmask\": \"" << stringValue << "\"\n"
            "    }\n"
            "}";

    JsonReader jsonReader(str);
    auto reflectable = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ(expectedValue, ReflectableUtil::getValue<DummyBitmask>(reflectable->find("nested.dummyBitmask")));
}

void checkReadStringifiedBitmaskThrows(const char* stringValue, const char* expectedMessage)
{
    std::stringstream str;
    str <<
            "{\n"
            "    \"nested\": {\n"
            "        \"dummyBitmask\": \"" << stringValue << "\"\n" <<
            "    }\n"
            "}";

    JsonReader jsonReader(str);
    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ(expectedMessage, e.what());
            throw;
        }
    }, CppRuntimeException);
}

} // namespace

TEST(JsonReaderTest, readObject)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 13,\n"
        "    \"nested\": {\n"
        "        \"value\": 10,\n"
        "        \"text\": \"nested\",\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 203,\n"
        "                 240\n"
        "             ],\n"
        "             \"bitSize\": 12\n"
        "        },\n"
        "        \"dummyEnum\": 0,\n"
        "        \"dummyBitmask\": 1\n"
        "    },\n"
        "    \"text\": \"test\",\n"
        "    \"nestedArray\": [\n"
        "        {\n"
        "            \"value\": 5,\n"
        "            \"text\": \"nestedArray\",\n"
        "            \"data\": {\n"
        "                 \"buffer\": [\n"
        "                     202,\n"
        "                     254\n"
        "                 ],"
        "                 \"bitSize\": 15\n"
        "            },\n"
        "            \"dummyEnum\": 1,\n"
        "            \"dummyBitmask\": 2\n"
        "        }\n"
        "    ],\n"
        "    \"textArray\": [\n"
        "        \"this\",\n"
        "        \"is\",\n"
        "        \"text\",\n"
        "        \"array\"\n"
        "    ],\n"
        "    \"externArray\": [\n"
        "        {\n"
        "            \"buffer\": [\n"
        "                222,\n"
        "                209\n"
        "            ],"
        "            \"bitSize\": 13\n"
        "        }\n"
        "    ],\n"
        "    \"optionalBool\": null\n"
        "}"
    );

    JsonReader jsonReader(str);
    IReflectablePtr reflectable = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(reflectable);

    reflectable->initializeChildren();

    ASSERT_EQ(13, reflectable->getField("value")->getUInt32());
    ASSERT_EQ(13, reflectable->getField("nested")->getParameter("param")->getUInt32());
    ASSERT_EQ(10, reflectable->find("nested.value")->getUInt32());
    ASSERT_EQ("nested"_sv, reflectable->find("nested.text")->getStringView());
    ASSERT_EQ(BitBuffer({{0xCB, 0xF0}}, 12), reflectable->find("nested.data")->getBitBuffer());
    ASSERT_EQ(enumToValue(DummyEnum::ONE), reflectable->find("nested.dummyEnum")->getInt8());
    ASSERT_EQ(DummyBitmask(DummyBitmask::Values::READ).getValue(),
            reflectable->find("nested.dummyBitmask")->getUInt8());
    ASSERT_EQ("test"_sv, reflectable->getField("text")->getStringView());
    ASSERT_EQ(1, reflectable->getField("nestedArray")->size());
    ASSERT_EQ(5, reflectable->getField("nestedArray")->at(0)->getField("value")->getUInt32());
    ASSERT_EQ("nestedArray"_sv, reflectable->getField("nestedArray")->at(0)->getField("text")->getStringView());
    ASSERT_EQ(BitBuffer({{0xCA, 0xFE}}, 15),
            reflectable->getField("nestedArray")->at(0)->getField("data")->getBitBuffer());
    ASSERT_EQ(enumToValue(DummyEnum::TWO),
            reflectable->getField("nestedArray")->at(0)->getField("dummyEnum")->getInt8());
    ASSERT_EQ(DummyBitmask(DummyBitmask::Values::WRITE).getValue(),
            reflectable->getField("nestedArray")->at(0)->getField("dummyBitmask")->getUInt8());
    ASSERT_EQ(4, reflectable->getField("textArray")->size());
    ASSERT_EQ("this"_sv, reflectable->getField("textArray")->at(0)->getStringView());
    ASSERT_EQ("is"_sv, reflectable->getField("textArray")->at(1)->getStringView());
    ASSERT_EQ("text"_sv, reflectable->getField("textArray")->at(2)->getStringView());
    ASSERT_EQ("array"_sv, reflectable->getField("textArray")->at(3)->getStringView());
    ASSERT_EQ(1, reflectable->getField("externArray")->size());
    ASSERT_EQ(BitBuffer({{0xDE, 0xD1}}, 13), reflectable->getField("externArray")->at(0)->getBitBuffer());
    ASSERT_EQ(nullptr, reflectable->getField("optionalBool"));
    ASSERT_EQ(nullptr, reflectable->getField("optionalNested")); // not present in json
}
TEST(JsonReaderTest, readTwoObjects)
{
    std::stringstream str(
        "{\"value\": 13}\n"
        "{\"value\": 42, \"text\": \"test\"}\n"
    );

    JsonReader jsonReader(str);

    auto obj1 = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(obj1);
    ASSERT_EQ(13, obj1->getField("value")->getUInt32());
    ASSERT_EQ(""_sv, obj1->getField("text")->getStringView());

    auto obj2 = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(obj2);
    ASSERT_EQ(42, obj2->getField("value")->getUInt32());
    ASSERT_EQ("test"_sv, obj2->getField("text")->getStringView());
}

TEST(JsonReaderTest, readParameterizedObject)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 10,\n"
        "    \"text\": \"nested\",\n"
        "    \"data\": {\n"
        "         \"bitSize\": 12,\n"
        "         \"buffer\": [\n"
        "             203,\n"
        "             240\n"
        "         ]\n"
        "    },\n"
        "    \"dummyEnum\": 0,\n"
        "    \"dummyBitmask\": 1\n"
        "}\n"
    );

    JsonReader jsonReader(str);
    auto reflectable = jsonReader.read(DummyNested::typeInfo());

    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{static_cast<uint32_t>(13)}});

    ASSERT_EQ(13, reflectable->getParameter("param")->getUInt32());
    ASSERT_EQ(10, reflectable->getField("value")->getUInt32());
}

TEST(JsonReaderTest, readUnonrderedBitBuffer)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 13,\n"
        "    \"nested\": {\n"
        "        \"value\": 10,\n"
        "        \"text\": \"nested\",\n"
        "        \"data\": {\n"
        "             \"bitSize\": 12,\n"
        "             \"buffer\": [\n"
        "                 203,\n"
        "                 240\n"
        "             ]\n"
        "        },\n"
        "        \"dummyEnum\": -1,\n"
        "        \"dummyBitmask\": 1\n"
        "    }\n"
        "}"
    );

    JsonReader jsonReader(str);
    auto reflectable = jsonReader.read(DummyObject::typeInfo());
    ASSERT_TRUE(reflectable);

    reflectable->initializeChildren();

    ASSERT_EQ(13, reflectable->getField("value")->getUInt32());
    ASSERT_EQ(13, reflectable->getField("nested")->getParameter("param")->getUInt32());
    ASSERT_EQ(10, reflectable->find("nested.value")->getUInt32());
    ASSERT_EQ("nested"_sv, reflectable->find("nested.text")->getStringView());
    ASSERT_EQ(BitBuffer({{0xCB, 0xF0}}, 12), reflectable->find("nested.data")->getBitBuffer());
    ASSERT_EQ(enumToValue(DummyEnum::MinusOne), reflectable->find("nested.dummyEnum")->getInt8());
    ASSERT_EQ(DummyBitmask(DummyBitmask::Values::READ).getValue(),
            reflectable->find("nested.dummyBitmask")->getUInt8());
}

TEST(JsonReaderTest, readStringifiedEnum)
{
    checkReadStringifiedEnum("ONE", DummyEnum::ONE);
    checkReadStringifiedEnum("MinusOne", DummyEnum::MinusOne);
    checkReadStringifiedEnumThrows("NONEXISTING",
            "ZserioTreeCreator: Cannot create enum 'DummyEnum' "
            "from string value 'NONEXISTING'! (JsonParser:3:22)");
    checkReadStringifiedEnumThrows("***",
            "ZserioTreeCreator: Cannot create enum 'DummyEnum' "
            "from string value '***'! (JsonParser:3:22)");
    checkReadStringifiedEnumThrows("10 /* no match */",
            "ZserioTreeCreator: Cannot create enum 'DummyEnum' "
            "from string value '10 /* no match */'! (JsonParser:3:22)");
    checkReadStringifiedEnumThrows("-10 /* no match */",
            "ZserioTreeCreator: Cannot create enum 'DummyEnum' "
            "from string value '-10 /* no match */'! (JsonParser:3:22)");
    checkReadStringifiedEnumThrows("",
            "ZserioTreeCreator: Cannot create enum 'DummyEnum' "
            "from string value ''! (JsonParser:3:22)");
}

TEST(JsonReaderTest, readStringifiedBitmask)
{
    checkReadStringifiedBitmask("READ",
            DummyBitmask::Values::READ);
    checkReadStringifiedBitmask("READ | WRITE",
            DummyBitmask::Values::READ | DummyBitmask::Values::WRITE);
    checkReadStringifiedBitmaskThrows("NONEXISTING",
            "ZserioTreeCreator: Cannot create bitmask 'DummyBitmask' "
            "from string value 'NONEXISTING'! (JsonParser:3:25)");
    checkReadStringifiedBitmaskThrows("READ | NONEXISTING",
            "ZserioTreeCreator: Cannot create bitmask 'DummyBitmask' "
            "from string value 'READ | NONEXISTING'! (JsonParser:3:25)");
    checkReadStringifiedBitmaskThrows("READ * NONEXISTING",
            "ZserioTreeCreator: Cannot create bitmask 'DummyBitmask' "
            "from string value 'READ * NONEXISTING'! (JsonParser:3:25)");
    checkReadStringifiedBitmask("7 /* READ | WRITE */", DummyBitmask(7));
    checkReadStringifiedBitmask("15 /* READ | WRITE */", DummyBitmask(15));
    checkReadStringifiedBitmask("4 /* no match */", DummyBitmask(4));
    checkReadStringifiedBitmaskThrows("",
            "ZserioTreeCreator: Cannot create bitmask 'DummyBitmask' "
            "from string value ''! (JsonParser:3:25)");
    checkReadStringifiedBitmaskThrows(" ",
            "ZserioTreeCreator: Cannot create bitmask 'DummyBitmask' "
            "from string value ' '! (JsonParser:3:25)");
    checkReadStringifiedBitmaskThrows(" | ",
            "ZserioTreeCreator: Cannot create bitmask 'DummyBitmask' "
            "from string value ' | '! (JsonParser:3:25)");
}

TEST(JsonReaderTest, jsonParserException)
{
    std::stringstream str(
        "{\"value\"\n\"value\""
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const JsonParserException& e)
        {
            ASSERT_STREQ("JsonParser:2:1: unexpected token: VALUE, expecting KEY_SEPARATOR!", e.what());
            throw;
        }
    }, JsonParserException);
}

TEST(JsonReaderTest, wrongKeyException)
{
    std::stringstream str(
        "{\"value\": 13,\n\"nonexisting\": 10}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("ZserioTreeCreator: Member 'nonexisting' not found in 'DummyObject'! "
                    "(JsonParser:2:16)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, wrongValueTypeException)
{
    std::stringstream str(
        "{\n  \"value\": \"13\"\n}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("ZserioTreeCreator: Value '13' cannot be converted to integral value! "
                    "(JsonParser:2:12)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, wrongBitBufferException)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 13,\n"
        "    \"nested\": {\n"
        "        \"value\": 10,\n"
        "        \"text\": \"nested\",\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 203,\n"
        "                 240\n"
        "             ],\n"
        "             \"bitSize\": {\n"
        "             }\n"
        "        }\n"
        "    }\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("JsonReader: Unexpected beginObject in BitBuffer! (JsonParser:12:14)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, partialBitBufferException)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 13,\n"
        "    \"nested\": {\n"
        "        \"value\": 10,\n"
        "        \"text\": \"nested\",\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 203,\n"
        "                 240\n"
        "             ]\n"
        "        }\n"
        "    }\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("JsonReader: Unexpected end in BitBuffer! (JsonParser:12:5)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, jsonArrayException)
{
    std::stringstream str("[1, 2]");
    JsonReader jsonReader(str);

    ASSERT_THROW(jsonReader.read(DummyObject::typeInfo()), CppRuntimeException);
}

TEST(JsonReaderTest, jsonValueException)
{
    std::stringstream str("\"text\"");
    JsonReader jsonReader(str);

    ASSERT_THROW(jsonReader.read(DummyObject::typeInfo()), CppRuntimeException);
}

TEST(JsonReaderTest, bigLongValueException)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 4294967296\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("ZserioTreeCreator: Integral value '4294967296' overflow (<0, 4294967295>)! "
                    "(JsonParser:2:14)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, floatLongValueException)
{
    std::stringstream str(
        "{\n"
        "    \"value\": 1.234\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("ZserioTreeCreator: Value '1.233' cannot be converted to integral value! "
                    "(JsonParser:2:14)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, bigBitBufferByteValueException)
{
    std::stringstream str(
        "{\n"
        "    \"nested\": {\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 256\n"
        "             ],\n"
        "             \"bitSize\": 7\n"
        "        }\n"
        "    }\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("JsonReader: Cannot create byte for Bit Buffer from value '256'! (JsonParser:5:18)",
                    e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, negativeBitBufferByteValueException)
{
    std::stringstream str(
        "{\n"
        "    \"nested\": {\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 -1\n"
        "             ],\n"
        "             \"bitSize\": 7\n"
        "        }\n"
        "    }\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("JsonReader: Unexpected visitValue (int) in BitBuffer! (JsonParser:5:18)", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, wrongBitBufferSizeValueException)
{
    std::stringstream str(
        "{\n"
        "    \"nested\": {\n"
        "        \"data\": {\n"
        "             \"buffer\": [\n"
        "                 255\n"
        "             ],\n"
        "             \"bitSize\": 18446744073709551616\n"
        "        }\n"
        "    }\n"
        "}"
    );
    JsonReader jsonReader(str);

    ASSERT_THROW({
        try
        {
            jsonReader.read(DummyObject::typeInfo());
        }
        catch (const CppRuntimeException& e)
        {
            ASSERT_STREQ("JsonTokenizer:7:25: Value is outside of the 64-bit integer range!", e.what());
            throw;
        }
    }, CppRuntimeException);
}

TEST(JsonReaderTest, bitBufferAdapterUninitializedCalls)
{
    detail::BitBufferAdapter<std::allocator<uint8_t>> bitBufferAdapter{std::allocator<uint8_t>()};

    ASSERT_THROW(bitBufferAdapter.beginObject(), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.endObject(), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.beginArray(), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.endArray(), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitKey("nonexisting"_sv), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue(static_cast<uint64_t>(10)), CppRuntimeException);
    bitBufferAdapter.visitKey("buffer"_sv);
    ASSERT_THROW(bitBufferAdapter.visitKey("nonexisting"_sv), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue(nullptr), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue(true), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue(static_cast<int64_t>(-1)), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue(static_cast<uint64_t>(1)), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue(0.0), CppRuntimeException);
    ASSERT_THROW(bitBufferAdapter.visitValue("BadValue"_sv), CppRuntimeException);
}

TEST(JsonReaderTest, creatorAdapter)
{
    detail::CreatorAdapter<std::allocator<uint8_t>> creatorAdapter{std::allocator<uint8_t>()};

    ASSERT_THROW(creatorAdapter.get(), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.beginObject(), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.endObject(), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.beginArray(), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.endArray(), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitKey("nonexistent"_sv), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitValue(nullptr), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitValue(true), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitValue(static_cast<int64_t>(-1)), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitValue(static_cast<uint64_t>(1)), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitValue(0.0), CppRuntimeException);
    ASSERT_THROW(creatorAdapter.visitValue("BadValue"_sv), CppRuntimeException);
}

} // namespace zserio
