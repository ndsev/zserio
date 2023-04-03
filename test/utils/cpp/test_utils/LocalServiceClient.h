#ifndef TEST_UTILS_LOCAL_SERVICE_CLIENT_H_INC
#define TEST_UTILS_LOCAL_SERVICE_CLIENT_H_INC

#include "zserio/IService.h"
#include "zserio/AllocatorHolder.h"

namespace test_utils
{

template <typename ALLOC>
class LocalServiceClient : public zserio::IBasicServiceClient<ALLOC>, public zserio::AllocatorHolder<ALLOC>
{
protected:
    using zserio::AllocatorHolder<ALLOC>::get_allocator_ref;

public:
    explicit LocalServiceClient(zserio::IBasicService<ALLOC>& service, const ALLOC& allocator = ALLOC()) :
            zserio::AllocatorHolder<ALLOC>(allocator), m_service(service)
    {}

    std::vector<uint8_t, ALLOC> callMethod(zserio::StringView methodName,
            const zserio::IBasicServiceData<ALLOC>& requestData,
            void* context = nullptr) override
    {
        return callMethodOnService(methodName, requestData.getData(), context);
    }

private:
    virtual std::vector<uint8_t, ALLOC> callMethodOnService(zserio::StringView methodName,
            zserio::Span<const uint8_t> requestBytes, void* context)
    {
        auto responseData = m_service.callMethod(methodName, requestBytes, context);
        zserio::Span<const uint8_t> responseBytes = responseData->getData();
        return std::vector<uint8_t, ALLOC>{responseBytes.begin(), responseBytes.end(), get_allocator_ref()};
    }

    zserio::IBasicService<ALLOC>& m_service;
};

} // namespace test_utils

#endif // TEST_UTILS_LOCAL_SERVICE_CLIENT_H_INC
