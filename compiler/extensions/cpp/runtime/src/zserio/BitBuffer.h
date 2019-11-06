#ifndef ZSERIO_BIT_BUFFER_H_INC
#define ZSERIO_BIT_BUFFER_H_INC

#include <vector>
#include <cstddef>

#include "zserio/Types.h"

namespace zserio
{

/* TODO[mikir] */
struct InPlaceType
{
    explicit constexpr InPlaceType(int) {}
};

/* TODO[mikir] */
constexpr InPlaceType InPlace{int()};

/* TODO[mikir] */
class BitBuffer
{
public:
    BitBuffer();
    explicit BitBuffer(size_t bitSize);
    explicit BitBuffer(std::vector<uint8_t>& buffer, size_t lastByteBits = 8);
    explicit BitBuffer(std::vector<uint8_t>&& buffer, size_t lastByteBits = 8);
    explicit BitBuffer(const uint8_t* buffer, size_t bitSize);
    explicit BitBuffer(uint8_t* buffer, size_t bitSize, InPlaceType);

    ~BitBuffer() = default;

    BitBuffer(const BitBuffer&) = default;
    BitBuffer& operator=(const BitBuffer&) = default;

    BitBuffer(BitBuffer&&) = default;
    BitBuffer& operator=(BitBuffer&&) = default;

    bool operator==(const BitBuffer& other) const;

    const uint8_t* get(size_t& bitSize) const;
    uint8_t* get(size_t& bitSize);

    const uint8_t* getBuffer() const;
    uint8_t* getBuffer();

    size_t getBitSize() const;

private:
    uint8_t* m_externalBuffer = nullptr;
    std::vector<uint8_t> m_internalBuffer;
    size_t m_bitSize;
};

} // namespace zserio

#endif // ifndef ZSERIO_BIT_BUFFER_H_INC
