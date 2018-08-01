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

static const char* ${package.name}_method_names[] = {
  "/${package.name}.${package.name}/${name}",
};

std::unique_ptr< ${name}::Stub> ${name}::NewStub(const std::shared_ptr< ::grpc::ChannelInterface>& channel, const ::grpc::StubOptions& options) {
  (void)options;
  std::unique_ptr< ${name}::Stub> stub(new ${name}::Stub(channel));
  return stub;
}

${name}::Stub::Stub(const std::shared_ptr< ::grpc::ChannelInterface>& channel)
  : channel_(channel), rpcmethod_${name}_(${package.name}_method_names[0], ::grpc::internal::RpcMethod::NORMAL_RPC, channel)
  {}

::grpc::Status ${name}::Stub::${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ${responseTypeFullName}* response) {
  return ::grpc::internal::BlockingUnaryCall(channel_.get(), rpcmethod_${name}_, context, request, response);
}

::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>* ${name}::Stub::Async${name}Raw(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
  return ::grpc::internal::ClientAsyncResponseReaderFactory< ${responseTypeFullName}>::Create(channel_.get(), cq, rpcmethod_${name}_, context, request, true);
}

::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>* ${name}::Stub::PrepareAsync${name}Raw(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
  return ::grpc::internal::ClientAsyncResponseReaderFactory< ${responseTypeFullName}>::Create(channel_.get(), cq, rpcmethod_${name}_, context, request, false);
}

${name}::Service::Service() {
  AddMethod(new ::grpc::internal::RpcServiceMethod(
      ${package.name}_method_names[0],
      ::grpc::internal::RpcMethod::NORMAL_RPC,
      new ::grpc::internal::RpcMethodHandler< ${name}::Service, ${requestTypeFullName}, ${responseTypeFullName}>(
          std::mem_fn(&${name}::Service::${name}), this)));
}

${name}::Service::~Service() {
}

::grpc::Status ${name}::Service::${name}(::grpc::ServerContext* context, const ${requestTypeFullName}* request, ${responseTypeFullName} * response) {
  (void) context;
  (void) request;
  (void) response;
  return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}

<@namespace_end package.path/>

namespace grpc
{

template<>
Status SerializationTraits<${requestTypeFullName}>::Serialize(
        const ${requestTypeFullName}& msg,
        ByteBuffer* bp,
        bool* own_buffer)
{
    zserio::BitStreamWriter writer;
    auto& m = const_cast<${requestTypeFullName}&>(msg);
    m.write(writer);

    size_t size;
    auto *buffer = writer.getWriteBuffer(size);
    Slice slice(buffer, size);
    *bp = ByteBuffer(&slice, 1);
    *own_buffer = true;
    return grpc::Status::OK;
}

template<>
Status SerializationTraits<${requestTypeFullName}>::Deserialize(
        ByteBuffer* buffer,
        ${requestTypeFullName}* msg)
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

template<>
Status SerializationTraits<${responseTypeFullName}>::Serialize(
        const ${responseTypeFullName}& msg,
        ByteBuffer* bp,
        bool* own_buffer)
{
    zserio::BitStreamWriter writer;
    auto& m = const_cast<${responseTypeFullName}&>(msg);
    m.write(writer);

    size_t size;
    auto *buffer = writer.getWriteBuffer(size);
    Slice slice(buffer, size);
    *bp = ByteBuffer(&slice, 1);
    *own_buffer = true;
    return grpc::Status::OK;
}

template<>
Status SerializationTraits<${responseTypeFullName}>::Deserialize(
        ByteBuffer* buffer,
        ${responseTypeFullName}* msg)
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

} // namespace grpc
