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
            Template_A_B_C_7FE93D34(A_B(1), true),
            Template_A_B_C_5EB4E3FC(A(1), "string"));
    InstantiationNameClash instantiationNameClash(testStruct_uint32);

    zserio::BitStreamWriter writer;
    instantiationNameClash.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiationNameClash readInstantiationNameClash(reader);

    ASSERT_TRUE(instantiationNameClash == readInstantiationNameClash);
}

} // namespace struct_template_clash
} // namespace templates
