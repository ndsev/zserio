<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include "<@include_path package.path, "${name}.h"/>"

#include <zserio/BitStreamWriter.h>
#include <zserio/BitStreamReader.h>

<@user_includes cppUserIncludes, false/>

#include <grpcpp/impl/codegen/async_stream.h>
#include <grpcpp/impl/codegen/async_unary_call.h>
#include <grpcpp/impl/codegen/channel_interface.h>
#include <grpcpp/impl/codegen/client_unary_call.h>
#include <grpcpp/impl/codegen/method_handler_impl.h>
#include <grpcpp/impl/codegen/rpc_service_method.h>
#include <grpcpp/impl/codegen/service_type.h>
#include <grpcpp/impl/codegen/sync_stream.h>

<@namespace_begin package.path/>

std::unique_ptr< ${name}::Stub> ${name}::NewStub(const std::shared_ptr< ::grpc::ChannelInterface>& channel, const ::grpc::StubOptions& options) {
  (void)options;
  std::unique_ptr< ${name}::Stub> stub(new ${name}::Stub(channel));
  return stub;
}

<#list rpcList as rpc>
static const char* ${package.name}_${name}_${rpc.name}_method = "/${package.name}/${name}/${rpc.name}";
</#list>

${name}::Stub::Stub(const std::shared_ptr< ::grpc::ChannelInterface>& channel)
  : channel_(channel)
<#list rpcList as rpc>
  , rpcmethod_${name}_${rpc.name}(${package.name}_${name}_${rpc.name}_method, ::grpc::internal::RpcMethod::NORMAL_RPC, channel)
</#list>
  {}

<#list rpcList as rpc>
::grpc::Status ${name}::Stub::${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) {
  return ::grpc::internal::BlockingUnaryCall(channel_.get(), rpcmethod_${name}_${rpc.name}, context, request, response);
}

::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>* ${name}::Stub::Async${rpc.name}Raw(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
  return ::grpc::internal::ClientAsyncResponseReaderFactory< ${rpc.responseTypeFullName}>::Create(channel_.get(), cq, rpcmethod_${name}_${rpc.name}, context, request, true);
}

::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>* ${name}::Stub::PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
  return ::grpc::internal::ClientAsyncResponseReaderFactory< ${rpc.responseTypeFullName}>::Create(channel_.get(), cq, rpcmethod_${name}_${rpc.name}, context, request, false);
}

</#list>

${name}::Service::Service() {
<#list rpcList as rpc>
  AddMethod(new ::grpc::internal::RpcServiceMethod(
      ${package.name}_${name}_${rpc.name}_method,
      ::grpc::internal::RpcMethod::NORMAL_RPC,
      new ::grpc::internal::RpcMethodHandler< ${name}::Service, ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>(
          std::mem_fn(&${name}::Service::${rpc.name}), this)));
</#list>
}

${name}::Service::~Service() {
}

<#list rpcList as rpc>
::grpc::Status ${name}::Service::${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName} * response) {
  (void) context;
  (void) request;
  (void) response;
  return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}
</#list>

<@namespace_end package.path/>

namespace grpc
{
<#list parameterTypeNames as paramName>

template<>
Status SerializationTraits<${paramName}>::Serialize(
        const ${paramName}& msg,
        ByteBuffer* bp,
        bool* own_buffer)
{
    zserio::BitStreamWriter writer;
    auto& m = const_cast<${paramName}&>(msg);
    m.write(writer);

    size_t size;
    auto *buffer = writer.getWriteBuffer(size);
    Slice slice(buffer, size);
    *bp = ByteBuffer(&slice, 1);
    *own_buffer = true;
    return grpc::Status::OK;
}

template<>
Status SerializationTraits<${paramName}>::Deserialize(
        ByteBuffer* buffer,
        ${paramName}* msg)
{
    std::vector<grpc::Slice> slices;
    buffer->Dump(&slices);

    size_t size = 0;
    for (auto &slice : slices)
        size += slice.size();

    uint8_t *tmp = new uint8_t[size];
    if (!tmp)
        return grpc::Status(grpc::StatusCode::DATA_LOSS, "Unable to allocate memory");

    auto pos = tmp;
    for (auto &slice : slices)
    {
        memcpy(pos, slice.begin(), slice.size());
        pos += slice.size();
    }

    zserio::BitStreamReader reader(tmp, size);
    msg->read(reader);
    delete [] tmp;

    return grpc::Status::OK;
}
</#list>

} // namespace grpc
