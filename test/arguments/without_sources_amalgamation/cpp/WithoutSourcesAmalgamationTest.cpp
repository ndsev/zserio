#include <fstream>
#include <string>

#include "gtest/gtest.h"

namespace without_sources_amalgamation
{

class WithoutSourcesAmalgamation : public ::testing::Test
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

TEST_F(WithoutSourcesAmalgamation, checkSources)
{
    ASSERT_FALSE(isFilePresent(
            "arguments/without_sources_amalgamation/gen/without_sources_amalgamation/GeoMapTable.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/without_sources_amalgamation/gen/without_sources_amalgamation/GeoMapTableRow.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/without_sources_amalgamation/gen/without_sources_amalgamation/MasterDatabase.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/without_sources_amalgamation/gen/without_sources_amalgamation/Tile.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/without_sources_amalgamation/gen/without_sources_amalgamation/WorldDb.cpp"));
}

} // namespace without_sources_amalgamation
