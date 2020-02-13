#ifndef ZSERIO_ISERVICE_H_INC
#define ZSERIO_ISERVICE_H_INC

#include <string>
#include <vector>
#include "zserio/Types.h"

namespace zserio
{

/** Generic interface for all Zserio services. */
class IService
{
public:
    virtual ~IService() = default;

    /**
     * Calls method with the given name synchronously.
     *
     * \param methodName Name of the service method to call.
     * \param requestData Request data to be passed to the method.
     * \param responseData Response data to fill.
     * \param context Context specific for particular service.
     *
     * \throw ServiceException if the call fails.
     */
    virtual void callMethod(const std::string& methodName, const std::vector<uint8_t>& requestData,
            std::vector<uint8_t>& responseData, void* context = nullptr) = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_ISERVICE_H_INC
