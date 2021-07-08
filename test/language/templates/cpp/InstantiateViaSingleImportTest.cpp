#include "gtest/gtest.h"

#include "templates/instantiate_via_single_import/InstantiateViaSingleImport.h"

namespace templates
{
namespace instantiate_via_single_import
{

TEST(InstantiateViaSingleImportTest, readWrite)
{
    InstantiateViaSingleImport instantiateViaSingleImport;
    instantiateViaSingleImport.setTest32(pkg::U32{13});
    instantiateViaSingleImport.setTestStr(pkg::Test_string{"test"});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateViaSingleImport.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateViaSingleImport readInstantiateViaSingleImport(reader);

    ASSERT_TRUE(instantiateViaSingleImport == readInstantiateViaSingleImport);
}

} // namespace instantiate_via_single_import
} // namespace templates
