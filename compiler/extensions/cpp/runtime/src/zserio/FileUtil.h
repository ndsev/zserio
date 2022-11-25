/**
 * \file
 * File utilities.
 *
 * These utilities are not used by generated code and they are provided only for user convenience.
 *
 * \note Please note that file operations allocate memory as needed and are not designed to use allocators.
 */

#ifndef ZSERIO_FILE_UTIL_H_INC
#define ZSERIO_FILE_UTIL_H_INC

#include "zserio/BitBuffer.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace zserio
{

/**
 * Read file to bit buffer object.
 *
 * \param fileName File to read.
 *
 * \return Bit buffer representing the file contents.
 *
 * \throw CppRuntimeException When reading fails.
 */
BitBuffer readBufferFromFile(const std::string& fileName);

/**
 * Writes given buffer to file.
 *
 * \param buffer Buffer to write.
 * \param bitSize Buffer bit size.
 * \param fileName Name of the file to write.
 *
 * \throw CppRuntimeException When writing fails.
 */
void writeBufferToFile(const uint8_t* buffer, size_t bitSize, BitsTag, const std::string& fileName);

/**
 * Writes given buffer to file.
 *
 * Overloaded function provided for convenience.
 *
 * \param buffer Buffer to write.
 * \param byteSize Buffer byte size.
 * \param fileName Name of the file to write.
 *
 * \throw CppRuntimeException When writing fails.
 */
inline void writeBufferToFile(const uint8_t* buffer, size_t byteSize, const std::string& fileName)
{
    writeBufferToFile(buffer, byteSize * 8, BitsTag(), fileName);
}

/**
 * Writes given bit buffer to file.
 *
 * Overloaded function provided for convenience.
 *
 * \param bitBuffer Bit buffer to write.
 * \param fileName Name of the file to write.
 *
 * \throw CppRuntimeException When writing fails.
 */
template <typename ALLOC>
inline void writeBufferToFile(const BasicBitBuffer<ALLOC>& bitBuffer, const std::string& fileName)
{
    writeBufferToFile(bitBuffer.getBuffer(), bitBuffer.getBitSize(), BitsTag(), fileName);
}

/**
 * Writes write-buffer of the given bit stream writer to file.
 *
 * Overloaded function provided for convenience.
 *
 * \param writer Bit stream writer to use.
 * \param fileName Name of the file to write.
 *
 * \throw CppRuntimeException When writing fails.
 */
inline void writeBufferToFile(const BitStreamWriter& writer, const std::string& fileName)
{
    writeBufferToFile(writer.getWriteBuffer(), writer.getBitPosition(), BitsTag(), fileName);
}

} // namespace zserio

#endif // ZSERIO_FILE_UTIL_H_INC
