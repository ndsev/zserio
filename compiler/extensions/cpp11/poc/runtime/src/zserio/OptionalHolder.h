#ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
#define ZSERIO_OPTIONAL_HOLDER_H_INC

#include <cstddef>
#include <iostream>

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
        new (&m_inPlace) T(std::move(*other.getObject()));
    }

    in_place_storage& operator=(in_place_storage&& other)
    {
        new (&m_inPlace) T(std::move(*other.getObject()));
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
        m_heap = other.m_heap;
        other.m_heap = nullptr;
    }

    heap_storage& operator=(heap_storage&& other)
    {
        m_heap = other.m_heap;
        other.m_heap = nullptr;

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

    optional_holder(const optional_holder<T, STORAGE>& other)
    {
        copy(other);
    }

    optional_holder<T, STORAGE>& operator=(const optional_holder<T, STORAGE>& other)
    {
        if (this != &other)
            copy(other);

        return *this;
    }

    optional_holder(optional_holder<T, STORAGE>&& other)
    {
        move(std::move(other));
    }

    optional_holder& operator=(optional_holder<T, STORAGE>&& other)
    {
        move(std::move(other));
    }

    optional_holder(const T& value)
    {
        set(value);
    }

    optional_holder(T&& value)
    {
        set(std::move(value));
    }

    ~optional_holder()
    {
        reset();
    }

    bool operator==(const optional_holder<T, STORAGE>& other) const
    {
        if (this != &other)
        {
            if (isSet() && other.isSet())
                return *(m_storage.getObject()) == *(other.m_storage.getObject());

            return (!isSet() && !other.isSet());
        }

        return true;
    }

    void* getResetStorage()
    {
        reset();
        return m_storage.getStorage();
    }

    void reset(T* value = nullptr) // TODO: split to reset and setimpl something
    {
        if (value == nullptr)
        {
            if (isSet())
            {
                m_storage.getObject()->~T();
                m_isSet = false;
            }
        }
        else
        {
            if (isSet())
                throw CppRuntimeException("Invalid usage of OptionalHolder reset method!");
            m_isSet = true;
        }
    }

    void set(const T& value)
    {
        reset(new (getResetStorage()) T(value));
    }

    void set(T&& value)
    {
        reset(new (getResetStorage()) T(std::move(value)));
    }

    T& get()
    {
        checkIsSet();
        return *(m_storage.getObject());
    }

    const T& get() const
    {
        checkIsSet();
        return *(m_storage.getObject());
    }

    bool isSet() const
    {
        return m_isSet;
    }

    int hashCode() const
    {
        int result = HASH_SEED;
        if (isSet())
            result = calcHashCode(result, *(m_storage.getObject()));
        else
            result = calcHashCode(result, 0);

        return result;
    }

private:
    void copy(const optional_holder<T, STORAGE>& other)
    {
        if (other.isSet())
            set(*(other.m_storage.getObject()));
        else
            reset();
    }

    void move(optional_holder<T, STORAGE>&& other)
    {
        reset();
        if (other.isSet())
        {
            m_storage = std::move(other.m_storage);
            other.m_isSet = false;
            m_isSet = true;
        }
    }

    void checkIsSet() const
    {
        if (!isSet())
            throw CppRuntimeException("Trying to access value of non-present optional field!");
    }

    STORAGE m_storage;
    bool    m_isSet = false;
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
