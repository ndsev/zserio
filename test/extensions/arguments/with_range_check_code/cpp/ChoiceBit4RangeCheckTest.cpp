#include "gtest/gtest.h"
#include "with_range_check_code/choice_bit4_range_check/ChoiceBit4RangeCheckCompound.h"
#include "zserio/CppRuntimeException.h"

namespace with_range_check_code
{
namespace choice_bit4_range_check
{

class ChoiceBit4RangeCheckTest : public ::testing::Test
{
protected:
    void checkChoiceBit4Value(uint8_t value)
    {
        ChoiceBit4RangeCheckCompound choiceBit4RangeCheckCompound;
        const bool selector = true;
        choiceBit4RangeCheckCompound.initialize(selector);
        choiceBit4RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        choiceBit4RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const ChoiceBit4RangeCheckCompound readChoiceBit4RangeCheckCompound(reader, selector);
        ASSERT_EQ(choiceBit4RangeCheckCompound, readChoiceBit4RangeCheckCompound);
    }

    static const uint8_t BIT4_LOWER_BOUND;
    static const uint8_t BIT4_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint8_t ChoiceBit4RangeCheckTest::BIT4_LOWER_BOUND = UINT8_C(0);
const uint8_t ChoiceBit4RangeCheckTest::BIT4_UPPER_BOUND = UINT8_C(15);

TEST_F(ChoiceBit4RangeCheckTest, choiceBit4LowerBound)
{
    checkChoiceBit4Value(BIT4_LOWER_BOUND);
}

TEST_F(ChoiceBit4RangeCheckTest, choiceBit4UpperBound)
{
    checkChoiceBit4Value(BIT4_UPPER_BOUND);
}

TEST_F(ChoiceBit4RangeCheckTest, choiceBit4AboveUpperBound)
{
    try
    {
        checkChoiceBit4Value(BIT4_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ(
                "Value 16 of ChoiceBit4RangeCheckCompound.value exceeds the range of <0..15>!", excpt.what());
    }
}

} // namespace choice_bit4_range_check
} // namespace with_range_check_code
