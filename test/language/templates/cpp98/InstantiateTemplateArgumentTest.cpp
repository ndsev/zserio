#include "gtest/gtest.h"

#include "templates/instantiate_template_argument/InstantiateTemplateArgument.h"

namespace templates
{
namespace instantiate_template_argument
{

TEST(InstantiateTemplateArgumentTest, readWrite)
{
    InstantiateTemplateArgument instantiateTemplateArgument;
    Other_Str& other = instantiateTemplateArgument.getOther();
    Str& str = other.getValue();
    str.setValue("test");

    zserio::BitStreamWriter writer;
    instantiateTemplateArgument.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateTemplateArgument readInstantiateTemplateArgument(reader);

    ASSERT_TRUE(instantiateTemplateArgument == readInstantiateTemplateArgument);
}

} // namespace instantiate_template_argument
} // namespace templates
