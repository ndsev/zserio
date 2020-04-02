#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace with_sources_amalgamation
{

class WithSourcesAmalgamation : public ::testing::Test
{
protected:
    bool isFilePresent(const char* fileName)
    {
        std::ifstream file(fileName);
        const bool isPresent = file.good();
        file.close();

        return isPresent;
    }
};

TEST_F(WithSourcesAmalgamation, checkWithSourcesAmalgamationPackage)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/with_sources_amalgamation/WithSourcesAmalgamation.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/with_sources_amalgamation/GeoMapTable.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/with_sources_amalgamation/GeoMapTableRow.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/with_sources_amalgamation/WorldDb.cpp"));
}

TEST_F(WithSourcesAmalgamation, checkImportedTilePackage)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/_imported_tile_/ImportedTile.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/_imported_tile_/Tile.cpp"));
}

TEST_F(WithSourcesAmalgamation, check___Package)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/___/Amalgamation.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen/__/Empty.cpp"));
}

TEST_F(WithSourcesAmalgamation, checkWithSourcesAmalgamationDefaultPackage)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/with_sources_amalgamation/gen_default_package/"
                "WithSourcesAmalgamationDefaultPackage.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen_default_package/WithSourcesAmalgamationStructure.cpp"));
}

} // namespace with_sources_amalgamation
