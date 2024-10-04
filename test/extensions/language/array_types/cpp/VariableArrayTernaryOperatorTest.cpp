#include "array_types/variable_array_ternary_operator/VariableArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace variable_array_ternary_operator
{

using allocator_type = VariableArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class VariableArrayTernaryOperator : public ::testing::Test
{
protected:
    void fillVariableArray(VariableArray& variableArray, bool isFirstSizeUsed)
    {
        variableArray.setIsFirstSizeUsed(isFirstSizeUsed);
        const uint8_t currentSize =
                (isFirstSizeUsed) ? variableArray.getFirstSize() : variableArray.getSecondSize();
        vector_type<VariableArrayElement>& array = variableArray.getArray();
        array.clear();
        const size_t arraySize = static_cast<size_t>(currentSize) * static_cast<size_t>(currentSize);
        for (size_t i = 0; i < arraySize; ++i)
        {
            VariableArrayElement variableArrayElement;
            variableArrayElement.setElement(i);
            variableArrayElement.initialize(currentSize);
            array.push_back(variableArrayElement);
        }
    }

    void checkVariableArray(const VariableArray& variableArray, bool isFirstSizeUsed)
    {
        ASSERT_EQ(isFirstSizeUsed, variableArray.getIsFirstSizeUsed());
        const uint8_t currentSize = (isFirstSizeUsed) ? FIRST_SIZE : SECOND_SIZE;
        const vector_type<VariableArrayElement>& array = variableArray.getArray();
        const size_t arraySize = static_cast<size_t>(currentSize) * static_cast<size_t>(currentSize);
        for (size_t i = 0; i < arraySize; ++i)
        {
            const VariableArrayElement& variableArrayElement = array[i];
            ASSERT_EQ(currentSize, variableArrayElement.getBitSize());
            ASSERT_EQ(i, variableArrayElement.getElement());
        }
    }

    void testWriteReadFile(bool isFirstSizeUsed)
    {
        VariableArray variableArray;
        fillVariableArray(variableArray, isFirstSizeUsed);

        const std::string blobName = (isFirstSizeUsed) ? BLOB_NAME_FIRST : BLOB_NAME_SECOND;
        zserio::serializeToFile(variableArray, blobName);

        VariableArray readVariableArray = zserio::deserializeFromFile<VariableArray>(blobName);
        checkVariableArray(readVariableArray, isFirstSizeUsed);
        ASSERT_EQ(variableArray, readVariableArray);
    }

    static const uint8_t FIRST_SIZE;
    static const uint8_t SECOND_SIZE;

    static const std::string BLOB_NAME_FIRST;
    static const std::string BLOB_NAME_SECOND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t VariableArrayTernaryOperator::FIRST_SIZE = 10;
const uint8_t VariableArrayTernaryOperator::SECOND_SIZE = 20;

const std::string VariableArrayTernaryOperator::BLOB_NAME_FIRST =
        "language/array_types/variable_array_ternary_operator1.blob";
const std::string VariableArrayTernaryOperator::BLOB_NAME_SECOND =
        "language/array_types/variable_array_ternary_operator2.blob";

TEST_F(VariableArrayTernaryOperator, firstWriteReadFile)
{
    const bool isFirstOffsetUsed = true;
    testWriteReadFile(isFirstOffsetUsed);
}

TEST_F(VariableArrayTernaryOperator, secondWriteReadFile)
{
    const bool isFirstOffsetUsed = false;
    testWriteReadFile(isFirstOffsetUsed);
}

} // namespace variable_array_ternary_operator
} // namespace array_types
