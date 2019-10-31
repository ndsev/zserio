#include "gtest/gtest.h"

#include "templates/instantiate_via_import/InstantiateViaImport.h"

namespace templates
{
namespace instantiate_via_import
{

TEST(InstantiateViaImportTest, readWrite)
{
    InstantiateViaImport instantiateViaImport;
    pkg::U32 u32;
    u32.setValue(13);
    instantiateViaImport.setTest32(u32);
    pkg::Test_string str;
    str.setValue("test");
    instantiateViaImport.setTestStr(str);

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
