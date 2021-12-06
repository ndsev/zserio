#include <string.h>
#include <algorithm>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

CppRuntimeException::CppRuntimeException(const char* message)
{
    appendImpl(message, strlen(message));
}

CppRuntimeException::CppRuntimeException(StringView message)
{
    appendImpl(message.data(), message.size());
}

const char* CppRuntimeException::what() const noexcept
{
    return m_buffer;
}

CppRuntimeException& CppRuntimeException::append(const char* message)
{
    appendImpl(message, strlen(message));
    return *this;
}

CppRuntimeException& CppRuntimeException::append(StringView message)
{
    appendImpl(message.data(), message.size());
    return *this;
}

CppRuntimeException& CppRuntimeException::append(bool value)
{
    return append(value ? ::zserio::makeStringView("true") : ::zserio::makeStringView("false"));
}

void CppRuntimeException::appendImpl(const char* message, size_t messageLen)
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

} // namespace zserio
