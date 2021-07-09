#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/uint8_range_check/UInt8RangeCheckCompound.h"

namespace with_range_check_code
{
namespace uint8_range_check
{

class UInt8RangeCheckTest : public ::testing::Test
{
protected:
    void checkUInt8Value(uint8_t value)
    {
        UInt8RangeCheckCompound uint8RangeCheckCompound;
        uint8RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        uint8RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const UInt8RangeCheckCompound readUInt8RangeCheckCompound(reader);
        ASSERT_EQ(uint8RangeCheckCompound, readUInt8RangeCheckCompound);
    }

    static const uint8_t UINT8_LOWER_BOUND;
    static const uint8_t UINT8_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t UInt8RangeCheckTest::UINT8_LOWER_BOUND = UINT8_C(0);
const uint8_t UInt8RangeCheckTest::UINT8_UPPER_BOUND = UINT8_C(255);

TEST_F(UInt8RangeCheckTest, uint8LowerBound)
{
    checkUInt8Value(UINT8_LOWER_BOUND);
}

TEST_F(UInt8RangeCheckTest, uint8UpperBound)
{
    checkUInt8Value(UINT8_UPPER_BOUND);
}

} // namespace uint8_range_check
} // namespace with_range_check_code
