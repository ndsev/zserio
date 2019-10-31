#include "gtest/gtest.h"

#include "templates/instantiate_unused/U32.h"

namespace templates
{
namespace instantiate_unused
{

TEST(InstantiateUnusedTest, readWrite)
{
    U32 u32; // check that unused template is instantiated via the instantiate command
    u32.setValue(13);

    zserio::BitStreamWriter writer;
    u32.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    U32 readU32(reader);

    ASSERT_TRUE(u32 == readU32);
}

} // namespace instantiate_unused
} // namespace templates
