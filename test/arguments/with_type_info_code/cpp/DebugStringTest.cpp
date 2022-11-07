#include <fstream>

#include "gtest/gtest.h"

#include "zserio/DebugStringUtil.h"
#include "zserio/JsonWriter.h"
#include "zserio/RebindAlloc.h"
#include "zserio/ReflectableUtil.h"
#include "zserio/SerializeUtil.h"
#include "zserio/Walker.h"

#include "WithTypeInfoCodeCreator.h"

using namespace zserio::literals;

namespace with_type_info_code
{

using allocator_type = WithTypeInfoCode::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

using Walker = zserio::BasicWalker<allocator_type>;
using JsonWriter = zserio::BasicJsonWriter<allocator_type>;
using IWalkFilter = zserio::IBasicWalkFilter<allocator_type>;
using ArrayLengthWalkFilter = zserio::BasicArrayLengthWalkFilter<allocator_type>;
using DepthWalkFilter = zserio::BasicDepthWalkFilter<allocator_type>;
using RegexWalkFilter = zserio::BasicRegexWalkFilter<allocator_type>;
using AndWalkFilter = zserio::BasicAndWalkFilter<allocator_type>;
using IReflectablePtr = zserio::IBasicReflectablePtr<allocator_type>;
using JsonReader = zserio::BasicJsonReader<allocator_type>;

class DebugStringTest : public ::testing::Test
{
protected:
    string_type getJsonNameWithArrayLengthFilter(size_t arrayLength)
    {
        return string_type("arguments/with_type_info_code/with_type_info_code_array_length_") +
                zserio::toString<allocator_type>(arrayLength) + ".json";
    }

    void checkReflectable(const WithTypeInfoCode& withTypeInfoCode, const IReflectablePtr& readReflectable)
    {
        // check using objects
        const WithTypeInfoCode& readWithTypeInfoCode =
                zserio::ReflectableUtil::getValue<WithTypeInfoCode, allocator_type>(readReflectable);
        ASSERT_EQ(withTypeInfoCode, readWithTypeInfoCode);

        // check using reflectables
        auto origReflectable = withTypeInfoCode.reflectable();
        ASSERT_TRUE(zserio::ReflectableUtil::equal<allocator_type>(origReflectable, readReflectable));
    }

    void checkWithTypeInfoCodeArrayLength(const IReflectablePtr& reflectable, size_t maxArrayLength)
    {
        ASSERT_EQ(WithTypeInfoCode::typeInfo().getSchemaName(), reflectable->getTypeInfo().getSchemaName());

        ASSERT_LE(reflectable->find("complexStruct.array")->size(), maxArrayLength);
        ASSERT_LE(reflectable->find("complexStruct.arrayWithLen")->size(), maxArrayLength);
        auto paramStructArrayReflectable = reflectable->find("complexStruct.paramStructArray");
        ASSERT_LE(paramStructArrayReflectable->size(), maxArrayLength);
        for (size_t i = 0; i < paramStructArrayReflectable->size(); ++i)
        {
            ASSERT_LE(paramStructArrayReflectable->at(i)->getField("array")->size(), maxArrayLength);
        }
        ASSERT_LE(reflectable->find("complexStruct.dynamicBitFieldArray")->size(), maxArrayLength);

        ASSERT_LE(reflectable->find("parameterizedStruct.array")->size(), maxArrayLength);
        ASSERT_LE(reflectable->find("templatedParameterizedStruct.array")->size(), maxArrayLength);
        ASSERT_LE(reflectable->find("externArray")->size(), maxArrayLength);
        ASSERT_LE(reflectable->find("bytesArray")->size(), maxArrayLength);
        ASSERT_LE(reflectable->find("implicitArray")->size(), maxArrayLength);
    }

    void checkWithTypeInfoCodeDepth0(const IReflectablePtr& reflectable)
    {
        ASSERT_EQ(WithTypeInfoCode::typeInfo().getSchemaName(), reflectable->getTypeInfo().getSchemaName());

        // in C++ all fields are default constructed,
        // so just check that optionals are not set and arrays are empty
        ASSERT_EQ(0, reflectable->find("complexStruct.array")->size());
        ASSERT_FALSE(reflectable->find("complexStruct.arrayWithLen"));
        ASSERT_FALSE(reflectable->find("complexStruct.paramStructArray"));
        ASSERT_EQ(0, reflectable->find("complexStruct.dynamicBitFieldArray")->size());
        ASSERT_FALSE(reflectable->find("complexStruct.optionalEnum"));
        ASSERT_FALSE(reflectable->find("complexStruct.optionalBitmask"));
        ASSERT_FALSE(reflectable->find("complexStruct.optionalExtern"));
        ASSERT_FALSE(reflectable->find("complexStruct.optionalBytes"));
        ASSERT_EQ(0, reflectable->find("parameterizedStruct.array")->size());
        ASSERT_FALSE(reflectable->find("recursiveStruct.fieldRecursion"));
        ASSERT_EQ(0, reflectable->find("recursiveStruct.arrayRecursion")->size());
        ASSERT_EQ(0, reflectable->find("templatedParameterizedStruct.array")->size());
        ASSERT_EQ(0, reflectable->find("externArray")->size());
        ASSERT_EQ(0, reflectable->find("bytesArray")->size());
        ASSERT_EQ(0, reflectable->find("implicitArray")->size());
    }

    void checkWithTypeInfoCodeDepth1ArrayLength0(const IReflectablePtr& reflectable)
    {
        ASSERT_EQ(WithTypeInfoCode::typeInfo().getSchemaName(), reflectable->getTypeInfo().getSchemaName());

        // in C++ all fields are default constructed,
        // so just check that optionals are not set and arrays are empty
        ASSERT_EQ(0, reflectable->find("complexStruct.array")->size());
        ASSERT_FALSE(reflectable->find("complexStruct.arrayWithLen"));
        ASSERT_FALSE(reflectable->find("complexStruct.paramStructArray"));
        ASSERT_EQ(0, reflectable->find("complexStruct.dynamicBitFieldArray")->size());
        ASSERT_FALSE(reflectable->find("complexStruct.optionalEnum"));
        ASSERT_FALSE(reflectable->find("complexStruct.optionalBitmask"));
        ASSERT_FALSE(reflectable->find("complexStruct.optionalExtern"));
        ASSERT_FALSE(reflectable->find("complexStruct.optionalBytes"));
        ASSERT_EQ(0, reflectable->find("parameterizedStruct.array")->size());
        ASSERT_FALSE(reflectable->find("recursiveStruct.fieldRecursion"));
        ASSERT_EQ(0, reflectable->find("recursiveStruct.arrayRecursion")->size());
        ASSERT_EQ(0, reflectable->find("templatedParameterizedStruct.array")->size());
        ASSERT_EQ(0, reflectable->find("externArray")->size());
        ASSERT_EQ(0, reflectable->find("bytesArray")->size());
        ASSERT_EQ(0, reflectable->find("implicitArray")->size());
    }

    void checkWithTypeInfoCodeRegex(const IReflectablePtr& reflectable)
    {
        ASSERT_EQ(WithTypeInfoCode::typeInfo().getSchemaName(), reflectable->getTypeInfo().getSchemaName());

        ASSERT_NE(0, reflectable->find("simpleStruct.fieldOffset")->getUInt32());
        ASSERT_NE(0, reflectable->find("complexStruct.simpleStruct.fieldOffset")->getUInt32());
        ASSERT_NE(0, reflectable->find("complexStruct.anotherSimpleStruct.fieldOffset")->getUInt32());
    }

    static const string_type JSON_NAME_WITH_OPTIONALS;
    static const string_type JSON_NAME_WITHOUT_OPTIONALS;
    static const string_type JSON_NAME_WITH_DEPTH0_FILTER;
    static const string_type JSON_NAME_WITH_DEPTH5_FILTER;
    static const string_type JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER;
    static const string_type JSON_NAME_WITH_REGEX_FILTER;
    static constexpr uint8_t JSON_INDENT = 4;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const string_type DebugStringTest::JSON_NAME_WITH_OPTIONALS =
        "arguments/with_type_info_code/with_type_info_code_optionals.json";
const string_type DebugStringTest::JSON_NAME_WITHOUT_OPTIONALS =
        "arguments/with_type_info_code/with_type_info_code.json";
const string_type DebugStringTest::JSON_NAME_WITH_DEPTH0_FILTER =
        "arguments/with_type_info_code/with_type_info_code_depth0.json";
const string_type DebugStringTest::JSON_NAME_WITH_DEPTH5_FILTER =
        "arguments/with_type_info_code/with_type_info_code_depth5.json";
const string_type DebugStringTest::JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER =
        "arguments/with_type_info_code/with_type_info_code_depth1_array_length0.json";
const string_type DebugStringTest::JSON_NAME_WITH_REGEX_FILTER =
        "arguments/with_type_info_code/with_type_info_code_regex.json";

TEST_F(DebugStringTest, jsonWriterWithOptionals)
{
    // non-constant reflectable
    WithTypeInfoCode withTypeInfoCode;
    const bool createOptionals = true;
    fillWithTypeInfoCode(withTypeInfoCode, createOptionals);
    withTypeInfoCode.initializeOffsets(0);

    {
        zserio::toJsonFile(withTypeInfoCode, JSON_NAME_WITH_OPTIONALS);

        auto reflectable = zserio::fromJsonFile(WithTypeInfoCode::typeInfo(), JSON_NAME_WITH_OPTIONALS,
                allocator_type());
        ASSERT_TRUE(reflectable);
        reflectable->initializeChildren();
        checkReflectable(withTypeInfoCode, reflectable);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        zserio::toJsonFile(withTypeInfoCodeConst, JSON_NAME_WITH_OPTIONALS);

        WithTypeInfoCode readWithTypeInfoCode =
                zserio::fromJsonFile<WithTypeInfoCode>(JSON_NAME_WITH_OPTIONALS, allocator_type());

        readWithTypeInfoCode.initializeChildren();
        ASSERT_EQ(withTypeInfoCode, readWithTypeInfoCode);
    }
}

TEST_F(DebugStringTest, jsonWriterWithoutOptionals)
{
    WithTypeInfoCode withTypeInfoCode;
    const bool createOptionals = false;
    fillWithTypeInfoCode(withTypeInfoCode, createOptionals);
    withTypeInfoCode.initializeOffsets(0);

    {
        zserio::toJsonFile(withTypeInfoCode, JSON_NAME_WITHOUT_OPTIONALS);

        WithTypeInfoCode readWithTypeInfoCode =
                zserio::fromJsonFile<WithTypeInfoCode>(JSON_NAME_WITHOUT_OPTIONALS, allocator_type());

        readWithTypeInfoCode.initializeChildren();
        ASSERT_EQ(withTypeInfoCode, readWithTypeInfoCode);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        zserio::toJsonFile(withTypeInfoCodeConst, JSON_NAME_WITHOUT_OPTIONALS);

        auto reflectable = zserio::fromJsonFile(WithTypeInfoCode::typeInfo(), JSON_NAME_WITHOUT_OPTIONALS,
                allocator_type());
        ASSERT_TRUE(reflectable);
        reflectable->initializeChildren();
        checkReflectable(withTypeInfoCode, reflectable);
    }
}

TEST_F(DebugStringTest, jsonWriterWithArrayLengthFilter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    for (size_t i = 0; i < 11; ++i)
    {
        const string_type jsonFileName = getJsonNameWithArrayLengthFilter(i);

        {
            {
                std::ofstream jsonFile(jsonFileName.c_str(), std::ios::out | std::ios::trunc);
                JsonWriter jsonWriter(jsonFile, JSON_INDENT);
                ArrayLengthWalkFilter walkFilter(i);
                Walker walker(jsonWriter, walkFilter);
                walker.walk(withTypeInfoCode.reflectable());
            }
            {
                std::ifstream jsonFile(jsonFileName.c_str());
                JsonReader jsonReader(jsonFile);
                auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
                ASSERT_TRUE(reflectable);
                checkWithTypeInfoCodeArrayLength(reflectable, i);
            }
        }
        {
            {
                // constant reflectable
                const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
                std::ofstream jsonFile(jsonFileName.c_str(), std::ios::out | std::ios::trunc);
                JsonWriter jsonWriter(jsonFile, JSON_INDENT);
                ArrayLengthWalkFilter walkFilter(i);
                Walker walker(jsonWriter, walkFilter);
                walker.walk(withTypeInfoCodeConst.reflectable());
            }
            {
                std::ifstream jsonFile(jsonFileName.c_str());
                JsonReader jsonReader(jsonFile);
                auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
                ASSERT_TRUE(reflectable);
                checkWithTypeInfoCodeArrayLength(reflectable, i);
            }
        }
    }
}

TEST_F(DebugStringTest, jsonWriterWithDepth0Filter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    {
        {
            std::ofstream jsonFile(JSON_NAME_WITH_DEPTH0_FILTER.c_str(), std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            DepthWalkFilter walkFilter(0);
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_DEPTH0_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            checkWithTypeInfoCodeDepth0(reflectable);
        }
    }
    {
        // constant reflectable
        {
            const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
            std::ofstream jsonFile(JSON_NAME_WITH_DEPTH0_FILTER.c_str(), std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            DepthWalkFilter walkFilter(0);
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCodeConst.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_DEPTH0_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            checkWithTypeInfoCodeDepth0(reflectable);
        }
    }
}

TEST_F(DebugStringTest, jsonWriterWithDepth1ArrayLength0Filter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    {
        {
            std::ofstream jsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER.c_str(),
                    std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            DepthWalkFilter depthFilter(1);
            ArrayLengthWalkFilter arrayLengthFilter(0);
            AndWalkFilter walkFilter({
                    std::ref<IWalkFilter>(depthFilter),
                    std::ref<IWalkFilter>(arrayLengthFilter)
            });
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            checkWithTypeInfoCodeDepth1ArrayLength0(reflectable);
        }
    }
    {
        {
            // constant reflectable
            const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
            std::ofstream jsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER.c_str(),
                    std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            DepthWalkFilter depthFilter(1);
            ArrayLengthWalkFilter arrayLengthFilter(0);
            AndWalkFilter walkFilter({
                    std::ref<IWalkFilter>(depthFilter),
                    std::ref<IWalkFilter>(arrayLengthFilter)
            });
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCodeConst.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            checkWithTypeInfoCodeDepth1ArrayLength0(reflectable);
        }
    }
}

TEST_F(DebugStringTest, jsonWriterWithDepth5Filter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    {
        {
            std::ofstream jsonFile(JSON_NAME_WITH_DEPTH5_FILTER.c_str(), std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            DepthWalkFilter walkFilter(5);
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_DEPTH5_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            reflectable->initializeChildren();
            checkReflectable(withTypeInfoCode, reflectable);
        }
    }
    {
        {
            // constant reflectable
            const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
            std::ofstream jsonFile(JSON_NAME_WITH_DEPTH5_FILTER.c_str(), std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            DepthWalkFilter walkFilter(5);
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCodeConst.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_DEPTH5_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            reflectable->initializeChildren();
            checkReflectable(withTypeInfoCode, reflectable);
        }
    }
}

TEST_F(DebugStringTest, jsonWriterWithRegexFilter)
{
    WithTypeInfoCode withTypeInfoCode;
    const bool createOptionals = false;
    fillWithTypeInfoCode(withTypeInfoCode, createOptionals);
    withTypeInfoCode.initializeOffsets(0);

    {
        {
            std::ofstream jsonFile(JSON_NAME_WITH_REGEX_FILTER.c_str(), std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            RegexWalkFilter walkFilter(".*fieldOffset");
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_REGEX_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            checkWithTypeInfoCodeRegex(reflectable);
        }
    }
    {
        {
            // constant reflectable
            const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
            std::ofstream jsonFile(JSON_NAME_WITH_REGEX_FILTER.c_str(), std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            RegexWalkFilter walkFilter(".*fieldOffset");
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCodeConst.reflectable());
        }
        {
            std::ifstream jsonFile(JSON_NAME_WITH_REGEX_FILTER.c_str());
            JsonReader jsonReader(jsonFile);
            auto reflectable = jsonReader.read(WithTypeInfoCode::typeInfo());
            ASSERT_TRUE(reflectable);
            checkWithTypeInfoCodeRegex(reflectable);
        }
    }
}

} // namespace with_type_info_code
