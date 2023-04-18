#include <cstring>
#include <array>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

CppRuntimeException::CppRuntimeException(const char* message) :
        m_buffer()
{
    append(message);
}

const char* CppRuntimeException::what() const noexcept
{
    return m_buffer.data();
}

void CppRuntimeException::append(const char* message)
{
    const size_t available = m_buffer.size() - 1 - m_len;
    const size_t numCharsToAppend = strnlen(message, available);
    appendImpl(message, numCharsToAppend);
}

void CppRuntimeException::append(const char* message, size_t messageLen)
{
    const size_t available = m_buffer.size() - 1 - m_len;
    const size_t numCharsToAppend = std::min(messageLen, available);
    appendImpl(message, numCharsToAppend);
}

void CppRuntimeException::appendImpl(const char* message, size_t numCharsToAppend)
{
    if (numCharsToAppend > 0)
    {
        memcpy(m_buffer.data() + m_len, message, numCharsToAppend);
        m_len += numCharsToAppend;
    }
    m_buffer[m_len] = 0;
}

CppRuntimeException& operator<<(CppRuntimeException& exception, const char* message)
{
    exception.append(message);
    return exception;
}

CppRuntimeException& operator<<(CppRuntimeException& exception, bool value)
{
    return exception << (value ? "true" : "false");
}

CppRuntimeException& operator<<(CppRuntimeException& exception, float value)
{
    std::array<char, 24> integerPartBuffer = {};
    std::array<char, 24> floatingPartBuffer = {};
    const char* integerPartString = nullptr;
    const char* floatingPartString = nullptr;
    convertFloatToString(integerPartBuffer, floatingPartBuffer, value, integerPartString, floatingPartString);
    CppRuntimeException& result = exception << integerPartString;
    if (floatingPartString != nullptr)
        result = result << "." << floatingPartString;

    return result;
}

CppRuntimeException& operator<<(CppRuntimeException& exception, double value)
{
    return exception << (static_cast<float>(value));
}

} // namespace zserio
