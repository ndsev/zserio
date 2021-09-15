#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "structure_types/field_constructor_clashing/FieldConstructorClashing.h"

namespace structure_types
{
namespace field_constructor_clashing
{

class FieldConstructorClashingTest : public ::testing::Test
{
protected:
    void fillFieldConstructorClashing(FieldConstructorClashing& fieldConstructorClashing)
    {
        auto& compoundReadArray = fieldConstructorClashing.getCompoundReadArray();
        compoundReadArray.emplace_back(CompoundRead{Field{FIELD1}, Field{FIELD2}});

        auto& compoundPackingArray = fieldConstructorClashing.getCompoundPackingArray();
        compoundPackingArray.emplace_back(CompoundPackingRead{Field{FIELD1}, Field{FIELD2}, Field{FIELD3}});
    }

    static const uint32_t FIELD1;
    static const uint32_t FIELD2;
    static const uint32_t FIELD3;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint32_t FieldConstructorClashingTest::FIELD1 = 1;
const uint32_t FieldConstructorClashingTest::FIELD2 = 9;
const uint32_t FieldConstructorClashingTest::FIELD3 = 5;

TEST_F(FieldConstructorClashingTest, writeRead)
{
    FieldConstructorClashing fieldConstructorClashing;
    fillFieldConstructorClashing(fieldConstructorClashing);

    zserio::BitStreamWriter writer(bitBuffer);
    fieldConstructorClashing.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    FieldConstructorClashing readFieldConstructorClashing{reader};
    ASSERT_EQ(fieldConstructorClashing, readFieldConstructorClashing);
}

} // namespace field_constructor_clashing
} // namespace structure_types
