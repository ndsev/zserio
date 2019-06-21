#ifndef ZSERIO_ANY_HOLDER_H_INC
#define ZSERIO_ANY_HOLDER_H_INC

#include <cstddef>
#include <type_traits>

#include "CppRuntimeException.h"
#include "OptionalHolder.h"
#include "Types.h"

namespace zserio
{

class AnyHolder
{
public:
    AnyHolder()
    {
        m_untypedHolder.heap = NULL;
    }

    ~AnyHolder()
    {
        clearHolder();
    }

    // TODO: implement move constructor and move semantic!

    AnyHolder(const AnyHolder& other)
    {
        m_untypedHolder.heap = NULL;
        copy(other);
    }

    template <typename T,
            typename std::enable_if<!std::is_same<typename std::decay<T>::type, AnyHolder>::value, int>::type = 0>
    explicit AnyHolder(T&& value)
    {
        m_untypedHolder.heap = NULL;
        set(std::forward<T>(value));
    }

    AnyHolder& operator=(const AnyHolder& other)
    {
        if (this != &other)
        {
            clearHolder();
            copy(other);
        }

        return *this;
    }

    template <typename T,
            typename std::enable_if<!std::is_same<typename std::decay<T>::type, AnyHolder>::value, int>::type = 0>
    AnyHolder& operator=(T&& value)
    {
        set(std::forward<T>(value));
    }

    void reset()
    {
        clearHolder();
    }

    template <typename T>
    void set(T&& value)
    {
        createHolder<typename std::decay<T>::type>()->set(std::forward<T>(value));
    }

    template <typename T>
    T& get()
    {
        checkType<T>();
        return getHolder<T>()->get();
    }

    template <typename T>
    const T& get() const
    {
        checkType<T>();
        return getHolder<T>()->get();
    }

    template <typename T>
    bool isType() const
    {
        return hasHolder() && getUntypedHolder()->isType(TypeIdHolder::get<T>());
    }

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

    void clearHolder()
    {
        if (m_isInPlace)
        {
            reinterpret_cast<IHolder*>(&m_untypedHolder.inPlace)->~IHolder();
            m_isInPlace = false;
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

        Holder<T>* holder;
        if (sizeof(Holder<T>) <= sizeof(UntypedHolder::MaxInPlaceType))
        {
            holder = new (&m_untypedHolder.inPlace) Holder<T>();
            m_isInPlace = true;
        }
        else
        {
            holder = new Holder<T>();
            m_untypedHolder.heap = holder;
        }

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
        typedef std::aligned_storage<2 * sizeof(void*), alignof(void*)>::type MaxInPlaceType;

        IHolder* heap;
        MaxInPlaceType inPlace;
    };

    UntypedHolder   m_untypedHolder;
    bool            m_isInPlace = false;
};

} // namespace zserio

#endif // ifndef ZSERIO_ANY_HOLDER_H_INC
