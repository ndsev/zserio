#include "gtest/gtest.h"

#include "templates/struct_template_clash_other_template/InstantiationNameClashOtherTemplate.h"

namespace templates
{
namespace struct_template_clash_other_template
{

TEST(StructTemplateClashOtherTemplateTest, readWrite)
{
    InstantiationNameClashOtherTemplate instantiationNameClashOtherTemplate(
            Test_A_uint32_FA82A3B7(42), Test_A_uint32_5D68B0C2(A_uint32(10)));

    zserio::BitStreamWriter writer;
    instantiationNameClashOtherTemplate.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiationNameClashOtherTemplate readInstantiationNameClashOtherTemplate(reader);

    ASSERT_TRUE(instantiationNameClashOtherTemplate == readInstantiationNameClashOtherTemplate);
}

} // namespace struct_template_clash_other_template
} // namespace templates
