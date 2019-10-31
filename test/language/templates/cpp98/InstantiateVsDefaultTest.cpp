#include "gtest/gtest.h"

#include "templates/instantiate_vs_default/InstantiateVsDefault.h"

namespace templates
{
namespace instantiate_vs_default
{

TEST(InstantiateVsDefaultTest, readWrite)
{
    InstantiateVsDefault instantiateVsDefault;
    pkg::Test_uint32 u32;
    u32.setValue(13);
    instantiateVsDefault.setTest32(u32);
    TStr str;
    str.setValue("test");
    instantiateVsDefault.setTestStr(str);

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
