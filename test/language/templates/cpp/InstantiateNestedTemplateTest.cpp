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

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateNestedTemplate.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateNestedTemplate readInstantiateNestedTemplate(reader);

    ASSERT_TRUE(instantiateNestedTemplate == readInstantiateNestedTemplate);
}

} // namespace instantiate_nested_template
} // namespace templates
