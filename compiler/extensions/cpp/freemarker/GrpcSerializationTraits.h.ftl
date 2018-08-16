<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "GrpcSerializationTraits"/>

#include <grpcpp/impl/codegen/status.h>
#include <grpcpp/impl/codegen/byte_buffer.h>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>

<@user_includes headerUserIncludes, true/>
<@namespace_begin rootPackage.path/>

template <class T>
::grpc::Status SerializeGrpcMessage(const T& msg, ::grpc::ByteBuffer* buffer, bool* own_buffer)
{
    zserio::BitStreamWriter writer;
    auto& m = const_cast<T&>(msg);
    m.write(writer);

    size_t size;
    auto *writeBuffer = writer.getWriteBuffer(size);
    ::grpc::Slice slice(writeBuffer, size);
    *buffer = ::grpc::ByteBuffer(&slice, 1);
    *own_buffer = true;
    return ::grpc::Status::OK;
}

template <class T>
::grpc::Status DeserializeGrpcMessage(::grpc::ByteBuffer* buffer, T* msg)
{
    std::vector<::grpc::Slice> slices;
    buffer->Dump(&slices);

    size_t size = 0;
    for (auto &slice : slices)
        size += slice.size();

    uint8_t *tmp = new uint8_t[size];
    if (!tmp)
        return ::grpc::Status(::grpc::StatusCode::DATA_LOSS, "Unable to allocate memory");

    auto pos = tmp;
    for (auto &slice : slices)
    {
        memcpy(pos, slice.begin(), slice.size());
        pos += slice.size();
    }

    zserio::BitStreamReader reader(tmp, size);
    msg->read(reader);
    delete [] tmp;

    return ::grpc::Status::OK;
}

<@namespace_end rootPackage.path/>

namespace grpc
{
<#list rpcTypeNames as rpcTypeName>

template <>
class SerializationTraits<${rpcTypeName}, void>
{
public:
    static Status Serialize(const ${rpcTypeName}& msg, ByteBuffer* buffer, bool* own_buffer)
    {
        return <#if rootPackage.name?has_content>${rootPackage.name}</#if><#rt>
                <#lt>::SerializeGrpcMessage(msg, buffer, own_buffer);
    }

    static Status Deserialize(ByteBuffer* buffer, ${rpcTypeName}* msg)
    {
        return <#if rootPackage.name?has_content>${rootPackage.name}</#if><#rt>
                <#lt>::DeserializeGrpcMessage(buffer, msg);
    }
};
</#list>

} // namespace grpc

<@include_guard_end rootPackage.path, "GrpcSerializationTraits"/>
