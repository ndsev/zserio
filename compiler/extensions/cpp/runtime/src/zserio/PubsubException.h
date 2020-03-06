#ifndef ZSERIO_PUBSUB_EXCEPTION_H_INC
#define ZSERIO_PUBSUB_EXCEPTION_H_INC

#include <string>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when an error in Pub/Sub occurs.
 */
class PubsubException : public CppRuntimeException
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the Pub/Sub failure.
     */
    explicit PubsubException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ZSERIO_PUBSUB_EXCEPTION_H_INC
