#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/dynamic_bit_range_check/DynamicBitRangeCheckCompound.h"

namespace with_range_check_code
{
namespace dynamic_bit_range_check
{

class DynamicBitRangeCheckTest : public ::testing::Test
{
protected:
    void checkDynamicBitValue(uint64_t value)
    {
        DynamicBitRangeCheckCompound dynamicBitRangeCheckCompound;
        dynamicBitRangeCheckCompound.setNumBits(NUM_BITS);
        dynamicBitRangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        dynamicBitRangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const DynamicBitRangeCheckCompound readDynamicBitRangeCheckCompound(reader);
        ASSERT_EQ(dynamicBitRangeCheckCompound, readDynamicBitRangeCheckCompound);
    }

    static const uint8_t    NUM_BITS = 10;

    static const uint64_t   DYNAMIC_BIT_LOWER_BOUND = UINT64_C(0);
    static const uint64_t   DYNAMIC_BIT_UPPER_BOUND = (UINT64_C(1) << NUM_BITS) - 1;
};

TEST_F(DynamicBitRangeCheckTest, dynamicBitLowerBound)
{
    checkDynamicBitValue(DYNAMIC_BIT_LOWER_BOUND);
}

TEST_F(DynamicBitRangeCheckTest, dynamicBitUpperBound)
{
    checkDynamicBitValue(DYNAMIC_BIT_UPPER_BOUND);
}

TEST_F(DynamicBitRangeCheckTest, dynamicBitAboveUpperBound)
{
    try
    {
        checkDynamicBitValue(DYNAMIC_BIT_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 1024 of DynamicBitRangeCheckCompound.value exceeds the range of <0..1023>!",
                excpt.what());
    }
}

} // namespace dynamic_bit_range_check
} // namespace with_range_check_code
