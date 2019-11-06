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

    zserio::BitStreamWriter writer;
    instantiateViaSingleImport.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateViaSingleImport readInstantiateViaSingleImport(reader);

    ASSERT_TRUE(instantiateViaSingleImport == readInstantiateViaSingleImport);
}

} // namespace instantiate_via_single_import
} // namespace templates
