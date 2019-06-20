#ifndef ZSERIO_CONTAINER_H_INC
#define ZSERIO_CONTAINER_H_INC

#include <cstddef>
#include <stdexcept>
#include <algorithm>
#include <iterator>

namespace zserio
{

template <typename ELEMENT_TYPE>
class Container
{
public:
    // typedefs
    typedef ELEMENT_TYPE* iterator;
    typedef const ELEMENT_TYPE* const_iterator;

    // constructors
    Container() : m_data(NULL), m_size(0), m_capacity(0)
    {
    }

    explicit Container(size_t size) : m_data(NULL), m_size(0), m_capacity(0)
    {
        resize(size);
    }

    // copy constructor
    Container(const Container<ELEMENT_TYPE>& other) : m_data(NULL), m_size(0), m_capacity(0)
    {
        assign(other.begin(), other.end());
    }

    // destructor
    ~Container()
    {
        if (m_data != NULL)
        {
            destroy_tail(begin());
            delete[] reinterpret_cast<unsigned char*>(m_data);
            m_data = NULL;
            m_capacity = 0;
        }
    }

    // assignment operator
    const Container<ELEMENT_TYPE>& operator=(const Container<ELEMENT_TYPE>& other)
    {
        if (this != &other)
            assign(other.begin(), other.end());

        return *this;
    }

    // comparison operator
    bool operator==(const Container<ELEMENT_TYPE>& other) const
    {
        if (this != &other)
        {
            if (m_size != other.size())
                return false;

            for (size_t i = 0; i < m_size; ++i)
                if (!(m_data[i] == other.m_data[i]))
                    return false;
        }

        return true;
    }

    // STL vector like interface
    template <class InputIterator>
    void assign(InputIterator first, InputIterator last)
    {
        clear();
        const size_t newSize = std::distance(first, last);
        reserve(newSize);
        for (InputIterator it = first; it != last; ++it)
            push_back_impl(*it);
    }

    void assign(size_t size, const ELEMENT_TYPE& value)
    {
        clear();
        const size_t newSize = size;
        reserve(newSize);
        for (size_t i = 0; i < size; ++i)
            push_back_impl(value);
    }

    ELEMENT_TYPE& at(size_t index)
    {
        if (index >= m_size)
            throw std::range_error("Container::at(): Index is out of range!");

        return m_data[index];
    }

    const ELEMENT_TYPE& at(size_t index) const
    {
        if (index >= m_size)
            throw std::range_error("Container::at(): Index is out of range!");

        return m_data[index];
    }

    ELEMENT_TYPE& back()
    {
        if (empty())
            throw std::range_error("Container::back(): Called on empty vector!");

        return m_data[m_size - 1];
    }

    const ELEMENT_TYPE& back() const
    {
        if (empty())
            throw std::range_error("Container::back(): Called on empty vector!");

        return m_data[m_size - 1];
    }

    iterator begin()
    {
        return m_data;
    }

    const_iterator begin() const
    {
        return m_data;
    }

    size_t capacity() const
    {
        return m_capacity;
    }

    void clear()
    {
        if (m_data != NULL)
            destroy_tail(begin());
    }

    bool empty() const
    {
        return (m_size == 0);
    }

    iterator end()
    {
        return m_data + m_size;
    }

    const_iterator end() const
    {
        return m_data + m_size;
    }

    iterator erase(iterator position)
    {
        if (position < m_data || position >= m_data + m_size)
            throw std::range_error("Container::erase(): Iterator is out of range!");
        const size_t index = position - m_data;

        Container<ELEMENT_TYPE> newContainer;
        newContainer.reserve(m_size - 1);
        for (const_iterator it = begin(); it != position; ++it)
            newContainer.push_back_impl(*it);
        for (const_iterator it = position + 1; it != end(); ++it)
            newContainer.push_back_impl(*it);
        swap(newContainer);

        return m_data + index;
    }

    iterator erase(iterator first, iterator last)
    {
        if (first < m_data || first >= m_data + m_size || last < m_data || last > m_data + m_size)
            throw std::range_error("Container::erase(): Iterator is out of range!");
        const size_t indexFirst = first - m_data;

        if (last > first)
        {
            Container<ELEMENT_TYPE> newContainer;
            newContainer.reserve(m_size - (last - first));
            for (const_iterator it = begin(); it != first; ++it)
                newContainer.push_back_impl(*it);
            for (const_iterator it = last; it != end(); ++it)
                newContainer.push_back_impl(*it);
            swap(newContainer);
        }

        return m_data + indexFirst;
    }

    ELEMENT_TYPE& front()
    {
        if (empty())
            throw std::range_error("Container:front(): Called on empty vector!");

        return m_data[0];
    }

    const ELEMENT_TYPE& front() const
    {
        if (empty())
            throw std::range_error("Container:front(): Called on empty vector!");

        return m_data[0];
    }

    iterator insert(iterator position, const ELEMENT_TYPE& value)
    {
        if (position < m_data || position >= m_data + m_size)
            throw std::range_error("Container::insert(): Iterator is out of range!");
        const size_t index = position - m_data;

        Container<ELEMENT_TYPE> newContainer;
        newContainer.reserve(m_size + 1);
        for (const_iterator it = begin(); it != position; ++it)
            newContainer.push_back_impl(*it);
        newContainer.push_back_impl(value);
        for (const_iterator it = position; it != end(); ++it)
            newContainer.push_back_impl(*it);
        swap(newContainer);

        return m_data + index;
    }

    void insert(iterator position, size_t size, const ELEMENT_TYPE& value)
    {
        if (position < m_data || position >= m_data + m_size)
            throw std::range_error("Container::insert(): Iterator is out of range!");

        Container<ELEMENT_TYPE> newContainer;
        newContainer.reserve(m_size + size);
        for (const_iterator it = begin(); it != position; ++it)
            newContainer.push_back_impl(*it);
        for (size_t i = 0; i < size; ++i)
            newContainer.push_back_impl(value);
        for (const_iterator it = position; it != end(); ++it)
            newContainer.push_back_impl(*it);
        swap(newContainer);
    }

    template <class InputIterator>
    void insert(iterator position, InputIterator first, InputIterator last)
    {
        if (position < m_data || position >= m_data + m_size)
            throw std::range_error("Container::insert(): Iterator is out of range!");

        Container<ELEMENT_TYPE> newContainer;
        const size_t newSize = m_size + std::distance(first, last);
        newContainer.reserve(newSize);
        for (const_iterator it = begin(); it != position; ++it)
            newContainer.push_back_impl(*it);
        for (InputIterator it = first; it != last; ++it)
            newContainer.push_back_impl(*it);
        for (const_iterator it = position; it != end(); ++it)
            newContainer.push_back_impl(*it);
        swap(newContainer);
    }

    ELEMENT_TYPE& operator[](size_t index)
    {
        return m_data[index];
    }

    const ELEMENT_TYPE& operator[](size_t index) const
    {
        return m_data[index];
    }

    void pop_back()
    {
        erase(end() - 1);
    }

    void push_back(const ELEMENT_TYPE& value)
    {
        push_back_reserve();
        push_back_impl(value);
    }

    void resize(size_t size)
    {
        if (m_size > size)
        {
            destroy_tail(m_data + size);
        }
        else if (m_size < size)
        {
            reserve(size);
            while (m_size < size)
                push_back_impl(ELEMENT_TYPE());
        }
    }

    void reserve(size_t size)
    {
        if (m_data == NULL)
        {
            unsigned char* data = new unsigned char[sizeof(ELEMENT_TYPE) * size];
            m_data = reinterpret_cast<ELEMENT_TYPE*>(data);
            m_capacity = size;
        }
        else
        {
            if (m_capacity < size)
            {
                Container<ELEMENT_TYPE> newContainer;
                newContainer.reserve(size);
                for (const_iterator it = begin(); it < end(); ++it)
                    newContainer.push_back_impl(*it);
                swap(newContainer);
            }
        }
    }

    size_t size() const
    {
        return m_size;
    }

    void swap(Container<ELEMENT_TYPE>& other)
    {
        std::swap(m_data, other.m_data);
        std::swap(m_size, other.m_size);
        std::swap(m_capacity, other.m_capacity);
    }

    // new interface to avoid copying in push_back method
    void* get_next_storage()
    {
        push_back_reserve();

        return end();
    }

    void commit_storage(void* storage)
    {
        if (storage == NULL || storage != end())
            throw std::runtime_error("Container::commitStorage(): Not valid storage!");

        m_size++;
    }

private:
    void push_back_reserve()
    {
        if (m_size + 1 > m_capacity)
            reserve((m_size + 1) * 2);
    }

    void push_back_impl(const ELEMENT_TYPE& value)
    {
        unsigned char* storage = reinterpret_cast<unsigned char*>(end());
        new (storage) ELEMENT_TYPE(value);
        m_size++;
    }

    void destroy_tail(iterator first)
    {
        for (iterator it = end() - 1; it >= first; it--)
        {
            it->~ELEMENT_TYPE();
            m_size--;
        }
    }

    ELEMENT_TYPE*   m_data;
    size_t          m_size;
    size_t          m_capacity;
};

} // namespace zserio

#endif // ZSERIO_CONTAINER_H_INC
