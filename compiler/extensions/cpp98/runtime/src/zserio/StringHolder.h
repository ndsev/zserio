#ifndef ZSERIO_STRING_HOLDER_H_INC
#define ZSERIO_STRING_HOLDER_H_INC

#include <cstddef>
#include <string>

namespace zserio
{

class StringHolder
{
public:
    StringHolder() : m_hasStringObject(false)
    {
        m_holder.stringLiteral = NULL;
    }

    StringHolder(const std::string& stringObject) : m_hasStringObject(true)
    {
        m_holder.stringObject = new std::string(stringObject);
    }

    template<int N> StringHolder(const char (&stringLiteral)[N]) : m_hasStringObject(false)
    {
        m_holder.stringLiteral = stringLiteral;
    }

    StringHolder(const StringHolder& other) : m_hasStringObject(false)
    {
        copy(other);
    }

    StringHolder& operator=(const StringHolder& other)
    {
        if (this != &other)
            copy(other);

        return *this;
    }

    ~StringHolder()
    {
        reset();
    }

    const char* get() const
    {
        if (m_hasStringObject)
            return m_holder.stringObject->c_str();

        return m_holder.stringLiteral;
    }

    bool operator==(const StringHolder& other) const
    {
        if (this != &other)
        {
            if (other.m_hasStringObject)
            {
                if (m_hasStringObject)
                    return *(m_holder.stringObject) == *(other.m_holder.stringObject);
                else
                    return std::string(m_holder.stringLiteral) == *(other.m_holder.stringObject);
            }
            else
            {
                if (m_hasStringObject)
                    return *(m_holder.stringObject) == std::string(other.m_holder.stringLiteral);
                else
                    return std::string(m_holder.stringLiteral) == std::string(other.m_holder.stringLiteral);
            }
        }

        return true;
    }

    bool operator==(const std::string& other) const
    {
        if (m_hasStringObject)
            return *(m_holder.stringObject) == other;

        return std::string(m_holder.stringLiteral) == other;
    }

    bool operator==(const char *other) const
    {
        if (m_hasStringObject)
            return *(m_holder.stringObject) == std::string(other);

        return std::string(m_holder.stringLiteral) == std::string(other);
    }

private:
    template<int N> StringHolder(char (&nonConstString)[N])
    {
    }

    void copy(const StringHolder& other)
    {
        if (other.m_hasStringObject)
        {
            if (m_hasStringObject)
            {
                *(m_holder.stringObject) = *(other.m_holder.stringObject);
            }
            else
            {
                m_holder.stringObject = new std::string(*(other.m_holder.stringObject));
                m_hasStringObject = true;
            }
        }
        else
        {
            reset(other.m_holder.stringLiteral);
        }
    }

    void reset(const char* stringLiteral = NULL)
    {
        if (m_hasStringObject)
        {
            delete m_holder.stringObject;
            m_hasStringObject = false;
        }

        m_holder.stringLiteral = stringLiteral;
    }

private:
    union StringPointerHolder
    {
        const char*     stringLiteral;
        std::string*    stringObject;
    };

    StringPointerHolder m_holder;
    bool                m_hasStringObject;
};

} // namespace zserio

#endif // ifndef ZSERIO_STRING_HOLDER_H_INC
