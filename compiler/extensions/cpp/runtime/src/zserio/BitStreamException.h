#ifndef ZSERIO_BIT_STREAM_EXCEPTION_H_INC
#define ZSERIO_BIT_STREAM_EXCEPTION_H_INC

#include <string>

#include "CppRuntimeException.h"

namespace zserio
{

class BitStreamException : public CppRuntimeException
{
public:
    explicit BitStreamException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_EXCEPTION_H_INC
