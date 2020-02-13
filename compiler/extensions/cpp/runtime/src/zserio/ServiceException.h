#ifndef ZSERIO_SERVICE_EXCEPTION_H_INC
#define ZSERIO_SERVICE_EXCEPTION_H_INC

#include <string>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when a call of a service method fails.
 */
class ServiceException : public CppRuntimeException
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the service method call failure.
     */
    explicit ServiceException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_SERVICE_EXCEPTION_H_INC
