#include "gtest/gtest.h"

#include "templates/struct_recursive_template/StructRecursiveTemplate.h"

namespace templates
{
namespace struct_recursive_template
{

TEST(StructRecursiveTemplateTest, readWrite)
{
    StructRecursiveTemplate structRecursiveTemplate;
    structRecursiveTemplate.setCompound1(Compound_Compound_uint32{Compound_uint32{42}});
    structRecursiveTemplate.setCompound2(Compound_Compound_Compound_string{
            Compound_Compound_string{Compound_string{std::string{"string"}}}});

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
