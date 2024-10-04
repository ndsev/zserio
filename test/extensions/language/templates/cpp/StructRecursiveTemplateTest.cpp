#include "gtest/gtest.h"
#include "templates/struct_recursive_template/StructRecursiveTemplate.h"
#include "zserio/RebindAlloc.h"

namespace templates
{
namespace struct_recursive_template
{

using allocator_type = StructRecursiveTemplate::allocator_type;
using string_type = zserio::string<allocator_type>;

TEST(StructRecursiveTemplateTest, readWrite)
{
    StructRecursiveTemplate structRecursiveTemplate;
    structRecursiveTemplate.setCompound1(Compound_Compound_uint32{Compound_uint32{42}});
    structRecursiveTemplate.setCompound2(Compound_Compound_Compound_string{
            Compound_Compound_string{Compound_string{string_type{"string"}}}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    structRecursiveTemplate.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    StructRecursiveTemplate readStructRecursiveTemplate(reader);

    ASSERT_TRUE(structRecursiveTemplate == readStructRecursiveTemplate);
}

} // namespace struct_recursive_template
} // namespace templates
