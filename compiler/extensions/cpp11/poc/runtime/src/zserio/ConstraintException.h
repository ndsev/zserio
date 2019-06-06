#ifndef ZSERIO_CONSTRAINT_EXCEPTION_H_INC
#define ZSERIO_CONSTRAINT_EXCEPTION_H_INC

#include <string>

#include "CppRuntimeException.h"

namespace zserio
{

class ConstraintException : public CppRuntimeException
{
public:
    explicit ConstraintException(const std::string& message) : CppRuntimeException(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_CONSTRAINT_EXCEPTION_H_INC
