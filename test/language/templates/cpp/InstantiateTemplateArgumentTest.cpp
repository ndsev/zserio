#include "gtest/gtest.h"
#include "templates/instantiate_template_argument/InstantiateTemplateArgument.h"

namespace templates
{
namespace instantiate_template_argument
{

TEST(InstantiateTemplateArgumentTest, readWrite)
{
    InstantiateTemplateArgument instantiateTemplateArgument{Other_Str{Str{"test"}}};

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTemplateArgument readInstantiateTemplateArgument(reader);

    ASSERT_TRUE(instantiateTemplateArgument == readInstantiateTemplateArgument);
}

} // namespace instantiate_template_argument
} // namespace templates
