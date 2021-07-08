#include "gtest/gtest.h"

#include "templates/instantiate_only_nested/InstantiateOnlyNested.h"

namespace templates
{
namespace instantiate_only_nested
{

TEST(InstantiateOnlyNestedTest, readWrite)
{
    InstantiateOnlyNested instantiateOnlyNested;
    instantiateOnlyNested.setTest32(pkg::Test_uint32{N32{13}});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateOnlyNested.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateOnlyNested readInstantiateOnlyNested(reader);

    ASSERT_TRUE(instantiateOnlyNested == readInstantiateOnlyNested);
}

} // namespace instantiate_only_nested
} // namespace templates
