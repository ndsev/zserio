#ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
#define ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC

#include <exception>

#include "zserio/StringConvertUtil.h"
#include "zserio/StringView.h"

namespace zserio
{

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
     * Appends any integral value to the description.
     *
     * \param value Integral value to append.
     */
    template <typename T, typename std::enable_if<std::is_integral<T>::value, int>::type = 0>
    CppRuntimeException& append(T value)
    {
        char buffer[24];
        const char* stringValue = zserio::convertIntToString(buffer, value);
        return append(stringValue);
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
