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
