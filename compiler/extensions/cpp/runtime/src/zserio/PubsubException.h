#ifndef ZSERIO_PUBSUB_EXCEPTION_H_INC
#define ZSERIO_PUBSUB_EXCEPTION_H_INC

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when an error in Pub/Sub occurs.
 */
class PubsubException : public CppRuntimeException
{
public:
    using CppRuntimeException::CppRuntimeException;
};

} // namespace zserio

#endif // ZSERIO_PUBSUB_EXCEPTION_H_INC
