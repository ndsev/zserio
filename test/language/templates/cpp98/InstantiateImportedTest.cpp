#include "gtest/gtest.h"

#include "templates/instantiate_imported/InstantiateImported.h"

namespace templates
{
namespace instantiate_imported
{

TEST(InstantiateImportedTest, readWrite)
{
    InstantiateImported instantiateImported;
    pkg::U32 u32;
    u32.setValue(13);
    instantiateImported.setTest32(u32);
    Test_string str;
    str.setValue("test");
    instantiateImported.setTestStr(str);

    zserio::BitStreamWriter writer;
    instantiateImported.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateImported readInstantiateImported(reader);

    ASSERT_TRUE(instantiateImported == readInstantiateImported);
}

} // namespace instantiate_imported
} // namespace templates
