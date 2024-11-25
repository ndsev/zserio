#include <fstream>

#include "enumeration_types/deprecated_enum_item/Dummy.h"
#include "enumeration_types/deprecated_enum_item/Traffic.h"
#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

using namespace zserio::literals;

namespace enumeration_types
{
namespace deprecated_enum_item
{

using allocator_type = Dummy::allocator_type;

class DeprecatedEnumItemTest : public ::testing::Test
{
protected:
    static constexpr uint8_t NONE_VALUE = 1;
    static constexpr uint8_t HEAVY_VALUE = 2;
    static constexpr uint8_t LIGHT_VALUE = 3;
    static constexpr uint8_t MID_VALUE = 4;

    static constexpr size_t TRAFFIC_BIT_SIZE = 8;

    static const char* const TRAFFIC_HEADER_PATH;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

constexpr uint8_t DeprecatedEnumItemTest::NONE_VALUE;
constexpr uint8_t DeprecatedEnumItemTest::HEAVY_VALUE;
constexpr uint8_t DeprecatedEnumItemTest::LIGHT_VALUE;
constexpr uint8_t DeprecatedEnumItemTest::MID_VALUE;
constexpr size_t DeprecatedEnumItemTest::TRAFFIC_BIT_SIZE;
const char* const DeprecatedEnumItemTest::TRAFFIC_HEADER_PATH =
        "language/enumeration_types/gen/enumeration_types/deprecated_enum_item/Traffic.h";

TEST_F(DeprecatedEnumItemTest, deprecatedMacroPresent)
{
    ASSERT_STRING_IN_FILE_PRESENT(TRAFFIC_HEADER_PATH, "HEAVY ZSERIO_DEPRECATED =");
}

TEST_F(DeprecatedEnumItemTest, EnumTraits)
{
    ASSERT_EQ(std::string("NONE"), zserio::EnumTraits<Traffic>::names[0]);
    ASSERT_EQ(std::string("HEAVY"), zserio::EnumTraits<Traffic>::names[1]);
    ASSERT_EQ(std::string("LIGHT"), zserio::EnumTraits<Traffic>::names[2]);
    ASSERT_EQ(std::string("MID"), zserio::EnumTraits<Traffic>::names[3]);

    ASSERT_EQ(Traffic::NONE, zserio::EnumTraits<Traffic>::values[0]);
    ASSERT_EQ(Traffic::HEAVY, zserio::EnumTraits<Traffic>::values[1]);
    ASSERT_EQ(Traffic::LIGHT, zserio::EnumTraits<Traffic>::values[2]);
    ASSERT_EQ(Traffic::MID, zserio::EnumTraits<Traffic>::values[3]);
}

TEST_F(DeprecatedEnumItemTest, enumToOrdinal)
{
    ASSERT_EQ(0, zserio::enumToOrdinal(Traffic::NONE));
    ASSERT_EQ(1, zserio::enumToOrdinal(Traffic::HEAVY));
    ASSERT_EQ(2, zserio::enumToOrdinal(Traffic::LIGHT));
    ASSERT_EQ(3, zserio::enumToOrdinal(Traffic::MID));
}

TEST_F(DeprecatedEnumItemTest, valueToEnum)
{
    ASSERT_EQ(Traffic::NONE, zserio::valueToEnum<Traffic>(NONE_VALUE));
    ASSERT_EQ(Traffic::HEAVY, zserio::valueToEnum<Traffic>(HEAVY_VALUE));
    ASSERT_EQ(Traffic::LIGHT, zserio::valueToEnum<Traffic>(LIGHT_VALUE));
    ASSERT_EQ(Traffic::MID, zserio::valueToEnum<Traffic>(MID_VALUE));
}

TEST_F(DeprecatedEnumItemTest, stringToEnum)
{
    ASSERT_EQ(Traffic::NONE, zserio::stringToEnum<Traffic>("NONE"));
    ASSERT_EQ(Traffic::HEAVY, zserio::stringToEnum<Traffic>("HEAVY"));
    ASSERT_EQ(Traffic::LIGHT, zserio::stringToEnum<Traffic>("LIGHT"));
    ASSERT_EQ(Traffic::MID, zserio::stringToEnum<Traffic>("MID"));
}

TEST_F(DeprecatedEnumItemTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<Traffic>(MID_VALUE + 1), zserio::CppRuntimeException);
}

TEST_F(DeprecatedEnumItemTest, stringToEnumFailure)
{
    ASSERT_THROW(zserio::stringToEnum<Traffic>("NONEXISTING"), zserio::CppRuntimeException);
}

TEST_F(DeprecatedEnumItemTest, enumHashCode)
{
    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1703, zserio::calcHashCode(zserio::HASH_SEED, Traffic::NONE));
    ASSERT_EQ(1704, zserio::calcHashCode(zserio::HASH_SEED, Traffic::HEAVY));
    ASSERT_EQ(1705, zserio::calcHashCode(zserio::HASH_SEED, Traffic::LIGHT));
    ASSERT_EQ(1706, zserio::calcHashCode(zserio::HASH_SEED, Traffic::MID));
}

TEST_F(DeprecatedEnumItemTest, bitSizeOf)
{
    ASSERT_EQ(TRAFFIC_BIT_SIZE, zserio::bitSizeOf(Traffic::HEAVY));
}

TEST_F(DeprecatedEnumItemTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + TRAFFIC_BIT_SIZE, zserio::initializeOffsets(bitPosition, Traffic::HEAVY));
}

TEST_F(DeprecatedEnumItemTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(HEAVY_VALUE, 8);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    auto traffic = zserio::read<Traffic>(reader);
    ASSERT_EQ(Traffic::HEAVY, traffic);
}

TEST_F(DeprecatedEnumItemTest, writeRead)
{
    zserio::BitStreamWriter writer(bitBuffer);
    zserio::write(writer, Traffic::NONE);
    zserio::write(writer, Traffic::HEAVY);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(Traffic::NONE, zserio::read<Traffic>(reader));
    ASSERT_EQ(Traffic::HEAVY, zserio::read<Traffic>(reader));
}

TEST_F(DeprecatedEnumItemTest, enumTypeInfo)
{
    const auto& typeInfo = zserio::enumTypeInfo<Traffic, allocator_type>();

    ASSERT_EQ(4, typeInfo.getEnumItems().size());

    ASSERT_EQ("NONE"_sv, typeInfo.getEnumItems()[0].schemaName);
    ASSERT_EQ(NONE_VALUE, typeInfo.getEnumItems()[0].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[0].isDeprecated);
    ASSERT_FALSE(typeInfo.getEnumItems()[0].isRemoved);

    ASSERT_EQ("HEAVY"_sv, typeInfo.getEnumItems()[1].schemaName);
    ASSERT_EQ(HEAVY_VALUE, typeInfo.getEnumItems()[1].value);
    ASSERT_TRUE(typeInfo.getEnumItems()[1].isDeprecated);
    ASSERT_FALSE(typeInfo.getEnumItems()[1].isRemoved);

    ASSERT_EQ("LIGHT"_sv, typeInfo.getEnumItems()[2].schemaName);
    ASSERT_EQ(LIGHT_VALUE, typeInfo.getEnumItems()[2].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[2].isDeprecated);
    ASSERT_FALSE(typeInfo.getEnumItems()[2].isRemoved);

    ASSERT_EQ("MID"_sv, typeInfo.getEnumItems()[3].schemaName);
    ASSERT_EQ(MID_VALUE, typeInfo.getEnumItems()[3].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[3].isDeprecated);
    ASSERT_FALSE(typeInfo.getEnumItems()[3].isRemoved);
}

} // namespace deprecated_enum_item
} // namespace enumeration_types
