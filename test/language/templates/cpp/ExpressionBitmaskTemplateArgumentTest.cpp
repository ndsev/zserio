#include "gtest/gtest.h"
#include "templates/expression_bitmask_template_argument/BitmaskTemplateArgumentHolder.h"

namespace templates
{
namespace expression_bitmask_template_argument
{

TEST(ExpressionBitmaskTemplateArgumentTest, readWrite)
{
    const BitmaskTemplateArgument_Permission bitmaskTemplateArgument_Permission(false, 10);
    ASSERT_TRUE(bitmaskTemplateArgument_Permission.isExpressionFieldUsed());

    BitmaskTemplateArgumentHolder bitmaskTemplateArgumentHolder(bitmaskTemplateArgument_Permission);
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    bitmaskTemplateArgumentHolder.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    const BitmaskTemplateArgumentHolder readBitmaskTemplateArgumentHolder(reader);

    ASSERT_TRUE(bitmaskTemplateArgumentHolder == readBitmaskTemplateArgumentHolder);
}

} // namespace expression_bitmask_template_argument
} // namespace templates
