#include <type_traits>

#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "without_writer_code/Tile.h"
#include "without_writer_code/WorldDb.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/StringView.h"
#include "zserio/pmr/PolymorphicAllocator.h"

#include "WithoutWriterCode.h"

namespace without_writer_code
{

class WithoutWriterWithSettersCode : public ::testing::Test
{};

static const char* const PATH = "arguments/without_writer_code/gen_with_setters/without_writer_code/";

TEST_F(WithoutWriterWithSettersCode, checkTileMethods)
{
    const char* type = "Tile";

    ASSERT_METHOD_PRESENT(PATH, type, " Tile()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "void setVersion(", "void Tile::setVersion(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isVersionSet(", "bool Tile::isVersionSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetVersion(", "void Tile::resetVersion(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setNumElementOffset(", "void Tile::setNumElementOffset(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setVersionString(", "void Tile::setVersionString(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isVersionStringSet(", "bool Tile::isVersionStringSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetVersionString(", "void Tile::resetVersionString(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setOptionalVersionInfo(", "void Tile::setOptionalVersionInfo(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isOptionalVersionInfoSet(", "bool Tile::isOptionalVersionInfoSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetOptionalVersionInfo(", "void Tile::resetOptionalVersionInfo(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setNumElements(", "void Tile::setNumElements(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setData(", "void Tile::setData(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Tile::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Tile::write(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, MethodNames::REFLECTABLE_DECLARATION, MethodNames::TILE_REFLECTABLE_DEFINITION);

    ASSERT_METHOD_PRESENT(
            PATH, type, "Tile(::zserio::BitStreamReader&", "Tile::Tile(::zserio::BitStreamReader&");
    ASSERT_METHOD_PRESENT(PATH, type, "~Tile() = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile(const Tile&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile& operator=(const Tile&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile(Tile&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "Tile& operator=(Tile&&) = default;", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Tile(::zserio::PropagateAllocatorT,", "Tile::Tile(::zserio::PropagateAllocatorT,");

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::TILE_TYPE_INFO_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::REFLECTABLE_CONST_DECLARATION,
            MethodNames::TILE_REFLECTABLE_CONST_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "void initializeChildren(", "void Tile::initializeChildren(");
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability getVersionAvailability() const",
            "VersionAvailability Tile::getVersionAvailability() const");
    ASSERT_METHOD_PRESENT(PATH, type, "uint8_t getVersion(", "uint8_t Tile::getVersion(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isVersionUsed(", "bool Tile::isVersionUsed(");
    ASSERT_METHOD_PRESENT(PATH, type, MethodNames::GET_VERSION_STRING_DECLARATION,
            MethodNames::GET_VERSION_STRING_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, "bool isVersionStringUsed(", "bool Tile::isVersionStringUsed(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "uint8_t getOptionalVersionInfo(", "uint8_t Tile::getOptionalVersionInfo(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "bool isOptionalVersionInfoUsed(", "bool Tile::isOptionalVersionInfoUsed(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t getNumElementsOffset(", "uint32_t Tile::getNumElementsOffset(");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t getNumElements(", "uint32_t Tile::getNumElements(");
    ASSERT_METHOD_PRESENT(PATH, type, "& getOffsets() const", "& Tile::getOffsets() const");
    ASSERT_METHOD_PRESENT(PATH, type, "& getData() const", "& Tile::getData() const");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t bitSizeOf(", "size_t Tile::bitSizeOf(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool operator==(", "bool Tile::operator==");
    ASSERT_METHOD_PRESENT(PATH, type, "uint32_t hashCode(", "uint32_t Tile::hashCode(");

    const auto& typeInfo = Tile::typeInfo();
    ASSERT_EQ("without_writer_code.Tile", zserio::toString(typeInfo.getSchemaName()));

    // not implemented without writer code
    ASSERT_THROW(typeInfo.createInstance(), zserio::CppRuntimeException);
}

TEST_F(WithoutWriterWithSettersCode, checkWorldDbMethods)
{
    const char* type = "WorldDb";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void createSchema(", "void WorldDb::createSchema(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void deleteSchema(", "void WorldDb::deleteSchema(");

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::WORLD_DB_CTOR_DECLARATION, MethodNames::WORLD_DB_CTOR_DEFINITION);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb(sqlite3*", "WorldDb::WorldDb(sqlite3*");
    ASSERT_METHOD_PRESENT(PATH, type, "~WorldDb()", "WorldDb::~WorldDb(");
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb(const WorldDb&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb& operator=(const WorldDb&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb(WorldDb&&) = delete;", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "WorldDb& operator=(WorldDb&&) = delete;", nullptr);

    ASSERT_METHOD_PRESENT(
            PATH, type, MethodNames::TYPE_INFO_DECLARATION, MethodNames::WORLD_DB_TYPE_INFO_DEFINITION);

    ASSERT_METHOD_PRESENT(PATH, type, "::zserio::SqliteConnection& connection(",
            "::zserio::SqliteConnection& WorldDb::connection(");
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable& getEurope(", "GeoMapTable& WorldDb::getEurope(");
    ASSERT_METHOD_PRESENT(PATH, type, "GeoMapTable& getAmerica(", "GeoMapTable& WorldDb::getAmerica(");
    ASSERT_METHOD_PRESENT(PATH, type, "static ::zserio::StringView databaseName() noexcept",
            "::zserio::StringView WorldDb::databaseName() noexcept");
    ASSERT_METHOD_PRESENT(PATH, type,
            "static const ::std::array<::zserio::StringView, 2>& tableNames() noexcept",
            "const ::std::array<::zserio::StringView, 2>& WorldDb::tableNames() noexcept");
}

} // namespace without_writer_code
