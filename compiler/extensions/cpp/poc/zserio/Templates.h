#ifndef ZSERIO_TEMPLATES_H
#define ZSERIO_TEMPLATES_H

#include <type_traits>
#include <string>

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/VarUInt64Util.h"
#include "zserio/BitStreamException.h"
#include "zserio/PreWriteAction.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/Enums.h"
#include "zserio/Arrays.h"

namespace zserio
{

struct StringWrapper
{
    explicit StringWrapper(BitStreamReader& in) :
            m_value(in.readString())
    {
    }

    StringWrapper(const std::string& value) : // TODO: move ctor
            m_value(value)
    {
    }

    size_t bitSizeOf(size_t bitPosition)
    {
        return bitPosition + zserio::bitSizeOfString(m_value);
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + zserio::bitSizeOfString(m_value);
    }

    void read(BitStreamReader& in)
    {
        m_value = in.readString();
    }

    void write(BitStreamWriter& out, zserio::PreWriteAction)
    {
        out.writeString(m_value);
    }

    operator std::string&()
    {
        return m_value;
    }

    operator const std::string&() const
    {
        return m_value;
    }

    std::string m_value;
};

struct UInt32Wrapper
{
    typedef uint32 type;
    typedef StdIntArrayTraits<uint32_t> array_traits;

    explicit UInt32Wrapper(BitStreamReader& in) :
            m_value(in.readBits(32))
    {}

    UInt32Wrapper(uint32_t value) :
            m_value(value)
    {}

    size_t bitSizeOf(size_t bitPosition)
    {
        return 32;
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + 32;
    }

    void read(BitStreamReader& in)
    {
        m_value = in.readBits(32);
    }

    void write(BitStreamWriter& out, zserio::PreWriteAction)
    {
        out.writeBits(m_value, 32);
    }

    operator const uint32_t&() const
    {
        return m_value;
    }

    operator uint32_t&()
    {
        return m_value;
    }

    uint32_t m_value;
};

} // namespace zserio

#endif // ZSERIO_TEMPLATES_H
