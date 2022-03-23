#ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
#define ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC

#include <type_traits>
#include <exception>

#include "zserio/StringConvertUtil.h"
#include "zserio/StringView.h"
#include "zserio/Enums.h"

namespace zserio
{

namespace detail
{

// This decltype wrapper is needed because of old MSVC compiler 2015.
template <typename T, typename U = decltype(&T::getValue)>
struct decltype_get_value
{
    using type = U;
};

template <typename ...T>
using void_t = void;

template <typename T, typename = void>
struct has_get_value : std::false_type
{};

template <typename T>
struct has_get_value<T, void_t<typename decltype_get_value<T>::type>> : std::true_type
{};

} // namespace detail

/**
 * Exception throw when an error within the Zserio C++ runtime library occurs.
 */
class CppRuntimeException : public std::exception
{
public:
    static constexpr size_t BUFFER_SIZE = 512;

    /**
     * Constructor.
     *
     * \param message Description of the error.
     */
    explicit CppRuntimeException(const char* message = "");

    /**
     * Constructor.
     *
     * \param message Description of the error.
     */
    explicit CppRuntimeException(StringView message);

    /**
     * Appends any value for which append method is implemented to the description.
     *
     * \param value Value to append.
     */
    template <typename T>
    CppRuntimeException& operator+(const T& value)
    {
        return append(value);
    }

    const char* what() const noexcept override;

protected:
    /**
     * Appends a message to the description.
     *
     * \param message Description of the error to append.
     */
    CppRuntimeException& append(const char* message);

    /**
     * Appends a message to the description.
     *
     * \param message Description of the error to append.
     */
    CppRuntimeException& append(StringView message);

    /**
     * Appends a bool value to the description.
     *
     * \param value Bool value to append.
     */
    CppRuntimeException& append(bool value);

    /**
     * Appends a float value to the description.
     *
     * \param value Float value to append.
     */
    CppRuntimeException& append(float value);

    /**
     * Appends a double value to the description.
     *
     * \param value Double value to append.
     */
    CppRuntimeException& append(double value);

    /**
     * Appends any integral value to the description.
     *
     * \param value Integral value to append.
     */
    template <typename T, typename std::enable_if<std::is_integral<T>::value, int>::type = 0>
    CppRuntimeException& append(T value)
    {
        char buffer[24];
        const char* stringValue = convertIntToString(buffer, value);
        return append(stringValue);
    }

    /**
     * Appends any enumeration value to the description.
     *
     * \param value Enumeration value to append.
     */
    template <typename T, typename std::enable_if<std::is_enum<T>::value, int>::type = 0>
    CppRuntimeException& append(T value)
    {
        const char* stringValue = enumToString(value);
        return append(stringValue);
    }

    /**
     * Appends any object which implement getValue() method (e.g. bitmask).
     *
     * \param value Object with getValue() method to append.
     */
    template <typename T, typename std::enable_if<detail::has_get_value<T>::value, int>::type = 0>
    CppRuntimeException& append(T value)
    {
        return append(value.getValue());
    }

private:
    void appendImpl(const char* message, size_t size);

    char m_buffer[BUFFER_SIZE];
    size_t m_len = 0;
};

namespace detail
{

/** Helper middle class for descendants which defines correct operator+ overloads. */
template <typename EXCEPTION>
class CppRuntimeExceptionHelper : public CppRuntimeException
{
public:
    using CppRuntimeException::CppRuntimeException;

    template <typename T>
    EXCEPTION& operator+(const T& value)
    {
        return static_cast<EXCEPTION&>(append(value));
    }

protected:
    using BaseType = CppRuntimeExceptionHelper<EXCEPTION>;
};

} // namespace detail

} // namespace zserio

#endif // ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
