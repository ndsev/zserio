#include "gtest/gtest.h"

#include "templates/instantiate_nested_template/InstantiateNestedTemplate.h"

namespace templates
{
namespace instantiate_nested_template
{

TEST(InstantiateNestedTemplateTest, readWrite)
{
    InstantiateNestedTemplate instantiateNestedTemplate;
    instantiateNestedTemplate.setTest(TStr{NStr{"test"}});

    zserio::BitStreamWriter writer;
    instantiateNestedTemplate.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateNestedTemplate readInstantiateNestedTemplate(reader);

    ASSERT_TRUE(instantiateNestedTemplate == readInstantiateNestedTemplate);
}

} // namespace instantiate_nested_template
} // namespace templates
