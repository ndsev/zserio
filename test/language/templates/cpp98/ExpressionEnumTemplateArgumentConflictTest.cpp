#include "gtest/gtest.h"

#include "templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflictHolder.h"

namespace templates
{
namespace expression_enum_template_argument_conflict
{

TEST(ExpressionEnumTemplateArgumentConflictTest, readWrite)
{
    EnumTemplateArgumentConflict_Letters enumTemplateArgumentConflict_Letters;
    enumTemplateArgumentConflict_Letters.setBoolField(false);
    enumTemplateArgumentConflict_Letters.setExpressionField(10);
    ASSERT_TRUE(enumTemplateArgumentConflict_Letters.hasExpressionField());

    EnumTemplateArgumentConflictHolder enumTemplateArgumentConflictHolder;
    enumTemplateArgumentConflictHolder.setEnumTemplateArgumentConflict(enumTemplateArgumentConflict_Letters);
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
