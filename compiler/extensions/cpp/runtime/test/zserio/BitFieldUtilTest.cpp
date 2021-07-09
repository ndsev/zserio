#include "zserio/BitFieldUtil.h"
#include "zserio/Types.h"
#include "zserio/CppRuntimeException.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BitFieldUtilTest, getBitFieldLowerBound)
{
    EXPECT_THROW(getBitFieldLowerBound(0, true), CppRuntimeException);
    EXPECT_THROW(getBitFieldLowerBound(65, true), CppRuntimeException);

    EXPECT_THROW(getBitFieldLowerBound(0, false), CppRuntimeException);
    EXPECT_THROW(getBitFieldLowerBound(65, false), CppRuntimeException);

    EXPECT_EQ(INT64_C(-1), getBitFieldLowerBound(1, true));
    EXPECT_EQ(INT64_C(-2), getBitFieldLowerBound(2, true));
    EXPECT_EQ(INT64_C(-128), getBitFieldLowerBound(8, true));
    EXPECT_EQ(INT64_C(-32768), getBitFieldLowerBound(16, true));
    EXPECT_EQ(INT64_C(-2147483648), getBitFieldLowerBound(32, true));

    // -1 to avoid gcc warning about 9223372036854775808 being so high that's it's treated as unsigned
    EXPECT_EQ(INT64_C(-9223372036854775807)-1, getBitFieldLowerBound(64, true));

    EXPECT_EQ(UINT64_C(0), getBitFieldLowerBound(1, false));
    EXPECT_EQ(UINT64_C(0), getBitFieldLowerBound(2, false));
    EXPECT_EQ(UINT64_C(0), getBitFieldLowerBound(8, false));
    EXPECT_EQ(UINT64_C(0), getBitFieldLowerBound(16, false));
    EXPECT_EQ(UINT64_C(0), getBitFieldLowerBound(32, false));
    EXPECT_EQ(UINT64_C(0), getBitFieldLowerBound(64, false));
}

TEST(BitFieldUtilTest, getBitFieldUpperBound)
{
    EXPECT_THROW(getBitFieldUpperBound(0, true), CppRuntimeException);
    EXPECT_THROW(getBitFieldUpperBound(65, true), CppRuntimeException);

    EXPECT_THROW(getBitFieldUpperBound(0, false), CppRuntimeException);
    EXPECT_THROW(getBitFieldUpperBound(65, false), CppRuntimeException);

    EXPECT_EQ(UINT64_C(0), getBitFieldUpperBound(1, true));
    EXPECT_EQ(UINT64_C(1), getBitFieldUpperBound(2, true));
    EXPECT_EQ(UINT64_C(127), getBitFieldUpperBound(8, true));
    EXPECT_EQ(UINT64_C(32767), getBitFieldUpperBound(16, true));
    EXPECT_EQ(UINT64_C(2147483647), getBitFieldUpperBound(32, true));
    EXPECT_EQ(UINT64_C(9223372036854775807), getBitFieldUpperBound(64, true));

    EXPECT_EQ(UINT64_C(1), getBitFieldUpperBound(1, false));
    EXPECT_EQ(UINT64_C(3), getBitFieldUpperBound(2, false));
    EXPECT_EQ(UINT64_C(255), getBitFieldUpperBound(8, false));
    EXPECT_EQ(UINT64_C(65535), getBitFieldUpperBound(16, false));
    EXPECT_EQ(UINT64_C(4294967295), getBitFieldUpperBound(32, false));
    EXPECT_EQ(UINT64_C(0xffffffffffffffff), getBitFieldUpperBound(64, false));
}

} // namespace zserio
