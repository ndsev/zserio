#include "gtest/gtest.h"

#include "templates/expression_bitmask_template_argument/BitmaskTemplateArgumentHolder.h"

namespace templates
{
namespace expression_bitmask_template_argument
{

TEST(ExpressionBitmaskTemplateArgumentTest, readWrite)
{
    const BitmaskTemplateArgument_Permission bitmaskTemplateArgument_Permission(false, 10);
    ASSERT_TRUE(bitmaskTemplateArgument_Permission.hasExpressionField());

    BitmaskTemplateArgumentHolder bitmaskTemplateArgumentHolder(bitmaskTemplateArgument_Permission);
    zserio::BitStreamWriter writer;
    bitmaskTemplateArgumentHolder.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    const BitmaskTemplateArgumentHolder readBitmaskTemplateArgumentHolder(reader);

    ASSERT_TRUE(bitmaskTemplateArgumentHolder == readBitmaskTemplateArgumentHolder);
}

} // namespace expression_bitmask_template_argument
} // namespace templates
