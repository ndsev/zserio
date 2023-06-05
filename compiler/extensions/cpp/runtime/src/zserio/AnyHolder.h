#ifndef ZSERIO_ANY_HOLDER_H_INC
#define ZSERIO_ANY_HOLDER_H_INC

#include <cstddef>
#include <type_traits>

#include "zserio/AllocatorHolder.h"
#include "zserio/CppRuntimeException.h"
#include "zserio/OptionalHolder.h"
#include "zserio/Types.h"
#include "zserio/RebindAlloc.h"

namespace zserio
{

namespace detail
{
    class TypeIdHolder
    {
    public:
        using type_id = const int*;

        template <typename T>
        static type_id get()
        {
            static int currentTypeId;

            return &currentTypeId;
        }
    };

    // Interface for object holders
    template <typename ALLOC>
    class IHolder
    {
    public:
        virtual ~IHolder() = default;
        virtual IHolder* clone(const ALLOC& allocator) const = 0;
        virtual IHolder* clone(void* storage) const = 0;
        virtual IHolder* move(const ALLOC& allocator) = 0;
        virtual IHolder* move(void* storage) = 0;
        virtual void destroy(const ALLOC& allocator) = 0;
        virtual bool isType(detail::TypeIdHolder::type_id typeId) const = 0;
    };

    // Base of object holders, holds a value in the InplaceOptionalHolder
    template <typename T, typename ALLOC>
    class HolderBase : public IHolder<ALLOC>
    {
    public:
        template <typename U = T>
        void set(U&& value)
        {
            m_typedHolder = std::forward<U>(value);
        }

        T& get()
        {
            return m_typedHolder.value();
        }

        const T& get() const
        {
            return m_typedHolder.value();
        }

        bool isType(detail::TypeIdHolder::type_id typeId) const override
        {
            return detail::TypeIdHolder::get<T>() == typeId;
        }

    protected:
        InplaceOptionalHolder<T>& getHolder()
        {
            return m_typedHolder;
        }

        const InplaceOptionalHolder<T>& getHolder() const
        {
            return m_typedHolder;
        }

    private:
        InplaceOptionalHolder<T> m_typedHolder;
    };

    // Holder allocated on heap
    template <typename T, typename ALLOC>
    class HeapHolder : public HolderBase<T, ALLOC>
    {
    private:
        struct ConstructTag {};

    public:
        using this_type = HeapHolder<T, ALLOC>;

        explicit HeapHolder(ConstructTag) noexcept {}

        static this_type* create(const ALLOC& allocator)
        {
            using AllocType = RebindAlloc<ALLOC, this_type>;
            using AllocTraits = std::allocator_traits<AllocType>;

            AllocType typedAlloc = allocator;
            typename AllocTraits::pointer ptr = AllocTraits::allocate(typedAlloc, 1);
            // this never throws because HeapHolder constructor never throws
            AllocTraits::construct(typedAlloc, std::addressof(*ptr), ConstructTag{});
            return ptr;
        }

        IHolder<ALLOC>* clone(const ALLOC& allocator) const override
        {
            this_type* holder = create(allocator);
            holder->set(this->getHolder());
            return holder;
        }

        IHolder<ALLOC>* clone(void*) const override
        {
            throw CppRuntimeException("AnyHolder: Unexpected clone call.");
        }

        IHolder<ALLOC>* move(const ALLOC& allocator) override
        {
            this_type* holder = create(allocator);
            holder->set(std::move(this->getHolder()));
            return holder;
        }

        IHolder<ALLOC>* move(void*) override
        {
            throw CppRuntimeException("AnyHolder: Unexpected clone call.");
        }

        void destroy(const ALLOC& allocator) override
        {
            using AllocType = RebindAlloc<ALLOC, this_type>;
            using AllocTraits = std::allocator_traits<AllocType>;

            AllocType typedAlloc = allocator;
            AllocTraits::destroy(typedAlloc, this);
            AllocTraits::deallocate(typedAlloc, this, 1);
        }
    };

    // Holder allocated in the in-place storage
    template <typename T, typename ALLOC>
    class NonHeapHolder : public HolderBase<T, ALLOC>
    {
    public:
        using this_type = NonHeapHolder<T, ALLOC>;

        static this_type* create(void* storage)
        {
            return new (storage) this_type();
        }

        IHolder<ALLOC>* clone(const ALLOC&) const override
        {
            throw CppRuntimeException("AnyHolder: Unexpected clone call.");
        }

        IHolder<ALLOC>* clone(void* storage) const override
        {
            NonHeapHolder* holder = new (storage) NonHeapHolder();
            holder->set(this->getHolder());
            return holder;
        }

        IHolder<ALLOC>* move(const ALLOC&) override
        {
            throw CppRuntimeException("AnyHolder: Unexpected clone call.");
        }

        IHolder<ALLOC>* move(void* storage) override
        {
            NonHeapHolder* holder = new (storage) NonHeapHolder();
            holder->set(std::move(this->getHolder()));
            return holder;
        }

        void destroy(const ALLOC&) override
        {
            this->~NonHeapHolder();
        }

    private:
        NonHeapHolder() = default;
    };

    template <typename ALLOC>
    union UntypedHolder
    {
        // 2 * sizeof(void*) for T + sizeof(void*) for Holder's vptr
        using MaxInPlaceType = std::aligned_storage<3 * sizeof(void*), alignof(void*)>::type;

        detail::IHolder<ALLOC>* heap;
        MaxInPlaceType inPlace;
    };

    template <typename T, typename ALLOC>
    using has_non_heap_holder = std::integral_constant<bool,
            sizeof(NonHeapHolder<T, ALLOC>) <= sizeof(typename UntypedHolder<ALLOC>::MaxInPlaceType) &&
            std::is_nothrow_move_constructible<T>::value &&
                alignof(T) <= alignof(typename UntypedHolder<ALLOC>::MaxInPlaceType)>;
} // namespace detail

/**
 * Type safe container for single values of any type which doesn't need RTTI.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class AnyHolder : public AllocatorHolder<ALLOC>
{
    using AllocTraits = std::allocator_traits<ALLOC>;
    using AllocatorHolder<ALLOC>::get_allocator_ref;
    using AllocatorHolder<ALLOC>::set_allocator;

public:
    using AllocatorHolder<ALLOC>::get_allocator;
    using allocator_type = ALLOC;

    /**
     * Empty constructor.
     */
    AnyHolder() : AnyHolder(ALLOC())
    {
    }

    /**
     * Constructor from given allocator
     */
    explicit AnyHolder(const ALLOC& allocator) : AllocatorHolder<ALLOC>(allocator)
    {
        m_untypedHolder.heap = nullptr;
    }

    /**
     * Constructor from any value.
     *
     * \param value Value of any type to hold. Supports move semantic.
     */
    template <typename T,
            typename std::enable_if<!std::is_same<typename std::decay<T>::type, AnyHolder>::value &&
                                    !std::is_same<typename std::decay<T>::type, ALLOC>::value, int>::type = 0>
    explicit AnyHolder(T&& value, const ALLOC& allocator = ALLOC()) : AllocatorHolder<ALLOC>(allocator)
    {
        m_untypedHolder.heap = nullptr;
        set(std::forward<T>(value));
    }

    /**
     * Destructor.
     */
    ~AnyHolder()
    {
        clearHolder();
    }

    /**
     * Copy constructor.
     *
     * \param other Any holder to copy.
     */
    AnyHolder(const AnyHolder& other) :
        AllocatorHolder<ALLOC>(AllocTraits::select_on_container_copy_construction(other.get_allocator_ref()))
    {
        copy(other);
    }

    /**
     * Allocator-extended copy constructor.
     *
     * \param other Any holder to copy.
     * \param allocator Allocator to be used for dynamic memory allocations.
     */
    AnyHolder(const AnyHolder& other, const ALLOC& allocator) : AllocatorHolder<ALLOC>(allocator)
    {
        copy(other);
    }

    /**
     * Copy assignment operator.
     *
     * \param other Any holder to copy.
     *
     * \return Reference to this.
     */
    AnyHolder& operator=(const AnyHolder& other)
    {
        if (this != &other)
        {
            // TODO: do not dealloc unless necessary
            clearHolder();
            if (AllocTraits::propagate_on_container_copy_assignment::value)
                set_allocator(other.get_allocator_ref());
            copy(other);
        }

        return *this;
    }

    /**
     * Move constructor.
     *
     * \param other Any holder to move from.
     */
    AnyHolder(AnyHolder&& other) noexcept : AllocatorHolder<ALLOC>(std::move(other.get_allocator_ref()))
    {
        move(std::move(other));
    }

    /**
     * Allocator-extended move constructor.
     *
     * \param other Any holder to move from.
     * \param allocator Allocator to be used for dynamic memory allocations.
     */
    AnyHolder(AnyHolder&& other, const ALLOC& allocator) : AllocatorHolder<ALLOC>(allocator)
    {
        move(std::move(other));
    }

    /**
     * Move assignment operator.
     *
     * \param other Any holder to move from.
     *
     * \return Reference to this.
     */
    AnyHolder& operator=(AnyHolder&& other)
    {
        if (this != &other)
        {
            clearHolder();
            if (AllocTraits::propagate_on_container_move_assignment::value)
                set_allocator(std::move(other.get_allocator_ref()));
            move(std::move(other));
        }

        return *this;
    }

    /**
     * Value assignment operator.
     *
     * \param value Any value to assign. Supports move semantic.
     *
     * \return Reference to this.
     */
    template <typename T, typename
            std::enable_if<!std::is_same<typename std::decay<T>::type, AnyHolder>::value, int>::type = 0>
    AnyHolder& operator=(T&& value)
    {
        set(std::forward<T>(value));

        return *this;
    }

    /**
     * Resets the holder.
     */
    void reset()
    {
        clearHolder();
    }

    /**
     * Sets any value to the holder.
     *
     * \param value Any value to set. Supports move semantic.
     */
    template <typename T>
    void set(T&& value)
    {
        createHolder<typename std::decay<T>::type>()->set(std::forward<T>(value));
    }

    /**
     * Gets value of the given type.
     *
     * \return Reference to value of the requested type if the type match to the stored value.
     *
     * \throw CppRuntimeException if the requested type doesn't match to the stored value.
     */
    template <typename T>
    T& get()
    {
        checkType<T>();
        return getHolder<T>(detail::has_non_heap_holder<T, ALLOC>())->get();
    }

    /**
     * Gets value of the given type.
     *
     * \return Value of the requested type if the type match to the stored value.
     *
     * \throw CppRuntimeException if the requested type doesn't match to the stored value.
     */
    template <typename T>
    const T& get() const
    {
        checkType<T>();
        return getHolder<T>(detail::has_non_heap_holder<T, ALLOC>())->get();
    }

    /**
     * Check whether the holder holds the given type.
     *
     * \return True if the stored value is of the given type, false otherwise.
     */
    template <typename T>
    bool isType() const
    {
        return hasHolder() && getUntypedHolder()->isType(detail::TypeIdHolder::get<T>());
    }

    /**
     * Checks whether the holder has any value.
     *
     * \return True if the holder has assigned any value, false otherwise.
     */
    bool hasValue() const
    {
        return hasHolder();
    }

private:
    void copy(const AnyHolder& other)
    {
        if (other.m_isInPlace)
        {
            other.getUntypedHolder()->clone(&m_untypedHolder.inPlace);
            m_isInPlace = true;
        }
        else if (other.m_untypedHolder.heap != nullptr)
        {
            m_untypedHolder.heap = other.getUntypedHolder()->clone(get_allocator_ref());
        }
        else
        {
            m_untypedHolder.heap = nullptr;
        }
    }

    void move(AnyHolder&& other)
    {
        if (other.m_isInPlace)
        {
            other.getUntypedHolder()->move(&m_untypedHolder.inPlace);
            m_isInPlace = true;
            other.clearHolder();
        }
        else if (other.m_untypedHolder.heap != nullptr)
        {
            if (get_allocator_ref() == other.get_allocator_ref())
            {
                // take over the other's storage
                m_untypedHolder.heap = other.m_untypedHolder.heap;
                other.m_untypedHolder.heap = nullptr;
            }
            else
            {
                // cannot steal the storage, allocate our own and move the holder
                m_untypedHolder.heap = other.getUntypedHolder()->move(get_allocator_ref());
                other.clearHolder();
            }
        }
        else
        {
            m_untypedHolder.heap = nullptr;
        }
    }

    void clearHolder()
    {
        if (hasHolder())
        {
            getUntypedHolder()->destroy(get_allocator_ref());
            m_isInPlace = false;
            m_untypedHolder.heap = nullptr;
        }
    }

    bool hasHolder() const
    {
        return (m_isInPlace || m_untypedHolder.heap != nullptr);
    }

    template <typename T>
    detail::HolderBase<T, ALLOC>* createHolder()
    {
        if (hasHolder())
        {
            if (getUntypedHolder()->isType(detail::TypeIdHolder::get<T>()))
                return getHolder<T>(detail::has_non_heap_holder<T, ALLOC>());

            clearHolder();
        }

        return createHolderImpl<T>(detail::has_non_heap_holder<T, ALLOC>());
    }

    template <typename T>
    detail::HolderBase<T, ALLOC>* createHolderImpl(std::true_type)
    {
        detail::NonHeapHolder<T, ALLOC>* holder =
                detail::NonHeapHolder<T, ALLOC>::create(&m_untypedHolder.inPlace);
        m_isInPlace = true;
        return holder;
    }

    template <typename T>
    detail::HolderBase<T, ALLOC>* createHolderImpl(std::false_type)
    {
        detail::HeapHolder<T, ALLOC>* holder = detail::HeapHolder<T, ALLOC>::create(get_allocator_ref());
        m_untypedHolder.heap = holder;
        return holder;
    }

    template <typename T>
    void checkType() const
    {
        if (!isType<T>())
            throwBadType();
    }

    /** Optimization which increases changes to inline checkType(). */
    void throwBadType() const
    {
        throw CppRuntimeException("Bad type in AnyHolder");
    }

    template <typename T>
    detail::HeapHolder<T, ALLOC>* getHeapHolder()
    {
        return static_cast<detail::HeapHolder<T, ALLOC>*>(m_untypedHolder.heap);
    }

    template <typename T>
    const detail::HeapHolder<T, ALLOC>* getHeapHolder() const
    {
        return static_cast<detail::HeapHolder<T, ALLOC>*>(m_untypedHolder.heap);
    }

    template <typename T>
    detail::NonHeapHolder<T, ALLOC>* getInplaceHolder()
    {
        return reinterpret_cast<detail::NonHeapHolder<T, ALLOC>*>(&m_untypedHolder.inPlace);
    }

    template <typename T>
    const detail::NonHeapHolder<T, ALLOC>* getInplaceHolder() const
    {
        return reinterpret_cast<const detail::NonHeapHolder<T, ALLOC>*>(&m_untypedHolder.inPlace);
    }

    template <typename T>
    detail::HolderBase<T, ALLOC>* getHolder(std::true_type)
    {
        return static_cast<detail::HolderBase<T, ALLOC>*>(getInplaceHolder<T>());
    }

    template <typename T>
    detail::HolderBase<T, ALLOC>* getHolder(std::false_type)
    {
        return static_cast<detail::HolderBase<T, ALLOC>*>(getHeapHolder<T>());
    }

    template <typename T>
    const detail::HolderBase<T, ALLOC>* getHolder(std::true_type) const
    {
        return static_cast<const detail::HolderBase<T, ALLOC>*>(getInplaceHolder<T>());
    }

    template <typename T>
    const detail::HolderBase<T, ALLOC>* getHolder(std::false_type) const
    {
        return static_cast<const detail::HolderBase<T, ALLOC>*>(getHeapHolder<T>());
    }

    detail::IHolder<ALLOC>* getUntypedHolder()
    {
        return (m_isInPlace) ?
                reinterpret_cast<detail::IHolder<ALLOC>*>(&m_untypedHolder.inPlace) :
                m_untypedHolder.heap;
    }

    const detail::IHolder<ALLOC>* getUntypedHolder() const
    {
        return (m_isInPlace) ?
                reinterpret_cast<const detail::IHolder<ALLOC>*>(&m_untypedHolder.inPlace) :
                m_untypedHolder.heap;
    }

    detail::UntypedHolder<ALLOC> m_untypedHolder;
    bool m_isInPlace = false;
};

} // namespace zserio

#endif // ifndef ZSERIO_ANY_HOLDER_H_INC
