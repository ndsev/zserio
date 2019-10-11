#ifndef ZSERIO_BIT_STREAM_H_INC
#define ZSERIO_BIT_STREAM_H_INC

#include <functional>
#include <vector>

#include "zserio/Types.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/PreWriteAction.h"

namespace zserio
{

class BitStream
{
public:
    BitStream();
    BitStream(const uint8_t* buffer, size_t bufferBitSize);

    template <typename ZSERIO_OBJECT>
    BitStream(ZSERIO_OBJECT& object)
    {
        bind(object);
    }

    ~BitStream() = default;

    BitStream(const BitStream&) = default;
    BitStream& operator=(const BitStream&) = default;

    BitStream(BitStream&&) = default;
    BitStream& operator=(BitStream&&) = default;

    void setBuffer(const uint8_t* buffer, size_t bufferBitSize);
    const uint8_t* getBuffer(size_t& bufferBitSize) const;

    template <typename ZSERIO_OBJECT>
    void bind(ZSERIO_OBJECT& object)
    {
        m_bitSizeOfFunc = std::bind(&ZSERIO_OBJECT::bitSizeOf, &object, std::placeholders::_1);
        m_hashCodeFunc = std::bind(&ZSERIO_OBJECT::hashCode, &object);
        m_readFunc = std::bind(&ZSERIO_OBJECT::read, &object, std::placeholders::_1);
        m_writeFunc = std::bind(&ZSERIO_OBJECT::write, &object, std::placeholders::_1, std::placeholders::_2);
    }

    const std::function<size_t(size_t)>& getBitSizeOfFunc() const;
    const std::function<int()>& getHashCodeFunc() const;
    const std::function<void(zserio::BitStreamReader&)>& getReadFunc() const;
    const std::function<void(zserio::BitStreamWriter&, zserio::PreWriteAction)>& getWriteFunc() const;

private:
    std::function<size_t(size_t)> m_bitSizeOfFunc = nullptr;
    std::function<int()> m_hashCodeFunc = nullptr;
    std::function<void(BitStreamReader&)> m_readFunc = nullptr;
    std::function<void(BitStreamWriter&, PreWriteAction)> m_writeFunc = nullptr;

    std::vector<uint8_t> m_buffer;
    size_t m_bufferBitSize = 0;
};

size_t bitSizeOf(const BitStream& bitStream, size_t bitPosition);
void read(BitStream& bitStream, BitStreamReader& in);
void write(const BitStream& bitStream, BitStreamWriter& out);

} // namespace zserio

#endif // ifndef ZSERIO_BIT_STREAM_H_INC
