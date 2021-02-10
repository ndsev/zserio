#include "gtest/gtest.h"

#include "templates/expression_enum_template_argument/EnumTemplateArgumentHolder.h"

namespace templates
{
namespace expression_enum_template_argument
{

TEST(ExpressionEnumTemplateArgumentTest, readWrite)
{
    const EnumTemplateArgument_Color enumTemplateArgument_Color(false, 10);
    ASSERT_TRUE(enumTemplateArgument_Color.isExpressionFieldUsed());

    EnumTemplateArgumentHolder enumTemplateArgumentHolder(enumTemplateArgument_Color);
    zserio::BitStreamWriter writer;
    enumTemplateArgumentHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const EnumTemplateArgumentHolder readEnumTemplateArgumentHolder(reader);

    ASSERT_TRUE(enumTemplateArgumentHolder == readEnumTemplateArgumentHolder);
}

} // namespace expression_enum_template_argument
} // namespace templates
