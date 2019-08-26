#ifndef ZSERIO_BIT_STREAM_EXCEPTION_H_INC
#define ZSERIO_BIT_STREAM_EXCEPTION_H_INC

#include <string>

#include "CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown from BitStreamReader or BitStreamWriter in case of an error.
 */
class BitStreamException : public CppRuntimeException
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the error.
     */
    explicit BitStreamException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_EXCEPTION_H_INC
