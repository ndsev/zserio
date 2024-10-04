#include "gtest/gtest.h"
#include "with_range_check_code/union_int4_range_check/UnionInt4RangeCheckCompound.h"
#include "zserio/CppRuntimeException.h"

namespace with_range_check_code
{
namespace union_int4_range_check
{

class UnionInt4RangeCheckTest : public ::testing::Test
{
protected:
    void checkUnionInt4Value(int8_t value)
    {
        UnionInt4RangeCheckCompound unionInt4RangeCheckCompound;
        unionInt4RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        unionInt4RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const UnionInt4RangeCheckCompound readUnionInt4RangeCheckCompound(reader);
        ASSERT_EQ(unionInt4RangeCheckCompound, readUnionInt4RangeCheckCompound);
    }

    static const int8_t INT4_LOWER_BOUND;
    static const int8_t INT4_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const int8_t UnionInt4RangeCheckTest::INT4_LOWER_BOUND = INT8_C(-8);
const int8_t UnionInt4RangeCheckTest::INT4_UPPER_BOUND = INT8_C(7);

TEST_F(UnionInt4RangeCheckTest, unionInt4LowerBound)
{
    checkUnionInt4Value(INT4_LOWER_BOUND);
}

TEST_F(UnionInt4RangeCheckTest, unionInt4UpperBound)
{
    checkUnionInt4Value(INT4_UPPER_BOUND);
}

TEST_F(UnionInt4RangeCheckTest, unionInt4BelowLowerBound)
{
    try
    {
        checkUnionInt4Value(INT4_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ(
                "Value -9 of UnionInt4RangeCheckCompound.value exceeds the range of <-8..7>!", excpt.what());
    }
}

TEST_F(UnionInt4RangeCheckTest, unionInt4AboveUpperBound)
{
    try
    {
        checkUnionInt4Value(INT4_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ(
                "Value 8 of UnionInt4RangeCheckCompound.value exceeds the range of <-8..7>!", excpt.what());
    }
}

} // namespace union_int4_range_check
} // namespace with_range_check_code
