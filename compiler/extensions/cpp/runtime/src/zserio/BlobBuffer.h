#ifndef ZSERIO_BLOB_BUFFER_H_INC
#define ZSERIO_BLOB_BUFFER_H_INC

#include "zserio/Span.h"
#include "zserio/Types.h"
#include <vector>

namespace zserio
{

/**
 * Interface to generic data blob.
 */
class IBlobBuffer
{
public:
    virtual ~IBlobBuffer() = default;

    /**
     * Resize the blob.
     *
     * \param size New size of the blob. Returned blob is guaranteed to have at least this number of bytes.
     */
    virtual void resize(std::size_t size) = 0;

    /**
     * Get the blob data span.
     *
     * \return Span representing the blob data. The returned blob is valid until the instance of the IBlob is
     * destroyed or until the resize method is called.
     */
    virtual Span<uint8_t> data() = 0;
};

/**
 * Implementation of IBlobBuffer interface.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BlobBuffer : public IBlobBuffer
{
public:
    using allocator_type = ALLOC;

    /**
     * Getter for used allocator.
     *
     * \return Allocator used for blob allocation.
     */
    allocator_type get_allocator() const
    {
        return m_data.get_allocator();
    }

    /**
     * Constructor.
     *
     * \param allocator Allocator to be used for dynamic memory allocation.
     */
    BlobBuffer(const allocator_type& allocator = allocator_type()) :
        m_data(allocator)
    {}

    /**
     * Resize the blob.
     *
     * \param size New size of the blob. Returned blob is guaranteed to have at least this number of bytes.
     */
    virtual void resize(std::size_t size) override
    {
        m_data.resize(size);
    }

    /**
     * Get the blob data span.
     *
     * \return Span representing the blob data. The returned blob is valid until the instance of the Blob is
     * destroyed or until the resize method is called.
     */
    virtual Span<uint8_t> data() override
    {
        return m_data;
    }

private:
    std::vector<uint8_t, ALLOC> m_data;
};

} // namespace zserio

#endif // ZSERIO_BLOB_BUFFER_H_INC
