#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/bit4_range_check/Bit4RangeCheckCompound.h"

namespace with_range_check_code
{
namespace bit4_range_check
{

class Bit4RangeCheckTest : public ::testing::Test
{
protected:
    void checkBit4Value(uint8_t value)
    {
        Bit4RangeCheckCompound bit4RangeCheckCompound;
        bit4RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        bit4RangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const Bit4RangeCheckCompound readBit4RangeCheckCompound(reader);
        ASSERT_EQ(bit4RangeCheckCompound, readBit4RangeCheckCompound);
    }

    static const uint8_t    BIT4_LOWER_BOUND = UINT8_C(0);
    static const uint8_t    BIT4_UPPER_BOUND = UINT8_C(15);
};

TEST_F(Bit4RangeCheckTest, bit4LowerBound)
{
    checkBit4Value(BIT4_LOWER_BOUND);
}

TEST_F(Bit4RangeCheckTest, bit4UpperBound)
{
    checkBit4Value(BIT4_UPPER_BOUND);
}

TEST_F(Bit4RangeCheckTest, bit4AboveUpperBound)
{
    try
    {
        checkBit4Value(BIT4_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 16 of Bit4RangeCheckCompound.value exceeds the range of <0..15>!", excpt.what());
    }
}

} // namespace bit4_range_check
} // namespace with_range_check_code
