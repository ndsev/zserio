#include "gtest/gtest.h"

#include "templates/instantiate_type_as_template_argument/InstantiateTypeAsTemplateArgument.h"

namespace templates
{
namespace instantiate_type_as_template_argument
{

TEST(InstantiateTypeAsTemplateArgumentTest, readWrite)
{
    InstantiateTypeAsTemplateArgument instantiateTypeAsTemplateArgument{
            Other_Str{Str{"test"}}
    };

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeAsTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeAsTemplateArgument readInstantiateTypeAsTemplateArgument(reader);

    ASSERT_TRUE(instantiateTypeAsTemplateArgument == readInstantiateTypeAsTemplateArgument);
}

} // namespace instantiate_type_as_template_argument
} // namespace templates
