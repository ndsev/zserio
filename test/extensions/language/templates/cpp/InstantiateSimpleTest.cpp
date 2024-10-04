#include "gtest/gtest.h"
#include "templates/instantiate_simple/InstantiateSimple.h"

namespace templates
{
namespace instantiate_simple
{

TEST(InstantiateSimpleTest, readWrite)
{
    InstantiateSimple instantiateSimple;
    instantiateSimple.setTest(U32{13});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateSimple.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateSimple readInstantiateSimple(reader);

    ASSERT_TRUE(instantiateSimple == readInstantiateSimple);
}

} // namespace instantiate_simple
} // namespace templates
