#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/dynamic_int_range_check/DynamicIntRangeCheckCompound.h"

namespace with_range_check_code
{
namespace dynamic_int_range_check
{

class DynamicIntRangeCheckTest : public ::testing::Test
{
protected:
    void checkDynamicIntValue(uint8_t numBits, int64_t value)
    {
        DynamicIntRangeCheckCompound dynamicIntRangeCheckCompound;
        dynamicIntRangeCheckCompound.setNumBits(numBits);
        dynamicIntRangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);

        dynamicIntRangeCheckCompound.write(writer);
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const DynamicIntRangeCheckCompound readDynamicIntRangeCheckCompound(reader);
        ASSERT_EQ(dynamicIntRangeCheckCompound, readDynamicIntRangeCheckCompound);
    }

    static const uint8_t NUM_BITS;
    static const int64_t DYNAMIC_INT_LOWER_BOUND;
    static const int64_t DYNAMIC_INT_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t DynamicIntRangeCheckTest::NUM_BITS = 10;
const int64_t DynamicIntRangeCheckTest::DYNAMIC_INT_LOWER_BOUND = -(INT64_C(1) << (NUM_BITS - 1));
const int64_t DynamicIntRangeCheckTest::DYNAMIC_INT_UPPER_BOUND = (INT64_C(1) << (NUM_BITS - 1)) - 1;

TEST_F(DynamicIntRangeCheckTest, dynamicIntLowerBound)
{
    checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_LOWER_BOUND);
}

TEST_F(DynamicIntRangeCheckTest, dynamicIntUpperBound)
{
    checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_UPPER_BOUND);
}

TEST_F(DynamicIntRangeCheckTest, dynamicIntBelowLowerBound)
{
    try
    {
        checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value -513 of DynamicIntRangeCheckCompound.value exceeds the range of <-512..511>!",
                excpt.what());
    }
}

TEST_F(DynamicIntRangeCheckTest, dynamicIntAboveUpperBound)
{
    try
    {
        checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 512 of DynamicIntRangeCheckCompound.value exceeds the range of <-512..511>!",
                excpt.what());
    }
}

TEST_F(DynamicIntRangeCheckTest, numBitsMax)
{
    checkDynamicIntValue(64, INT64_MIN);
}

TEST_F(DynamicIntRangeCheckTest, numBitsAboveMax)
{
    try
    {
        checkDynamicIntValue(65, INT64_MIN);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Asking for bound of bitfield with invalid length 65", excpt.what());
    }
}

} // namespace dynamic_int_range_check
} // namespace with_range_check_code
