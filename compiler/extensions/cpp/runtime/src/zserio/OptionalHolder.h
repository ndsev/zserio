#ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
#define ZSERIO_OPTIONAL_HOLDER_H_INC

#include <cstddef>
#include <type_traits>

#include "zserio/CppRuntimeException.h"
#include "zserio/Types.h"
#include "zserio/UniquePtr.h"

namespace zserio
{

/**
 * Helper type for specification of an unset optional holder.
 */
struct NullOptType
{
    /**
     * Explicit constructor from int.
     *
     * \see https://en.cppreference.com/w/cpp/utility/optional/nullopt_t
     */
    constexpr explicit NullOptType(int) {}
};

/**
 * Constant used to convenient specification of an unset optional holder.
 */
constexpr NullOptType NullOpt{int()};

/**
 * Helper type for specification of in-place construction.
 */
struct InPlaceT
{
    constexpr explicit InPlaceT() = default;
};

/**
  * Constant used as a marker for in-place construction.
  */
constexpr InPlaceT InPlace{};

namespace detail
{

/**
 * Base class for optional holders. Provides common interface for various types of optional holders.
 */
template <typename T, typename Derived>
class optional_holder_base
{
public:
    /**
     * Operator equality.
     *
     * \param other Other holder to compare.
     *
     * \return True when the other holder has same value as this. False otherwise.
     */
    bool operator==(const optional_holder_base& other) const
    {
        if (this == &other)
            return true;

        if (getDerived()->hasValue() != other.getDerived()->hasValue())
            return false;

        if (getDerived()->hasValue())
            return get() == other.get();

        return true;
    }

    /**
     * Bool operator.
     *
     * Evaluates to true when this holder has assigned any value.
     */
    explicit operator bool() const noexcept
    {
        return getDerived()->hasValue();
    }

    /**
     * Dereference operator ->.
     *
     * \return Const reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    const T* operator->() const
    {
        return std::addressof(get());
    }

    /**
     * Dereference operator ->.
     *
     * \return Reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    T* operator->()
    {
        return std::addressof(get());
    }

    /**
     * Gets held value.
     *
     * \return Const reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    const T& value() const
    {
        return get();
    }

    /**
     * Gets held value.
     *
     * \return Reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    T& value()
    {
        return get();
    }

protected:
    void checkHasValue() const
    {
        if (!getDerived()->hasValue())
            throwNonPresentException();
    }

private:
    T& get()
    {
        return getDerived()->operator*();
    }

    const T& get() const
    {
        return getDerived()->operator*();
    }

    Derived* getDerived()
    {
        return static_cast<Derived*>(this);
    }

    const Derived* getDerived() const
    {
        return static_cast<const Derived*>(this);
    }

    /** Optimization which increases chances to inline checkHasValue(). */
    void throwNonPresentException() const
    {
        throw CppRuntimeException("Trying to access value of non-present optional field!");
    }
};

/**
 * Optional holder implementation that stores the object on heap.
 */
template <typename T, typename ALLOC>
class heap_optional_holder : public optional_holder_base<T, heap_optional_holder<T, ALLOC>>
{
public:
    using allocator_type = ALLOC;
    using allocator_traits = std::allocator_traits<allocator_type>;
    using storage_type = zserio::unique_ptr<T, allocator_type>;

    /**
     * Getter for the allocator.
     *
     * \return Allocator that is used for the dynamic memory allocat
     */
    allocator_type get_allocator() const
    {
        return m_storage.get_deleter().get_allocator();
    }

    /**
     * Empty constructor which creates an unset holder.
     *
     * \param allocator Allocator to be used to perform dynamic memory allocations.
     */
    explicit constexpr heap_optional_holder(const allocator_type& allocator = allocator_type()) noexcept :
            m_storage(nullptr, allocator)
    {}

    /**
     * Constructor from zserio::NullOpt constant to create an unset holder.
     *
     * \param allocator Allocator to be used to perform dynamic memory allocations.
     */
    constexpr heap_optional_holder(NullOptType,
            const allocator_type& allocator = allocator_type()) noexcept : m_storage(nullptr, allocator)
    {}

    /**
     * Constructor from a given value.
     *
     * \param value Value to store in the holder.
     * \param allocator Allocator to be used to perform dynamic memory allocations.
     */
    heap_optional_holder(const T& value, const allocator_type& allocator = allocator_type()) :
            m_storage(zserio::allocate_unique<T, allocator_type>(allocator, value))
    {}

    /**
     * Constructor from a given value passed by rvalue reference.
     *
     * \param value Value to store in the holder.
     * \param allocator Allocator to be used to perform dynamic memory allocations.
     */
    heap_optional_holder(T&& value, const allocator_type& allocator = allocator_type()) :
            m_storage(zserio::allocate_unique<T, allocator_type>(allocator, std::move(value)))
    {}

    /**
     * Constructor that initializes the value inplace by forwarding the arguments to the object's constructor.
     *
     * \param allocator Allocator to be used for dynamic memory allocations.
     * \param u Parameters for object's constructor.
     */
    template <typename ...U>
    explicit heap_optional_holder(const allocator_type& allocator, U&& ...u) :
            m_storage(zserio::allocate_unique<T, allocator_type>(allocator, std::forward<U>(u)...))
    {}

    /**
     * Destructor.
     */
    ~heap_optional_holder() = default;

    /**
     * Copy constructor.
     *
     * \param other Other holder to copy.
     */
    heap_optional_holder(const heap_optional_holder& other) :
            m_storage(copy_initialize(other, allocator_traits::select_on_container_copy_construction(
                    other.m_storage.get_deleter().get_allocator())))
    {}

    /**
     * Copy constructor.
     *
     * \param other Other holder to copy.
     * \param allocator Allocator to be used for dynamic memory allocations.
     */
    heap_optional_holder(const heap_optional_holder& other, const allocator_type& allocator) :
            m_storage(copy_initialize(other, allocator))
    {}

    /**
     * Move constructor.
     *
     * \param other Other holder to move.
     */
    heap_optional_holder(heap_optional_holder&& other) noexcept = default;

    /**
     * Allocator-extended move constructor.
     *
     * \param other Other holder to move.
     * \param allocator Allocator to be used for dynamic memory allocations.
     */
    heap_optional_holder(heap_optional_holder&& other, const allocator_type& allocator) :
            m_storage(move_initialize(std::move(other), allocator))
    {
    }

    /**
     * Assignment operator.
     *
     * \param other Other holder to copy-assign.
     *
     * \return Reference to the current holder.
     */
    heap_optional_holder& operator=(const heap_optional_holder& other)
    {
        if (this == &other)
            return *this;

        m_storage = copy_initialize(other,
                select_allocator(other.m_storage.get_deleter().get_allocator(),
                        typename allocator_traits::propagate_on_container_copy_assignment()));

        return *this;
    }

    /**
     * Move assignment operator.
     *
     * \param other Other holder to move-assign.
     *
     * \return Reference to the current holder.
     */
    heap_optional_holder& operator=(heap_optional_holder&& other)
    {
        if (this == &other)
            return *this;

        m_storage = move_initialize(std::move(other),
            select_allocator(other.m_storage.get_deleter().get_allocator(),
                    typename allocator_traits::propagate_on_container_move_assignment()));

        return *this;
    }

    /**
     * Assignment operator from value.
     *
     * \param value Value to assign.
     *
     * \return Reference to the current holder.
     */
    heap_optional_holder& operator=(const T& value)
    {
        set(value);

        return *this;
    }

    /**
     * Assignment operator from rvalue reference to value.
     *
     * \param value Value to move-assign.
     *
     * \return Reference to the current holder.
     */
    heap_optional_holder& operator=(T&& value)
    {
        set(std::move(value));

        return *this;
    }

    /**
     * Resets the current holder (switch to the unset state).
     */
    void reset() noexcept
    {
        m_storage.reset();
    }

    /**
     * Gets whether the holder has any value.
     *
     * \return True when this holder has assigned any value. False otherwise.
     */
    bool hasValue() const noexcept
    {
        return static_cast<bool>(m_storage);
    }

    /**
     * Dereference operator *.
     *
     * \return Const reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    const T& operator*() const
    {
        this->checkHasValue();
        return *m_storage.get();
    }

    /**
     * Dereference operator *.
     *
     * \return Reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    T& operator*()
    {
        this->checkHasValue();
        return *m_storage.get();
    }

private:
    allocator_type select_allocator(allocator_type other_allocator, std::true_type)
    {
        return other_allocator;
    }

    allocator_type select_allocator(allocator_type, std::false_type)
    {
        return m_storage.get_deleter().get_allocator(); // current allocator
    }

    template <typename U = T>
    void set(U&& value)
    {
        reset();
        m_storage = zserio::allocate_unique<T, allocator_type>(
                m_storage.get_deleter().get_allocator(), std::forward<U>(value));
    }

    static storage_type copy_initialize(const heap_optional_holder& other, const allocator_type& allocator)
    {
        if (other.hasValue())
            return zserio::allocate_unique<T, allocator_type>(allocator, *other);
        else
            return storage_type(nullptr, allocator);
    }

    static storage_type move_initialize(heap_optional_holder&& other, const allocator_type& allocator)
    {
        if (other.hasValue())
        {
            if (allocator == other.m_storage.get_deleter().get_allocator())
                return std::move(other.m_storage);

            return zserio::allocate_unique<T, allocator_type>(allocator, std::move(*other));
        }
        else
        {
            return storage_type(nullptr, allocator);
        }
    }

    storage_type m_storage;
};

/**
 * In place storage for optional holder.
 */
template <typename T>
class in_place_storage
{
public:
    /**
     * Constructor.
     */
    in_place_storage() = default;

    /**
     * Destructor.
     */
    ~in_place_storage() = default;

    /** Copying is disabled. */
    /** \{ */
    in_place_storage(const in_place_storage&) = delete;
    in_place_storage& operator=(const in_place_storage&) = delete;
    /** \} */

    /** Move constructor is disabled. */
    in_place_storage(in_place_storage&& other) = delete;

    /**
     * Move assignment operator.
     *
     * \param other Other storage to move.
     *
     * \return Reference to the current storage.
     */
    in_place_storage& operator=(in_place_storage&& other)
    {
        move(std::move(other));

        return *this;
    }

    /**
     * Gets pointer to the underlying in-place memory.
     *
     * \return Pointer to the storage.
     */
    void* getStorage()
    {
        return &m_inPlace;
    }

    /**
     * Gets pointer to the stored object.
     *
     * \return Pointer to the object.
     */
    T* getObject()
    {
        return reinterpret_cast<T*>(&m_inPlace);
    }

    /**
     * Gets pointer to the stored object.
     *
     * \return Const pointer to the object.
     */
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

    // Caution!
    // We use static constants as a WORKAROUND for a GCC 8/9 bug observed when cross-compiling with -m32 flag.
    // The compilers somehow spoils the aligned storage when alignof(T) is used directly as the template
    // argument which leads to "*** stack smashing detected ***" error (as observed in some language tests).
    static constexpr size_t SIZEOF_T = sizeof(T);
    static constexpr size_t ALIGNOF_T = alignof(T);
    using AlignedStorage = typename std::aligned_storage<SIZEOF_T, ALIGNOF_T>::type;
    AlignedStorage m_inPlace;
};

/**
 * Optional holder implementation for Zserio which allows usage of in place storage.
 */
template <typename T>
class inplace_optional_holder : public optional_holder_base<T, inplace_optional_holder<T>>
{
public:
    /**
     * Empty constructor which creates an unset holder.
     */
    constexpr inplace_optional_holder() noexcept = default;

    /**
     * Constructor from zserio::NullOpt constant to create an unset holder.
     */
    constexpr inplace_optional_holder(NullOptType) noexcept
    {}

    /**
     * Constructor from a given value.
     *
     * \param value Value to store in the holder.
     */
    inplace_optional_holder(const T& value)
    {
        new (m_storage.getStorage()) T(value);
        m_hasValue = true;
    }

    /**
     * Constructor from a given value passed by rvalue reference.
     *
     * \param value Value to store in the holder.
     */
    inplace_optional_holder(T&& value)
    {
        new (m_storage.getStorage()) T(std::move(value));
        m_hasValue = true;
    }

    /**
     * Copy constructor.
     *
     * \param other Other holder to copy.
     */
    inplace_optional_holder(const inplace_optional_holder& other)
    {
        if (other.hasValue())
        {
            new (m_storage.getStorage()) T(*other.m_storage.getObject());
            m_hasValue = true;
        }
    }

    /**
     * Move constructor.
     *
     * \param other Other holder to move.
     */
    inplace_optional_holder(inplace_optional_holder&& other)
            noexcept(std::is_nothrow_move_constructible<in_place_storage<T>>::value)
    {
        if (other.hasValue())
        {
            m_storage = std::move(other.m_storage);
            other.m_hasValue = false;
            m_hasValue = true;
        }
    }

    /**
     * Constructor that initializes the value inplace by forwarding the arguments to the object's constructor.
     *
     * \param u Parameters for object's constructor.
     */
    template <typename ...U>
    explicit inplace_optional_holder(InPlaceT, U&& ...u)
    {
        new (m_storage.getStorage()) T(std::forward<U>(u)...);
        m_hasValue = true;
    }

    /**
     * Destructor.
     */
    ~inplace_optional_holder()
    {
        reset();
    }

    /**
     * Assignment operator.
     *
     * \param other Other holder to copy-assign.
     *
     * \return Reference to the current holder.
     */
    inplace_optional_holder& operator=(const inplace_optional_holder& other)
    {
        if (this != &other)
        {
            reset();
            if (other.hasValue())
            {
                new (m_storage.getStorage()) T(*other.m_storage.getObject());
                m_hasValue = true;
            }
        }

        return *this;
    }

    /**
     * Move assignment operator.
     *
     * \param other Other holder to move-assign.
     *
     * \return Reference to the current holder.
     */
    inplace_optional_holder& operator=(inplace_optional_holder&& other)
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

        return *this;
    }

    /**
     * Assignment operator from value.
     *
     * \param value Value to assign.
     *
     * \return Reference to the current holder.
     */
    inplace_optional_holder& operator=(const T& value)
    {
        set(value);

        return *this;
    }

    /**
     * Assignment operator from rvalue reference to value.
     *
     * \param value Value to move-assign.
     *
     * \return Reference to the current holder.
     */
    inplace_optional_holder& operator=(T&& value)
    {
        set(std::move(value));

        return *this;
    }

    /**
     * Resets the current holder (switch to the unset state).
     */
    void reset() noexcept
    {
        if (hasValue())
        {
            m_storage.getObject()->~T();
            m_hasValue = false;
        }
    }

    /**
     * Gets whether the holder has any value.
     *
     * \return True when this holder has assigned any value. False otherwise.
     */
    bool hasValue() const noexcept
    {
        return m_hasValue;
    }

    /**
     * Dereference operator *.
     *
     * \return Const reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    const T& operator*() const
    {
        this->checkHasValue();
        return *m_storage.getObject();
    }

    /**
     * Dereference operator *.
     *
     * \return Reference to the assigned value.
     *
     * \throw CppRuntimeException when the holder is unset.
     */
    T& operator*()
    {
        this->checkHasValue();
        return *m_storage.getObject();
    }

private:
    template <typename U = T>
    void set(U&& value)
    {
        reset();
        new (m_storage.getStorage()) T(std::forward<U>(value));
        m_hasValue = true;
    }

    in_place_storage<T> m_storage;
    bool m_hasValue = false;
};

} // namespace detail

// Be aware that if Inplace/HeapOptionalHolder is defined by typename, C++ compiler will have problem with
// template function overload, see HashCodeUtil.h (overloads for objects and for Inplace/HeapOptionalHolder).
/**
 * Optional holder which uses in place storage.
 */
template <typename T>
using InplaceOptionalHolder = detail::inplace_optional_holder<T>;

/**
 * Optional holder which uses heap storage.
 */
template <typename T, typename ALLOC = std::allocator<T>>
using HeapOptionalHolder = detail::heap_optional_holder<T, ALLOC>;

} // namespace zserio

#endif // ifndef ZSERIO_OPTIONAL_HOLDER_H_INC
