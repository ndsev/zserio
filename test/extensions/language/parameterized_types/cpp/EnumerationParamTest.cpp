#include "gtest/gtest.h"
#include "parameterized_types/enumeration_param/EnumerationParam.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace enumeration_param
{

TEST(EnumerationParamTest, operatorEquality)
{
    EnumerationParam enumerationParam1(0);
    enumerationParam1.initialize(BasicColor::WHITE);
    EnumerationParam enumerationParam2(0);
    enumerationParam2.initialize(BasicColor::WHITE);
    ASSERT_TRUE(enumerationParam1 == enumerationParam2);

    enumerationParam2.setField(1);
    ASSERT_FALSE(enumerationParam1 == enumerationParam2);

    EnumerationParam enumerationParam3(zserio::NullOpt);
    enumerationParam3.initialize(BasicColor::BLACK);
    ASSERT_FALSE(enumerationParam1 == enumerationParam3);
    ASSERT_FALSE(enumerationParam2 == enumerationParam3);
}

TEST(EnumerationParamTest, operatorLessThan)
{
    EnumerationParam enumerationParam1(0);
    enumerationParam1.initialize(BasicColor::WHITE);
    EnumerationParam enumerationParam2(0);
    enumerationParam2.initialize(BasicColor::WHITE);
    ASSERT_FALSE(enumerationParam1 < enumerationParam2);
    ASSERT_FALSE(enumerationParam2 < enumerationParam1);

    enumerationParam2.setField(1);
    ASSERT_TRUE(enumerationParam1 < enumerationParam2);

    EnumerationParam enumerationParam3(zserio::NullOpt);
    enumerationParam3.initialize(BasicColor::BLACK);
    ASSERT_FALSE(enumerationParam1 < enumerationParam3);
}

TEST(EnumerationParamTest, hashCode)
{
    EnumerationParam enumerationParam1(0);
    enumerationParam1.initialize(BasicColor::WHITE);
    EnumerationParam enumerationParam2(0);
    enumerationParam2.initialize(BasicColor::WHITE);
    ASSERT_EQ(enumerationParam1.hashCode(), enumerationParam2.hashCode());

    enumerationParam2.setField(1);
    ASSERT_NE(enumerationParam1.hashCode(), enumerationParam2.hashCode());

    EnumerationParam enumerationParam3(zserio::NullOpt);
    enumerationParam3.initialize(BasicColor::BLACK);
    ASSERT_NE(enumerationParam1.hashCode(), enumerationParam3.hashCode());
    ASSERT_NE(enumerationParam2.hashCode(), enumerationParam3.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(63011, enumerationParam1.hashCode());
    ASSERT_EQ(63012, enumerationParam2.hashCode());
    ASSERT_EQ(1702, enumerationParam3.hashCode());
}

TEST(EnumerationParamTest, writeRead)
{
    EnumerationParam enumerationParam(1);
    auto bitBuffer = zserio::serialize(enumerationParam, BasicColor::WHITE);
    auto readEnumerationParam = zserio::deserialize<EnumerationParam>(bitBuffer, BasicColor::WHITE);
    ASSERT_EQ(enumerationParam, readEnumerationParam);
}

} // namespace enumeration_param
} // namespace parameterized_types
