#include "zserio/BuildInOperators.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(BuildInOperatorsTest, getNumBits)
{
    EXPECT_EQ(0, getNumBits(0));
    EXPECT_EQ(1, getNumBits(1));
    EXPECT_EQ(1, getNumBits(2));
    EXPECT_EQ(2, getNumBits(3));
    EXPECT_EQ(2, getNumBits(4));
    EXPECT_EQ(3, getNumBits(5));
    EXPECT_EQ(3, getNumBits(6));
    EXPECT_EQ(3, getNumBits(7));
    EXPECT_EQ(3, getNumBits(8));
    EXPECT_EQ(4, getNumBits(16));
    EXPECT_EQ(5, getNumBits(32));
    EXPECT_EQ(6, getNumBits(64));
    EXPECT_EQ(7, getNumBits(128));
    EXPECT_EQ(8, getNumBits(256));
    EXPECT_EQ(9, getNumBits(512));
    EXPECT_EQ(10, getNumBits(1024));
    EXPECT_EQ(11, getNumBits(2048));
    EXPECT_EQ(12, getNumBits(4096));
    EXPECT_EQ(13, getNumBits(8192));
    EXPECT_EQ(14, getNumBits(16384));
    EXPECT_EQ(15, getNumBits(32768));
    EXPECT_EQ(16, getNumBits(65536));
    EXPECT_EQ(24, getNumBits(UINT64_C(1) << 24));
    EXPECT_EQ(25, getNumBits((UINT64_C(1) << 24) + 1));
    EXPECT_EQ(32, getNumBits(UINT64_C(1) << 32));
    EXPECT_EQ(33, getNumBits((UINT64_C(1) << 32) + 1));
    EXPECT_EQ(63, getNumBits(UINT64_C(1) << 63));
    EXPECT_EQ(64, getNumBits((UINT64_C(1) << 63) + 1));
}

} // namespace zserio
