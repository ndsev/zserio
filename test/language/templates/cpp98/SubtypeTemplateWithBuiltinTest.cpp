#include "gtest/gtest.h"

#include "templates/subtype_template_with_builtin/SubtypeTemplateWithBuiltin.h"

namespace templates
{
namespace subtype_template_with_builtin
{

TEST(SubtypeTemplateWithBuiltinTest, readWrite)
{
    SubtypeTemplateWithBuiltin subtypeTemplateWithBuiltin;
    subtypeTemplateWithBuiltin.getTest().setValue(13);

    zserio::BitStreamWriter writer;
    subtypeTemplateWithBuiltin.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    SubtypeTemplateWithBuiltin readSubtypeTemplateWithBuiltin(reader);

    ASSERT_TRUE(subtypeTemplateWithBuiltin == readSubtypeTemplateWithBuiltin);
}

} // namespace subtype_template_with_builtin
} // namespace templates
