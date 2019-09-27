#include "gtest/gtest.h"

#include "templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflictHolder.h"

namespace templates
{
namespace expression_enum_template_argument_conflict
{

TEST(ExpressionEnumTemplateArgumentConflictTest, readWrite)
{
    const EnumTemplateArgumentConflict_Letters enumTemplateArgumentConflict_Letters(false, 10);
    EnumTemplateArgumentConflictHolder enumTemplateArgumentConflictHolder(enumTemplateArgumentConflict_Letters);
    ASSERT_TRUE(enumTemplateArgumentConflictHolder.getEnumTemplateArgumentConflict().hasExpressionField());

    zserio::BitStreamWriter writer;
    enumTemplateArgumentConflictHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const EnumTemplateArgumentConflictHolder readEnumTemplateArgumentConflictHolder(reader);

    ASSERT_TRUE(enumTemplateArgumentConflictHolder == readEnumTemplateArgumentConflictHolder);
}

} // namespace expression_enum_template_argument_conflict
} // namespace templates
