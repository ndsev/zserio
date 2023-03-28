#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/varuint64_range_check/VarUInt64RangeCheckCompound.h"

namespace with_range_check_code
{
namespace varuint64_range_check
{

class VarUInt64RangeCheckTest : public ::testing::Test
{
protected:
    void checkVarUInt64Value(uint64_t value)
    {
        VarUInt64RangeCheckCompound varUInt64RangeCheckCompound;
        varUInt64RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        varUInt64RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const VarUInt64RangeCheckCompound readVarUInt64RangeCheckCompound(reader);
        ASSERT_EQ(varUInt64RangeCheckCompound, readVarUInt64RangeCheckCompound);
    }

    static const uint64_t VARUINT64_LOWER_BOUND;
    static const uint64_t VARUINT64_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint64_t VarUInt64RangeCheckTest::VARUINT64_LOWER_BOUND = UINT64_C(0);
const uint64_t VarUInt64RangeCheckTest::VARUINT64_UPPER_BOUND = (UINT64_C(1) << 57U) - 1;

TEST_F(VarUInt64RangeCheckTest, varUInt64LowerBound)
{
    checkVarUInt64Value(VARUINT64_LOWER_BOUND);
}

TEST_F(VarUInt64RangeCheckTest, varUInt64UpperBound)
{
    checkVarUInt64Value(VARUINT64_UPPER_BOUND);
}

TEST_F(VarUInt64RangeCheckTest, varUInt64AboveUpperBound)
{
    try
    {
        checkVarUInt64Value(VARUINT64_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 144115188075855872 of VarUInt64RangeCheckCompound.value exceeds the range of "
                "<0..144115188075855871>!", excpt.what());
    }
}

} // namespace varuint64_range_check
} // namespace with_range_check_code
