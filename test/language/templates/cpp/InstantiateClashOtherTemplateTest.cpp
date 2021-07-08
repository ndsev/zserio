#include "gtest/gtest.h"

#include "templates/instantiate_clash_other_template/InstantiateClashOtherTemplate.h"

namespace templates
{
namespace instantiate_clash_other_template
{

TEST(InstantiateClashOtherTemplateTest, readWrite)
{
    InstantiateClashOtherTemplate instantiateClashOtherTemplate(Test_uint32_99604043(13));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateClashOtherTemplate.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateClashOtherTemplate readInstantiateClashOtherTemplate(reader);

    ASSERT_TRUE(instantiateClashOtherTemplate == readInstantiateClashOtherTemplate);
}

} // namespace instantiate_clash_other_template
} // namespace templates
