#ifndef ZSERIO_CONSTRAINT_EXCEPTION_H_INC
#define ZSERIO_CONSTRAINT_EXCEPTION_H_INC

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when a constraint check fails.
 */
class ConstraintException : public detail::CppRuntimeExceptionHelper<ConstraintException>
{
public:
    using BaseType::CppRuntimeExceptionHelper;
};

} // namespace zserio

#endif // ifndef ZSERIO_CONSTRAINT_EXCEPTION_H_INC
