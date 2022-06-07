#include <fstream>

#include "gtest/gtest.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/JsonWriter.h"
#include "zserio/Walker.h"

#include "WithTypeInfoCodeCreator.h"

using namespace zserio::literals;

namespace with_type_info_code
{

using allocator_type = WithTypeInfoCode::allocator_type;
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

class DebugStringTest : public ::testing::Test
{
protected:
    std::string getJsonNameWithArrayLengthFilter(size_t arrayLength)
    {
        return std::string("arguments/with_type_info_code/with_type_info_code_array_length_") +
                std::to_string(arrayLength) + ".json";
    }

    void checkJsonFile(const std::string& createdJsonFileName)
    {
        const std::string& createdJsonBaseName = createdJsonFileName.substr(
                createdJsonFileName.find_last_of("/") + 1);
        const std::string jsonDataFileName("arguments/with_type_info_code/data/" + createdJsonBaseName);

        std::ifstream jsonCreatedFile(createdJsonFileName);
        std::ifstream jsonExpectedFile(jsonDataFileName);
        std::string createdLine;
        std::string expectedLine;
        while (std::getline(jsonCreatedFile, createdLine) && std::getline(jsonExpectedFile, expectedLine))
            ASSERT_EQ(expectedLine, createdLine);
        ASSERT_TRUE(jsonCreatedFile.eof());
        ASSERT_TRUE(jsonExpectedFile.eof());
    }

    static const std::string JSON_NAME_WRITE_READ_WITH_OPTIONALS;
    static const std::string JSON_NAME_WRITE_READ_WITHOUT_OPTIONALS;
    static const std::string JSON_NAME_WITH_OPTIONALS;
    static const std::string JSON_NAME_WITHOUT_OPTIONALS;
    static const std::string JSON_NAME_WITH_DEPTH0_FILTER;
    static const std::string JSON_NAME_WITH_DEPTH5_FILTER;
    static const std::string JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER;
    static const std::string JSON_NAME_WITH_REGEX_FILTER;
    static constexpr uint8_t JSON_INDENT = 4;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string DebugStringTest::JSON_NAME_WITH_OPTIONALS =
        "arguments/with_type_info_code/with_type_info_code_optionals.json";
const std::string DebugStringTest::JSON_NAME_WITHOUT_OPTIONALS =
        "arguments/with_type_info_code/with_type_info_code.json";
const std::string DebugStringTest::JSON_NAME_WITH_DEPTH0_FILTER =
        "arguments/with_type_info_code/with_type_info_code_depth0.json";
const std::string DebugStringTest::JSON_NAME_WITH_DEPTH5_FILTER =
        "arguments/with_type_info_code/with_type_info_code_depth5.json";
const std::string DebugStringTest::JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER =
        "arguments/with_type_info_code/with_type_info_code_depth1_array_length0.json";
const std::string DebugStringTest::JSON_NAME_WITH_REGEX_FILTER =
        "arguments/with_type_info_code/with_type_info_code_regex.json";

TEST_F(DebugStringTest, jsonWriterWithOptionals)
{
    WithTypeInfoCode withTypeInfoCode;
    const bool createOptionals = true;
    fillWithTypeInfoCode(withTypeInfoCode, createOptionals);
    withTypeInfoCode.initializeOffsets(0);

    {
        std::ofstream jsonFile(JSON_NAME_WITH_OPTIONALS, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        Walker walker(jsonWriter);
        walker.walk(withTypeInfoCode.reflectable());
        checkJsonFile(JSON_NAME_WITH_OPTIONALS);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        std::ofstream jsonFile(JSON_NAME_WITH_OPTIONALS, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        Walker walker(jsonWriter);
        walker.walk(withTypeInfoCodeConst.reflectable());
        checkJsonFile(JSON_NAME_WITH_OPTIONALS);
    }
}

TEST_F(DebugStringTest, jsonWriterWithoutOptionals)
{
    WithTypeInfoCode withTypeInfoCode;
    const bool createOptionals = false;
    fillWithTypeInfoCode(withTypeInfoCode, createOptionals);
    withTypeInfoCode.initializeOffsets(0);

    {
        std::ofstream jsonFile(JSON_NAME_WITHOUT_OPTIONALS, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        Walker walker(jsonWriter);
        walker.walk(withTypeInfoCode.reflectable());
        checkJsonFile(JSON_NAME_WITHOUT_OPTIONALS);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        std::ofstream jsonFile(JSON_NAME_WITHOUT_OPTIONALS, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        Walker walker(jsonWriter);
        walker.walk(withTypeInfoCodeConst.reflectable());
        checkJsonFile(JSON_NAME_WITHOUT_OPTIONALS);
    }
}

TEST_F(DebugStringTest, jsonWriterWithArrayLengthFilter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    for (size_t i = 0; i < 11; ++i)
    {
        const std::string jsonFileName = getJsonNameWithArrayLengthFilter(i);

        {
            std::ofstream jsonFile(jsonFileName, std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            ArrayLengthWalkFilter walkFilter(i);
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCode.reflectable());
            checkJsonFile(jsonFileName);
        }
        {
            // constant reflectable
            const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
            std::ofstream jsonFile(jsonFileName, std::ios::out | std::ios::trunc);
            JsonWriter jsonWriter(jsonFile, JSON_INDENT);
            ArrayLengthWalkFilter walkFilter(i);
            Walker walker(jsonWriter, walkFilter);
            walker.walk(withTypeInfoCodeConst.reflectable());
            checkJsonFile(jsonFileName);
        }
    }
}

TEST_F(DebugStringTest, jsonWriterWithDepth0Filter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    {
        std::ofstream jsonFile(JSON_NAME_WITH_DEPTH0_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        DepthWalkFilter walkFilter(0);
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCode.reflectable());
        checkJsonFile(JSON_NAME_WITH_DEPTH0_FILTER);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        std::ofstream jsonFile(JSON_NAME_WITH_DEPTH0_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        DepthWalkFilter walkFilter(0);
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCodeConst.reflectable());
        checkJsonFile(JSON_NAME_WITH_DEPTH0_FILTER);
    }
}

TEST_F(DebugStringTest, jsonWriterWithDepth1ArrayLength0Filter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    {
        std::ofstream jsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        DepthWalkFilter depthFilter(1);
        ArrayLengthWalkFilter arrayLengthFilter(0);
        AndWalkFilter walkFilter({std::ref<IWalkFilter>(depthFilter), std::ref<IWalkFilter>(arrayLengthFilter)});
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCode.reflectable());
        checkJsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        std::ofstream jsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        DepthWalkFilter depthFilter(1);
        ArrayLengthWalkFilter arrayLengthFilter(0);
        AndWalkFilter walkFilter({std::ref<IWalkFilter>(depthFilter), std::ref<IWalkFilter>(arrayLengthFilter)});
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCodeConst.reflectable());
        checkJsonFile(JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER);
    }
}

TEST_F(DebugStringTest, jsonWriterWithDepth5Filter)
{
    WithTypeInfoCode withTypeInfoCode;
    fillWithTypeInfoCode(withTypeInfoCode);
    withTypeInfoCode.initializeOffsets(0);

    {
        std::ofstream jsonFile(JSON_NAME_WITH_DEPTH5_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        DepthWalkFilter walkFilter(5);
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCode.reflectable());
        checkJsonFile(JSON_NAME_WITH_DEPTH5_FILTER);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        std::ofstream jsonFile(JSON_NAME_WITH_DEPTH5_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        DepthWalkFilter walkFilter(5);
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCodeConst.reflectable());
        checkJsonFile(JSON_NAME_WITH_DEPTH5_FILTER);
    }
}

TEST_F(DebugStringTest, jsonWriterWithRegexFilter)
{
    WithTypeInfoCode withTypeInfoCode;
    const bool createOptionals = false;
    fillWithTypeInfoCode(withTypeInfoCode, createOptionals);
    withTypeInfoCode.initializeOffsets(0);

    {
        std::ofstream jsonFile(JSON_NAME_WITH_REGEX_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        RegexWalkFilter walkFilter(".*fieldOffset");
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCode.reflectable());
        checkJsonFile(JSON_NAME_WITH_REGEX_FILTER);
    }
    {
        // constant reflectable
        const WithTypeInfoCode& withTypeInfoCodeConst = withTypeInfoCode;
        std::ofstream jsonFile(JSON_NAME_WITH_REGEX_FILTER, std::ios::out | std::ios::trunc);
        JsonWriter jsonWriter(jsonFile, JSON_INDENT);
        RegexWalkFilter walkFilter(".*fieldOffset");
        Walker walker(jsonWriter, walkFilter);
        walker.walk(withTypeInfoCodeConst.reflectable());
        checkJsonFile(JSON_NAME_WITH_REGEX_FILTER);
    }
}

} // namespace with_type_info_code
