#include "default_values/optional_default_values/OptionalDefaultValues.h"
#include "gtest/gtest.h"
#include "test_utils/TestUtility.h"

namespace default_values
{
namespace optional_default_values
{

TEST(OptionalDefaultValuesTest, checkOptionalNoDefaultBoolField)
{
    OptionalDefaultValues data;
    ASSERT_FALSE(data.isOptionalNoDefaultBoolFieldSet());
}

TEST(OptionalDefaultValuesTest, checkOptionalNoDefaultStringField)
{
    OptionalDefaultValues data;
    ASSERT_FALSE(data.isOptionalNoDefaultStringFieldSet());
}

TEST(OptionalDefaultValuesTest, checkOptionalDefaultU32Field)
{
    OptionalDefaultValues data;
    ASSERT_TRUE(data.isOptionalDefaultU32FieldSet());
    ASSERT_EQ(13, data.getOptionalDefaultU32Field());
}

TEST(OptionalDefaultValuesTest, checkOptionalDefaultF64Field)
{
    OptionalDefaultValues data;
    ASSERT_TRUE(data.isOptionalDefaultF64FieldSet());
    ASSERT_EQ(1.234, data.getOptionalDefaultF64Field());
}

TEST(OptionalDefaultValuesTest, checkOptionalDefaultStringField)
{
    OptionalDefaultValues data;
    ASSERT_TRUE(data.isOptionalDefaultStringFieldSet());
    ASSERT_EQ("default", data.getOptionalDefaultStringField());
}

TEST(OptionalDefaultValuesTest, writeRead)
{
    OptionalDefaultValues data;
    test_utils::writeReadTest(data);
}

} // namespace optional_default_values
} // namespace default_values
