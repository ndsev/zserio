#ifndef ZSERIO_SPAN_H_INC
#define ZSERIO_SPAN_H_INC

#include <array>
#include <cstddef>
#include <limits>
#include <iterator>
#include <type_traits>
#include <vector>

namespace zserio
{

/**
 * Constant used to differentiate between spans of dynamic and static extent.
 */
constexpr std::size_t dynamic_extent = std::numeric_limits<std::size_t>::max();

namespace detail
{

template <typename T, std::size_t Extent>
struct SpanStorage
{
    SpanStorage() = default;

    SpanStorage(T* data, std::size_t) :
        m_data(data)
    {}

    T* m_data = nullptr;
    static constexpr std::size_t m_size = Extent;
};

template <typename T>
struct SpanStorage<T, dynamic_extent>
{
    SpanStorage() = default;

    SpanStorage(T* data, std::size_t size) :
        m_data(data), m_size(size)
    {}

    T* m_data = nullptr;
    std::size_t m_size = 0;
};

} // namespace detail

/**
 * Class that holds non-owning reference (aka. "view") to continuous sequence of objects.
 * The user of this class is responsible of making sure, that the referenced sequence is valid
 * as long as the instance of the Span is alive.
 * Inspired by C++20 std::span.
 */
template <typename T, std::size_t Extent = dynamic_extent>
class Span
{
public:
    using element_type = T;
    using value_type = typename std::remove_cv<T>::type;
    using size_type = std::size_t;
    using difference_type = std::ptrdiff_t;
    using pointer = T*;
    using const_pointer = const T*;
    using reference = T&;
    using iterator = pointer;
    using const_iterator = const_pointer;
    using reverse_iterator = std::reverse_iterator<iterator>;
    using const_reverse_iterator = std::reverse_iterator<const_iterator>;

    static constexpr size_type extent = Extent;

    /**
     * Constructor. Initializes empty Span.
     */
    template <size_type ext = Extent,
        typename std::enable_if<(ext == 0 || ext == dynamic_extent), int>::type = 0>
    constexpr Span() noexcept
    {}

    /**
     * Constructor. Initializes Span holding a reference starting at given pointer, having
     * given number of elements.
     *
     * \param first Pointer to first element in the sequence.
     * \param count Number of elements.
     */
    constexpr Span(pointer first, size_type count) :
        m_storage(first, count)
    {}

    /**
     * Constructor. Initializes Span holding a reference starting at given pointer, ending
     * at another pointer.
     *
     * \param first Pointer to first element in the sequence.
     * \param last Pointer to one-after-last element in the sequence.
     */
    constexpr Span(pointer first, pointer last) :
        m_storage(first, static_cast<size_t>(last - first))
    {}

    /**
     * Constructor. Initializes Span holding a reference to array.
     *
     * \param arr Array to which the Span will hold a reference.
     */
    template <size_type N, size_type ext = Extent,
        typename std::enable_if<(ext == dynamic_extent || ext == N), int>::type = 0>
    constexpr Span(element_type(&arr)[N]) noexcept :
        m_storage(arr, N)
    {}

    /**
     * Constructor. Initializes Span holding a reference to std::array.
     *
     * \param arr std::array to which the Span will hold a reference.
     */
    template <typename U, size_type N, size_type ext = Extent,
        typename std::enable_if<(ext == dynamic_extent || ext == N) &&
                std::is_convertible<U(*)[], T(*)[]>::value, int>::type = 0>
    constexpr Span(std::array<U, N>& arr) noexcept :
        m_storage(arr.data(), arr.size())
    {}

    /**
     * Constructor. Initializes Span holding a reference to std::array.
     *
     * \param arr std::array to which the Span will hold a reference.
     */
    template <typename U, size_type N, size_type ext = Extent,
        typename std::enable_if<(ext == dynamic_extent || ext == N) &&
                std::is_convertible<const U(*)[], T(*)[]>::value, int>::type = 0>
    constexpr Span(const std::array<U, N>& arr) noexcept :
        m_storage(arr.data(), arr.size())
    {}

    /**
     * Constructor. Initializes Span holding a reference to std::vector.
     *
     * \param vec std::vector to which the Span will hold a reference.
     */
    template <typename U, typename ALLOC, size_type ext = Extent,
        typename std::enable_if<(ext == dynamic_extent) &&
                std::is_convertible<U(*)[], T(*)[]>::value, int>::type = 0>
    constexpr Span(std::vector<U, ALLOC>& vec) :
        m_storage(vec.data(), vec.size())
    {}

    /**
     * Constructor. Initializes Span holding a reference to std::vector.
     *
     * \param vec std::vector to which the Span will hold a reference.
     */
    template <typename U, typename ALLOC, size_type ext = Extent,
        typename std::enable_if<(ext == dynamic_extent) &&
                std::is_convertible<const U(*)[], T(*)[]>::value, int>::type = 0>
    constexpr Span(const std::vector<U, ALLOC>& vec) :
        m_storage(vec.data(), vec.size())
    {}

    /**
     * Constructor. Convert between spans of different types.
     *
     * \param s Input span.
     */
    template <typename U, size_type N,
        typename std::enable_if<(Extent == N || Extent == dynamic_extent) &&
                std::is_convertible<U(*)[], T(*)[]>::value, int>::type = 0>
    constexpr Span(const Span<U, N>& s) noexcept :
        m_storage(s.data(), s.size())
    {}

    /**
     * Method generated by default.
     * \{
     */
    ~Span() = default;

    Span(const Span& other) noexcept = default;
    Span& operator=(const Span& other) noexcept = default;

    Span(Span&& other) noexcept = default;
    Span& operator=(Span&& other) noexcept = default;
    /**
     * \}
     */

    /**
     * Begin iteration.
     *
     * \return Iterator to the beginning of the sequence.
     */
    constexpr iterator begin() const noexcept
    {
        return data();
    }

    /**
     * End iteration.
     *
     * \return Iterator one-past-last element of the sequence.
     */
    constexpr iterator end() const noexcept
    {
        return data() + size();
    }

    /**
     * Begin reverse iteration.
     *
     * \return Reverse iterator to the last element.
     */
    constexpr reverse_iterator rbegin() const noexcept
    {
        return reverse_iterator(end());
    }

    /**
     * End reverse iteration.
     *
     * \return Reverse iterator one-before-first element of the sequence.
     */
    constexpr reverse_iterator rend() const noexcept
    {
        return reverse_iterator(begin());
    }

    /**
     * Get reference to the first element of the sequence. The sequence shall not be empty.
     *
     * \return Reference to the first element of the sequence
     */
    constexpr reference front() const
    {
        return data()[0];
    }

    /**
     * Get reference to the last element of the sequence. The sequence shall not be empty.
     *
     * \return Reference to the last element of the sequence
     */
    constexpr reference back() const
    {
        return data()[size() - 1];
    }

    /**
     * Access the element on given index.
     *
     * \param idx Index of element to be accessed. Must be less than size().
     * \return Element on given index.
     */
    constexpr reference operator[](size_type idx) const
    {
        return data()[idx];
    }

    /**
     * Get pointer to the sequence beginning.
     *
     * \return Pointer to the sequence beginning
     */
    constexpr pointer data() const noexcept
    {
        return m_storage.m_data;
    }

    /**
     * Get number of elements in the sequence.
     *
     * \return Number of elements in the sequence.
     */
    constexpr size_type size() const noexcept
    {
        return m_storage.m_size;
    }

    /**
     * Get size of sequence in bytes.
     *
     * \return Size of sequence in bytes.
     */
    constexpr size_type size_bytes() const noexcept
    {
        return size() * sizeof(element_type);
    }

    /**
     * Check if the sequence is empty.
     *
     * \return True if the sequence is empty, false otherwise.
     */
    constexpr bool empty() const noexcept
    {
        return size() == 0;
    }

    /**
     * Get subspan of given number of elements from the sequence start.
     *
     * \return Subspan of given number of elements from the sequence start.
     */
    template <size_type Count>
    constexpr Span<element_type, Count> first() const
    {
        static_assert(Count <= Extent, "Requested number of characters out of range.");
        return Span<element_type, Count>(data(), Count);
    }

    /**
     * Get subspan of given number of elements from the sequence start.
     *
     * \param Count Requested number of elements in the subspan. Shall not be bigger than size().
     * \return Subspan of given number of elements from the sequence start.
     */
    constexpr Span<element_type, dynamic_extent> first(size_type Count) const
    {
        return Span<element_type, dynamic_extent>(data(), Count);
    }

    /**
     * Get subspan of given number of elements from the sequence end.
     *
     * \return Subspan of given number of elements from the sequence end.
     */
    template <size_type Count>
    constexpr Span<element_type, Count> last() const
    {
        static_assert(Count <= Extent, "Requested number of characters out of range.");
        return Span<element_type, Count>(data() + (size() - Count), Count);
    }

    /**
     * Get subspan of given number of elements from the sequence end.
     *
     * \param Count Requested number of elements in the subspan. Shall not be bigger than size().
     * \return Subspan of given number of elements from the sequence end.
     */
    constexpr Span<element_type, dynamic_extent> last(size_type Count) const
    {
        return Span<element_type, dynamic_extent>(data() + (size() - Count), Count);
    }

    template <size_type Offset, size_type Count>
    using SubspanReturnType =
        Span<T, Count != dynamic_extent ? Count : (Extent != dynamic_extent ? Extent - Offset
            : dynamic_extent)>;

    /**
     * Get subspan of given number of elements beginning at given index.
     *
     * \return Subspan of given number of elements beginning at given index.
     */
    /** \{ */
    template <size_type Offset, size_type Count = dynamic_extent,
            typename std::enable_if<Count == dynamic_extent, int>::type = 0>
    constexpr SubspanReturnType<Offset, Count> subspan() const
    {
        static_assert((Extent == dynamic_extent) || (Offset <= Extent),
                "Requested number of characters out of range.");
        return SubspanReturnType<Offset, Count>(data() + Offset, size() - Offset);
    }

    template <size_type Offset, size_type Count,
            typename std::enable_if<Count != dynamic_extent, int>::type = 0>
    constexpr SubspanReturnType<Offset, Count> subspan() const
    {
        static_assert((Extent == dynamic_extent) || (Offset <= Extent && Offset + Count <= Extent),
                "Requested number of characters out of range.");
        return SubspanReturnType<Offset, Count>(data() + Offset, Count);
    }
    /** \} */

    /**
     * Get subspan of given number of elements beginning at given index.
     *
     * \param Offset Index in the original sequence, where the new subspan should start
     * \param Count Requested number of elements in the subspan.
     * \return Subspan of given number of elements beginning at given index.
     */
    constexpr Span<element_type, dynamic_extent> subspan(size_type Offset,
        size_type Count = dynamic_extent) const
    {
        return Span<element_type, dynamic_extent>(data() + Offset,
            Count == dynamic_extent ? size() - Offset : Count );
    }

private:
    detail::SpanStorage<T, Extent> m_storage;
};

} // namespace zserio

#endif // ZSERIO_SPAN_H_INC
