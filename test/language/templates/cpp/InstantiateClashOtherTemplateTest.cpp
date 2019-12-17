#include "gtest/gtest.h"

#include "templates/instantiate_clash_other_template/InstantiateClashOtherTemplate.h"

namespace templates
{
namespace instantiate_clash_other_template
{

TEST(InstantiateClashOtherTemplateTest, readWrite)
{
    InstantiateClashOtherTemplate instantiateClashOtherTemplate(Test_uint32_99604043(13));

    zserio::BitStreamWriter writer;
    instantiateClashOtherTemplate.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateClashOtherTemplate readInstantiateClashOtherTemplate(reader);

    ASSERT_TRUE(instantiateClashOtherTemplate == readInstantiateClashOtherTemplate);
}

} // namespace instantiate_clash_other_template
} // namespace templates
