#include "array_types/variable_array_subtyped_struct/VariableArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/StringConvertUtil.h"

namespace array_types
{
namespace variable_array_subtyped_struct
{

using allocator_type = VariableArray::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class VariableArraySubtypedStructTest : public ::testing::Test
{
protected:
    void writeVariableArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeSignedBits(static_cast<uint8_t>(numElements), 8);
        for (size_t i = 0; i < numElements; ++i)
        {
            writer.writeBits(static_cast<uint32_t>(i), 32);
            writer.writeString(std::string("Name") + std::to_string(i));
        }
    }

    static const std::string BLOB_NAME;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string VariableArraySubtypedStructTest::BLOB_NAME =
        "language/array_types/variable_array_subtyped_struct.blob";

TEST_F(VariableArraySubtypedStructTest, bitSizeOf)
{
    const size_t numElements = 33;
    vector_type<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        const ArrayElement arrayElement(
                static_cast<uint32_t>(i), string_type("Name") + zserio::toString<allocator_type>(i));
        compoundArray.push_back(arrayElement);
    }
    VariableArray variableArray;
    variableArray.setNumElements(static_cast<uint8_t>(numElements));
    variableArray.setCompoundArray(compoundArray);

    const size_t bitPosition = 2;
    const size_t numOneNumberIndexes = 10; // (numElements > 9) ? 10 : numElements
    const size_t expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
    ASSERT_EQ(expectedBitSize, variableArray.bitSizeOf(bitPosition));
}

TEST_F(VariableArraySubtypedStructTest, initializeOffsets)
{
    const size_t numElements = 33;
    vector_type<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        const ArrayElement arrayElement(
                static_cast<uint32_t>(i), string_type("Name") + zserio::toString<allocator_type>(i));
        compoundArray.push_back(arrayElement);
    }
    VariableArray variableArray;
    variableArray.setNumElements(static_cast<uint8_t>(numElements));
    variableArray.setCompoundArray(compoundArray);

    const size_t bitPosition = 2;
    const size_t numOneNumberIndexes = 10; // (numElements > 9) ? 10 : numElements
    const size_t expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
    ASSERT_EQ(expectedEndBitPosition, variableArray.initializeOffsets(bitPosition));
}

TEST_F(VariableArraySubtypedStructTest, readConstructor)
{
    const size_t numElements = 59;
    zserio::BitStreamWriter writer(bitBuffer);
    writeVariableArrayToByteArray(writer, numElements);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    VariableArray variableArray(reader);

    ASSERT_EQ(numElements, static_cast<size_t>(variableArray.getNumElements()));
    const vector_type<ArrayElement>& compoundArray = variableArray.getCompoundArray();
    ASSERT_EQ(numElements, compoundArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(i, compoundArray[i].getId());
        ASSERT_EQ(std::string("Name") + std::to_string(i), compoundArray[i].getName().c_str());
    }
}

TEST_F(VariableArraySubtypedStructTest, writeRead)
{
    const size_t numElements = 33;
    vector_type<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        const ArrayElement arrayElement(
                static_cast<uint32_t>(i), string_type("Name") + zserio::toString<allocator_type>(i));
        compoundArray.push_back(arrayElement);
    }
    VariableArray variableArray;
    variableArray.setNumElements(static_cast<uint8_t>(numElements));
    variableArray.setCompoundArray(compoundArray);

    zserio::BitStreamWriter writer(bitBuffer);
    variableArray.write(writer);

    ASSERT_EQ(variableArray.bitSizeOf(), writer.getBitPosition());
    ASSERT_EQ(variableArray.initializeOffsets(), writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    VariableArray readVariableArray(reader);
    const vector_type<ArrayElement>& readCompoundArray = readVariableArray.getCompoundArray();
    ASSERT_EQ(numElements, readCompoundArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(i, readCompoundArray[i].getId());
        ASSERT_EQ(std::string("Name") + std::to_string(i), readCompoundArray[i].getName().c_str());
    }
}

TEST_F(VariableArraySubtypedStructTest, writeReadFile)
{
    const size_t numElements = 33;
    vector_type<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        const ArrayElement arrayElement(
                static_cast<uint32_t>(i), string_type("Name") + zserio::toString<allocator_type>(i));
        compoundArray.push_back(arrayElement);
    }
    VariableArray variableArray;
    variableArray.setNumElements(static_cast<uint8_t>(numElements));
    variableArray.setCompoundArray(compoundArray);

    zserio::serializeToFile(variableArray, BLOB_NAME);

    VariableArray readVariableArray = zserio::deserializeFromFile<VariableArray>(BLOB_NAME);
    const vector_type<ArrayElement>& readCompoundArray = readVariableArray.getCompoundArray();
    ASSERT_EQ(numElements, readCompoundArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(i, readCompoundArray[i].getId());
        ASSERT_EQ(std::string("Name") + std::to_string(i), readCompoundArray[i].getName().c_str());
    }
}

TEST_F(VariableArraySubtypedStructTest, writeWrongArray)
{
    const size_t numElements = 33;
    vector_type<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        const ArrayElement arrayElement(
                static_cast<uint32_t>(i), string_type("Name") + zserio::toString<allocator_type>(i));
        compoundArray.push_back(arrayElement);
    }
    VariableArray variableArray;
    variableArray.setNumElements(static_cast<uint8_t>(numElements + 1));
    variableArray.setCompoundArray(compoundArray);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(variableArray.write(writer), zserio::CppRuntimeException);
}

} // namespace variable_array_subtyped_struct
} // namespace array_types
