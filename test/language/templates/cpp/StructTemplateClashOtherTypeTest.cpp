#include "gtest/gtest.h"

#include "templates/struct_template_clash_other_type/InstantiationNameClashOtherType.h"

namespace templates
{
namespace struct_template_clash_other_type
{

TEST(StructTemplateClashOtherTypeTest, readWrite)
{
    InstantiationNameClashOtherType instantiationNameClashOtherType(Test_uint32_99604043(42));

    zserio::BitStreamWriter writer;
    instantiationNameClashOtherType.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiationNameClashOtherType readInstantiationNameClashOtherType(reader);

    ASSERT_TRUE(instantiationNameClashOtherType == readInstantiationNameClashOtherType);
}

} // namespace struct_template_clash_other_type
} // namespace templates
