#include <curl/curl.h>
#include <string>
#include <vector>
#include <algorithm>
#include <iostream>

#include "ZserioService.h"
#include "zserio/CppRuntimeException.h"

namespace zserio_service_http
{

namespace
{
    struct CurlDeleter
    {
        void operator()(CURL* curl) const
        {
            curl_easy_cleanup(curl);
        }
    };

    struct RequestWrapper
    {
        explicit RequestWrapper(const std::vector<uint8_t>& requestData)
        :   requestData(requestData)
        {}

        size_t read = 0;
        const std::vector<uint8_t>& requestData;
    };
}

struct HttpClient::HttpClientImpl
{
public:
    HttpClientImpl(const std::string& url, uint16_t port)
    :   m_curl(curl_easy_init()), m_url(url + ":" + std::to_string(port))
    {
        curl_easy_setopt(m_curl.get(), CURLOPT_FAILONERROR, 1L);
        curl_easy_setopt(m_curl.get(), CURLOPT_NOPROGRESS, 1L);
        curl_easy_setopt(m_curl.get(), CURLOPT_FOLLOWLOCATION, 1L); // TODO: is needed?
        curl_easy_setopt(m_curl.get(), CURLOPT_READFUNCTION, &HttpClientImpl::readFunction);
        curl_easy_setopt(m_curl.get(), CURLOPT_WRITEFUNCTION, &HttpClientImpl::writeFunction);
        curl_easy_setopt(m_curl.get(), CURLOPT_POST, 1L);
    }

    static size_t writeFunction(void* ptr, size_t size, size_t nmemb, void* responseDataPtr)
    {
        std::vector<uint8_t>& responseData = *static_cast<std::vector<uint8_t>*>(responseDataPtr);
        responseData.clear();

        // CURL doc says that size is alway 1 and total number of received bytes is size * nmemb
        responseData.insert(responseData.end(), static_cast<uint8_t*>(ptr), static_cast<uint8_t*>(ptr) + nmemb);
        return nmemb;
    }

    static size_t readFunction(void* ptr, size_t size, size_t nitems, void* requestWrapperPtr)
    {
        RequestWrapper& requestWrapper = *static_cast<RequestWrapper*>(requestWrapperPtr);
        const std::vector<uint8_t>& requestData = requestWrapper.requestData;
        if (requestWrapper.read >= requestData.size())
            return 0;

        uint8_t* buffer = static_cast<uint8_t*>(ptr);

        const size_t chunkSize = std::min(size*nitems, requestData.size() - requestWrapper.read);
        const uint8_t* data = requestData.data() + requestWrapper.read;
        std::copy(data, data + chunkSize, buffer);

        requestWrapper.read += chunkSize;
        return chunkSize;
    }

    bool request(const std::string& path, const std::vector<uint8_t>& requestData,
            std::vector<uint8_t>& responseData)
    {
        curl_easy_setopt(m_curl.get(), CURLOPT_URL, (m_url + path).c_str());
        RequestWrapper requestWrapper(requestData);
        curl_easy_setopt(m_curl.get(), CURLOPT_READDATA, &requestWrapper);
        curl_easy_setopt(m_curl.get(), CURLOPT_POSTFIELDSIZE, requestData.size());
        curl_easy_setopt(m_curl.get(), CURLOPT_WRITEDATA, &responseData);
        CURLcode code  = curl_easy_perform(m_curl.get());
        if (code != CURLE_OK)
        {
            std::cerr << "HttpClientImpl::request failed: " << curl_easy_strerror(code) << "!" << std::endl;
            return false;
        }

        return true;
    }

private:
    std::unique_ptr<CURL, CurlDeleter> m_curl;
    std::string m_url;
};

HttpClient::HttpClient(const std::string& url, uint16_t port)
:   m_impl(new HttpClientImpl(url, port))
{
}

HttpClient::~HttpClient()
{
}

void HttpClient::callMethod(const std::string& procName, const std::vector<uint8_t>& requestData,
        std::vector<uint8_t>& responseData, void* context)
{
    std::string path = std::string("/") + procName;
    std::replace(path.begin(), path.end(), '.', '/');
    if (!m_impl->request(path, requestData, responseData))
        throw ::zserio::CppRuntimeException("HttpClient::callProcedure failed!");
}

} // namespace zserio_service_http
