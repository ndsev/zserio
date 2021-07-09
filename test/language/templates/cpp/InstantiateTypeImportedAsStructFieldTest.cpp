#include "gtest/gtest.h"

#include "templates/instantiate_type_imported_as_struct_field/InstantiateTypeImportedAsStructField.h"

namespace templates
{
namespace instantiate_type_imported_as_struct_field
{

TEST(InstantiateTypeImportedAsStructFieldTest, readWrite)
{
    InstantiateTypeImportedAsStructField instantiateTypeImportedAsStructField;
    instantiateTypeImportedAsStructField.setTest(pkg::Test32{13});

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    instantiateTypeImportedAsStructField.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    InstantiateTypeImportedAsStructField readInstantiateTypeImportedAsStructField(reader);

    ASSERT_TRUE(instantiateTypeImportedAsStructField == readInstantiateTypeImportedAsStructField);
}

} // namespace instantiate_type_imported_as_struct_field
} // namespace templates
