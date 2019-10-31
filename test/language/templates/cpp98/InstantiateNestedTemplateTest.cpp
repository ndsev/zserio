#include "gtest/gtest.h"

#include "templates/instantiate_nested_template/InstantiateNestedTemplate.h"

namespace templates
{
namespace instantiate_nested_template
{

TEST(InstantiateNestedTemplateTest, readWrite)
{
    InstantiateNestedTemplate instantiateNestedTemplate;
    NStr nStr;
    nStr.setValue("test");
    TStr tStr;
    tStr.setValue(nStr);
    instantiateNestedTemplate.setTest(tStr);

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
