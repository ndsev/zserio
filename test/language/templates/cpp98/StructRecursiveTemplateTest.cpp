#include "gtest/gtest.h"

#include "templates/struct_recursive_template/StructRecursiveTemplate.h"

namespace templates
{
namespace struct_recursive_template
{

TEST(StructRecursiveTemplateTest, readWrite)
{
    StructRecursiveTemplate structRecursiveTemplate;
    structRecursiveTemplate.getCompound1().getValue().setValue(42);
    structRecursiveTemplate.getCompound2().getValue().getValue().setValue("string");

    zserio::BitStreamWriter writer;
    structRecursiveTemplate.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    StructRecursiveTemplate readStructRecursiveTemplate(reader);

    ASSERT_TRUE(structRecursiveTemplate == readStructRecursiveTemplate);
}

} // namespace struct_recursive_template
} // namespace templates
