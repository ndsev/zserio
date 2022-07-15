/**
 * \file
 * It provides help methods for serialization and deserialization of generated objects.
 *
 * These utilities are not used by generated code and they are provided only for user convenience.
 *
 * \note Please note that file operations allocate memory as needed and are not designed to use allocators.
 */

#ifndef ZSERIO_SERIALIZE_UTIL_H_INC
#define ZSERIO_SERIALIZE_UTIL_H_INC

#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/FileUtil.h"
#include "zserio/Traits.h"

namespace zserio
{

namespace detail
{

template <typename T>
void initializeChildrenImpl(std::true_type, T& object)
{
    object.initializeChildren();
}

template <typename T>
void initializeChildrenImpl(std::false_type, T&)
{}

template <typename T>
void initializeChildren(T& object)
{
    initializeChildrenImpl(has_initialize_children<T>(), object);
}

template <typename T, typename ...ARGS>
void initializeImpl(std::true_type, T& object, ARGS&&... arguments)
{
    object.initialize(std::forward<ARGS>(arguments)...);
}

template <typename T>
void initializeImpl(std::false_type, T& object)
{
    initializeChildren(object);
}

template <typename T, typename ...ARGS>
void initialize(T& object, ARGS&&... arguments)
{
    initializeImpl(has_initialize<T>(), object, std::forward<ARGS>(arguments)...);
}

// This implementation needs to be in detail because old MSVC compiler 2015 has problems with calling overload.
template <typename T, typename ALLOC, typename ...ARGS>
BasicBitBuffer<ALLOC> serialize(T& object, const ALLOC& allocator, ARGS&&... arguments)
{
    detail::initialize(object, std::forward<ARGS>(arguments)...);
    BasicBitBuffer<ALLOC> bitBuffer(object.initializeOffsets(), allocator);
    BitStreamWriter writer(bitBuffer);
    object.write(writer);
    return bitBuffer;
}

} // namespace detail

/**
 * Serializes given generated object to bit buffer using given allocator.
 *
 * Before serialization, the method properly calls on the given zserio object methods `initialize()`
 * (if exits), `initializeChildren()` (if exists) and `%initializeOffsets()`.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *     #include <zserio/pmr/PolymorphicAllocator.h>
 *
 *     const zserio::pmr::PolymorphicAllocator<> allocator;
 *     SomeZserioObject object(allocator);
 *     const zserio::BasicBitBuffer<zserio::pmr::PolymorphicAllocator<>> bitBuffer =
 *             zserio::serialize(object, allocator);
 * \endcode
 *
 * \param object Generated object to serialize.
 * \param allocator Allocator to use to allocate bit buffer.
 * \param arguments Object's actual parameters for initialize() method (optional).
 *
 * \return Bit buffer containing the serialized object.
 *
 * \throw CppRuntimeException When serialization fails.
 */
template <typename T, typename ALLOC, typename ...ARGS,
        typename std::enable_if<!std::is_enum<T>::value && is_allocator<ALLOC>::value, int>::type = 0>
BasicBitBuffer<ALLOC> serialize(T& object, const ALLOC& allocator, ARGS&&... arguments)
{
    return detail::serialize(object, allocator, std::forward<ARGS>(arguments)...);
}

/**
 * Serializes given generated object to bit buffer using default allocator 'std::allocator<uint8_t>'.
 *
 * Before serialization, the method properly calls on the given zserio object methods `initialize()`
 * (if exits), `initializeChildren()` (if exists) and `%initializeOffsets()`.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *
 *     SomeZserioObject object;
 *     const zserio::BitBuffer bitBuffer = zserio::serialize(object);
 * \endcode
 *
 * \param object Generated object to serialize.
 * \param arguments Object's actual parameters for initialize() method (optional).
 *
 * \return Bit buffer containing the serialized object.
 *
 * \throw CppRuntimeException When serialization fails.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>, typename ...ARGS,
        typename std::enable_if<!std::is_enum<T>::value &&
                !is_first_allocator<typename std::decay<ARGS>::type...>::value, int>::type = 0>
BasicBitBuffer<ALLOC> serialize(T& object, ARGS&&... arguments)
{
    return detail::serialize(object, ALLOC(), std::forward<ARGS>(arguments)...);
}

/**
 * Serializes given generated enum to bit buffer.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *
 *     const SomeZserioEnum enumValue = SomeZserioEnum::SomeEnumValue;
 *     const zserio::BitBuffer bitBuffer = zserio::serialize(enumValue);
 * \endcode
 *
 * \param enumValue Generated enum to serialize.
 * \param allocator Allocator to use to allocate bit buffer.
 *
 * \return Bit buffer containing the serialized enum.
 *
 * \throw CppRuntimeException When serialization fails.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_enum<T>::value, int>::type = 0>
BasicBitBuffer<ALLOC> serialize(T enumValue, const ALLOC& allocator = ALLOC())
{
    BasicBitBuffer<ALLOC> bitBuffer(zserio::bitSizeOf(enumValue), allocator);
    BitStreamWriter writer(bitBuffer);
    zserio::write(writer, enumValue);
    return bitBuffer;
}

/**
 * Deserializes given bit buffer to instance of generated object.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *
 *     SomeZserioObject object;
 *     const zserio::BitBuffer bitBuffer = zserio::serialize(object);
 *     SomeZserioObject readObject = zserio::deserialize<SomeZserioObject>(bitBuffer);
 * \endcode
 *
 * \param bitBuffer Bit buffer to use.
 * \param arguments Object's actual parameters together with allocator for object's read constructor (optional).
 *
 * \return Generated object created from the given bit buffer.
 *
 * \throw CppRuntimeException When deserialization fails.
 */
template <typename T, typename ALLOC, typename ...ARGS>
typename std::enable_if<!std::is_enum<T>::value, T>::type deserialize(
        const BasicBitBuffer<ALLOC>& bitBuffer, ARGS&&... arguments)
{
    BitStreamReader reader(bitBuffer);
    return T(reader, std::forward<ARGS>(arguments)...);
}

/**
 * Deserializes given bit buffer to instance of generated enum.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *
 *     const SomeZserioEnum enumValue = SomeZserioEnum::SomeEnumValue;
 *     const zserio::BitBuffer bitBuffer = zserio::serialize(enumValue);
 *     const SomeZserioEnum readEnumValue = zserio::deserialize<DummyEnum>(bitBuffer);
 * \endcode
 *
 * \param bitBuffer Bit buffer to use.
 *
 * \return Generated enum created from the given bit buffer.
 *
 * \throw CppRuntimeException When deserialization fails.
 */
template <typename T, typename ALLOC>
typename std::enable_if<std::is_enum<T>::value, T>::type deserialize(const BasicBitBuffer<ALLOC>& bitBuffer)
{
    BitStreamReader reader(bitBuffer);
    return zserio::read<T>(reader);
}

/**
 * Serializes given generated object to file.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *
 *     SomeZserioObject object;
 *     zserio::serializeToFile(object, "FileName.bin");
 * \endcode
 *
 * \note Please note that BitBuffer is always allocated using 'std::allocator<uint8_t>'.
 *
 * \param object Generated object to serialize.
 * \param fileName File name to write.
 *
 * \throw CppRuntimeException When serialization fails.
 */
template <typename T, typename ...ARGS>
void serializeToFile(T& object, const std::string& fileName, ARGS&&... arguments)
{
    const BitBuffer bitBuffer = serialize(object, std::forward<ARGS>(arguments)...);
    writeBufferToFile(bitBuffer, fileName);
}

/**
 * Deserializes given file contents to instance of generated object.
 *
 * Example:
 * \code{.cpp}
 *     #include <zserio/SerializeUtil.h>
 *
 *     const std::string fileName = "FileName.bin";
 *     SomeZserioObject object;
 *     zserio::serializeToFile(object, fileName);
 *     SomeZserioObject readObject = zserio::deserializeFromFile<SomeZserioObject>(fileName);
 * \endcode
 *
 * \note Please note that BitBuffer is always allocated using 'std::allocator<uint8_t>'.
 *
 * \param fileName File to use.
 * \param arguments Object's arguments (optional).
 *
 * \return Generated object created from the given file contents.
 *
 * \throw CppRuntimeException When deserialization fails.
 */
template <typename T, typename ...ARGS>
T deserializeFromFile(const std::string& fileName, ARGS&&... arguments)
{
    const BitBuffer bitBuffer = readBufferFromFile(fileName);
    return deserialize<T>(bitBuffer, std::forward<ARGS>(arguments)...);
}

} // namespace zserio

#endif // ZSERIO_SERIALIZE_UTIL_H_INC
