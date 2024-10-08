#include "default_values/structure_default_values/BasicColor.h"
#include "default_values/structure_default_values/Permission.h"
#include "default_values/structure_default_values/StructureDefaultValues.h"
#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

namespace default_values
{
namespace structure_default_values
{

TEST(StructureDefaultValuesTest, checkDefaultBoolValue)
{
    StructureDefaultValues structureDefaultValues;
    ASSERT_EQ(true, structureDefaultValues.getBoolValue());
}

TEST(StructureDefaultValuesTest, checkDefaultBit4Value)
{
    StructureDefaultValues structureDefaultValues;
    ASSERT_EQ(0x0F, structureDefaultValues.getBit4Value());
}

TEST(StructureDefaultValuesTest, checkDefaultInt16Value)
{
    StructureDefaultValues structureDefaultValues;
    ASSERT_EQ(0x0BEE, structureDefaultValues.getInt16Value());
}

TEST(StructureDefaultValuesTest, checkDefaultFloat16Value)
{
    StructureDefaultValues structureDefaultValues;
    float diff = 1.23F - structureDefaultValues.getFloat16Value();
    if (diff < 0.0F)
    {
        diff = -diff;
    }
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(StructureDefaultValuesTest, checkDefaultFloat32Value)
{
    StructureDefaultValues structureDefaultValues;
    float diff = 1.234F - structureDefaultValues.getFloat32Value();
    if (diff < 0.0F)
    {
        diff = -diff;
    }
    ASSERT_TRUE(diff <= std::numeric_limits<float>::epsilon());
}

TEST(StructureDefaultValuesTest, checkDefaultFloat64Value)
{
    StructureDefaultValues structureDefaultValues;
    double diff = 1.2345 - structureDefaultValues.getFloat64Value();
    if (diff < 0.0)
    {
        diff = -diff;
    }
    ASSERT_TRUE(diff <= std::numeric_limits<double>::epsilon());
}

TEST(StructureDefaultValuesTest, checkDefaultStringValue)
{
    StructureDefaultValues structureDefaultValues;
    ASSERT_EQ("string", structureDefaultValues.getStringValue());
}

TEST(StructureDefaultValuesTest, checkDefaultEnumValue)
{
    StructureDefaultValues structureDefaultValues;
    ASSERT_EQ(BasicColor::BLACK, structureDefaultValues.getEnumValue());
}

TEST(StructureDefaultValuesTest, checkDefaultBitmaskValue)
{
    StructureDefaultValues structureDefaultValues;
    ASSERT_EQ(Permission::Values::READ_WRITE, structureDefaultValues.getBitmaskValue());
}

} // namespace structure_default_values
} // namespace default_values
