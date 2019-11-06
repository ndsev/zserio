#include "gtest/gtest.h"

#include "subtypes/subtype_imported/SubtypeImported.h"

namespace subtypes
{
namespace subtype_imported
{

TEST(SubtypeImportedTest, readWrite)
{
    SubtypeImported subtypeImported{pkg::SubTest{42}};

    zserio::BitStreamWriter writer;
    subtypeImported.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    SubtypeImported readSubtypeImported(reader);

    ASSERT_TRUE(subtypeImported == readSubtypeImported);
}

} // namespace subtype_imported
} // namespace subtypes
