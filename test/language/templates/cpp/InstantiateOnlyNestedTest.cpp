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

    zserio::BitStreamWriter writer;
    instantiateOnlyNested.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateOnlyNested readInstantiateOnlyNested(reader);

    ASSERT_TRUE(instantiateOnlyNested == readInstantiateOnlyNested);
}

} // namespace instantiate_only_nested
} // namespace templates
