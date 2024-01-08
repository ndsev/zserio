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

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateVsDefault.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateVsDefault readInstantiateVsDefault(reader);

    ASSERT_TRUE(instantiateVsDefault == readInstantiateVsDefault);
}

} // namespace instantiate_vs_default
} // namespace templates
