#include "gtest/gtest.h"

#include "templates/struct_template_clash/InstantiationNameClash.h"

namespace templates
{
namespace struct_template_clash
{

TEST(StructTemplateClashTest, readWrite)
{
    const TestStruct_uint32 testStruct_uint32(
            42,
            Template_A_B_C_7FE93D34(A_B(1), C(true)),
            Template_A_B_C_5EB4E3FC(A(1), B_C("string")));
    InstantiationNameClash instantiationNameClash(testStruct_uint32);

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiationNameClash.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiationNameClash readInstantiationNameClash(reader);

    ASSERT_TRUE(instantiationNameClash == readInstantiationNameClash);
}

} // namespace struct_template_clash
} // namespace templates
