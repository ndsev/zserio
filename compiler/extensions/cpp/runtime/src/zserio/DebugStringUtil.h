/**
 * DebugStringUtil.h is not used by generated code and is provided only for user convenience.
 *
 * \note Please note that file operations allocate memory as needed and are not designed to use allocators.
 */

#ifndef ZSERIO_DEBUG_STRING_UTIL_H_INC
#define ZSERIO_DEBUG_STRING_UTIL_H_INC

#include <sstream>
#include <fstream>
#include <utility>

#include "zserio/Walker.h"
#include "zserio/JsonWriter.h"
#include "zserio/Traits.h"

namespace zserio
{

namespace detail
{

// Implementations needs to be in detail because old MSVC compiler 2015 has problems with calling overload.

template <typename T, typename WALK_FILTER, typename ALLOC>
void toDebugStream(T& object, std::ostream& os, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator)
{
    JsonWriter jsonWriter(os, indent);
    Walker walker(jsonWriter, walkFilter);
    walker.walk(object.reflectable(allocator));
}

template <typename T, typename WALK_FILTER, typename ALLOC>
string<ALLOC> toDebugString(T& object, uint8_t indent, WALK_FILTER&& walkFilter, const ALLOC& allocator)
{
    std::ostringstream os = std::ostringstream(string<ALLOC>(allocator));
    detail::toDebugStream(object, os, indent, walkFilter, allocator);
    return os.str();
}

template <typename T, typename WALK_FILTER, typename ALLOC>
void toDebugFile(T& object, const std::string& fileName, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator)
{
    std::ofstream os = std::ofstream(fileName.c_str(), std::ofstream::out);
    if (!os)
        throw CppRuntimeException("toDebugFile: Failed to open '" + fileName +"' for writing!");

    detail::toDebugStream(object, os, indent, walkFilter, allocator);

    if (!os)
        throw CppRuntimeException("toDebugFile: Failed to write '" + fileName +"'!");
}

} // namespace detail

/**
 * Writes contents of given zserio object to debug stream in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toDebugStream(T& object, std::ostream& os, const ALLOC& allocator = ALLOC())
{
    detail::toDebugStream(object, os, 4, DefaultWalkFilter(), allocator);
}

/**
 * Writes contents of given zserio object to debug stream in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toDebugStream(T& object, std::ostream& os, uint8_t indent, const ALLOC& allocator = ALLOC())
{
    detail::toDebugStream(object, os, indent, DefaultWalkFilter(), allocator);
}

/**
 * Writes contents of given zserio object to debug stream in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IWalkFilter,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toDebugStream(T& object, std::ostream& os, WALK_FILTER&& walkFilter, const ALLOC& allocator = ALLOC())
{
    detail::toDebugStream(object, os, 4, walkFilter, allocator);
}

/**
 * Writes contents of given zserio object to debug stream in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IWalkFilter,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toDebugStream(T& object, std::ostream& os, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    detail::toDebugStream(object, os, indent, walkFilter, allocator);
}

/**
 * Gets debug string in JSON format using zserio Walker with JsonWriter for given zserio object.
 *
 * \param object Zserio object to use.
 * \param allocator Allocator to use.
 *
 * \return Debug string in JSON.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
string<ALLOC> toDebugString(T& object, const ALLOC& allocator = ALLOC())
{
    return detail::toDebugString(object, 4, DefaultWalkFilter(), allocator);
}

/**
 * Gets debug string in JSON format using zserio Walker with JsonWriter for given zserio object.
 *
 * \param object Zserio object to use.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 *
 * \return Debug string in JSON.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
string<ALLOC> toDebugString(T& object, uint8_t indent, const ALLOC& allocator = ALLOC())
{
    return detail::toDebugString(object, indent, DefaultWalkFilter(), allocator);
}

/**
 * Gets debug string in JSON format using zserio Walker with JsonWriter for given zserio object.
 *
 * \param object Zserio object to use.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 *
 * \return Debug string in JSON.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IWalkFilter,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
string<ALLOC> toDebugString(T& object, WALK_FILTER&& walkFilter, const ALLOC& allocator = ALLOC())
{
    return detail::toDebugString(object, 4, walkFilter, allocator);
}

/**
 * Gets debug string in JSON format using zserio Walker with JsonWriter for given zserio object.
 *
 * \param object Zserio object to use.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 *
 * \return Debug string in JSON.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IWalkFilter,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
string<ALLOC> toDebugString(T& object, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toDebugString(object, indent, walkFilter, allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param fileName File name to write.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toDebugFile(T& object, const std::string& fileName, const ALLOC& allocator = ALLOC())
{
    return detail::toDebugFile(object, fileName, 4, DefaultWalkFilter(), allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param fileName File name to write.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toDebugFile(T& object, const std::string& fileName, uint8_t indent, const ALLOC& allocator = ALLOC())
{
    return detail::toDebugFile(object, fileName, indent, DefaultWalkFilter(), allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param fileName File name to write.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IWalkFilter,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toDebugFile(T& object, const std::string& fileName, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toDebugFile(object, fileName, 4, walkFilter, allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using zserio Walker with JsonWriter.
 *
 * \param object Zserio object to use.
 * \param fileName File name to write.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IWalkFilter,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toDebugFile(T& object, const std::string& fileName, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toDebugFile(object, fileName, indent, walkFilter, allocator);
}

} // namespace zserio

#endif // ZSERIO_DEBUG_STRING_UTIL_H_INC
