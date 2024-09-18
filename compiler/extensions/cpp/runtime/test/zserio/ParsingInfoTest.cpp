#include "gtest/gtest.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/ParsingInfo.h"

namespace zserio
{

TEST(ParsingInfoTest, defaultConstructor)
{
    ParsingInfo parsingInfo;
    ASSERT_THROW(parsingInfo.getBitPosition(), CppRuntimeException);
    ASSERT_THROW(parsingInfo.getBitSize(), CppRuntimeException);
}

TEST(ParsingInfoTest, bitPositionConstructor)
{
    ParsingInfo parsingInfo(0);
    ASSERT_EQ(0, parsingInfo.getBitPosition());
    ASSERT_THROW(parsingInfo.getBitSize(), CppRuntimeException);
}

TEST(ParsingInfoTest, initializeBitSize)
{
    ParsingInfo parsingInfo(1);
    parsingInfo.initializeBitSize(3);
    ASSERT_EQ(1, parsingInfo.getBitPosition());
    ASSERT_EQ(2, parsingInfo.getBitSize());

    ASSERT_THROW(parsingInfo.initializeBitSize(0), CppRuntimeException);
}

TEST(ParsingInfoTest, getBitPosition)
{
    ParsingInfo parsingInfo(1);
    ASSERT_EQ(1, parsingInfo.getBitPosition());
}

TEST(ParsingInfoTest, getBitSize)
{
    ParsingInfo parsingInfo(1);
    parsingInfo.initializeBitSize(3);
    ASSERT_EQ(2, parsingInfo.getBitSize());
}

} // namespace zserio
