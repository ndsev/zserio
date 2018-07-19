#ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
#define ZSERIO_OPTIONAL_HOLDER_H_INC

#include <cstddef>

#include "CppRuntimeException.h"
#include "HashCodeUtil.h"
#include "AlignedStorage.h"
#include "Types.h"

namespace zserio
{

namespace detail
{

template <typename T>
class in_place_storage
{
public:
    in_place_storage() {}

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
    // disable copy constructor and assignment operator
    in_place_storage(const in_place_storage&);
    in_place_storage& operator=(const in_place_storage&);

    typename AlignedStorage<T>::type m_inPlace;
};

template <typename T>
class heap_storage
{
public:
    heap_storage() : m_heap(NULL) {}

    ~heap_storage()
    {
        delete [] m_heap;
        m_heap = NULL;
    }

    void* getStorage()
    {
        if (m_heap == NULL)
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
    // disable copy constructor and assignment operator
    heap_storage(const heap_storage&);
    heap_storage& operator=(const heap_storage&);

    unsigned char* m_heap;
};

template<typename T, typename STORAGE>
class optional_holder
{
public:
    optional_holder() : m_isSet(false) {}

    optional_holder(const optional_holder<T, STORAGE>& other) : m_isSet(false)
    {
        copy(other);
    }

    ~optional_holder()
    {
        reset();
    }

    optional_holder<T, STORAGE>& operator=(const optional_holder<T, STORAGE>& other)
    {
        if (this != &other)
            copy(other);

        return *this;
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

    void reset(T* value = NULL)
    {
        if (value == NULL)
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
                throw CppRuntimeException("Optional field is set");
            m_isSet = true;
        }
    }

    void set(const T& value)
    {
        reset(new (getResetStorage()) T(value));
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

    void checkIsSet() const
    {
        if (!isSet())
            throw CppRuntimeException("Optional field is not set");
    }

    STORAGE m_storage;
    bool    m_isSet;
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
class OptimizedOptionalHolder :
          public detail::optimized_optional_holder<T, detail::is_optimized_in_place<T>::value>::type
{
};

} // namespace zserio

#endif // ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
