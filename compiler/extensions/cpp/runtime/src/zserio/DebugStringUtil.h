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

#include "zserio/JsonReader.h"
#include "zserio/JsonWriter.h"
#include "zserio/ReflectableUtil.h"
#include "zserio/Traits.h"
#include "zserio/Walker.h"

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
void toJsonFile(const T& object, const string<ALLOC>& fileName, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator)
{
    std::ofstream os = std::ofstream(fileName.c_str(), std::ios::out | std::ios::trunc);
    if (!os)
        throw CppRuntimeException("DebugStringUtil.toJsonFile: Failed to open '") << fileName << "'!";

    detail::toJsonStream(object, os, indent, walkFilter, allocator);

    if (!os)
        throw CppRuntimeException("DebugStringUtil.toJsonFile: Failed to write '") << fileName << "'!";
}

// needed due to GCC compilation problems, GCC tries to instantiate return type even though the
// particular fuction template has different number of arguments, this prevents the return type instantiation
// in case that the ALLOC is not an allocator
template <typename ALLOC,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
struct DebugStringTraits
{
    using ReflectablePtr = IBasicReflectablePtr<ALLOC>;
};

} // namespace detail

/**
 * Writes contents of given zserio object to debug stream in JSON format using Walker with JsonWriter.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
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
template <typename T, typename ALLOC = typename T::allocator_type,
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
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
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
template <typename T, typename ALLOC = typename T::allocator_type,
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
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *     #include <zserio/Walker.h>
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
template <typename T, typename WALK_FILTER, typename ALLOC = typename T::allocator_type,
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
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *     #include <zserio/Walker.h>
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
template <typename T, typename WALK_FILTER, typename ALLOC = typename T::allocator_type,
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
 *     #include <iostream>
 *     #include <zserio/DebugStringUtil.h>
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
template <typename T, typename ALLOC = typename T::allocator_type,
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
 *     #include <iostream>
 *     #include <zserio/DebugStringUtil.h>
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
template <typename T, typename ALLOC = typename T::allocator_type,
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
 *     #include <iostream>
 *     #include <zserio/DebugStringUtil.h>
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
template <typename T, typename WALK_FILTER, typename ALLOC = typename T::allocator_type,
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
 *     #include <iostream>
 *     #include <zserio/DebugStringUtil.h>
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
template <typename T, typename WALK_FILTER, typename ALLOC = typename T::allocator_type,
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
 *     #include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     zserio::toJsonFile(object, "FileName.json");
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param allocator Allocator to use.
 */
template <typename T, typename ALLOC = typename T::allocator_type,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toJsonFile(const T& object, const string<ALLOC>& fileName, const ALLOC& allocator = ALLOC())
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
 *     #include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     const uint8_t indent = 4;
 *     zserio::toJsonFile(object, "FileName.json", indent);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param indent Indent argument for JsonWriter.
 * \param allocator Allocator to use.
 *
 * \throw CppRuntimeException When the writing fails.
 */
template <typename T, typename ALLOC = typename T::allocator_type,
        typename std::enable_if<is_allocator<ALLOC>::value, int>::type = 0>
void toJsonFile(const T& object, const string<ALLOC>& fileName, uint8_t indent,
        const ALLOC& allocator = ALLOC())
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
 *     #include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     zserio::toJsonFile(object, "FileName.json", walkFilter);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = typename T::allocator_type,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toJsonFile(const T& object, const string<ALLOC>& fileName, WALK_FILTER&& walkFilter,
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
 *     #include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject object;
 *     const uint8_t indent = 4;
 *     zserio::ArrayLengthWalkFilter walkFilter(5);
 *     zserio::toJsonFile(object, "FileName.json", indent, walkFilter);
 * \endcode
 *
 * \param object Zserio object to use.
 * \param fileName Name of file to write.
 * \param indent Indent argument for JsonWriter.
 * \param walkFilter WalkFilter to use by Walker.
 * \param allocator Allocator to use.
 */
template <typename T, typename WALK_FILTER, typename ALLOC = typename T::allocator_type,
        typename std::enable_if<std::is_base_of<IBasicWalkFilter<ALLOC>,
                typename std::decay<WALK_FILTER>::type>::value, int>::type = 0>
void toJsonFile(const T& object, const string<ALLOC>& fileName, uint8_t indent, WALK_FILTER&& walkFilter,
        const ALLOC& allocator = ALLOC())
{
    return detail::toJsonFile(object, fileName, indent, walkFilter, allocator);
}

/**
 * Parses JSON debug string from given text stream and creates instance of the requested zserio object
 * according to the data contained in the debug string.
 *
 * \note The created object can be only partially initialzed depending on the JSON debug string.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *
 *     std::istringstream is("{ \"fieldU32\": 13 }");
 *     IReflectablePtr reflectable = fromJsonStream(SomeZserioObject::typeInfo(), is);
 *
 *     reflectable->getField("fieldU32")->getUInt32(); // 13
 * \endcode
 *
 * \param typeInfo Type info of the generated zserio object to create.
 * \param is Text stream to use.
 * \param allocator Allocator to use.
 *
 * \return Reflectable instance of the requested zserio object.
 * \throw CppRuntimeException In case of any error.
 */
template <typename ALLOC = std::allocator<uint8_t>>
typename detail::DebugStringTraits<ALLOC>::ReflectablePtr
fromJsonStream(const IBasicTypeInfo<ALLOC>& typeInfo, std::istream& is, const ALLOC& allocator = ALLOC())
{
    BasicJsonReader<ALLOC> jsonReader(is, allocator);
    return jsonReader.read(typeInfo);
}

/**
 * Parses JSON debug string from given text stream and creates instance of the requested zserio object
 * according to the data contained in the debug string.
 *
 * \note The created object can be only partially initialzed depending on the JSON debug string.
 * \note An reflectable instance is created first and then the object is move-constructed from it's any value.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *
 *     std::istringstream is("{ \"fieldU32\": 13 }");
 *     SomeZserioObject someZserioObject = fromJsonStream<SomeZserioObject>(is);
 *
 *     someZserioObject.getFieldU32(); // 13
 * \endcode
 *
 * \param is Text stream to use.
 * \param allocator Allocator to use.
 *
 * \return Instance of the requested zserio object.
 * \throw CppRuntimeException In case of any error.
 */
template <typename T, typename ALLOC = typename T::allocator_type>
T fromJsonStream(std::istream& is, const ALLOC& allocator = ALLOC())
{
    return std::move(ReflectableUtil::getValue<T, ALLOC>(fromJsonStream(T::typeInfo(), is, allocator)));
}

/**
 * Parses JSON debug string from given JSON string and creates instance of the requested zserio object
 * according to the data contained in the debug string.
 *
 * \note The created object can be only partially initialzed depending on the JSON debug string.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *
 *     std::string str("{ \"fieldU32\": 13 }")
 *     IReflectablePtr reflectable = fromJsonString(SomeZserioObject::typeInfo(), str);
 *
 *     reflectable->getField("fieldU32")->getUInt32(); // 13
 * \endcode
 *
 * \param typeInfo Type info of the generated zserio object to create.
 * \param json String to use.
 * \param allocator Allocator to use.
 *
 * \return Reflectable instance of the requested zserio object.
 * \throw CppRuntimeException In case of any error.
 */
template <typename ALLOC = std::allocator<uint8_t>>
typename detail::DebugStringTraits<ALLOC>::ReflectablePtr
fromJsonString(const IBasicTypeInfo<ALLOC>& typeInfo, const string<ALLOC>& json,
        const ALLOC& allocator = ALLOC())
{
    std::basic_istringstream<char, std::char_traits<char>, RebindAlloc<ALLOC, char>> is(json);
    return fromJsonStream(typeInfo, is, allocator);
}

/**
 * Parses JSON debug string from given JSON string and creates instance of the requested zserio object
 * according to the data contained in the debug string.
 *
 * \note The created object can be only partially initialzed depending on the JSON debug string.
 * \note An reflectable instance is created first and then the object is move-constructed from it's any value.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *
 *     std::string str("{ \"fieldU32\": 13 }")
 *     SomeZserioObject someZserioObject = fromJsonString<SomeZserioObject>(str);
 *
 *     someZserioObject.getFieldU32(); // 13
 * \endcode
 *
 * \param json String to use.
 * \param allocator Allocator to use.
 *
 * \return Instance of the requested zserio object.
 * \throw CppRuntimeException In case of any error.
 */
template <typename T, typename ALLOC = typename T::allocator_type>
T fromJsonString(const string<ALLOC>& json, const ALLOC& allocator = ALLOC())
{
    return std::move(ReflectableUtil::getValue<T, ALLOC>(fromJsonString(T::typeInfo(), json, allocator)));
}

/**
 * Parses JSON debug string from given text file and creates instance of the requested zserio object
 * according to the data contained in the debug string.
 *
 * \note The created object can be only partially initialzed depending on the JSON debug string.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *
 *     IReflectablePtr reflectable = fromJsonFile(SomeZserioObject::typeInfo(), "FileName.json");
 *
 *     reflectable->getField("fieldU32")->getUInt32(); // 13
 * \endcode
 *
 * \param typeInfo Type info of the generated zserio object to create.
 * \param fileName Name of file to read.
 * \param allocator Allocator to use.
 *
 * \return Reflectable instance of the requested zserio object.
 * \throw CppRuntimeException In case of any error.
 */
template <typename ALLOC = std::allocator<uint8_t>>
typename detail::DebugStringTraits<ALLOC>::ReflectablePtr
fromJsonFile(const IBasicTypeInfo<ALLOC>& typeInfo, const string<ALLOC>& fileName,
        const ALLOC& allocator = ALLOC())
{
    std::ifstream is = std::ifstream(fileName.c_str());
    if (!is)
        throw CppRuntimeException("DebugStringUtil.fromJsonFile: Failed to open '") << fileName + "'!";
    return fromJsonStream(typeInfo, is, allocator);
}

/**
 * Parses JSON debug string from given text file and creates instance of the requested zserio object
 * according to the data contained in the debug string.
 *
 * \note The created object can be only partially initialzed depending on the JSON debug string.
 * \note An reflectable instance is created first and then the object is move-constructed from it's any value.
 *
 * Example:
 * \code{.cpp}
 *     #include <sstream>
 *     #include <zserio/DebugStringUtil.h>
 *
 *     SomeZserioObject someZserioObject = fromJsonFile<SomeZserioObject>("FileName.json");
 *
 *     someZserioObject.getFieldU32(); // 13
 * \endcode
 *
 * \param fileName Name of file to read.
 * \param allocator Allocator to use.
 *
 * \return Instance of the requested zserio object.
 * \throw CppRuntimeException In case of any error.
 */
template <typename T, typename ALLOC = typename T::allocator_type>
T fromJsonFile(const string<ALLOC>& fileName, const ALLOC& allocator = ALLOC())
{
    return std::move(ReflectableUtil::getValue<T, ALLOC>(fromJsonFile(T::typeInfo(), fileName, allocator)));
}

} // namespace zserio

#endif // ZSERIO_DEBUG_STRING_UTIL_H_INC
