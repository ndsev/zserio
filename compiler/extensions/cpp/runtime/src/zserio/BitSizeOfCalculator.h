#ifndef ZSERIO_BITSIZEOF_CALCULATOR_H_INC
#define ZSERIO_BITSIZEOF_CALCULATOR_H_INC

#include <cstddef>
#include <string>

#include "zserio/Types.h"
#include "zserio/BitBuffer.h"

namespace zserio
{

/**
 * Calculates bit size of Zserio varint16 type.
 *
 * \param value Varint16 value.
 *
 * \return Bit size of the current varint16 value.
 */
size_t bitSizeOfVarInt16(int16_t value);

/**
 * Calculates bit size of Zserio varint32 type.
 *
 * \param value Varint32 value.
 *
 * \return Bit size of the current varint32 value.
 */
size_t bitSizeOfVarInt32(int32_t value);

/**
 * Calculates bit size of Zserio varint64 type.
 *
 * \param value Varint64 value.
 *
 * \return Bit size of the current varint64 value.
 */
size_t bitSizeOfVarInt64(int64_t value);

/**
 * Calculates bit size of Zserio varuint16 type.
 *
 * \param value Varuint16 value.
 *
 * \return Bit size of the current varuint16 value.
 */
size_t bitSizeOfVarUInt16(uint16_t value);

/**
 * Calculates bit size of Zserio varuint32 type.
 *
 * \param value Varuint32 value.
 *
 * \return Bit size of the current varuint32 value.
 */
size_t bitSizeOfVarUInt32(uint32_t value);

/**
 * Calculates bit size of Zserio varuint64 type.
 *
 * \param value Varuint64 value.
 *
 * \return Bit size of the current varuint64 value.
 */
size_t bitSizeOfVarUInt64(uint64_t value);

/**
 * Calculates bit size of Zserio varint type.
 *
 * \param value Varint value.
 *
 * \return Bit size of the current varint value.
 */
size_t bitSizeOfVarInt(int64_t value);

/**
 * Calculates bit size of Zserio varuint type.
 *
 * \param value Varuint value.
 *
 * \return Bit size of the current varuint value.
 */
size_t bitSizeOfVarUInt(uint64_t value);

/**
 * Calculates bit size of the string.
 *
 * \param stringValue String for which to calculate bit size.
 *
 * \return Bit size of the given string.
 */
size_t bitSizeOfString(const std::string& stringValue);

/**
 * Calculates bit size of the bit buffer.
 *
 * \param bitBuffer Bit buffer for which to calculate bit size.
 *
 * \return Bit size of the given bit buffer.
 */
size_t bitSizeOfBitBuffer(const BitBuffer& bitBuffer);

} // namespace zserio

#endif // ifndef ZSERIO_BITSIZEOF_CALCULATOR_H_INC
