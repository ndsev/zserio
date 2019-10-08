#include "gtest/gtest.h"

#include "templates/subtype_template_with_compound/SubtypeTemplateWithCompound.h"

namespace templates
{
namespace subtype_template_with_compound
{

TEST(SubtypeTemplateWithCompoundTest, readWrite)
{
    SubtypeTemplateWithCompound subtypeTemplateWithCompound;
    subtypeTemplateWithCompound.getValue1().setValue(13);
    subtypeTemplateWithCompound.getValue2().getValue().setValue(42);

    zserio::BitStreamWriter writer;
    subtypeTemplateWithCompound.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    SubtypeTemplateWithCompound readSubtypeTemplateWithCompound(reader);

    ASSERT_TRUE(subtypeTemplateWithCompound == readSubtypeTemplateWithCompound);
}

} // namespace subtype_template_with_compound
} // namespace templates
