#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "identifiers/bitmask_name_clashing_with_java/BitmaskNameClashingWithJava.h"

namespace identifiers
{
namespace bitmask_name_clashing_with_java
{

class BitmaskNameClashingWithJavaTest : public ::testing::Test
{
protected:
    static const size_t BIT_SIZE;
};

const size_t BitmaskNameClashingWithJavaTest::BIT_SIZE = 8;

TEST_F(BitmaskNameClashingWithJavaTest, emptyConstructor)
{
    BitmaskNameClashingWithJava bitmaskNameClashingWithJava;
    ASSERT_EQ(0, bitmaskNameClashingWithJava.getStringField().getValue());
}

TEST_F(BitmaskNameClashingWithJavaTest, bitSizeOf)
{
    BitmaskNameClashingWithJava bitmaskNameClashingWithJava{String::Values::WRITE};
    ASSERT_EQ(BIT_SIZE, bitmaskNameClashingWithJava.bitSizeOf());
}

TEST_F(BitmaskNameClashingWithJavaTest, toStringMethod)
{
    BitmaskNameClashingWithJava bitmaskNameClashingWithJava{String::Values::READ};
    ASSERT_EQ(std::string{"1[READ]"}, bitmaskNameClashingWithJava.getStringField().toString());
}

} // namespace bitmask_name_clashing_with_java
} // namespace identifiers
