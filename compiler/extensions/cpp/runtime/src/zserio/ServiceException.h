#ifndef ZSERIO_SERVICE_EXCEPTION_H_INC
#define ZSERIO_SERVICE_EXCEPTION_H_INC

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when a call of a service method fails.
 */
class ServiceException : public detail::CppRuntimeExceptionHelper<ServiceException>
{
public:
    using BaseType::CppRuntimeExceptionHelper;
};

} // namespace zserio

#endif // ZSERIO_SERVICE_EXCEPTION_H_INC
