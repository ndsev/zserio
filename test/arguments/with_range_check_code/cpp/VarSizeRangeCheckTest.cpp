#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/varsize_range_check/VarSizeRangeCheckCompound.h"

namespace with_range_check_code
{
namespace varsize_range_check
{

class VarSizeRangeCheckTest : public ::testing::Test
{
protected:
    void checkVarSizeValue(uint32_t value)
    {
        VarSizeRangeCheckCompound varSizeRangeCheckCompound;
        varSizeRangeCheckCompound.setValue(value);
        zserio::BitStreamWriter writer(bitBuffer);
        varSizeRangeCheckCompound.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        const VarSizeRangeCheckCompound readVarSizeRangeCheckCompound(reader);
        ASSERT_EQ(varSizeRangeCheckCompound, readVarSizeRangeCheckCompound);
    }

    static const uint32_t VARSIZE_LOWER_BOUND;
    static const uint32_t VARSIZE_UPPER_BOUND;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint32_t VarSizeRangeCheckTest::VARSIZE_LOWER_BOUND = UINT32_C(0);
const uint32_t VarSizeRangeCheckTest::VARSIZE_UPPER_BOUND = (UINT32_C(1) << 31) - 1;

TEST_F(VarSizeRangeCheckTest, varSizeLowerBound)
{
    checkVarSizeValue(VARSIZE_LOWER_BOUND);
}

TEST_F(VarSizeRangeCheckTest, varSizeUpperBound)
{
    checkVarSizeValue(VARSIZE_UPPER_BOUND);
}

TEST_F(VarSizeRangeCheckTest, varSizeAboveUpperBound)
{
    try
    {
        checkVarSizeValue(VARSIZE_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 2147483648 of VarSizeRangeCheckCompound.value exceeds the range of "
                "<0..2147483647>!", excpt.what());
    }
}

} // namespace varsize_range_check
} // namespace with_range_check_code
