#ifndef ZSERIO_ANY_HOLDER_H_INC
#define ZSERIO_ANY_HOLDER_H_INC

#include <cstddef>

#include "CppRuntimeException.h"
#include "HashCodeUtil.h"
#include "OptionalHolder.h"
#include "AlignedStorage.h"
#include "Types.h"

namespace zserio
{

class AnyHolder
{
public:
    AnyHolder() : m_isInPlace(false)
    {
        m_untypedHolder.heap = NULL;
    }

    ~AnyHolder()
    {
        clearHolder();
    }

    AnyHolder(const AnyHolder& other) : m_isInPlace(false)
    {
        m_untypedHolder.heap = NULL;
        copy(other);
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

    bool operator==(const AnyHolder& other) const
    {
        if (this != &other)
        {
            const bool hasLeftHolder = hasHolder();
            const bool hasRightHolder = other.hasHolder();
            if (hasLeftHolder && hasRightHolder)
                return getUntypedHolder()->compare(*(other.getUntypedHolder()));

            return !hasLeftHolder && !hasRightHolder;
        }

        return true;
    }

    template<typename T> void* getResetStorage()
    {
        return createHolder<T>()->getResetStorage();
    }

    template<typename T> void reset(T* value = NULL)
    {
        createHolder<T>()->reset(value);
    }

    template<typename T> void set(const T& value)
    {
        createHolder<T>()->set(value);
    }

    template<typename T> T& get()
    {
        checkType<T>();
        return getHolder<T>()->get();
    }

    template<typename T> const T& get() const
    {
        checkType<T>();
        return getHolder<T>()->get();
    }

    template<typename T> bool isType() const
    {
        return hasHolder() && getUntypedHolder()->isType(TypeIdHolder::get<T>());
    }

    bool isSet() const
    {
        return hasHolder() && getUntypedHolder()->isSet();
    }

    int hashCode() const
    {
        return (hasHolder()) ? getUntypedHolder()->hashCode() : calcHashCode(HASH_SEED, 0);
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
        virtual bool compare(const IHolder& other) const = 0;
        virtual bool isType(TypeIdHolder::type_id typeId) const = 0;
        virtual int hashCode() const = 0;
    };

    template <typename T>
    class Holder : public IHolder
    {
    public:
        Holder() {}

        void* getResetStorage()
        {
            return m_typedHolder.getResetStorage();
        }

        void reset(T* value = NULL)
        {
            m_typedHolder.reset(value);
        }

        void set(const T& value)
        {
            m_typedHolder.reset(new (m_typedHolder.getResetStorage()) T(value));
        }

        T& get()
        {
            return m_typedHolder.get();
        }

        const T& get() const
        {
            return m_typedHolder.get();
        }

        virtual bool isSet() const
        {
            return m_typedHolder.isSet();
        }

        virtual IHolder* clone(void* storage) const
        {
            Holder<T>* holder = (storage == NULL) ? new Holder<T>() : new (storage) Holder<T>();
            holder->m_typedHolder = m_typedHolder;

            return holder;
        }

        virtual bool compare(const IHolder& other) const
        {
            return m_typedHolder == (static_cast<const Holder<T>*>(&other))->m_typedHolder;
        }

        virtual bool isType(TypeIdHolder::type_id typeId) const
        {
            return TypeIdHolder::get<T>() == typeId;
        }

        virtual int hashCode() const
        {
            return m_typedHolder.hashCode();
        }

    private:
        InPlaceOptionalHolder<T>    m_typedHolder;
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

    template<typename T> Holder<T>* createHolder()
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

    template<typename T> void checkType() const
    {
        if (!isType<T>())
            throw CppRuntimeException("Bad type in AnyHolder");
    }

    template<typename T> Holder<T>* getHolder()
    {
        return static_cast<Holder<T>*>(getUntypedHolder());
    }

    template<typename T> const Holder<T>* getHolder() const
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
        typedef unsigned char MaxInPlaceType[sizeof(void*) + 3 * sizeof(uint64_t) - sizeof(bool)];

        IHolder*                                heap;
        AlignedStorage<MaxInPlaceType>::type    inPlace;
    };

    UntypedHolder   m_untypedHolder;
    bool            m_isInPlace;
};

} // namespace zserio

#endif // ifndef ZSERIO_ANY_HOLDER_H_INC
