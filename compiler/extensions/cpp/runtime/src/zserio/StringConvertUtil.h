#ifndef ZSERIO_STRING_CONVERT_UTIL_H_INC
#define ZSERIO_STRING_CONVERT_UTIL_H_INC

#include <string>
#include <sstream>

namespace zserio
{

/**
 * Utilities for conversion of a given value to string.
 *
 * \param value Value to convert.
 *
 * \return String representation of the given value.
 *
 * \{
 */
template <typename T>
std::string convertToString(T value)
{
    std::stringstream stream;
    stream << value;

    return stream.str();
}

template <>
std::string convertToString<bool>(bool value);

template <>
std::string convertToString<char>(char value);

template <>
std::string convertToString<signed char>(signed char value);

template <>
std::string convertToString<unsigned char>(unsigned char value);

template <>
std::string convertToString<unsigned int>(unsigned int value);

template <>
std::string convertToString<int>(int value);
/** \} */

} // namespace zserio

#endif // ifndef ZSERIO_STRING_CONVERT_UTIL_H_INC
