#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "identifiers/structure_name_clashing_with_java/StructureNameClashingWithJava.h"

namespace identifiers
{
namespace structure_name_clashing_with_java
{

class StructureNameClashingWithJavaTest : public ::testing::Test
{
protected:
    static const size_t BIT_SIZE;
};

const size_t StructureNameClashingWithJavaTest::BIT_SIZE =
        8 * 1 + // all auto optionals
        8 + // Byte
        16 + // Short
        32 + // Integer
        64 + // Long
        64 + // BigInteger
        32 + // Float
        64 + // Double
        8; // String '\0'

TEST_F(StructureNameClashingWithJavaTest, emptyConstructor)
{
    {
        StructureNameClashingWithJava structureNameClashingWithJava;
        ASSERT_FALSE(structureNameClashingWithJava.getByteField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getShortField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getIntegerField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getLongField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getBigIntegerField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getFloatField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getDoubleField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getStringField().isValueUsed());
    }

    {
        StructureNameClashingWithJava structureNameClashingWithJava = {};
        ASSERT_FALSE(structureNameClashingWithJava.getByteField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getShortField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getIntegerField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getLongField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getBigIntegerField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getFloatField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getDoubleField().isValueUsed());
        ASSERT_FALSE(structureNameClashingWithJava.getStringField().isValueUsed());
    }
}

TEST_F(StructureNameClashingWithJavaTest, bitSizeOf)
{
    StructureNameClashingWithJava structureNameClashingWithJava{
        Byte{int8_t(0)},
        Short{int16_t(0)},
        Integer{int32_t(0)},
        Long{int64_t(0)},
        BigInteger{uint64_t(0)},
        Float{0.0f},
        Double{0.0},
        String{""}
    };

    ASSERT_EQ(BIT_SIZE, structureNameClashingWithJava.bitSizeOf());
}

} // namespace structure_name_clashing_with_java
} // namespace identifiers
