#include "gtest/gtest.h"

#include "templates/instantiate_imported/InstantiateImported.h"

namespace templates
{
namespace instantiate_imported
{

TEST(InstantiateImportedTest, readWrite)
{
    InstantiateImported instantiateImported;
    instantiateImported.setTest32(pkg::U32{13});
    instantiateImported.setTestStr(Test_string{"test"});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateImported.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateImported readInstantiateImported(reader);

    ASSERT_TRUE(instantiateImported == readInstantiateImported);
}

} // namespace instantiate_imported
} // namespace templates
