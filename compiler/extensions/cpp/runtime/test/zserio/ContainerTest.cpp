#include "zserio/Container.h"

#include "gtest/gtest.h"

namespace zserio
{

class Element
{
public:
    Element()
    {
        init(0);
    }

    explicit Element(int number)
    {
        init(number);
    }

    // copy constuctor
    Element(const Element& other)
    {
        init(*other.m_pointer);
    }

    // assignment operator
    Element& operator=(const Element& other)
    {
        if (this != &other)
            init(*other.m_pointer);

        return *this;
    }

    // comparison operator
    bool operator==(const Element& other) const
    {
        if (this != &other && m_number != other.m_number)
            return false;

        return true;
    }

    int getNumber() const
    {
        return *m_pointer;
    }

    void check() const
    {
        for (size_t i = 0; i < NumDummyElements; ++i)
            EXPECT_EQ(m_number, m_dummyContainer[i]);
    }

private:
    void init(int number)
    {
        for (size_t i = 0; i < NumDummyElements; ++i)
            m_dummyContainer[i] = number;
        m_number = number;
        m_pointer = &m_number;
    }

    static const size_t NumDummyElements = 8;

    int     m_dummyContainer[NumDummyElements];
    int     m_number;
    int*    m_pointer;
};

template <typename T>
class ContainerHolder
{
public:
    explicit ContainerHolder(Container<T>* container = NULL) : m_container(container) {}
    ~ContainerHolder()
    {
        reset();
    }

    Container<T>* operator->() const
    {
        return m_container;
    }

    Container<T>& operator*() const
    {
        return *m_container;
    }

    void reset(Container<T>* container = NULL)
    {
        if (m_container != NULL)
            delete m_container;

        m_container = container;
    }

private:
    ContainerHolder(const ContainerHolder& other);
    const ContainerHolder& operator=(const ContainerHolder& other);

    Container<T>* m_container;
};

class ContainerTest : public ::testing::Test
{
protected:
    void checkContainer(const Container<Element>& container)
    {
        for (Container<Element>::const_iterator it = container.begin(); it != container.end(); ++it)
            it->check();
    }
};

TEST_F(ContainerTest, EmptyConstructor)
{
    Container<Element> container;
    EXPECT_EQ(0, container.size());
    EXPECT_EQ(0, container.capacity());
}

TEST_F(ContainerTest, SizeConstructor)
{
    const size_t numElements = 2;
    Container<Element> container(numElements);
    EXPECT_EQ(numElements, container.size());
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(Element(), container.at(i));
}

TEST_F(ContainerTest, CopyConstructor)
{
    const size_t numElements = 10;
    ContainerHolder<Element> origContainer(new Container<Element>());
    for (size_t i = 0; i < numElements; ++i)
        origContainer->push_back(Element(static_cast<int>(i + 1)));

    Container<Element> copiedContainer(*origContainer);
    origContainer.reset();

    EXPECT_EQ(numElements, copiedContainer.size());
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(i + 1, copiedContainer.at(i).getNumber());
    checkContainer(copiedContainer);
}

TEST_F(ContainerTest, AssignmentOperator)
{
    const size_t numElements = 10;
    ContainerHolder<Element> origContainer(new Container<Element>(numElements));
    for (size_t i = 0; i < numElements; ++i)
        (*origContainer)[i] = Element(static_cast<int>(i + 1));

    Container<Element> assignedContainer = *origContainer;
    origContainer.reset();

    EXPECT_EQ(numElements, assignedContainer.size());
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(i + 1, assignedContainer.at(i).getNumber());
    checkContainer(assignedContainer);
}

TEST_F(ContainerTest, ComparisonOperator)
{
    Container<Element> container1;
    Container<Element> container2;
    EXPECT_TRUE(container1 == container2);

    container1.push_back(Element(1));
    EXPECT_FALSE(container1 == container2);
}

TEST_F(ContainerTest, Assign)
{
    ContainerHolder<Element> origContainer(new Container<Element>());
    const size_t numElements = 10;
    for (size_t i = 0; i < numElements; ++i)
        origContainer->push_back(Element(static_cast<int>(i + 1)));

    Container<Element> container;
    container.assign(origContainer->begin() + 1, origContainer->end() - 1);
    origContainer.reset();

    EXPECT_EQ(numElements - 2, container.size());
    for (size_t i = 0; i < numElements - 2; ++i)
        EXPECT_EQ(i + 2, container.at(i).getNumber());
    checkContainer(container);

    container.assign(numElements, Element(10));
    EXPECT_EQ(numElements, container.size());
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(10, container.at(i).getNumber());
    checkContainer(container);
}

TEST_F(ContainerTest, At)
{
    const size_t numElements = 2;
    Container<Element> container(numElements);
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(Element(), container.at(i));
    EXPECT_THROW(container.at(numElements), std::range_error);

    const Container<Element>& constContainer = container;
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(Element(), constContainer.at(i));
    EXPECT_THROW(constContainer.at(numElements), std::range_error);
}

TEST_F(ContainerTest, Back)
{
    const int number = 2;
    Container<Element> container;
    EXPECT_THROW(container.back(), std::range_error);
    container.push_back(Element(number - 1));
    container.push_back(Element(number));
    EXPECT_EQ(number, container.back().getNumber());

    const Container<Element>& constContainer = container;
    EXPECT_EQ(number, constContainer.back().getNumber());
}

TEST_F(ContainerTest, Begin)
{
    const size_t numElements = 2;
    Container<Element> container;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(i + 1));
    EXPECT_EQ(1, container.begin()->getNumber());

    const Container<Element>& constContainer = container;
    EXPECT_EQ(1, constContainer.begin()->getNumber());
}

TEST_F(ContainerTest, Capacity)
{
    Container<Element> container;
    EXPECT_EQ(0, container.capacity());

    container.push_back(Element());
    EXPECT_EQ(2, container.capacity());

    container.push_back(Element());
    EXPECT_EQ(2, container.capacity());
    container.push_back(Element());
    EXPECT_EQ(6, container.capacity());

    container.push_back(Element());
    container.push_back(Element());
    container.push_back(Element());
    EXPECT_EQ(6, container.capacity());
    container.push_back(Element());
    EXPECT_EQ(14, container.capacity());
}

TEST_F(ContainerTest, Clear)
{
    const size_t numElements = 10;
    Container<Element> container;
    EXPECT_EQ(0, container.size());
    EXPECT_EQ(0, container.capacity());
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(i + 1));

    container.clear();
    EXPECT_EQ(0, container.size());
    EXPECT_EQ(14, container.capacity());

    container.push_back(Element(numElements));
    EXPECT_EQ(numElements, container.back().getNumber());
    EXPECT_EQ(1, container.size());
    EXPECT_EQ(14, container.capacity());
}

TEST_F(ContainerTest, Empty)
{
    Container<Element> container;
    EXPECT_TRUE(container.empty());

    container.push_back(Element());
    EXPECT_FALSE(container.empty());

    container.clear();
    EXPECT_TRUE(container.empty());
}

TEST_F(ContainerTest, End)
{
    const size_t numElements = 2;
    Container<Element> container;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(i + 1));
    EXPECT_EQ(numElements, (container.end() - 1)->getNumber());

    const Container<Element>& constContainer = container;
    EXPECT_EQ(numElements, (constContainer.end() - 1)->getNumber());
}

TEST_F(ContainerTest, Erase)
{
    Container<Element> container;
    const size_t numElements = 10;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(static_cast<int>(i + 1)));

    EXPECT_THROW(container.erase(container.end()), std::range_error);
    const size_t erasedElementIndex = 2;
    Container<Element>::iterator it = container.erase(container.begin() + erasedElementIndex);
    EXPECT_EQ(erasedElementIndex + 2, it->getNumber());
    EXPECT_EQ(numElements - 1, container.size());
    for (size_t i = 0; i < numElements - 1; ++i)
    {
        const size_t expectedNumber = (i < erasedElementIndex) ? i + 1 : i + 2;
        EXPECT_EQ(expectedNumber, container[i].getNumber());
    }

    container.insert(container.begin() + erasedElementIndex, Element(erasedElementIndex + 1));
    EXPECT_THROW(container.erase(container.end()), std::range_error);
    const size_t erasedFirstIndex = 2;
    const size_t erasedLastIndex = 7;
    it = container.erase(container.begin() + erasedFirstIndex, container.begin() + erasedLastIndex);
    EXPECT_EQ(erasedLastIndex + 1, it->getNumber());
    const size_t expectedNumElements = numElements - (erasedLastIndex - erasedFirstIndex);
    EXPECT_EQ(expectedNumElements, container.size());
    for (size_t i = 0; i < expectedNumElements; ++i)
    {
        const size_t expectedNumber = (i < erasedFirstIndex) ? i + 1 :
            i - erasedFirstIndex + erasedLastIndex + 1;
        EXPECT_EQ(expectedNumber, container[i].getNumber());
    }
}

TEST_F(ContainerTest, Front)
{
    const int number = 1;
    Container<Element> container;
    EXPECT_THROW(container.front(), std::range_error);
    container.push_back(Element(number));
    container.push_back(Element(number + 1));
    EXPECT_EQ(number, container.front().getNumber());

    const Container<Element>& constContainer = container;
    EXPECT_EQ(number, constContainer.front().getNumber());
}

TEST_F(ContainerTest, Insert)
{
    Container<Element> container;
    const size_t numElements = 10;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(static_cast<int>(i + 1)));
    EXPECT_THROW(container.erase(container.end()), std::range_error);

    EXPECT_THROW(container.insert(container.end() + 1, Element(numElements)), std::range_error);
    const size_t insertedElementIndex = 2;
    Container<Element>::iterator it = container.insert(container.begin() + insertedElementIndex,
            Element(numElements));
    EXPECT_EQ(numElements, it->getNumber());
    EXPECT_EQ(numElements + 1, container.size());
    for (size_t i = 0; i < numElements + 1; ++i)
    {
        const size_t expectedNumber = (i < insertedElementIndex) ? i + 1 :
                                     ((i == insertedElementIndex) ? numElements : i);
        EXPECT_EQ(expectedNumber, container[i].getNumber());
    }

    container.erase(container.begin() + insertedElementIndex);
    const size_t numInsertedElements = 3;
    EXPECT_THROW(container.insert(container.end() + 1, numInsertedElements, Element(numElements)),
            std::range_error);
    container.insert(container.begin() + insertedElementIndex, numInsertedElements, Element(numElements));
    EXPECT_EQ(numElements + numInsertedElements, container.size());
    for (size_t i = 0; i < numElements + numInsertedElements; ++i)
    {
        const size_t expectedNumber = (i < insertedElementIndex) ? i + 1 :
            ((i >= insertedElementIndex && i < insertedElementIndex + numInsertedElements) ? numElements :
             i + 1 - numInsertedElements);
        EXPECT_EQ(expectedNumber, container[i].getNumber());
    }

    container.erase(container.begin() + insertedElementIndex,
                container.begin() + insertedElementIndex + numInsertedElements);
    Container<Element> insertedContainer;
    for (size_t i = 0; i < numInsertedElements; ++i)
        insertedContainer.push_back(Element(static_cast<int>(numElements)));
    EXPECT_THROW(container.insert(container.end() + 1, insertedContainer.begin(), insertedContainer.end()),
            std::range_error);
    container.insert(container.begin() + insertedElementIndex, insertedContainer.begin(),
            insertedContainer.end());
    EXPECT_EQ(numElements + numInsertedElements, container.size());
    for (size_t i = 0; i < numElements + numInsertedElements; ++i)
    {
        const size_t expectedNumber = (i < insertedElementIndex) ? i + 1 :
            ((i >= insertedElementIndex && i < insertedElementIndex + numInsertedElements) ? numElements :
             i + 1 - numInsertedElements);
        EXPECT_EQ(expectedNumber, container[i].getNumber());
    }
}

TEST_F(ContainerTest, ContainerOperator)
{
    const size_t numElements = 10;
    Container<Element> container;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(i + 1));

    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(Element(i + 1), container[i]);

    const Container<Element>& constContainer = container;
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(Element(i + 1), constContainer[i]);
}

TEST_F(ContainerTest, PopBack)
{
    const size_t numElements = 10;
    Container<Element> container;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(i + 1));

    for (size_t i = 0; i < numElements; ++i)
    {
        container.pop_back();
        EXPECT_EQ(numElements - i - 1, container.size());
    }
}

TEST_F(ContainerTest, PushBack)
{
    const size_t numElements = 10;
    Container<Element> container;
    for (size_t i = 0; i < numElements; ++i)
    {
        container.push_back(Element(i + 1));
        EXPECT_EQ(i + 1, container.size());
        EXPECT_EQ(i + 1, container[i].getNumber());
    }
}

TEST_F(ContainerTest, Resize)
{
    Container<Element> container;
    EXPECT_EQ(0, container.size());

    const size_t numElements = 10;
    container.resize(numElements);
    EXPECT_EQ(numElements, container.size());
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(Element(), container[i]);

    container.resize(0);
    EXPECT_EQ(0, container.size());
}

TEST_F(ContainerTest, Reserve)
{
    Container<Element> container;
    EXPECT_EQ(0, container.capacity());

    const size_t numElements = 10;
    container.reserve(numElements);
    EXPECT_EQ(numElements, container.capacity());
    EXPECT_EQ(0, container.size());

    container.reserve(0);
    EXPECT_EQ(numElements, container.capacity());
    EXPECT_EQ(0, container.size());
}

TEST_F(ContainerTest, Size)
{
    Container<Element> container;
    EXPECT_EQ(0, container.size());

    const size_t numElements = 10;
    for (size_t i = 0; i < numElements; ++i)
        container.push_back(Element(i + 1));
    EXPECT_EQ(numElements, container.size());

    container.clear();
    EXPECT_EQ(0, container.size());
}

TEST_F(ContainerTest, Swap)
{
    const size_t numElements = 10;
    Container<Element> container1;
    for (size_t i = 0; i < numElements; ++i)
        container1.push_back(Element(i + 1));

    Container<Element> container2;
    container1.swap(container2);

    EXPECT_EQ(0, container1.size());
    EXPECT_EQ(0, container1.capacity());
    EXPECT_EQ(numElements, container2.size());
    for (size_t i = 0; i < numElements; ++i)
        EXPECT_EQ(i + 1, container2[i].getNumber());
}

TEST_F(ContainerTest, GetNextStorage)
{
    Container<Element> container;
    void* storage = container.get_next_storage();
    EXPECT_EQ(container.end(), storage);
    EXPECT_EQ(0, container.size());
    EXPECT_EQ(2, container.capacity());
}

TEST_F(ContainerTest, CommitStorage)
{
    Container<Element> container;
    EXPECT_THROW(container.commit_storage(NULL), std::runtime_error);

    const size_t numElements = 10;
    for (size_t i = 0; i < numElements; ++i)
    {
        void* storage = container.get_next_storage();
        container.commit_storage(new (storage) Element(i + 1));
        EXPECT_EQ(i + 1, container.size());
        EXPECT_EQ(i + 1, container[i].getNumber());
    }
}

} // namespace zserio
