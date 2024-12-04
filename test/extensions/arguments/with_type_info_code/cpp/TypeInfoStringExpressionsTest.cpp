#include "gtest/gtest.h"
#include "type_info_string_expressions/TypeInfoStringExpressions.h"
#include "zserio/SerializeUtil.h"

using namespace zserio::literals;

namespace type_info_string_expressions
{

using allocator_type = TypeInfoStringExpressions::allocator_type;
using ITypeInfo = zserio::IBasicTypeInfo<allocator_type>;

class TypeInfoStringExpressionsTest : public ::testing::Test
{
protected:
    static void fillData(TypeInfoStringExpressions& typeInfoStringExpressions)
    {
        TestChoice& choiceField = typeInfoStringExpressions.getChoiceField();
        choiceField.setStructField(TestStruct());
        TestStruct& structField = choiceField.getStructField();
        structField.getArrayField().resize(LENGTHOF_LITERAL * 2);
        structField.setDynBitField(0);
        structField.setBitField(0);
        structField.setEnumField(TestEnum::ONE);
        structField.setBitmaskField(TestBitmask::Values::READ);
    }

private:
    static constexpr size_t LENGTHOF_LITERAL = 7;
};

constexpr size_t TypeInfoStringExpressionsTest::LENGTHOF_LITERAL;

TEST_F(TypeInfoStringExpressionsTest, writeRead)
{
    TypeInfoStringExpressions typeInfoStringExpressions;
    fillData(typeInfoStringExpressions);

    auto bitBuffer = zserio::serialize(typeInfoStringExpressions);
    TypeInfoStringExpressions readTypeInfoStringExpressions =
            zserio::deserialize<TypeInfoStringExpressions>(bitBuffer);

    ASSERT_EQ(typeInfoStringExpressions, readTypeInfoStringExpressions);
}

TEST_F(TypeInfoStringExpressionsTest, typeInfo)
{
    const ITypeInfo& typeInfo = TypeInfoStringExpressions::typeInfo();

    // choiceField
    const auto& choiceFieldInfo = typeInfo.getFields()[0];
    ASSERT_EQ("::zserio::makeStringView(\"literal\").size()"_sv, choiceFieldInfo.typeArguments[0]);

    // TestChoice
    const auto& choiceTypeInfo = choiceFieldInfo.typeInfo;
    ASSERT_EQ("getSelector() + ::zserio::makeStringView(\"literal\").size()"_sv, choiceTypeInfo.getSelector());
    ASSERT_EQ("0 + ::zserio::makeStringView(\"literal\").size()"_sv,
            choiceTypeInfo.getCases()[0].caseExpressions[0]);

    // structField
    const auto& structFieldInfo = choiceTypeInfo.getFields()[1];
    ASSERT_EQ("::zserio::makeStringView(\"literal\").size()"_sv, structFieldInfo.typeArguments[0]);

    // TestStruct
    const auto& structTypeInfo = structFieldInfo.typeInfo;

    // arrayField
    const auto& arrayFieldInfo = structTypeInfo.getFields()[0];
    ASSERT_EQ("getParam() + ::zserio::makeStringView(\"literal\").size()"_sv, arrayFieldInfo.arrayLength);
    ASSERT_EQ("getArrayField().size() > ::zserio::makeStringView(\"literal\").size()"_sv,
            arrayFieldInfo.constraint);

    // dynBitField
    const auto& dynBitFieldInfo = structTypeInfo.getFields()[1];
    ASSERT_EQ("::zserio::makeStringView(\"literal\").size()"_sv, dynBitFieldInfo.typeArguments[0]);
    ASSERT_EQ("getParam() + ::zserio::makeStringView(\"literal\").size() != 0"_sv,
            dynBitFieldInfo.optionalCondition);

    // bitField
    const auto& bitFieldInfo = structTypeInfo.getFields()[2];
    ASSERT_EQ("::zserio::makeStringView(\"literal\").size() + 1"_sv, bitFieldInfo.alignment);

    // enumField
    const auto& enumFieldInfo = structTypeInfo.getFields()[3];

    // TestEnum
    const auto& enumTypeInfo = enumFieldInfo.typeInfo;
    ASSERT_EQ("::zserio::makeStringView(\"literal\").size()"_sv, enumTypeInfo.getUnderlyingTypeArguments()[0]);

    // bitmaskField
    const auto& bitmaskFieldInfo = structTypeInfo.getFields()[4];

    // TestBitmask
    const auto& bitmaskTypeInfo = bitmaskFieldInfo.typeInfo;
    ASSERT_EQ(
            "::zserio::makeStringView(\"literal\").size()"_sv, bitmaskTypeInfo.getUnderlyingTypeArguments()[0]);
}

} // namespace type_info_string_expressions
