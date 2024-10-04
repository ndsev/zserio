#include "gtest/gtest.h"
#include "templates/instantiate_with_instantiate_template_argument/InstantiateWithInstantiateTemplateArgument.h"

namespace templates
{
namespace instantiate_with_instantiate_template_argument
{

TEST(InstantiateWithInstantiateTemplateArgumentTest, readWrite)
{
    InstantiateWithInstantiateTemplateArgument instantiateWithInstantiateTemplateArgument;

    instantiateWithInstantiateTemplateArgument.setOther8(Other8(Data8(13)));
    instantiateWithInstantiateTemplateArgument.setOther32(Other32(Data32(0xCAFE)));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateWithInstantiateTemplateArgument.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateWithInstantiateTemplateArgument readInstantiateWithInstantiateTemplateArgument(reader);

    ASSERT_EQ(instantiateWithInstantiateTemplateArgument, readInstantiateWithInstantiateTemplateArgument);
}

} // namespace instantiate_with_instantiate_template_argument
} // namespace templates
