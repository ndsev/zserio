#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/int4_range_check/Int4RangeCheckCompound.h"

namespace with_range_check_code
{
namespace int4_range_check
{

class Int4RangeCheckTest : public ::testing::Test
{
protected:
    void checkInt4Value(int8_t value)
    {
        Int4RangeCheckCompound int4RangeCheckCompound;
        int4RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer;
        int4RangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const Int4RangeCheckCompound readInt4RangeCheckCompound(reader);
        ASSERT_EQ(int4RangeCheckCompound, readInt4RangeCheckCompound);
    }

    static const int8_t     INT4_LOWER_BOUND = INT8_C(-8);
    static const int8_t     INT4_UPPER_BOUND = INT8_C(7);
};

TEST_F(Int4RangeCheckTest, int4LowerBound)
{
    checkInt4Value(INT4_LOWER_BOUND);
}

TEST_F(Int4RangeCheckTest, int4UpperBound)
{
    checkInt4Value(INT4_UPPER_BOUND);
}

TEST_F(Int4RangeCheckTest, int4BelowLowerBound)
{
    try
    {
        checkInt4Value(INT4_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value -9 of Int4RangeCheckCompound.value exceeds the range of <-8..7>!", excpt.what());
    }
}

TEST_F(Int4RangeCheckTest, int4AboveUpperBound)
{
    try
    {
        checkInt4Value(INT4_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 8 of Int4RangeCheckCompound.value exceeds the range of <-8..7>!", excpt.what());
    }
}

} // namespace int4_range_check
} // namespace with_range_check_code
