#include <fstream>
#include <string>

#include "gtest/gtest.h"

#include "AmalgamationStructure.h"

namespace default_package_amalgamation
{

class DefaultPackageAmalgamationTest : public ::testing::Test
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

TEST_F(DefaultPackageAmalgamationTest, checkDefaultPackage)
{
    ASSERT_TRUE(isFilePresent(
            "arguments/with_sources_amalgamation/gen_default_package/DefaultPackageAmalgamation.cpp"));
    ASSERT_FALSE(isFilePresent(
            "arguments/with_sources_amalgamation/gen_default_package/AmalgamationStructure.cpp"));
}

TEST_F(DefaultPackageAmalgamationTest, readWrite)
{
    AmalgamationStructure structure{4, 0x0f};
    zserio::BitStreamWriter writer;
    structure.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    AmalgamationStructure readStructure(reader);

    ASSERT_EQ(structure, readStructure);
}

} // namespace default_package_amalgamation
