#include "gtest/gtest.h"
#include "templates/instantiate_via_import/InstantiateViaImport.h"

namespace templates
{
namespace instantiate_via_import
{

TEST(InstantiateViaImportTest, readWrite)
{
    InstantiateViaImport instantiateViaImport;
    instantiateViaImport.setTest32(pkg::U32{13});
    instantiateViaImport.setTestStr(pkg::Test_string{"test"});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateViaImport.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateViaImport readInstantiateViaImport(reader);

    ASSERT_TRUE(instantiateViaImport == readInstantiateViaImport);
}

} // namespace instantiate_via_import
} // namespace templates
