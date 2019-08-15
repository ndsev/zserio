#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/varint64_range_check/VarInt64RangeCheckCompound.h"

namespace with_range_check_code
{
namespace varint64_range_check
{

class VarInt64RangeCheckTest : public ::testing::Test
{
protected:
    void checkVarInt64Value(int64_t value)
    {
        VarInt64RangeCheckCompound varInt64RangeCheckCompound;
        varInt64RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        varInt64RangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const VarInt64RangeCheckCompound readVarInt64RangeCheckCompound(reader);
        ASSERT_EQ(varInt64RangeCheckCompound, readVarInt64RangeCheckCompound);
    }

    static const int64_t VARINT64_LOWER_BOUND;
    static const int64_t VARINT64_UPPER_BOUND;
};

const int64_t VarInt64RangeCheckTest::VARINT64_LOWER_BOUND = -((INT64_C(1) << 56) - 1);
const int64_t VarInt64RangeCheckTest::VARINT64_UPPER_BOUND = (INT64_C(1) << 56) - 1;

TEST_F(VarInt64RangeCheckTest, varInt64LowerBound)
{
    checkVarInt64Value(VARINT64_LOWER_BOUND);
}

TEST_F(VarInt64RangeCheckTest, varInt64UpperBound)
{
    checkVarInt64Value(VARINT64_UPPER_BOUND);
}

TEST_F(VarInt64RangeCheckTest, varInt64BelowLowerBound)
{
    try
    {
        checkVarInt64Value(VARINT64_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value -72057594037927936 of VarInt64RangeCheckCompound.value exceeds the range of "
                "<-72057594037927935..72057594037927935>!", excpt.what());
    }
}

TEST_F(VarInt64RangeCheckTest, varInt64AboveUpperBound)
{
    try
    {
        checkVarInt64Value(VARINT64_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 72057594037927936 of VarInt64RangeCheckCompound.value exceeds the range of "
                "<-72057594037927935..72057594037927935>!", excpt.what());
    }
}

} // namespace varint64_range_check
} // namespace with_range_check_code
