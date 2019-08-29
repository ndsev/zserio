#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/variable_int_range_check/VariableIntRangeCheckCompound.h"

namespace with_range_check_code
{
namespace variable_int_range_check
{

class VariableIntRangeCheckTest : public ::testing::Test
{
protected:
    void checkVariableIntValue(int64_t value)
    {
        VariableIntRangeCheckCompound variableIntRangeCheckCompound;
        variableIntRangeCheckCompound.setNumBits(NUM_BITS);
        variableIntRangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        variableIntRangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const VariableIntRangeCheckCompound readVariableIntRangeCheckCompound(reader);
        ASSERT_EQ(variableIntRangeCheckCompound, readVariableIntRangeCheckCompound);
    }

    static const uint8_t    NUM_BITS = 10;

    static const int64_t    VARIABLE_INT_LOWER_BOUND = -(INT64_C(1) << (NUM_BITS - 1));
    static const int64_t    VARIABLE_INT_UPPER_BOUND = (INT64_C(1) << (NUM_BITS - 1)) - 1;
};

TEST_F(VariableIntRangeCheckTest, variableIntLowerBound)
{
    checkVariableIntValue(VARIABLE_INT_LOWER_BOUND);
}

TEST_F(VariableIntRangeCheckTest, variableIntUpperBound)
{
    checkVariableIntValue(VARIABLE_INT_UPPER_BOUND);
}

TEST_F(VariableIntRangeCheckTest, variableIntBelowLowerBound)
{
    try
    {
        checkVariableIntValue(VARIABLE_INT_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value -513 of VariableIntRangeCheckCompound.value exceeds the range of <-512..511>!",
                excpt.what());
    }
}

TEST_F(VariableIntRangeCheckTest, variableIntAboveUpperBound)
{
    try
    {
        checkVariableIntValue(VARIABLE_INT_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 512 of VariableIntRangeCheckCompound.value exceeds the range of <-512..511>!",
                excpt.what());
    }
}

} // namespace variable_int_range_check
} // namespace with_range_check_code
