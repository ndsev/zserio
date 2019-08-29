#ifndef ZSERIO_BIT_STREAM_READER_H_INC
#define ZSERIO_BIT_STREAM_READER_H_INC

#include <cstddef>
#include <string>

#include "Types.h"

namespace zserio
{

class BitStreamReader
{
public:
    typedef size_t BitPosType;

    struct ReaderContext
    {
        ReaderContext(const uint8_t* buffer, size_t bufferByteSize);
        explicit ReaderContext(const std::string& filename);
        ~ReaderContext();

        /** Cache buffer union to cover both 32bit and 64bit implementations. */
        union BitCache
        {
            uint32_t buffer32; /**< Cache buffer used on 32bit platforms. */
            uint64_t buffer64; /**< Cache buffer used on 64bit platforms. */
        };

        uint8_t* buffer;
        BitPosType bufferBitSize;
        bool hasInternalBuffer;

        BitCache cache;
        uint8_t cacheNumBits;

        BitPosType bitIndex;

    private:
        // disable copy constructor and assignment operator
        ReaderContext(const ReaderContext& other);
        ReaderContext& operator=(const ReaderContext& other);

        void Init();
    };

    BitStreamReader(const uint8_t* buffer, size_t bufferByteSize);
    explicit BitStreamReader(const std::string& filename);
    ~BitStreamReader();

    uint32_t readBits(uint8_t numBits = 32);
    uint64_t readBits64(uint8_t numBits = 64);

    int64_t readSignedBits64(uint8_t numBits = 64);
    int32_t readSignedBits(uint8_t numBits = 32);

    int64_t readVarInt64();
    int32_t readVarInt32();
    int16_t readVarInt16();

    uint64_t readVarUInt64();
    uint32_t readVarUInt32();
    uint16_t readVarUInt16();

    int64_t readVarInt();
    uint64_t readVarUInt();

    float readFloat16();
    float readFloat32();
    double readFloat64();

    std::string readString();
    bool readBool();

    BitPosType getBitPosition() const { return m_context.bitIndex; }
    void setBitPosition(BitPosType pos);

    void alignTo(size_t alignment);

private:
    ReaderContext m_context;
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_READER_H_INC
