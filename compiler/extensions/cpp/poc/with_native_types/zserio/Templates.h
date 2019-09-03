#ifndef ZSERIO_TEMPLATES_H
#define ZSERIO_TEMPLATES_H

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitPositionUtil.h"
#include "zserio/VarUInt64Util.h"
#include "zserio/BitStreamException.h"
#include "zserio/PreWriteAction.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/Enums.h"

namespace zserio
{

template <typename T>
struct NativeWrapper
{
    typedef T type;

    NativeWrapper() :
            m_value()
    {
    }

    NativeWrapper(type value) :
            m_value(value)
    {
    }

    NativeWrapper(BitStreamReader& in)
    {
        // TODO:
    }

    NativeWrapper(const NativeWrapper& other) :
            m_value(other.m_value)
    {
    }

    NativeWrapper& operator=(const NativeWrapper& other)
    {
        m_value = other.m_value;
        return *this;
    }

    operator type&()
    {
        return m_value;
    }

    operator const type&() const
    {
        return m_value;
    }

    bool operator==(const NativeWrapper& other)
    {
        return false; // TODO:
    }

    size_t bitSizeOf(size_t) const
    {
        return 0; // TODO:
    }

    size_t initializeOffsets(size_t bitPosition)
    {
        return 0; // TODO:
    }

    void write(BitStreamWriter& out)
    {
        // TODO:
    }

private:
    type m_value;
};

/**
 * Array traits of Zserio structure, choice and union types.
 */
template <typename T, typename ELEMENT_FACTORY = void>
class TemplateObjectArrayTraits
{
public:
    /**
     * Constructor.
     *
     * \param elementFactory Factory which knows how to create a single array element.
     */
    explicit TemplateObjectArrayTraits(const ELEMENT_FACTORY& elementFactory) : m_elementFactory(elementFactory)
    {
    }

    /** Type of the single array element. */
    typedef typename T::type type;

    /**
     * Reads the single array element.
     *
     * \param array Array to read the element to.
     * \param in Bit stream reader.
     * \param index Index need in case of parameterized type which depends on the current index.
     */
    void read(std::vector<type>& array, BitStreamReader& in, size_t index) const
    {
        m_elementFactory.create(array, in, index);
    }

private:
    const ELEMENT_FACTORY& m_elementFactory;
};

/**
 * Array traits for Zserio structure, choice and union types.
 */
template <typename T>
class TemplateObjectArrayTraits<T>
{
public:
    /** Type of the single array element. */
    typedef typename T::type type;

    /**
     * Calculates bit size of the array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Bit size of the array element.
     */
    static size_t bitSizeOf(size_t bitPosition, const type& value)
    {
        return value.bitSizeOf(bitPosition);
    }

    /**
     * Initializes indexed offsets of the single array element.
     *
     * \param bitPosition Current bit position.
     * \param value Element's value.
     *
     * \return Updated bit position which points to the first bit after the array element.
     */
    static size_t initializeOffsets(size_t bitPosition, type& value)
    {
        return value.initializeOffsets(bitPosition);
    }

    /**
     * Writes the single array element.
     *
     * \param out Bit stream writer.
     * \param value Element's value to write.
     */
    static void write(BitStreamWriter& out, type& value)
    {
        value.write(out, NO_PRE_WRITE_ACTION);
    }

    /** Determines whether the bit size of the single element is constant. */
    static const bool IS_BITSIZEOF_CONSTANT = false;
};

} // namespace zserio

#endif // ZSERIO_TEMPLATES_H
