#include "gtest/gtest.h"

#include "templates/instantiate_type_imported_as_struct_field/InstantiateTypeImportedAsStructField.h"

namespace templates
{
namespace instantiate_type_imported_as_struct_field
{

TEST(InstantiateTypeImportedAsStructFieldTest, readWrite)
{
    InstantiateTypeImportedAsStructField instantiateTypeImportedAsStructField;
    pkg::Test32& test = instantiateTypeImportedAsStructField.getTest();
    test.setValue(13);

    zserio::BitStreamWriter writer;
    instantiateTypeImportedAsStructField.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiateTypeImportedAsStructField readInstantiateTypeImportedAsStructField(reader);

    ASSERT_TRUE(instantiateTypeImportedAsStructField == readInstantiateTypeImportedAsStructField);
}

} // namespace instantiate_type_imported_as_struct_field
} // namespace templates
