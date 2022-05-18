/**
 * \file
 * It provides utilities for JSON debug string which can be obtained from zserio objects.
 *
 * These utilities are not used by generated code and they are provided only for user convenience.
 *
 * \note Please note that Zserio objects must be generated with `-withTypeInfoCode` and `-withReflectionCode`
 * zserio options to enable JSON debug string!
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
void toJsonStream(const T& object, std::ostream& os, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator)
{
    static_assert(has_reflectable<T>::value, "DebugStringUtil.toJsonStream: "
            "Zserio object must have reflections enabled (see zserio option -withReflectionCode)!");

    BasicJsonWriter<ALLOC> jsonWriter(os, indent);
    BasicWalker<ALLOC> walker(jsonWriter, walkFilter);
    walker.walk(object.reflectable(allocator));
}

template <typename T, typename WALK_FILTER, typename ALLOC>
string<ALLOC> toJsonString(const T& object, uint8_t indent, WALK_FILTER&& walkFilter, const ALLOC& allocator)
{
    auto os = std::basic_ostringstream<char, std::char_traits<char>, RebindAlloc<ALLOC, char>>(
            string<ALLOC>(allocator));
    detail::toJsonStream(object, os, indent, walkFilter, allocator);
    return os.str();
}

template <typename T, typename WALK_FILTER, typename ALLOC>
void toJsonFile(const T& object, const std::string& fileName, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator)
{
    std::ofstream os = std::ofstream(fileName.c_str(), std::ofstream::out);
    if (!os)
        throw CppRuntimeException("DebugStringUtil.toJsonFile: Failed to open '" + fileName +"' for writing!");

    detail::toJsonStream(object, os, indent, walkFilter, allocator);

    if (!os)
        throw CppRuntimeException("DebugStringUtil.toJsonFile: Failed to write '" + fileName +"'!");
}

} // namespace detail

/**
 * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
 *
 * Example:
 * \code{.cpp}
 *     include <sstream>
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     std::ostringstream os;
 *     zserio::toJsonStream(object, os);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toJsonStream(const T& object, std::ostream& os, const ALLOC& allocator = ALLOC())
{
    detail::toJsonStream(object, os, 4, BasicDefaultWalkFilter<ALLOC>(), allocator);
}

/**
 * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
 *
 * This function allows setting of indentation of JSON output.
 *
 * Example:
 * \code{.cpp}
 *     include <sstream>
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     std::ostringstream os;
 *     const uint8_t indent = 4;
 *     zserio::toJsonStream(object, os, indent);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toJsonStream(const T& object, std::ostream& os, uint8_t indent, const ALLOC& allocator = ALLOC())
{
    detail::toJsonStream(object, os, indent, BasicDefaultWalkFilter<ALLOC>(), allocator);
}

/**
 * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
 *
 * This function allows setting of walk filter.
 *
 * The following example shows filtering of arrays up to 5 elements:
 * \code{.cpp}
 *     include <sstream>
 *     include <zserio/DebugStringUtil.h>
 *     include <zserio/Walker.h>
 *
 *     SomeZserioObject object;
 *     std::ostringstream os;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     zserio::toJsonStream(object, os, walkFilter);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toJsonStream(const T& object, std::ostream& os, WALK_FILTER&& walkFilter, const ALLOC& allocator = ALLOC())
{
    detail::toJsonStream(object, os, 4, walkFilter, allocator);
}

/**
 * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
 *
 * This function allows setting of indentation of JSON output together with the walk filter.
 *
 * Example:
 * \code{.cpp}
 *     include <sstream>
 *     include <zserio/DebugStringUtil.h>
 *     include <zserio/Walker.h>
 *
 *     SomeZserioObject object;
 *     std::ostringstream os;
 *     const uint8_t indent = 4;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     zserio::toJsonStream(object, os, indent, walkFilter);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param os Output stream to use.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toJsonStream(const T& object, std::ostream& os, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    detail::toJsonStream(object, os, indent, walkFilter, allocator);
}

/**
 * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
 *
 * Example:
 * \code{.cpp}
 *     include <iostream>
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     std::cout << zserio::toJsonString(object) << std::endl;
 * \endcode
 *
 * \param object Zserio object to use.
 * \param allocator Allocator to use.
 *
 * \return JSON debug string.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
string<ALLOC> toJsonString(const T& object, const ALLOC& allocator = ALLOC())
{
    return detail::toJsonString(object, 4, BasicDefaultWalkFilter<ALLOC>(), allocator);
}

/**
 * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
 *
 * This function allows setting of indentation of JSON output.
 *
 * Example:
 * \code{.cpp}
 *     include <iostream>
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     const uint8_t indent = 4;
 *     std::cout << zserio::toJsonString(object, indent) << std::endl;
 * \endcode
 *
 * \param object Zserio object to use.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 *
 * \return JSON debug string.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
string<ALLOC> toJsonString(const T& object, uint8_t indent, const ALLOC& allocator = ALLOC())
{
    return detail::toJsonString(object, indent, BasicDefaultWalkFilter<ALLOC>(), allocator);
}

/**
 * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
 *
 * This function allows setting of walk filter.
 *
 * The following example shows filtering of arrays up to 5 elements:
 * \code{.cpp}
 *     include <iostream>
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     std::cout << zserio::toJsonString(object, walkFilter) << std::endl;
 * \endcode
 *
 * \param object Zserio object to use.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 *
 * \return JSON debug string.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
string<ALLOC> toJsonString(const T& object, WALK_FILTER&& walkFilter, const ALLOC& allocator = ALLOC())
{
    return detail::toJsonString(object, 4, walkFilter, allocator);
}

/**
 * Gets debug string in JSON format using Walker with JsonWriter for given zserio object.
 *
 * This function allows setting of indentation of JSON output together with the walk filter.
 *
 * Example:
 * \code{.cpp}
 *     include <iostream>
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     const uint8_t indent = 4;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     std::cout << zserio::toJsonString(object, indent, walkFilter) << std::endl;
 * \endcode
 *
 * \param object Zserio object to use.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 *
 * \return JSON debug string.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
string<ALLOC> toJsonString(const T& object, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toJsonString(object, indent, walkFilter, allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
 *
 * Example:
 * \code{.cpp}
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     zserio::toJsonFile(object, "FileName.bin");
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toJsonFile(const T& object, const std::string& fileName, const ALLOC& allocator = ALLOC())
{
    return detail::toJsonFile(object, fileName, 4, BasicDefaultWalkFilter<ALLOC>(), allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
 *
 * This function allows setting of indentation of JSON output.
 *
 * Example:
 * \code{.cpp}
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     const uint8_t indent = 4;
 *     zserio::toJsonFile(object, "FileName.bin", indent);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toJsonFile(const T& object, const std::string& fileName, uint8_t indent, const ALLOC& allocator = ALLOC())
{
    return detail::toJsonFile(object, fileName, indent, BasicDefaultWalkFilter<ALLOC>(), allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
 *
 * This function allows setting of walk filter.
 *
 * The following example shows filtering of arrays up to 5 elements:
 *
 * Example:
 * \code{.cpp}
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     zserio::toJsonFile(object, "FileName.bin", walkFilter);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toJsonFile(const T& object, const std::string& fileName, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toJsonFile(object, fileName, 4, walkFilter, allocator);
}

/**
 * Writes contents of given zserio object to debug file in JSON format using Walker with JsonWriter.
 *
 * This function allows setting of indentation of JSON output together with the walk filter.
 *
 * Example:
 * \code{.cpp}
 *     include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     const uint8_t indent = 4;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     zserio::toJsonFile(object, "FileName.bin", indent, walkFilter);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = std::allocator<uint8_t>,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toJsonFile(const T& object, const std::string& fileName, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toJsonFile(object, fileName, indent, walkFilter, allocator);
}

} // namespace zserio

#endif // ZSERIO_DEBUG_STRING_UTIL_H_INC
