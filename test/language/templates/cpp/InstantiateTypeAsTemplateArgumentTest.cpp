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

    zserio::BitStreamWriter writer;
    instantiateTypeAsTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateTypeAsTemplateArgument readInstantiateTypeAsTemplateArgument(reader);

    ASSERT_TRUE(instantiateTypeAsTemplateArgument == readInstantiateTypeAsTemplateArgument);
}

} // namespace instantiate_type_as_template_argument
} // namespace templates
