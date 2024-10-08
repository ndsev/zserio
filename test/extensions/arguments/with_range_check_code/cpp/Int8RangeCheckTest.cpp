#include "gtest/gtest.h"
#include "with_range_check_code/int8_range_check/Int8RangeCheckCompound.h"
#include "zserio/CppRuntimeException.h"

namespace with_range_check_code
{
namespace int8_range_check
{

class Int8RangeCheckTest : public ::testing::Test
{
protected:
    void checkInt8Value(int8_t value)
    {
        Int8RangeCheckCompound int8RangeCheckCompound;
        int8RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        int8RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const Int8RangeCheckCompound readInt8RangeCheckCompound(reader);
        ASSERT_EQ(int8RangeCheckCompound, readInt8RangeCheckCompound);
    }

    static const int8_t INT8_LOWER_BOUND;
    static const int8_t INT8_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const int8_t Int8RangeCheckTest::INT8_LOWER_BOUND = INT8_C(-128);
const int8_t Int8RangeCheckTest::INT8_UPPER_BOUND = INT8_C(127);

TEST_F(Int8RangeCheckTest, int8LowerBound)
{
    checkInt8Value(INT8_LOWER_BOUND);
}

TEST_F(Int8RangeCheckTest, int8UpperBound)
{
    checkInt8Value(INT8_UPPER_BOUND);
}

} // namespace int8_range_check
} // namespace with_range_check_code
