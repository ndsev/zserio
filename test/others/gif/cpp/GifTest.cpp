#include <fstream>
#include <vector>
#include <string>

#include "gif/GifFile.h"

#include "gtest/gtest.h"

#include "zserio/RebindAlloc.h"

namespace gif
{

using allocator_type = GifFile::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class GifTest : public ::testing::Test
{
protected:
    bool readFileToBuffer(const string_type& fileName, vector_type<uint8_t>& buffer)
    {
        std::ifstream inputStream(fileName.c_str(), std::ios::binary);
        if (!inputStream)
            return false;

        inputStream.seekg(0, inputStream.end);
        const size_t fileSize = static_cast<size_t>(inputStream.tellg());
        inputStream.seekg(0, inputStream.beg);
        buffer.resize(fileSize);
        inputStream.read(reinterpret_cast<char*>(&buffer[0]), static_cast<std::streamsize>(buffer.size()));
        const bool result = (inputStream) ? true : false;
        inputStream.close();

        return result;
    }

    void convertUInt8ArrayToString(const vector_type<uint8_t>& array, string_type& outputString)
    {
        for (vector_type<uint8_t>::const_iterator it = array.begin(); it != array.end(); ++it)
            outputString.append(1, static_cast<char>(*it));
    }
};

TEST_F(GifTest, OnePixGif)
{
    const string_type onePixGifFileName("others/gif/data/1pix.gif");
    vector_type<uint8_t> buffer;
    ASSERT_TRUE(readFileToBuffer(onePixGifFileName, buffer));
    zserio::BitStreamReader reader(&buffer[0], buffer.size());
    const GifFile gifFile(reader);

    string_type fileFormat;
    convertUInt8ArrayToString(gifFile.getSignature().getFormat(), fileFormat);
    const string_type expectedGifFileFormat("GIF");
    ASSERT_EQ(expectedGifFileFormat, fileFormat);

    string_type fileVersion;
    convertUInt8ArrayToString(gifFile.getSignature().getVersion(), fileVersion);
    const string_type expectedGifFileVersion("89a");
    ASSERT_EQ(expectedGifFileVersion, fileVersion);

    const screen_descriptor::ScreenDescriptor& screenDescriptor = gifFile.getScreen();
    const uint16_t expectedGifScreenWidth = 256;
    ASSERT_EQ(expectedGifScreenWidth, screenDescriptor.getWidth());

    const uint16_t expectedGifScreenHeight = 256;
    ASSERT_EQ(expectedGifScreenHeight, screenDescriptor.getHeight());

    const uint8_t expectedGifScreenBgColor = 255;
    ASSERT_EQ(expectedGifScreenBgColor, screenDescriptor.getBgColor());

    const uint8_t expectedScreenBitsOfColorResolution = 7;
    ASSERT_EQ(expectedScreenBitsOfColorResolution, screenDescriptor.getBitsOfColorResolution());

    const uint8_t expectedScreenBitsPerPixel = 7;
    ASSERT_EQ(expectedScreenBitsPerPixel, screenDescriptor.getBitsPerPixel());
}

} // namespace gif
