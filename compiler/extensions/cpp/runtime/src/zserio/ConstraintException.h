#ifndef ZSERIO_CONSTRAINT_EXCEPTION_H_INC
#define ZSERIO_CONSTRAINT_EXCEPTION_H_INC

#include <string>

#include "CppRuntimeException.h"

namespace zserio
{

/**
 * Exception thrown when a constraint check fails.
 */
class ConstraintException : public CppRuntimeException
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the constraint check failure.
     */
    explicit ConstraintException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_CONSTRAINT_EXCEPTION_H_INC
