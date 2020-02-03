#ifndef ZSERIO_SERVICE_HTTP_ZSERIO_SERVICE_H
#define ZSERIO_SERVICE_HTTP_ZSERIO_SERVICE_H

#include <memory>
#include "zserio_runtime/IService.h"
#include "zserio/Types.h"

namespace zserio_service_http
{
    class HttpClient : public ::zserio::IService
    {
    public:
        explicit HttpClient(const std::string& url, uint16_t port);
        ~HttpClient();

        void callProcedure(const std::string& procName, const std::vector<uint8_t>& requestData,
                std::vector<uint8_t>& responseData) const override;

    private:
        struct HttpClientImpl;
        std::unique_ptr<HttpClientImpl> m_impl;
    };
} // namespace zserio_service_http

#endif // ZSERIO_SERVICE_HTTP_ZSERIO_SERVICE_H
