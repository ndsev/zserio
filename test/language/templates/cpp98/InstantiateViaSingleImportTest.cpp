#include "gtest/gtest.h"

#include "templates/instantiate_via_single_import/InstantiateViaSingleImport.h"

namespace templates
{
namespace instantiate_via_single_import
{

TEST(InstantiateViaSingleImportTest, readWrite)
{
    InstantiateViaSingleImport instantiateViaSingleImport;
    pkg::U32 u32;
    u32.setValue(13);
    instantiateViaSingleImport.setTest32(u32);
    pkg::Test_string str;
    str.setValue("test");
    instantiateViaSingleImport.setTestStr(str);

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
