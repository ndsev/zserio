#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/variable_bit_range_check/VariableBitRangeCheckCompound.h"

namespace with_range_check_code
{
namespace variable_bit_range_check
{

class VariableBitRangeCheckTest : public ::testing::Test
{
protected:
    void checkVariableBitValue(uint64_t value)
    {
        VariableBitRangeCheckCompound variableBitRangeCheckCompound;
        variableBitRangeCheckCompound.setNumBits(NUM_BITS);
        variableBitRangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        variableBitRangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const VariableBitRangeCheckCompound readVariableBitRangeCheckCompound(reader);
        ASSERT_EQ(variableBitRangeCheckCompound, readVariableBitRangeCheckCompound);
    }

    static const uint8_t NUM_BITS;
    static const uint64_t VARIABLE_BIT_LOWER_BOUND;
    static const uint64_t VARIABLE_BIT_UPPER_BOUND;
};

const uint8_t VariableBitRangeCheckTest::NUM_BITS = 10;
const uint64_t VariableBitRangeCheckTest::VARIABLE_BIT_LOWER_BOUND = UINT64_C(0);
const uint64_t VariableBitRangeCheckTest::VARIABLE_BIT_UPPER_BOUND = (UINT64_C(1) << NUM_BITS) - 1;

TEST_F(VariableBitRangeCheckTest, variableBitLowerBound)
{
    checkVariableBitValue(VARIABLE_BIT_LOWER_BOUND);
}

TEST_F(VariableBitRangeCheckTest, variableBitUpperBound)
{
    checkVariableBitValue(VARIABLE_BIT_UPPER_BOUND);
}

TEST_F(VariableBitRangeCheckTest, variableBitAboveUpperBound)
{
    try
    {
        checkVariableBitValue(VARIABLE_BIT_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 1024 of VariableBitRangeCheckCompound.value exceeds the range of <0..1023>!",
                excpt.what());
    }
}

} // namespace variable_bit_range_check
} // namespace with_range_check_code
