#include "gtest/gtest.h"
#include "with_range_check_code/varuint16_range_check/VarUInt16RangeCheckCompound.h"
#include "zserio/CppRuntimeException.h"

namespace with_range_check_code
{
namespace varuint16_range_check
{

class VarUInt16RangeCheckTest : public ::testing::Test
{
protected:
    void checkVarUInt16Value(uint16_t value)
    {
        VarUInt16RangeCheckCompound varUInt16RangeCheckCompound;
        varUInt16RangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        varUInt16RangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const VarUInt16RangeCheckCompound readVarUInt16RangeCheckCompound(reader);
        ASSERT_EQ(varUInt16RangeCheckCompound, readVarUInt16RangeCheckCompound);
    }

    static const uint16_t VARUINT16_LOWER_BOUND;
    static const uint16_t VARUINT16_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint16_t VarUInt16RangeCheckTest::VARUINT16_LOWER_BOUND = UINT16_C(0);
const uint16_t VarUInt16RangeCheckTest::VARUINT16_UPPER_BOUND = static_cast<uint16_t>((1U << 15U) - 1);

TEST_F(VarUInt16RangeCheckTest, varUInt16LowerBound)
{
    checkVarUInt16Value(VARUINT16_LOWER_BOUND);
}

TEST_F(VarUInt16RangeCheckTest, varUInt16UpperBound)
{
    checkVarUInt16Value(VARUINT16_UPPER_BOUND);
}

TEST_F(VarUInt16RangeCheckTest, varUInt16AboveUpperBound)
{
    try
    {
        checkVarUInt16Value(VARUINT16_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 32768 of VarUInt16RangeCheckCompound.value exceeds the range of <0..32767>!",
                excpt.what());
    }
}

} // namespace varuint16_range_check
} // namespace with_range_check_code
