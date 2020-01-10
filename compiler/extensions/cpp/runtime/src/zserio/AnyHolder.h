#ifndef ZSERIO_ANY_HOLDER_H_INC
#define ZSERIO_ANY_HOLDER_H_INC

#include <cstddef>
#include <type_traits>

#include "zserio/CppRuntimeException.h"
#include "zserio/OptionalHolder.h"
#include "zserio/Types.h"

namespace zserio
{

/**
 * Type safe container for single values of any type which doesn't need RTTI.
 */
class AnyHolder
{
public:
    /**
     * Empty constructor.
     */
    AnyHolder()
    {
        m_untypedHolder.heap = NULL;
    }

    /**
     * Constructor from any value.
     *
     * \param value Value of any type to hold. Supports move semantic.
     */
    template <typename T,
            typename std::enable_if<!std::is_same<typename std::decay<T>::type, AnyHolder>::value, int>::type = 0>
    explicit AnyHolder(T&& value)
    {
        m_untypedHolder.heap = NULL;
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
    AnyHolder(const AnyHolder& other)
    {
        m_untypedHolder.heap = NULL;
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
            clearHolder();
            copy(other);
        }

        return *this;
    }

    /**
     * Move constructor.
     *
     * \param other Any holder to move from.
     */
    AnyHolder(AnyHolder&& other)
    {
        m_untypedHolder.heap = NULL;
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
    template <typename T,
            typename std::enable_if<!std::is_same<typename std::decay<T>::type, AnyHolder>::value, int>::type = 0>
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
        return getHolder<T>()->get();
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
        return getHolder<T>()->get();
    }

    /**
     * Check whether the holder holds the given type.
     *
     * \return True if the stored value is of the given type, false otherwise.
     */
    template <typename T>
    bool isType() const
    {
        return hasHolder() && getUntypedHolder()->isType(TypeIdHolder::get<T>());
    }

    /**
     * Checks whether the holder has any value.
     *
     * \return True if the holder has assigned any value, false otherwise.
     */
    bool hasValue() const
    {
        return hasHolder() && getUntypedHolder()->isSet();
    }

private:
    class TypeIdHolder
    {
    public:
        typedef int* type_id;

        template <typename T>
        static type_id get()
        {
            static int currentTypeId;

            return &currentTypeId;
        }
    };

    class IHolder
    {
    public:
        virtual ~IHolder() {}
        virtual bool isSet() const = 0;
        virtual IHolder* clone(void* storage) const = 0;
        virtual void move(void* storage) = 0;
        virtual bool isType(TypeIdHolder::type_id typeId) const = 0;
    };

    template <typename T>
    class Holder : public IHolder
    {
    public:
        Holder() {}

        void reset()
        {
            m_typedHolder.reset();
        }

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

        virtual bool isSet() const
        {
            return m_typedHolder.hasValue();
        }

        virtual IHolder* clone(void* storage) const
        {
            Holder<T>* holder = (storage == NULL) ? new Holder<T>() : new (storage) Holder<T>();
            holder->m_typedHolder = m_typedHolder;

            return holder;
        }

        virtual void move(void* storage)
        {
            Holder<T>* holder = new (storage) Holder<T>();
            holder->m_typedHolder = std::move(m_typedHolder);
        }

        virtual bool isType(TypeIdHolder::type_id typeId) const
        {
            return TypeIdHolder::get<T>() == typeId;
        }

    private:
        InPlaceOptionalHolder<T> m_typedHolder;
    };

    void copy(const AnyHolder& other)
    {
        if (other.m_isInPlace)
        {
            other.getUntypedHolder()->clone(&m_untypedHolder.inPlace);
            m_isInPlace = true;
        }
        else
        {
            if (other.m_untypedHolder.heap != NULL)
                m_untypedHolder.heap = other.getUntypedHolder()->clone(NULL);
        }
    }

    void move(AnyHolder&& other)
    {
        if (other.m_isInPlace)
        {
            other.getUntypedHolder()->move(&m_untypedHolder.inPlace);
            m_isInPlace = true;
            reinterpret_cast<IHolder*>(&other.m_untypedHolder.inPlace)->~IHolder();
            other.m_isInPlace = false;
        }
        else
        {
            m_untypedHolder.heap = other.m_untypedHolder.heap;
        }

        other.m_untypedHolder.heap = NULL;
    }

    void clearHolder()
    {
        if (m_isInPlace)
        {
            reinterpret_cast<IHolder*>(&m_untypedHolder.inPlace)->~IHolder();
            m_isInPlace = false;
            m_untypedHolder.heap = NULL;
        }
        else
        {
            if (m_untypedHolder.heap != NULL)
            {
                delete m_untypedHolder.heap;
                m_untypedHolder.heap = NULL;
            }
        }
    }

    bool hasHolder() const
    {
        return (m_isInPlace || m_untypedHolder.heap != NULL);
    }

    template <typename T>
    Holder<T>* createHolder()
    {
        if (hasHolder())
        {
            if (getUntypedHolder()->isType(TypeIdHolder::get<T>()))
                return getHolder<T>();

            clearHolder();
        }

        return createHolderImpl<T>(
                std::integral_constant<bool, sizeof(Holder<T>) <= sizeof(UntypedHolder::MaxInPlaceType)>());
    }

    template <typename T>
    Holder<T>* createHolderImpl(std::true_type)
    {
        Holder<T>* holder = new (&m_untypedHolder.inPlace) Holder<T>();
        m_isInPlace = true;
        return holder;
    }

    template <typename T>
    Holder<T>* createHolderImpl(std::false_type)
    {
        Holder<T>* holder = new Holder<T>();
        m_untypedHolder.heap = holder;
        return holder;
    }

    template <typename T>
    void checkType() const
    {
        if (!isType<T>())
            throw CppRuntimeException("Bad type in AnyHolder");
    }

    template <typename T>
    Holder<T>* getHolder()
    {
        return static_cast<Holder<T>*>(getUntypedHolder());
    }

    template <typename T>
    const Holder<T>* getHolder() const
    {
        return static_cast<const Holder<T>*>(getUntypedHolder());
    }

    IHolder* getUntypedHolder()
    {
        return (m_isInPlace) ? reinterpret_cast<IHolder*>(&m_untypedHolder.inPlace) : m_untypedHolder.heap;
    }

    const IHolder* getUntypedHolder() const
    {
        return (m_isInPlace) ? reinterpret_cast<const IHolder*>(&m_untypedHolder.inPlace) :
                m_untypedHolder.heap;
    }

    union UntypedHolder
    {
        // 2 * sizeof(void*) for T + sizeof(void*) for Holder's vptr
        typedef std::aligned_storage<3 * sizeof(void*), alignof(void*)>::type MaxInPlaceType;

        IHolder* heap;
        MaxInPlaceType inPlace;
    };

    UntypedHolder   m_untypedHolder;
    bool            m_isInPlace = false;
};

} // namespace zserio

#endif // ifndef ZSERIO_ANY_HOLDER_H_INC
