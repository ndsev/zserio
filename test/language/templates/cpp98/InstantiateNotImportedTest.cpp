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
    pkg::Test_uint32 u32;
    u32.setValue(13);
    instantiateNotImported.setTest32(u32);
    pkg::Test_string str;
    str.setValue("test");
    instantiateNotImported.setTestStr(str);

    zserio::BitStreamWriter writer;
    instantiateNotImported.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateNotImported readInstantiateNotImported(reader);

    ASSERT_TRUE(instantiateNotImported == readInstantiateNotImported);
}

TEST(InstantiateNotImportedTest, instantiationInPkg)
{
    pkg::U32 u32;
    u32.setValue(13);
    ASSERT_EQ(13, u32.getValue()); // just check that U32 exists in pkg
}

} // namespace instantiate_not_imported
} // namespace templates
