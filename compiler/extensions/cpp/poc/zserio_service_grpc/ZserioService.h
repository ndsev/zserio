#ifndef ZSERIO_SERVICE_GRPC_ZSERIO_SERVICE_H
#define ZSERIO_SERVICE_GRPC_ZSERIO_SERVICE_H

#include <memory>
#include <grpcpp/grpcpp.h>

#include "zserio_service_grpc/zserio_service.pb.h"
#include "zserio_service_grpc/zserio_service.grpc.pb.h"
#include "zserio_runtime/IService.h"

namespace zserio_service_grpc
{
    class GrpcService : public ::zserio_service_grpc::ZserioService::Service
    {
    public:
        explicit GrpcService(::zserio::IService& service);

        ::grpc::Status callProcedure(::grpc::ServerContext*, const ::zserio_service_grpc::Request* request,
                ::zserio_service_grpc::Response* response) override;

    private:
        ::zserio::IService& m_service;
    };

    class GrpcClient : public ::zserio::IService
    {
    public:
        explicit GrpcClient(const std::shared_ptr<::grpc::Channel>& channel);

        void callMethod(const std::string& procName, const std::vector<uint8_t>& requestData,
                std::vector<uint8_t>& responseData, void* context = nullptr) override;

    private:
        void callMethodWithContext(const std::string& methodName, const std::vector<uint8_t>& requestData,
                std::vector<uint8_t>& responseData, ::grpc::ClientContext* context);

        std::unique_ptr<::zserio_service_grpc::ZserioService::Stub> m_stub;
    };
} // namespace zserio_service_grpc

#endif // ZSERIO_SERVICE_GRPC_ZSERIO_SERVICE_H
