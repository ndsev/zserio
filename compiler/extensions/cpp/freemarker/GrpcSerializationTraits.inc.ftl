<#include "FileHeader.inc.ftl">
<#macro grpc_serialization_traits needsRpcTraits fullName>
    <#if needsRpcTraits>
<@namespace_begin ["grpc"]/>

template <>
class SerializationTraits<${fullName}, void>
{
public:
    static Status Serialize(const ${fullName}& const_msg, ByteBuffer* buffer, bool* own_buffer)
    {
        ${fullName}& msg = const_cast<${fullName}&>(const_msg);

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

    static Status Deserialize(ByteBuffer* buffer, ${fullName}* msg)
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
};
<@namespace_end ["grpc"]/>

    </#if>
</#macro>
