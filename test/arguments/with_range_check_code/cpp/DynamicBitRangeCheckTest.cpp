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
    void checkDynamicBitValue(uint8_t numBits, uint64_t value)
    {
        DynamicBitRangeCheckCompound dynamicBitRangeCheckCompound;
        dynamicBitRangeCheckCompound.setNumBits(numBits);
        dynamicBitRangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        dynamicBitRangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const DynamicBitRangeCheckCompound readDynamicBitRangeCheckCompound(reader);
        ASSERT_EQ(dynamicBitRangeCheckCompound, readDynamicBitRangeCheckCompound);
    }

    static const uint8_t NUM_BITS;
    static const uint64_t DYNAMIC_BIT_LOWER_BOUND;
    static const uint64_t DYNAMIC_BIT_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t DynamicBitRangeCheckTest::NUM_BITS = 10;
const uint64_t DynamicBitRangeCheckTest::DYNAMIC_BIT_LOWER_BOUND = UINT64_C(0);
const uint64_t DynamicBitRangeCheckTest::DYNAMIC_BIT_UPPER_BOUND = (UINT64_C(1) << NUM_BITS) - 1;

TEST_F(DynamicBitRangeCheckTest, dynamicBitLowerBound)
{
    checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_LOWER_BOUND);
}

TEST_F(DynamicBitRangeCheckTest, dynamicBitUpperBound)
{
    checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_UPPER_BOUND);
}

TEST_F(DynamicBitRangeCheckTest, dynamicBitAboveUpperBound)
{
    try
    {
        checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 1024 of DynamicBitRangeCheckCompound.value exceeds the range of <0..1023>!",
                excpt.what());
    }
}

TEST_F(DynamicBitRangeCheckTest, numBitsMax)
{
    checkDynamicBitValue(64, UINT64_MAX);
}

TEST_F(DynamicBitRangeCheckTest, numBitsAboveMax)
{
    try
    {
        checkDynamicBitValue(65, UINT64_MAX);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Asking for bound of bitfield with invalid length 65", excpt.what());
    }
}

} // namespace dynamic_bit_range_check
} // namespace with_range_check_code
