#include "gtest/gtest.h"

#include "templates/instantiate_unused/U32.h"

namespace templates
{
namespace instantiate_unused
{

TEST(InstantiateUnusedTest, readWrite)
{
    U32 u32{13}; // check that unused template is instantiated via the instantiate command

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    u32.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    U32 readU32(reader);

    ASSERT_TRUE(u32 == readU32);
}

} // namespace instantiate_unused
} // namespace templates
