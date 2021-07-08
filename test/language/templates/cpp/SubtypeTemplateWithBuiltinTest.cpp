#include "gtest/gtest.h"

#include "templates/subtype_template_with_builtin/SubtypeTemplateWithBuiltin.h"

namespace templates
{
namespace subtype_template_with_builtin
{

TEST(SubtypeTemplateWithBuiltinTest, readWrite)
{
    SubtypeTemplateWithBuiltin subtypeTemplateWithBuiltin;
    subtypeTemplateWithBuiltin.setTest(TestStructure_uint32{13});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    subtypeTemplateWithBuiltin.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    SubtypeTemplateWithBuiltin readSubtypeTemplateWithBuiltin(reader);

    ASSERT_TRUE(subtypeTemplateWithBuiltin == readSubtypeTemplateWithBuiltin);
}

} // namespace subtype_template_with_builtin
} // namespace templates
