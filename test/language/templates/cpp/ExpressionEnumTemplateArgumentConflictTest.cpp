#include "gtest/gtest.h"
#include "templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflictHolder.h"

namespace templates
{
namespace expression_enum_template_argument_conflict
{

TEST(ExpressionEnumTemplateArgumentConflictTest, readWrite)
{
    const EnumTemplateArgumentConflict_Letters enumTemplateArgumentConflict_Letters(false, 10);
    ASSERT_TRUE(enumTemplateArgumentConflict_Letters.isExpressionFieldUsed());

    EnumTemplateArgumentConflictHolder enumTemplateArgumentConflictHolder(enumTemplateArgumentConflict_Letters);
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    enumTemplateArgumentConflictHolder.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const EnumTemplateArgumentConflictHolder readEnumTemplateArgumentConflictHolder(reader);

    ASSERT_TRUE(enumTemplateArgumentConflictHolder == readEnumTemplateArgumentConflictHolder);
}

} // namespace expression_enum_template_argument_conflict
} // namespace templates
