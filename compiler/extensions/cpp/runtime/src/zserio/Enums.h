#ifndef ZSERIO_ENUMS_H_INC
#define ZSERIO_ENUMS_H_INC

#include <cstddef>
#include <type_traits>
#include <algorithm>

#include "zserio/Types.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/StringView.h"

// This should be implemented in runtime library header.
namespace zserio
{

// forward declarations
class BitStreamReader;
class BitStreamWriter;

/**
 * Enum traits for Zserio enums.
 */
template <typename T>
struct EnumTraits
{
};

/**
 * Gets ordinal number of the given enum item.
 *
 * \param value Enum item.
 *
 * \return Ordinal number of the enum item.
 */
template <typename T>
size_t enumToOrdinal(T value);

/**
 * Converts the given raw value to an appropriate enum item.
 *
 * \param rawValue Raw value of the proper underlying type.
 *
 * \return Enum item corresponding to the rawValue.
 *
 * \throw CppRuntimeException when the rawValue doesn't match to any enum item.
 */
template <typename T>
T valueToEnum(typename std::underlying_type<T>::type rawValue);

/**
 * Gets the underlying raw value of the given enum item.
 *
 * \param value Enum item.
 *
 * \return Raw value.
 */
template <typename T>
constexpr typename std::underlying_type<T>::type enumToValue(T value)
{
    return static_cast<typename std::underlying_type<T>::type>(value);
}

/**
 * Gets the hash code of the given enum item.
 *
 * \param value Enum item.
 *
 * \return Hash code of the enum item.
 */
template <typename T>
uint32_t enumHashCode(T value);

/**
 * Converts the given enum item name to an appropriate enum item.
 *
 * \param itemName Name of the enum item.
 *
 * \return Enum item corresponding to the itemName.
 *
 * \throw CppRuntimeException when the itemName doesn't match to any enum item.
 */
template <typename T>
T stringToEnum(StringView itemName)
{
    const auto foundIt = std::find_if(EnumTraits<T>::names.begin(), EnumTraits<T>::names.end(),
            [itemName](const char* enumItemName){ return itemName.compare(enumItemName) == 0; });
    if (foundIt == EnumTraits<T>::names.end())
    {
        throw CppRuntimeException("Enum item '") << itemName << "' doesn't exist in enum '" <<
                EnumTraits<T>::enumName << "'!";
    }

    const size_t ordinal = static_cast<size_t>(std::distance(EnumTraits<T>::names.begin(), foundIt));
    return EnumTraits<T>::values[ordinal];
}

/**
 * Gets the name of the given enum item.
 *
 * \param value Enum item.
 *
 * \return Name of the enum item.
 */
template <typename T>
const char* enumToString(T value)
{
    return EnumTraits<T>::names[enumToOrdinal(value)];
}

/**
 * Initializes packing context for the given enum item.
 *
 * \param context Packing context.
 * \param value Enum item.
 */
template <typename PACKING_CONTEXT, typename T>
void initPackingContext(PACKING_CONTEXT& context, T value);

/**
 * Gets bit size of the given enum item.
 *
 * Note that T can be varuint, so bitSizeOf cannot return constant value and depends on the concrete item.
 *
 * \param value Enum item.
 *
 * \return Bit size of the enum item.
 */
template <typename T>
size_t bitSizeOf(T value);

/**
 * Gets bit size of the given enum item when it's inside a packed array.
 *
 * Note that T can be varuint, so bitSizeOf cannot return constant value and depends on the concrete item.
 *
 * \param context Packing context.
 * \param value Enum item.
 *
 * \return Bit size of the enum item.
 */
template <typename PACKING_CONTEXT, typename T>
size_t bitSizeOf(PACKING_CONTEXT& context, T value);

/**
 * Initializes offsets for the enum item.
 *
 * Note that T can be varuint, so initializeOffsets cannot return constant value and
 * depends on the concrete item.
 *
 * \param bitPosition Current bit position.
 * \param value Enum item.
 *
 * \return Updated bit position which points to the first bit after the enum item.
 */
template <typename T>
size_t initializeOffsets(size_t bitPosition, T value);

/**
 * Initializes offsets for the enum item when it's inside a packed array.
 *
 * Note that T can be varuint, so initializeOffsets cannot return constant value and
 * depends on the concrete item.
 *
 * \param context Packing context.
 * \param bitPosition Current bit position.
 * \param value Enum item.
 *
 * \return Updated bit position which points to the first bit after the enum item.
 */
template <typename PACKING_CONTEXT, typename T>
size_t initializeOffsets(PACKING_CONTEXT& context, size_t bitPosition, T value);

/**
 * Reads an enum item.
 *
 * \param in Bit stream reader.
 *
 * \return Enum item read from the bit stream.
 */
template <typename T>
T read(BitStreamReader& in);

/**
 * Reads an enum item which is inside a packed array.
 *
 * \param context Packing context.
 * \param in Bit stream reader.
 *
 * \return Enum item read from the bit stream.
 */
template <typename T, typename PACKING_CONTEXT>
T read(PACKING_CONTEXT& context, BitStreamReader& in);

/**
 * Writes the enum item to the given bit stream.
 *
 * \param out Bit stream writer.
 * \param value Enum item to write.
 */
template <typename T>
void write(BitStreamWriter& out, T value);

/**
 * Writes the enum item which is inside a packed array to the given bit stream.
 *
 * \param context Packing context.
 * \param out Bit stream writer.
 * \param value Enum item to write.
 */
template <typename PACKING_CONTEXT, typename T>
void write(PACKING_CONTEXT& context, BitStreamWriter& out, T value);

/**
 * Appends any enumeration value to the exception's description.
 *
 * \param exception Exception to modify.
 * \param value Enumeration value to append.
 *
 * \return Reference to the exception to allow operator chaining.
 */
template <typename T, typename std::enable_if<std::is_enum<T>::value, int>::type = 0>
CppRuntimeException& operator<<(CppRuntimeException& exception, T value)
{
    exception << enumToString(value);
    return exception;
}

} // namespace zserio

#endif // ZSERIO_ENUMS_H_INC
