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

    zserio::BitStreamWriter writer;
    instantiateViaImport.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateViaImport readInstantiateViaImport(reader);

    ASSERT_TRUE(instantiateViaImport == readInstantiateViaImport);
}

} // namespace instantiate_via_import
} // namespace templates
