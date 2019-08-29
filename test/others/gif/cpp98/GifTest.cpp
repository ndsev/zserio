#include <fstream>
#include <vector>
#include <string>

#include "gif/GifFile.h"

#include "gtest/gtest.h"

namespace gif
{

class GifTest : public ::testing::Test
{
protected:
    bool readFileToBuffer(const std::string& fileName, std::vector<uint8_t>& buffer)
    {
        std::ifstream inputStream(fileName.c_str(), std::ios::binary);
        if (!inputStream)
            return false;

        inputStream.seekg(0, inputStream.end);
        const size_t fileSize = static_cast<size_t>(inputStream.tellg());
        inputStream.seekg(0, inputStream.beg);
        buffer.resize(fileSize);
        inputStream.read(reinterpret_cast<char*>(&buffer[0]), buffer.size());
        const bool result = (inputStream) ? true : false;
        inputStream.close();

        return result;
    }

    void convertUInt8ArrayToString(const zserio::UInt8Array& array, std::string& outputString)
    {
        for (zserio::UInt8Array::const_iterator it = array.begin(); it != array.end(); ++it)
            outputString.append(1, static_cast<char>(*it));
    }
};

TEST_F(GifTest, OnePixGif)
{
    const std::string onePixGifFileName("others/gif/data/1pix.gif");
    std::vector<uint8_t> buffer;
    ASSERT_TRUE(readFileToBuffer(onePixGifFileName, buffer));
    zserio::BitStreamReader reader(&buffer[0], buffer.size());
    const GifFile gifFile(reader);

    std::string fileFormat;
    convertUInt8ArrayToString(gifFile.getSignature().getFormat(), fileFormat);
    const std::string expectedGifFileFormat("GIF");
    ASSERT_EQ(expectedGifFileFormat, fileFormat);

    std::string fileVersion;
    convertUInt8ArrayToString(gifFile.getSignature().getVersion(), fileVersion);
    const std::string expectedGifFileVersion("89a");
    ASSERT_EQ(expectedGifFileVersion, fileVersion);

    const screen_descriptor::ScreenDescriptor& screenDescriptor = gifFile.getScreen();
    const uint16_t expectedGifScreenWidth = 256;
    ASSERT_EQ(expectedGifScreenWidth, screenDescriptor.getWidth());

    const uint16_t expectedGifScreenHeight = 256;
    ASSERT_EQ(expectedGifScreenHeight, screenDescriptor.getHeight());

    const uint8_t expectedGifScreenBgColor = 255;
    ASSERT_EQ(expectedGifScreenBgColor, screenDescriptor.getBgColor());

    const uint8_t expectedScreenBitsOfColorResolution = 7;
    ASSERT_EQ(expectedScreenBitsOfColorResolution, screenDescriptor.getBitsOfColorResulution());

    const uint8_t expectedScreenBitsPerPixel = 7;
    ASSERT_EQ(expectedScreenBitsPerPixel, screenDescriptor.getBitsPerPixel());
}

} // namespace gif
