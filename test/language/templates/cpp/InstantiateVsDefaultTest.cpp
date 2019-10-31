#include "gtest/gtest.h"

#include "templates/instantiate_vs_default/InstantiateVsDefault.h"

namespace templates
{
namespace instantiate_vs_default
{

TEST(InstantiateVsDefaultTest, readWrite)
{
    InstantiateVsDefault instantiateVsDefault;
    instantiateVsDefault.setTest32(pkg::Test_uint32{13});
    instantiateVsDefault.setTestStr(TStr{"test"});

    zserio::BitStreamWriter writer;
    instantiateVsDefault.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateVsDefault readInstantiateVsDefault(reader);

    ASSERT_TRUE(instantiateVsDefault == readInstantiateVsDefault);
}

} // namespace instantiate_vs_default
} // namespace templates
