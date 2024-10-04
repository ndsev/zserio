#include "gtest/gtest.h"
#include "with_range_check_code/varuint32_range_check/VarUInt32RangeCheckCompound.h"
#include "zserio/CppRuntimeException.h"

namespace with_range_check_code
{
namespace varuint32_range_check
{

class VarUInt32RangeCheckTest : public ::testing::Test
{
protected:
    void checkVarUInt32Value(uint32_t value)
    {
        VarUInt32RangeCheckCompound varUInt32RangeCheckCompound;
        varUInt32RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        varUInt32RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const VarUInt32RangeCheckCompound readVarUInt32RangeCheckCompound(reader);
        ASSERT_EQ(varUInt32RangeCheckCompound, readVarUInt32RangeCheckCompound);
    }

    static const uint32_t VARUINT32_LOWER_BOUND;
    static const uint32_t VARUINT32_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint32_t VarUInt32RangeCheckTest::VARUINT32_LOWER_BOUND = UINT32_C(0);
const uint32_t VarUInt32RangeCheckTest::VARUINT32_UPPER_BOUND = (UINT32_C(1) << 29U) - 1;

TEST_F(VarUInt32RangeCheckTest, varUInt32LowerBound)
{
    checkVarUInt32Value(VARUINT32_LOWER_BOUND);
}

TEST_F(VarUInt32RangeCheckTest, varUInt32UpperBound)
{
    checkVarUInt32Value(VARUINT32_UPPER_BOUND);
}

TEST_F(VarUInt32RangeCheckTest, varUInt32AboveUpperBound)
{
    try
    {
        checkVarUInt32Value(VARUINT32_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 536870912 of VarUInt32RangeCheckCompound.value exceeds the range of "
                     "<0..536870911>!",
                excpt.what());
    }
}

} // namespace varuint32_range_check
} // namespace with_range_check_code
