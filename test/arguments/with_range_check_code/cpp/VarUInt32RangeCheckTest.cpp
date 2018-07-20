#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"

#include "with_range_check_code/varuint32_range_check/VarUInt32RangeCheckCompound.h"

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
        zserio::BitStreamWriter writer;
        varUInt32RangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const VarUInt32RangeCheckCompound readVarUInt32RangeCheckCompound(reader);
        ASSERT_EQ(varUInt32RangeCheckCompound, readVarUInt32RangeCheckCompound);
    }

    static const uint32_t   VARUINT32_LOWER_BOUND = UINT32_C(0);
    static const uint32_t   VARUINT32_UPPER_BOUND = (UINT32_C(1) << 29) - 1;
};

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
                "<0..536870911>!", excpt.what());
    }
}

} // namespace varuint32_range_check
} // namespace with_range_check_code
