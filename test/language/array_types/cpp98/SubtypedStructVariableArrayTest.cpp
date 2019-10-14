#include "gtest/gtest.h"

#include "array_types/subtyped_struct_variable_array/SubtypedStructVariableArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/StringConvertUtil.h"

namespace array_types
{
namespace subtyped_struct_variable_array
{

class SubtypedStructVariableArrayTest : public ::testing::Test
{
protected:
    void writeSubtypedStructVariableArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeSignedBits(static_cast<uint8_t>(numElements), 8);
        for (size_t i = 0; i < numElements; ++i)
        {
            writer.writeBits(static_cast<uint32_t>(i), 32);
            writer.writeString(std::string("Name") + zserio::convertToString(i));
        }
    }
};

TEST_F(SubtypedStructVariableArrayTest, bitSizeOf)
{
    const size_t numElements = 33;
    zserio::ObjectArray<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        ArrayElement arrayElement;
        arrayElement.setId(static_cast<uint32_t>(i));
        arrayElement.setName(std::string("Name") + zserio::convertToString(i));
        compoundArray.push_back(arrayElement);
    }
    SubtypedStructVariableArray subtypedStructVariableArray;
    subtypedStructVariableArray.setNumElements(static_cast<uint8_t>(numElements));
    subtypedStructVariableArray.setCompoundArray(compoundArray);

    const size_t bitPosition = 2;
    const size_t numOneNumberIndexes = (numElements > 9) ? 10 : numElements;
    const size_t expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
    ASSERT_EQ(expectedBitSize, subtypedStructVariableArray.bitSizeOf(bitPosition));
}

TEST_F(SubtypedStructVariableArrayTest, initializeOffsets)
{
    const size_t numElements = 33;
    zserio::ObjectArray<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        ArrayElement arrayElement;
        arrayElement.setId(static_cast<uint32_t>(i));
        arrayElement.setName(std::string("Name") + zserio::convertToString(i));
        compoundArray.push_back(arrayElement);
    }
    SubtypedStructVariableArray subtypedStructVariableArray;
    subtypedStructVariableArray.setNumElements(static_cast<uint8_t>(numElements));
    subtypedStructVariableArray.setCompoundArray(compoundArray);

    const size_t bitPosition = 2;
    const size_t numOneNumberIndexes = (numElements > 9) ? 10 : numElements;
    const size_t expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
    ASSERT_EQ(expectedEndBitPosition, subtypedStructVariableArray.initializeOffsets(bitPosition));
}

TEST_F(SubtypedStructVariableArrayTest, read)
{
    const size_t numElements = 59;
    zserio::BitStreamWriter writer;
    writeSubtypedStructVariableArrayToByteArray(writer, numElements);
    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    SubtypedStructVariableArray subtypedStructVariableArray(reader);

    ASSERT_EQ(numElements, static_cast<size_t>(subtypedStructVariableArray.getNumElements()));
    const zserio::ObjectArray<ArrayElement>& compoundArray = subtypedStructVariableArray.getCompoundArray();
    ASSERT_EQ(numElements, compoundArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(i, compoundArray[i].getId());
        ASSERT_EQ(std::string("Name") + zserio::convertToString(i), compoundArray[i].getName());
    }
}

TEST_F(SubtypedStructVariableArrayTest, write)
{
    const size_t numElements = 33;
    zserio::ObjectArray<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        ArrayElement arrayElement;
        arrayElement.setId(static_cast<uint32_t>(i));
        arrayElement.setName(std::string("Name") + zserio::convertToString(i));
        compoundArray.push_back(arrayElement);
    }
    SubtypedStructVariableArray subtypedStructVariableArray;
    subtypedStructVariableArray.setNumElements(static_cast<int8_t>(numElements));
    subtypedStructVariableArray.setCompoundArray(compoundArray);

    zserio::BitStreamWriter writer;
    subtypedStructVariableArray.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    SubtypedStructVariableArray readSubtypedStructVariableArray(reader);
    const zserio::ObjectArray<ArrayElement>& readCompoundArray =
            readSubtypedStructVariableArray.getCompoundArray();
    ASSERT_EQ(numElements, readCompoundArray.size());
    for (size_t i = 0; i < numElements; ++i)
    {
        ASSERT_EQ(i, readCompoundArray[i].getId());
        ASSERT_EQ(std::string("Name") + zserio::convertToString(i), readCompoundArray[i].getName());
    }
}

TEST_F(SubtypedStructVariableArrayTest, writeWrongArray)
{
    const size_t numElements = 33;
    zserio::ObjectArray<ArrayElement> compoundArray;
    compoundArray.reserve(numElements);
    for (size_t i = 0; i < numElements; ++i)
    {
        ArrayElement arrayElement;
        arrayElement.setId(static_cast<uint32_t>(i));
        arrayElement.setName(std::string("Name") + zserio::convertToString(i));
        compoundArray.push_back(arrayElement);
    }
    SubtypedStructVariableArray subtypedStructVariableArray;
    subtypedStructVariableArray.setNumElements(static_cast<int8_t>(numElements + 1));
    subtypedStructVariableArray.setCompoundArray(compoundArray);

    zserio::BitStreamWriter writer;
    ASSERT_THROW(subtypedStructVariableArray.write(writer), zserio::CppRuntimeException);
}

} // namespace subtyped_struct_variable_array
} // namespace array_types
