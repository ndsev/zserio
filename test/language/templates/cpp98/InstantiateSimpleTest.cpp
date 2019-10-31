#include "gtest/gtest.h"

#include "templates/instantiate_simple/InstantiateSimple.h"

namespace templates
{
namespace instantiate_simple
{

TEST(InstantiateSimpleTest, readWrite)
{
    InstantiateSimple instantiateSimple;
    U32 u32;
    u32.setValue(13);
    instantiateSimple.setTest(u32);

    zserio::BitStreamWriter writer;
    instantiateSimple.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateSimple readInstantiateSimple(reader);

    ASSERT_TRUE(instantiateSimple == readInstantiateSimple);
}

} // namespace instantiate_simple
} // namespace templates
