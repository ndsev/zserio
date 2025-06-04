#include "default_values/extended_default_values/ExtendedDefaultValues.h"
#include "gtest/gtest.h"
#include "test_utils/TestUtility.h"

namespace default_values
{
namespace extended_default_values
{

TEST(ExtendedDefaultValuesTest, checkNoDefaultU32Field)
{
    ExtendedDefaultValues data;
    ASSERT_EQ(0, data.getNoDefaultU32Field());
}

TEST(ExtendedDefaultValuesTest, checkNoDefaultStringField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.getNoDefaultStringField().empty());
}

TEST(ExtendedDefaultValuesTest, checkExtendedDefaultBoolField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedDefaultBoolFieldPresent());
    ASSERT_TRUE(data.getExtendedDefaultBoolField());
}

TEST(ExtendedDefaultValuesTest, checkExtendedDefaultStringField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedDefaultStringFieldPresent());
    ASSERT_EQ("default", data.getExtendedDefaultStringField());
}

TEST(ExtendedDefaultValuesTest, checkExtendedOptionalDefaultFloatField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedOptionalDefaultFloatFieldPresent());
    ASSERT_TRUE(data.isExtendedOptionalDefaultFloatFieldSet());
    ASSERT_EQ(1.234F, data.getExtendedOptionalDefaultFloatField());
}

TEST(ExtendedDefaultValuesTest, checkExtendedOptionalDefaultStringField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedOptionalDefaultStringFieldPresent());
    ASSERT_TRUE(data.isExtendedOptionalDefaultStringFieldSet());
    ASSERT_EQ("default", data.getExtendedOptionalDefaultStringField());
}

TEST(ExtendedDefaultValuesTest, checkExtendedNoDefaultU32Field)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedNoDefaultU32FieldPresent());
    ASSERT_EQ(0, data.getExtendedNoDefaultU32Field());
}

TEST(ExtendedDefaultValuesTest, checkExtendedNoDefaultExternField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedNoDefaultExternFieldPresent());
    ASSERT_EQ(0, data.getExtendedNoDefaultExternField().getBitSize());
}

TEST(ExtendedDefaultValuesTest, checkExtendedOptionalNoDefaultU32Field)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedOptionalNoDefaultU32FieldPresent());
    ASSERT_FALSE(data.isExtendedOptionalNoDefaultU32FieldSet());
}

TEST(ExtendedDefaultValuesTest, checkExtendedOptionalNoDefaultBytesField)
{
    ExtendedDefaultValues data;
    ASSERT_TRUE(data.isExtendedOptionalNoDefaultBytesFieldPresent());
    ASSERT_FALSE(data.isExtendedOptionalNoDefaultBytesFieldSet());
}

TEST(ExtendedDefaultValuesTest, writeRead)
{
    ExtendedDefaultValues data;
    test_utils::writeReadTest(data);
}

} // namespace extended_default_values
} // namespace default_values
