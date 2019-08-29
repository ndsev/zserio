#ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
#define ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC

#include <string>
#include <stdexcept>

namespace zserio
{

class CppRuntimeException : public std::runtime_error
{
public:
    explicit CppRuntimeException(const std::string& message) : std::runtime_error(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
