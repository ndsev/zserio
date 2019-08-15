#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"
#include "with_range_check_code/int7_array_range_check/Int7ArrayRangeCheckCompound.h"

namespace with_range_check_code
{
namespace int7_array_range_check
{

class Int7ArrayRangeCheckTest : public ::testing::Test
{
protected:
    void checkInt7ArrayValue(int8_t value)
    {
        Int7ArrayRangeCheckCompound int7ArrayRangeCheckCompound;
        const uint16_t numElements = 1;
        int7ArrayRangeCheckCompound.setNumElements(numElements);
        std::vector<int8_t>& array = int7ArrayRangeCheckCompound.getArray();
        array.push_back(value);

        zserio::BitStreamWriter writer;
        int7ArrayRangeCheckCompound.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        const Int7ArrayRangeCheckCompound readInt7ArrayRangeCheckCompound(reader);
        ASSERT_EQ(int7ArrayRangeCheckCompound, readInt7ArrayRangeCheckCompound);
    }

    static const int8_t INT7_LOWER_BOUND;
    static const int8_t INT7_UPPER_BOUND;
};

const int8_t Int7ArrayRangeCheckTest::INT7_LOWER_BOUND = INT8_C(-64);
const int8_t Int7ArrayRangeCheckTest::INT7_UPPER_BOUND = INT8_C(63);

TEST_F(Int7ArrayRangeCheckTest, int7ArrayLowerBound)
{
    checkInt7ArrayValue(INT7_LOWER_BOUND);
}

TEST_F(Int7ArrayRangeCheckTest, int7ArrayUpperBound)
{
    checkInt7ArrayValue(INT7_UPPER_BOUND);
}

TEST_F(Int7ArrayRangeCheckTest, int7ArrayBelowLowerBound)
{
    try
    {
        checkInt7ArrayValue(INT7_LOWER_BOUND - 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value -65 of Int7ArrayRangeCheckCompound.array exceeds the range of <-64..63>!",
                excpt.what());
    }
}

TEST_F(Int7ArrayRangeCheckTest, int7ArrayAboveUpperBound)
{
    try
    {
        checkInt7ArrayValue(INT7_UPPER_BOUND + 1);
        FAIL() << "Actual: no exception, Expected: zserio::CppRuntimeException";
    }
    catch (const zserio::CppRuntimeException& excpt)
    {
        ASSERT_STREQ("Value 64 of Int7ArrayRangeCheckCompound.array exceeds the range of <-64..63>!",
                excpt.what());
    }
}

} // namespace int7_array_range_check
} // namespace with_range_check_code
