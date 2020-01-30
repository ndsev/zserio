#include "ZserioService.h"

namespace zserio_service_grpc
{
    GrpcService::GrpcService(const ::zserio::IService& service)
    :   m_service(service)
    {
    }

    ::grpc::Status GrpcService::callProcedure(::grpc::ServerContext*,
            const ::zserio_service_grpc::Request* request, ::zserio_service_grpc::Response* response)
    {
        const std::string& requestData = request->requestdata();
        std::vector<uint8_t> responseData;
        m_service.callProcedure(request->procname(), {requestData.begin(), requestData.end()}, responseData);
        response->set_responsedata({responseData.begin(), responseData.end()});
        return ::grpc::Status::OK;
    }

    GrpcClient::GrpcClient(const std::shared_ptr<::grpc::Channel>& channel)
    :   m_stub(::zserio_service_grpc::ZserioService::NewStub(channel))
    {
    }

    void GrpcClient::callProcedure(const std::string& procName, const std::vector<uint8_t>& requestData,
                std::vector<uint8_t>& responseData) const
    {
        ::zserio_service_grpc::Request request;
        request.set_procname(procName);
        request.set_requestdata({requestData.begin(), requestData.end()});
        ::zserio_service_grpc::Response response;
        ::grpc::ClientContext context;
        ::grpc::Status status = m_stub->callProcedure(&context, request, &response);

        // TODO: check status
        if (status.ok())
        {
            const std::string& grpcResponseData = response.responsedata();
            responseData.assign(grpcResponseData.begin(), grpcResponseData.end());
        }
    }
} // namespace zserio_service_grpc
