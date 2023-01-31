#ifndef ZSERIO_CPP_BIT_STREAM_WRITER_H_INC
#define ZSERIO_CPP_BIT_STREAM_WRITER_H_INC

#include "ZserioCpp.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/FileUtil.h"

namespace zserio_cpp
{

/**
 * Wrapper around Zserio C++ bit stream writer which increases buffer size in case that it is not sufficient.
 */
class BitStreamWriter
{
private:
    static constexpr size_t DEFAULT_BUFFER_SIZE = 64 * 1024; // 64kB
    static constexpr size_t RESIZE_FACTOR = 2;

public:
    /**
     * Constructor.
     */
    BitStreamWriter() :
            m_buffer(DEFAULT_BUFFER_SIZE),
            m_writer(std::make_unique<zserio::BitStreamWriter>(m_buffer))
    {}

    /**
     * Copying and moving is disallowed!
     * \{
     */
    BitStreamWriter(const BitStreamWriter& other) = delete;
    BitStreamWriter& operator=(const BitStreamWriter& other) = delete;

    BitStreamWriter(BitStreamWriter&& other) = delete;
    BitStreamWriter& operator=(BitStreamWriter&& other) = delete;
    /**
     * \}
     */

    /**
     * Generic write methods which increases size of internal buffer when it is needed.
     */
    template <typename WriteMethod, typename ...Args>
    void write(WriteMethod writeMethod, Args... arguments)
    {
        const size_t bitPosition = m_writer->getBitPosition();
        try
        {
            (*m_writer.*writeMethod)(std::forward<Args>(arguments)...);
        }
        catch (const zserio::BitStreamWriter::InsufficientCapacityException&)
        {
            m_buffer.resize(m_buffer.size() * RESIZE_FACTOR);
            m_writer = std::make_unique<zserio::BitStreamWriter>(m_buffer);
            m_writer->setBitPosition(bitPosition);
            write(writeMethod, std::forward<Args>(arguments)...);
        }
    }

    /**
     * Gets pointer to the internal byte buffer.
     *
     * \return Pointer to the beginning of the internal byte buffer.
     */
    uint8_t* getWriteBuffer()
    {
        return m_buffer.data();
    }

    /**
     * Gets byte size of the internal byte buffer.
     *
     * \return Byte size.
     */
    size_t getBufferByteSize() const
    {
        return (getBitPosition() + 7) / 8;
    }

    /**
     * Gets current bit position.
     *
     * \return Current bit position.
     */
    size_t getBitPosition() const
    {
        return m_writer->getBitPosition();
    }

    /**
     * Writes the internal buffer to file.
     *
     * \param fileName File name to use.
     */
    void toFile(const std::string& fileName) const
    {
        zserio::writeBufferToFile(m_buffer.data(), getBitPosition(), zserio::BitsTag(), fileName);
    }

private:
    std::vector<uint8_t> m_buffer;
    std::unique_ptr<zserio::BitStreamWriter> m_writer;
};

/**
 * Binds C++ bit stream writer to python runtime.
 *
 * \param module Module where to place the binding.
 */
void pybindBitStreamWriter(py::module_ module);

} // namespace zserio_cpp

#endif // ZSERIO_CPP_BIT_STREAM_WRITER_H_INC
