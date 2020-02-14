#ifndef ZSERIO_PUBSUB_EXCEPTION_H_INC
#define ZSERIO_PUBSUB_EXCEPTION_H_INC

#include <string>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when a call of a pubsub method fails.
 */
class PubSubException : public CppRuntimeException
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the pubsub method call failure.
     */
    explicit PubSubException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_PUBSUB_EXCEPTION_H_INC
