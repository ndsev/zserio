#include <string.h>
#include <algorithm>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

CppRuntimeException::CppRuntimeException(const char* message)
{
    append(message, strlen(message));
}

const char* CppRuntimeException::what() const noexcept
{
    return m_buffer;
}

void CppRuntimeException::append(const char* message, size_t messageLen)
{
    if (messageLen > 0)
    {
        const size_t available = BUFFER_SIZE - 1 - m_len;
        const size_t len = std::min(messageLen, available);

        memcpy(m_buffer + m_len, message, len);
        m_len += len;
    }
    *(m_buffer + m_len) = 0;
}

CppRuntimeException& operator<<(CppRuntimeException& exception, const char* message)
{
    exception.append(message, strlen(message));
    return exception;
}

CppRuntimeException& operator<<(CppRuntimeException& exception, bool value)
{
    return exception << (value ? "true" : "false");
}

CppRuntimeException& operator<<(CppRuntimeException& exception, float value)
{
    char buffer[48];
    const char* stringValue = convertFloatToString(buffer, value);
    return exception << stringValue;
}

CppRuntimeException& operator<<(CppRuntimeException& exception, double value)
{
    return exception << (static_cast<float>(value));
}

} // namespace zserio
