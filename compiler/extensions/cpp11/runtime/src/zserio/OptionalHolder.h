#ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
#define ZSERIO_OPTIONAL_HOLDER_H_INC

#include <cstddef>

#include "CppRuntimeException.h"
#include "HashCodeUtil.h"
#include "Types.h"

namespace zserio
{

struct NullOptType
{
    explicit constexpr NullOptType(int) {}
};

constexpr NullOptType NullOpt{int()};

namespace detail
{

template <typename T>
class in_place_storage
{
public:
    in_place_storage() {}

    in_place_storage(const in_place_storage&) = delete;
    in_place_storage& operator=(const in_place_storage&) = delete;

    in_place_storage(in_place_storage&& other)
    {
        move(std::move(other));
    }

    in_place_storage& operator=(in_place_storage&& other)
    {
        move(std::move(other));
    }

    void* getStorage()
    {
        return &m_inPlace;
    }

    T* getObject()
    {
        return reinterpret_cast<T*>(&m_inPlace);
    }

    const T* getObject() const
    {
        return reinterpret_cast<const T*>(&m_inPlace);
    }

private:
    void move(in_place_storage&& other)
    {
        new (&m_inPlace) T(std::move(*other.getObject()));
        other.getObject()->~T(); // ensure that destructor of object in original storage is called
    }

    typename std::aligned_storage<sizeof(T), alignof(T)>::type m_inPlace;
};

template <typename T>
class heap_storage
{
public:
    heap_storage() : m_heap(nullptr) {}

    heap_storage(const heap_storage&) = delete;
    heap_storage& operator=(const heap_storage&) = delete;

    ~heap_storage()
    {
        delete [] m_heap;
        m_heap = nullptr;
    }

    heap_storage(heap_storage&& other)
    {
        move(std::move(other));
    }

    heap_storage& operator=(heap_storage&& other)
    {
        move(std::move(other));

        return *this;
    }

    void* getStorage()
    {
        if (m_heap == nullptr)
            m_heap = new unsigned char [sizeof(T)];

        return m_heap;
    }

    T* getObject()
    {
        return reinterpret_cast<T*>(m_heap);
    }

    const T* getObject() const
    {
        return reinterpret_cast<const T*>(m_heap);
    }

private:
    void move(heap_storage&& other)
    {
        m_heap = other.m_heap;
        other.m_heap = nullptr;
    }

    unsigned char* m_heap;
};

template<typename T, typename STORAGE>
class optional_holder
{
public:
    constexpr optional_holder() noexcept
    {}

    constexpr optional_holder(NullOptType) noexcept
    {}

    template <typename U = T>
    optional_holder(U&& value)
    {
        new (getStorage()) T(std::forward<U>(value));
        m_hasValue = true;
    }

    optional_holder(const optional_holder<T, STORAGE>& other)
    {
        if (other.hasValue())
        {
            new (getStorage()) T(*other.m_storage.getObject());
            m_hasValue = true;
        }
    }

    optional_holder(optional_holder<T, STORAGE>&& other)
    {
        if (other.hasValue())
        {
            m_storage = std::move(other.m_storage);
            other.m_hasValue = false;
            m_hasValue = true;
        }
    }

    ~optional_holder()
    {
        reset();
    }

    optional_holder<T, STORAGE>& operator=(const optional_holder<T, STORAGE>& other)
    {
        if (this != &other)
        {
            reset();
            if (other.hasValue())
            {
                new (getStorage()) T(*other.m_storage.getObject());
                m_hasValue = true;
            }
        }

        return *this;
    }

    optional_holder& operator=(optional_holder<T, STORAGE>&& other)
    {
        if (this != &other)
        {
            reset();
            if (other.hasValue())
            {
                m_storage = std::move(other.m_storage);
                other.m_hasValue = false;
                m_hasValue = true;
            }
        }
    }

    template <typename U = T>
    optional_holder& operator=(U&& value)
    {
        set(std::forward<U>(value));
    }

    bool operator==(const optional_holder<T, STORAGE>& other) const
    {
        if (this != &other)
        {
            if (hasValue() && other.hasValue())
                return *(m_storage.getObject()) == *(other.m_storage.getObject());

            return (!hasValue() && !other.hasValue());
        }

        return true;
    }

    explicit operator bool() const noexcept
    {
        return hasValue();
    }

    const T& operator*() const
    {
        return *get();
    }

    T& operator*()
    {
        return *get();
    }

    const T* operator->() const
    {
        return get();
    }

    T* operator->()
    {
        return get();
    }

    void reset() noexcept
    {
        if (hasValue())
        {
            m_storage.getObject()->~T();
            m_hasValue = false;
        }
    }

    T& value()
    {
        return *get();
    }

    const T& value() const
    {
        return *get();
    }

    bool hasValue() const noexcept
    {
        return m_hasValue;
    }

private:
    template <typename U = T>
    void set(U&& value)
    {
        reset();
        new (getStorage()) T(std::forward<U>(value));
        m_hasValue = true;
    }

    T* get()
    {
        checkHasValue();
        return m_storage.getObject();
    }

    const T* get() const
    {
        checkHasValue();
        return m_storage.getObject();
    }

    void* getStorage()
    {
        return m_storage.getStorage();
    }

    void checkHasValue() const
    {
        if (!hasValue())
            throw CppRuntimeException("Trying to access value of non-present optional field!");
    }

    STORAGE m_storage;
    bool    m_hasValue = false;
};

template <typename T>
struct is_optimized_in_place
{
    static const bool value = (sizeof(in_place_storage<T>) <= 8 * sizeof(uint64_t) - sizeof(bool));
};

template <typename T, bool IS_IN_PLACE>
struct optimized_optional_holder
{
    typedef optional_holder<T, detail::in_place_storage<T> > type;
};

template <typename T>
struct optimized_optional_holder<T, false>
{
    typedef optional_holder<T, detail::heap_storage<T> > type;
};

} // namespace detail

template <typename T>
class InPlaceOptionalHolder : public detail::optional_holder<T, detail::in_place_storage<T> >
{
};

template <typename T>
class HeapOptionalHolder : public detail::optional_holder<T, detail::heap_storage<T> >
{
};

template <typename T>
class OptionalHolder :
        public detail::optimized_optional_holder<T, detail::is_optimized_in_place<T>::value>::type
{
private:
    typedef typename detail::optimized_optional_holder<T, detail::is_optimized_in_place<T>::value>::type impl;

public:
    using impl::optional_holder;
};

} // namespace zserio

#endif // ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
