#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/varint16_range_check/VarInt16RangeCheckCompound.h"

namespace with_range_check_code
{
namespace varint16_range_check
{

class VarInt16RangeCheckTest : public ::testing::Test
{
protected:
    void checkVarInt16Value(int16_t value)
    {
        VarInt16RangeCheckCompound varInt16RangeCheckCompound;
        varInt16RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        varInt16RangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const VarInt16RangeCheckCompound readVarInt16RangeCheckCompound(reader);
        ASSERT_EQ(varInt16RangeCheckCompound, readVarInt16RangeCheckCompound);
    }

    static const int16_t    VARINT16_LOWER_BOUND = -((INT16_C(1) << 14) - 1);
    static const int16_t    VARINT16_UPPER_BOUND = (INT16_C(1) << 14) - 1;
};

TEST_F(VarInt16RangeCheckTest, varInt16LowerBound)
{
    checkVarInt16Value(VARINT16_LOWER_BOUND);
}

TEST_F(VarInt16RangeCheckTest, varInt16UpperBound)
{
    checkVarInt16Value(VARINT16_UPPER_BOUND);
}

TEST_F(VarInt16RangeCheckTest, varInt16BelowLowerBound)
{
    try
    {
        checkVarInt16Value(VARINT16_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value -16384 of VarInt16RangeCheckCompound.value exceeds the range of <-16383..16383>!",
                excpt.what());
    }
}

TEST_F(VarInt16RangeCheckTest, varInt16AboveUpperBound)
{
    try
    {
        checkVarInt16Value(VARINT16_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 16384 of VarInt16RangeCheckCompound.value exceeds the range of <-16383..16383>!",
                excpt.what());
    }
}

} // namespace varint16_range_check
} // namespace with_range_check_code
