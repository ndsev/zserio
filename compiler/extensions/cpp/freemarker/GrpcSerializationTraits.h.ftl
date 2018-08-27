<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "GrpcSerializationTraits"/>

#include <vector>

#include <grpcpp/impl/codegen/status.h>
#include <grpcpp/impl/codegen/byte_buffer.h>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>

<@user_includes headerUserIncludes, true/>
<@namespace_begin rootPackage.path/>

template <class T>
::grpc::Status SerializeGrpcMessage(const T& const_msg, ::grpc::ByteBuffer* buffer, bool* own_buffer)
{
    T& msg = const_cast<T&>(const_msg);

    const size_t msgBitSize = msg.bitSizeOf();
    size_t msgByteSize = msgBitSize / 8 + (msgBitSize % 8 != 0 ? 1 : 0);

    ::grpc::Slice slice(msgByteSize); // allocates memory
    uint8_t* writeBuffer = const_cast<uint8_t*>(slice.begin());

    zserio::BitStreamWriter writer(writeBuffer, msgByteSize);
    msg.write(writer);

    *buffer = ::grpc::ByteBuffer(&slice, 1);
    *own_buffer = true; // the caller owns the buffer - i.e. doesn't need to copy data again

    return ::grpc::Status::OK;
}

template <class T>
::grpc::Status DeserializeGrpcMessage(::grpc::ByteBuffer* buffer, T* msg)
{
    std::vector<::grpc::Slice> slices;
    buffer->Dump(&slices);

    if (slices.size() == 1)
    {
        // optimization without need to copy data
        const ::grpc::Slice& slice = slices.at(0);
        zserio::BitStreamReader reader(slice.begin(), slice.size());

        msg->read(reader);

        return ::grpc::Status::OK;
    }

    std::vector<uint8_t> joinedBuffer;
    for (std::vector<::grpc::Slice>::const_iterator it = slices.begin(); it != slices.end(); ++it)
        joinedBuffer.insert(joinedBuffer.end(), it->begin(), it->end());

    zserio::BitStreamReader reader(&joinedBuffer[0], joinedBuffer.size());

    msg->read(reader);

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
