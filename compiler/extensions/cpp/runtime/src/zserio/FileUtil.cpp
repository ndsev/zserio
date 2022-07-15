#include <fstream>

#include "zserio/FileUtil.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/StringView.h"

namespace zserio
{

void writeBufferToFile(const uint8_t* buffer, size_t bitSize, BitsTag, const std::string& fileName)
{
    std::ofstream os(fileName.c_str(), std::ofstream::binary | std::ofstream::trunc);
    if (!os)
        throw CppRuntimeException("writeBufferToFile: Failed to open '") << fileName << "' for writing!";

    const size_t byteSize = (bitSize + 7) / 8;
    os.write(reinterpret_cast<const char*>(buffer), static_cast<std::streamsize>(byteSize));
    if (!os)
        throw CppRuntimeException("writeBufferToFile: Failed to write '") << fileName << "'!";
}

BitBuffer readBufferFromFile(const std::string& fileName)
{
    std::ifstream is(fileName.c_str(), std::ifstream::binary);
    if (!is)
        throw CppRuntimeException("readBufferFromFile: Cannot open '") << fileName << "' for reading!";

    is.seekg(0, is.end);
    const std::streampos fileSize = is.tellg();
    is.seekg(0);

    if (static_cast<int>(fileSize) == -1)
        throw CppRuntimeException("readBufferFromFile: Failed to get file size of '") << fileName << "'!";

    const size_t sizeLimit = std::numeric_limits<size_t>::max() / 8;
    if (static_cast<uint64_t>(fileSize) > sizeLimit)
    {
        throw CppRuntimeException("readBufferFromFile: File size exceeds limit '") << sizeLimit << "'!";
    }

    zserio::BitBuffer bitBuffer(static_cast<size_t>(fileSize) * 8);
    is.read(reinterpret_cast<char*>(bitBuffer.getBuffer()),
            static_cast<std::streamsize>(bitBuffer.getByteSize()));
    if (!is)
        throw CppRuntimeException("readBufferFromFile: Failed to read '") << fileName << "'!";

    return bitBuffer;
}

}
