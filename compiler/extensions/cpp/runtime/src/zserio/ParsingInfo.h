#ifndef ZSERIO_PARSING_INFO_INC_H
#define ZSERIO_PARSING_INFO_INC_H

#include <cstddef>
#include <limits>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/**
 * The parsing information returned from parsingInfo() method generated in case of '-withParsingInfo' command
 * line option.
 */
class ParsingInfo
{
public:
    /**
     * Default constructor.
     */
    ParsingInfo() :
            m_bitPosition(INVALID_BIT_POSITION),
            m_bitSize(INVALID_BIT_SIZE)
    {}

    /**
     * Constructor from bit position.
     *
     * \param bitPosition Bit position in bits.
     */
    explicit ParsingInfo(size_t bitPosition) :
            m_bitPosition(bitPosition),
            m_bitSize(INVALID_BIT_SIZE)
    {}

    /**
     * Initializes the bit size using the end bit position.
     *
     * It is supposed that bit position has been already correctly set before this method is called.
     *
     * \param endBitPosition End bit position to calculate the bit size from.
     *
     * \throw CppRuntimeException In case of invalid end bit position argument.
     */
    void initializeBitSize(size_t endBitPosition)
    {
        const size_t bitPosition = getBitPosition();
        if (endBitPosition < bitPosition)
        {
            throw CppRuntimeException("Invalid end bit position argument (")
                    << endBitPosition << " < " << bitPosition << ")!";
        }

        m_bitSize = endBitPosition - getBitPosition();
    }

    /**
     * Gets the bit position.
     *
     * \return The blob offset of the object in bits counted from zero.
     *
     * \throw CppRuntimeException In case of invalid (uninitialized) bit position.
     */
    size_t getBitPosition() const
    {
        if (m_bitPosition == INVALID_BIT_POSITION)
        {
            throw CppRuntimeException("Invalid bit position, call the reader constructor first!");
        }

        return m_bitPosition;
    }

    /**
     * Gets the bit size.
     *
     * \return Blob size of the object in bits.
     *
     * \throw CppRuntimeException In case of invalid (uninitialized) bit size.
     */
    size_t getBitSize() const
    {
        if (m_bitSize == INVALID_BIT_SIZE)
        {
            throw CppRuntimeException("Invalid bit size, call the reader constructor first!");
        }

        return m_bitSize;
    }

private:
    static constexpr size_t INVALID_BIT_POSITION = std::numeric_limits<size_t>::max();
    static constexpr size_t INVALID_BIT_SIZE = std::numeric_limits<size_t>::max();

    size_t m_bitPosition;
    size_t m_bitSize;
};

} // namespace zserio

#endif // ZSERIO_PARSING_INFO_INC_H
