#include "gtest/gtest.h"
#include "templates/struct_template_clash_other_type/InstantiationNameClashOtherType.h"

namespace templates
{
namespace struct_template_clash_other_type
{

TEST(StructTemplateClashOtherTypeTest, readWrite)
{
    InstantiationNameClashOtherType instantiationNameClashOtherType(Test_uint32_99604043(42));

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiationNameClashOtherType.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiationNameClashOtherType readInstantiationNameClashOtherType(reader);

    ASSERT_TRUE(instantiationNameClashOtherType == readInstantiationNameClashOtherType);
}

} // namespace struct_template_clash_other_type
} // namespace templates
