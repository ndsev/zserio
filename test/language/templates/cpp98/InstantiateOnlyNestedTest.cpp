#include "gtest/gtest.h"

#include "templates/instantiate_only_nested/InstantiateOnlyNested.h"

namespace templates
{
namespace instantiate_only_nested
{

TEST(InstantiateOnlyNestedTest, readWrite)
{
    InstantiateOnlyNested instantiateOnlyNested;
    N32 n32;
    n32.setValue(13);
    pkg::Test_uint32 t32;
    t32.setValue(n32);
    instantiateOnlyNested.setTest32(t32);

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
