#ifndef ZSERIO_BITS_H_INC
#define ZSERIO_BITS_H_INC

#include <functional>
#include <type_traits>

#include "zserio/BitBuffer.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/PreWriteAction.h"

namespace zserio
{

/* TODO[mikir] */
class Bits
{
public:
    Bits();
    explicit Bits(const BitBuffer& bitBuffer);
    explicit Bits(BitBuffer&& bitBuffer);

    template <typename ZSERIO_OBJECT,
            typename std::enable_if<!std::is_same<typename std::decay<ZSERIO_OBJECT>::type, BitBuffer>::value,
                    int>::type = 0>
    explicit Bits(ZSERIO_OBJECT& object)
    {
        bind(object);
    }

    ~Bits() = default;

    Bits(const Bits&) = default;
    Bits& operator=(const Bits&) = default;

    Bits(Bits&&) = default;
    Bits& operator=(Bits&&) = default;

    bool operator==(const Bits& other) const;

    void setBitBuffer(const BitBuffer& bitBuffer);
    void setBitBuffer(BitBuffer&& bitBuffer);
    const BitBuffer& getBitBuffer() const;

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
    void resetBinding();

    template<typename T, typename... U>
    size_t getFuncAddress(const std::function<T(U...)>& func) const
    {
        typedef T(funcType)(U...);
        funcType* const* funcPointer = func.template target<const funcType*>();

        return reinterpret_cast<size_t>(*funcPointer);
    }

    std::function<size_t(size_t)> m_bitSizeOfFunc = nullptr;
    std::function<int()> m_hashCodeFunc = nullptr;
    std::function<void(BitStreamReader&)> m_readFunc = nullptr;
    std::function<void(BitStreamWriter&, PreWriteAction)> m_writeFunc = nullptr;

    BitBuffer m_bitBuffer;
};

size_t bitSizeOf(const Bits& bits, size_t bitPosition);
void read(Bits& bits, BitStreamReader& in);
void write(const Bits& bits, BitStreamWriter& out);

} // namespace zserio

#endif // ifndef ZSERIO_BITS_H_INC
