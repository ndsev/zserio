#include "gtest/gtest.h"

#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/CppRuntimeException.h"

#include "enumeration_types/multiple_removed_enum_items/AllocatorType.h"
#include "enumeration_types/multiple_removed_enum_items/Traffic.h"

using namespace zserio::literals;

namespace enumeration_types
{
namespace multiple_removed_enum_items
{

using allocator_type = AllocatorType::allocator_type;

class MultipleRemovedEnumItemsTest : public ::testing::Test
{
protected:

    static constexpr uint8_t NONE_VALUE = 1;
    static constexpr uint8_t HEAVY_VALUE = 2;
    static constexpr uint8_t LIGHT_VALUE = 3;
    static constexpr uint8_t MID_VALUE = 4;

    static constexpr size_t TRAFFIC_BIT_SIZE = 8;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

constexpr uint8_t MultipleRemovedEnumItemsTest::NONE_VALUE;
constexpr uint8_t MultipleRemovedEnumItemsTest::HEAVY_VALUE;
constexpr uint8_t MultipleRemovedEnumItemsTest::LIGHT_VALUE;
constexpr uint8_t MultipleRemovedEnumItemsTest::MID_VALUE;
constexpr size_t MultipleRemovedEnumItemsTest::TRAFFIC_BIT_SIZE;

TEST_F(MultipleRemovedEnumItemsTest, EnumTraits)
{
    ASSERT_EQ(std::string("NONE"), zserio::EnumTraits<Traffic>::names[0]);
    ASSERT_EQ(std::string("ZSERIO_REMOVED_HEAVY"), zserio::EnumTraits<Traffic>::names[1]);
    ASSERT_EQ(std::string("ZSERIO_REMOVED_LIGHT"), zserio::EnumTraits<Traffic>::names[2]);
    ASSERT_EQ(std::string("ZSERIO_REMOVED_MID"), zserio::EnumTraits<Traffic>::names[3]);

    ASSERT_EQ(Traffic::NONE, zserio::EnumTraits<Traffic>::values[0]);
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_HEAVY, zserio::EnumTraits<Traffic>::values[1]);
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_LIGHT, zserio::EnumTraits<Traffic>::values[2]);
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_MID, zserio::EnumTraits<Traffic>::values[3]);
}

TEST_F(MultipleRemovedEnumItemsTest, enumToOrdinal)
{
    ASSERT_EQ(0, zserio::enumToOrdinal(Traffic::NONE));
    ASSERT_EQ(1, zserio::enumToOrdinal(Traffic::ZSERIO_REMOVED_HEAVY));
    ASSERT_EQ(2, zserio::enumToOrdinal(Traffic::ZSERIO_REMOVED_LIGHT));
    ASSERT_EQ(3, zserio::enumToOrdinal(Traffic::ZSERIO_REMOVED_MID));
}

TEST_F(MultipleRemovedEnumItemsTest, valueToEnum)
{
    ASSERT_EQ(Traffic::NONE, zserio::valueToEnum<Traffic>(NONE_VALUE));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_HEAVY, zserio::valueToEnum<Traffic>(HEAVY_VALUE));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_LIGHT, zserio::valueToEnum<Traffic>(LIGHT_VALUE));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_MID, zserio::valueToEnum<Traffic>(MID_VALUE));
}

TEST_F(MultipleRemovedEnumItemsTest, stringToEnum)
{
    ASSERT_EQ(Traffic::NONE, zserio::stringToEnum<Traffic>("NONE"));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_HEAVY, zserio::stringToEnum<Traffic>("ZSERIO_REMOVED_HEAVY"));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_LIGHT, zserio::stringToEnum<Traffic>("ZSERIO_REMOVED_LIGHT"));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_MID, zserio::stringToEnum<Traffic>("ZSERIO_REMOVED_MID"));
}

TEST_F(MultipleRemovedEnumItemsTest, valueToEnumFailure)
{
    ASSERT_THROW(zserio::valueToEnum<Traffic>(MID_VALUE + 1), zserio::CppRuntimeException);
}

TEST_F(MultipleRemovedEnumItemsTest, stringToEnumFailure)
{
    ASSERT_THROW(zserio::stringToEnum<Traffic>("NONEXISTING"), zserio::CppRuntimeException);
}

TEST_F(MultipleRemovedEnumItemsTest, enumHashCode)
{
    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1703, zserio::calcHashCode(zserio::HASH_SEED, Traffic::NONE));
    ASSERT_EQ(1704, zserio::calcHashCode(zserio::HASH_SEED, Traffic::ZSERIO_REMOVED_HEAVY));
    ASSERT_EQ(1705, zserio::calcHashCode(zserio::HASH_SEED, Traffic::ZSERIO_REMOVED_LIGHT));
    ASSERT_EQ(1706, zserio::calcHashCode(zserio::HASH_SEED, Traffic::ZSERIO_REMOVED_MID));
}

TEST_F(MultipleRemovedEnumItemsTest, bitSizeOf)
{
    ASSERT_EQ(TRAFFIC_BIT_SIZE, zserio::bitSizeOf(Traffic::NONE));
    ASSERT_EQ(TRAFFIC_BIT_SIZE, zserio::bitSizeOf(Traffic::ZSERIO_REMOVED_HEAVY));
    ASSERT_EQ(TRAFFIC_BIT_SIZE, zserio::bitSizeOf(Traffic::ZSERIO_REMOVED_LIGHT));
    ASSERT_EQ(TRAFFIC_BIT_SIZE, zserio::bitSizeOf(Traffic::ZSERIO_REMOVED_MID));
}

TEST_F(MultipleRemovedEnumItemsTest, initializeOffsets)
{
    const size_t bitPosition = 1;
    ASSERT_EQ(bitPosition + TRAFFIC_BIT_SIZE, zserio::initializeOffsets(bitPosition, Traffic::NONE));
    ASSERT_EQ(bitPosition + TRAFFIC_BIT_SIZE,
            zserio::initializeOffsets(bitPosition, Traffic::ZSERIO_REMOVED_HEAVY));
    ASSERT_EQ(bitPosition + TRAFFIC_BIT_SIZE,
            zserio::initializeOffsets(bitPosition, Traffic::ZSERIO_REMOVED_LIGHT));
    ASSERT_EQ(bitPosition + TRAFFIC_BIT_SIZE,
            zserio::initializeOffsets(bitPosition, Traffic::ZSERIO_REMOVED_MID));
}

TEST_F(MultipleRemovedEnumItemsTest, read)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writer.writeBits(NONE_VALUE, 8);
    writer.writeBits(HEAVY_VALUE, 8);
    writer.writeBits(LIGHT_VALUE, 8);
    writer.writeBits(MID_VALUE, 8);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(Traffic::NONE, zserio::read<Traffic>(reader));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_HEAVY, zserio::read<Traffic>(reader));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_LIGHT, zserio::read<Traffic>(reader));
    ASSERT_EQ(Traffic::ZSERIO_REMOVED_MID, zserio::read<Traffic>(reader));
}

TEST_F(MultipleRemovedEnumItemsTest, write)
{
    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_NO_THROW(zserio::write(writer, Traffic::NONE));

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ASSERT_EQ(Traffic::NONE, zserio::read<Traffic>(reader));

    ASSERT_THROW(zserio::write(writer, Traffic::ZSERIO_REMOVED_HEAVY), zserio::CppRuntimeException);
    ASSERT_THROW(zserio::write(writer, Traffic::ZSERIO_REMOVED_LIGHT), zserio::CppRuntimeException);
    ASSERT_THROW(zserio::write(writer, Traffic::ZSERIO_REMOVED_MID), zserio::CppRuntimeException);
}

TEST_F(MultipleRemovedEnumItemsTest, enumTypeInfo)
{
    const auto& typeInfo = zserio::enumTypeInfo<Traffic, allocator_type>();

    ASSERT_EQ(4, typeInfo.getEnumItems().size());

    ASSERT_EQ("NONE"_sv, typeInfo.getEnumItems()[0].schemaName);
    ASSERT_EQ(NONE_VALUE, typeInfo.getEnumItems()[0].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[0].isDeprecated);
    ASSERT_FALSE(typeInfo.getEnumItems()[0].isRemoved);

    ASSERT_EQ("HEAVY"_sv, typeInfo.getEnumItems()[1].schemaName);
    ASSERT_EQ(HEAVY_VALUE, typeInfo.getEnumItems()[1].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[1].isDeprecated);
    ASSERT_TRUE(typeInfo.getEnumItems()[1].isRemoved);

    ASSERT_EQ("LIGHT"_sv, typeInfo.getEnumItems()[2].schemaName);
    ASSERT_EQ(LIGHT_VALUE, typeInfo.getEnumItems()[2].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[2].isDeprecated);
    ASSERT_TRUE(typeInfo.getEnumItems()[2].isRemoved);

    ASSERT_EQ("MID"_sv, typeInfo.getEnumItems()[3].schemaName);
    ASSERT_EQ(MID_VALUE, typeInfo.getEnumItems()[3].value);
    ASSERT_FALSE(typeInfo.getEnumItems()[3].isDeprecated);
    ASSERT_TRUE(typeInfo.getEnumItems()[3].isRemoved);
}

} // namespace multiple_removed_enum_items
} // namespace enumeration_types
