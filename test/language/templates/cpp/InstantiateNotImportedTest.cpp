#include "gtest/gtest.h"

#include "templates/instantiate_not_imported/InstantiateNotImported.h"
#include "templates/instantiate_not_imported/pkg/U32.h"

namespace templates
{
namespace instantiate_not_imported
{

TEST(InstantiateNotImportedTest, readWrite)
{
    InstantiateNotImported instantiateNotImported;
    instantiateNotImported.setTest32(pkg::Test_uint32{13});
    instantiateNotImported.setTestStr(pkg::Test_string{"test"});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateNotImported.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateNotImported readInstantiateNotImported(reader);

    ASSERT_TRUE(instantiateNotImported == readInstantiateNotImported);
}

TEST(InstantiateNotImportedTest, instantiationInPkg)
{
    pkg::U32 u32{13};
    ASSERT_EQ(13, u32.getValue()); // just check that U32 exists in pkg
}

} // namespace instantiate_not_imported
} // namespace templates
