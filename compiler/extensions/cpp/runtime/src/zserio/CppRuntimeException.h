#ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
#define ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC

#include <string>
#include <stdexcept>

namespace zserio
{

/**
 * Exception throw when an error within the Zserio C++ runtime library occurs.
 */
class CppRuntimeException : public std::runtime_error
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the error.
     */
    explicit CppRuntimeException(const std::string& message) : std::runtime_error(message) {}
};

} // namespace zserio

#endif // ifndef ZSERIO_CPP_RUNTIME_EXCEPTION_H_INC
