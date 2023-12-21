#include <array>
#include <vector>

#include "gtest/gtest.h"
#include "zserio/Span.h"

namespace zserio
{

TEST(SpanTest, EmptyConstructor)
{
    Span<int, 0> span;
    ASSERT_TRUE(span.empty());

    Span<int> spanDyn;
    ASSERT_TRUE(spanDyn.empty());
}

TEST(SpanTest, PtrCntConstructor)
{
    std::vector<int> vec = {0, 13, 42, 666};
    Span<int> span(vec.data(), vec.size());
    ASSERT_EQ(vec.size(), span.size());
    ASSERT_EQ(vec.data(), span.data());

    Span<int, 4> spanStatic(vec.data(), vec.size());
    ASSERT_EQ(vec.size(), spanStatic.size());
    ASSERT_EQ(vec.data(), spanStatic.data());
}

TEST(SpanTest, PtrPtrConstructor)
{
    std::vector<int> vec = {0, 13, 42, 666};
    Span<int> span(vec.data(), vec.data() + vec.size());
    ASSERT_EQ(vec.size(), span.size());
    ASSERT_EQ(vec.data(), span.data());

    Span<int, 4> spanStatic(vec.data(), vec.data() + vec.size());
    ASSERT_EQ(vec.size(), spanStatic.size());
    ASSERT_EQ(vec.data(), spanStatic.data());
}

TEST(SpanTest, ArrayConstructor)
{
    int arr[] = {0, 13, 42};
    Span<int> span(arr);
    ASSERT_EQ(3, span.size());
    ASSERT_EQ(&arr[0], span.data());

    Span<int, 3> spanStatic(arr);
    ASSERT_EQ(3, spanStatic.size());
    ASSERT_EQ(&arr[0], spanStatic.data());
}

TEST(SpanTest, StdArrayConstructor)
{
    std::array<int, 3> arr = {0, 13, 42};
    Span<int> span(arr);
    ASSERT_EQ(arr.size(), span.size());
    ASSERT_EQ(arr.data(), span.data());

    Span<int, 3> spanStatic(arr);
    ASSERT_EQ(arr.size(), spanStatic.size());
    ASSERT_EQ(arr.data(), spanStatic.data());
}

TEST(SpanTest, ConstStdArrayConstructor)
{
    const std::array<int, 3> arr = {0, 13, 42};
    Span<const int> span(arr);
    ASSERT_EQ(arr.size(), span.size());
    ASSERT_EQ(arr.data(), span.data());

    Span<const int, 3> spanStatic(arr);
    ASSERT_EQ(arr.size(), spanStatic.size());
    ASSERT_EQ(arr.data(), spanStatic.data());
}

TEST(SpanTest, VectorConstructor)
{
    std::vector<int> vec = {0, 13, 42};
    Span<int> span(vec);
    ASSERT_EQ(vec.size(), span.size());
    ASSERT_EQ(vec.data(), span.data());
}

TEST(SpanTest, ConstVectorConstructor)
{
    const std::vector<int> vec = {0, 13, 42};
    Span<const int> span(vec);
    ASSERT_EQ(vec.size(), span.size());
    ASSERT_EQ(vec.data(), span.data());
}

TEST(SpanTest, SpanConstructor)
{
    std::vector<int> vec = {0, 13, 42};
    Span<int> span(vec);
    ASSERT_EQ(vec.size(), span.size());
    ASSERT_EQ(vec.data(), span.data());

    Span<const int> spanConst(span);
    ASSERT_EQ(span.size(), spanConst.size());
    ASSERT_EQ(span.data(), spanConst.data());

    Span<int, 3> spanStatic(vec.data(), vec.size());
    ASSERT_EQ(vec.size(), spanStatic.size());
    ASSERT_EQ(vec.data(), spanStatic.data());

    Span<const int, 3> constSpanStatic(spanStatic);
    ASSERT_EQ(spanStatic.size(), constSpanStatic.size());
    ASSERT_EQ(spanStatic.data(), constSpanStatic.data());
}

TEST(SpanTest, BeginEnd)
{
    std::vector<int> vec = {0, 13, 42};
    Span<int> span(vec);

    auto itVec = vec.begin();
    for (auto it = span.begin(); it != span.end(); ++it, ++itVec)
    {
        ASSERT_EQ(*itVec, *it);
    }

    Span<int, 3> spanStatic(vec.data(), 3);
    itVec = vec.begin();
    for (auto it = spanStatic.begin(); it != spanStatic.end(); ++it, ++itVec)
    {
        ASSERT_EQ(*itVec, *it);
    }
}

TEST(SpanTest, RbeginRend)
{
    std::vector<int> vec = {0, 13, 42};
    Span<int> span(vec);

    auto itVec = vec.rbegin();
    for (auto it = span.rbegin(); it != span.rend(); ++it, ++itVec)
    {
        ASSERT_EQ(*itVec, *it);
    }

    Span<int, 3> spanStatic(vec.data(), 3);
    itVec = vec.rbegin();
    for (auto it = spanStatic.rbegin(); it != spanStatic.rend(); ++it, ++itVec)
    {
        ASSERT_EQ(*itVec, *it);
    }
}

TEST(SpanTest, Front)
{
    std::vector<int> vec = {13, 42};
    Span<int> span(vec);
    ASSERT_EQ(vec.front(), span.front());

    Span<int, 2> spanStatic(vec.data(), 2);
    ASSERT_EQ(vec.front(), spanStatic.front());
}

TEST(SpanTest, Back)
{
    std::vector<int> vec = {13, 42};
    Span<int> span(vec);
    ASSERT_EQ(vec.back(), span.back());

    Span<int, 2> spanStatic(vec.data(), 2);
    ASSERT_EQ(vec.back(), spanStatic.back());
}

TEST(SpanTest, ArrayAccess)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);

    for (size_t i = 0; i < span.size(); ++i)
    {
        ASSERT_EQ(vec[i], span[i]);
    }

    Span<int, 3> spanStatic(vec.data(), 3);

    for (size_t i = 0; i < spanStatic.size(); ++i)
    {
        ASSERT_EQ(vec[i], spanStatic[i]);
    }
}

TEST(SpanTest, Data)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    ASSERT_EQ(vec.data(), span.data());

    Span<int, 3> spanStatic(vec.data(), 3);
    ASSERT_EQ(vec.data(), spanStatic.data());
}

TEST(SpanTest, Size)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    ASSERT_EQ(vec.size(), span.size());

    Span<int, 3> spanStatic(vec.data(), 3);
    ASSERT_EQ(vec.size(), spanStatic.size());
}

TEST(SpanTest, SizeBytes)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    ASSERT_EQ(vec.size() * sizeof(int), span.size_bytes());

    Span<int, 3> spanStatic(vec.data(), 3);
    ASSERT_EQ(vec.size() * sizeof(int), spanStatic.size_bytes());
}

TEST(SpanTest, Empty)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    Span<int> emptySpan;
    ASSERT_FALSE(span.empty());
    ASSERT_TRUE(emptySpan.empty());

    Span<int, 3> spanStatic(vec.data(), 3);
    Span<int, 0> emptySpanStatic;
    ASSERT_FALSE(spanStatic.empty());
    ASSERT_TRUE(emptySpanStatic.empty());
}

TEST(SpanTest, staticFirst)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    Span<int, 2> spanFirst = span.first<2>();
    ASSERT_EQ(2, spanFirst.size());
    ASSERT_TRUE(std::equal(span.begin(), span.end() - 1, spanFirst.begin()));

    Span<int, 3> spanStatic(vec.data(), 3);
    Span<int, 2> spanFirstStatic = spanStatic.first<2>();
    ASSERT_EQ(2, spanFirstStatic.size());
    ASSERT_TRUE(std::equal(spanStatic.begin(), spanStatic.end() - 1, spanFirstStatic.begin()));
}

TEST(SpanTest, first)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    Span<int> spanFirst = span.first(2);
    ASSERT_EQ(2, spanFirst.size());
    ASSERT_TRUE(std::equal(span.begin(), span.end() - 1, spanFirst.begin()));

    Span<int, 3> spanStatic(vec.data(), 3);
    Span<int> spanFirstStatic = spanStatic.first(2);
    ASSERT_EQ(2, spanFirstStatic.size());
    ASSERT_TRUE(std::equal(spanStatic.begin(), spanStatic.end() - 1, spanFirstStatic.begin()));
}

TEST(SpanTest, staticLast)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    Span<int, 2> spanLast = span.last<2>();
    ASSERT_EQ(2, spanLast.size());
    ASSERT_TRUE(std::equal(span.begin() + 1, span.end(), spanLast.begin()));

    Span<int, 3> spanStatic(vec.data(), 3);
    Span<int, 2> spanLastStatic = spanStatic.last<2>();
    ASSERT_EQ(2, spanLastStatic.size());
    ASSERT_TRUE(std::equal(spanStatic.begin() + 1, spanStatic.end(), spanLastStatic.begin()));
}

TEST(SpanTest, last)
{
    std::vector<int> vec = {13, 42, 666};
    Span<int> span(vec);
    Span<int> spanLast = span.last(2);
    ASSERT_EQ(2, spanLast.size());
    ASSERT_TRUE(std::equal(span.begin() + 1, span.end(), spanLast.begin()));

    Span<int, 3> spanStatic(vec.data(), 3);
    Span<int> spanLastStatic = spanStatic.last(2);
    ASSERT_EQ(2, spanLastStatic.size());
    ASSERT_TRUE(std::equal(spanStatic.begin() + 1, spanStatic.end(), spanLastStatic.begin()));
}

TEST(SpanTest, staticSubspan)
{
    std::vector<int> vec = {0, 13, 42, 666, 13};
    Span<int> span(vec);
    Span<int> spanSub1 = span.subspan<1>();
    ASSERT_EQ(4, spanSub1.size());
    ASSERT_TRUE(std::equal(span.begin() + 1, span.begin() + 5, spanSub1.begin()));

    Span<int> spanSub2 = span.subspan<1, 3>();
    ASSERT_EQ(3, spanSub2.size());
    ASSERT_TRUE(std::equal(span.begin() + 1, span.begin() + 4, spanSub2.begin()));

    Span<int, 5> spanStatic(vec.data(), 3);
    Span<int> spanSubStatic1 = spanStatic.subspan<1>();
    ASSERT_EQ(4, spanSubStatic1.size());
    ASSERT_TRUE(std::equal(spanStatic.begin() + 1, spanStatic.begin() + 5, spanSubStatic1.begin()));

    Span<int, 3> spanSubStatic2 = spanStatic.subspan<1, 3>();
    ASSERT_EQ(3, spanSubStatic2.size());
    ASSERT_TRUE(std::equal(spanStatic.begin() + 1, spanStatic.begin() + 4, spanSubStatic2.begin()));
}

TEST(SpanTest, subspan)
{
    std::vector<int> vec = {0, 13, 42, 666, 13};
    Span<int> span(vec);
    Span<int> spanSubDyn1 = span.subspan(1);
    ASSERT_EQ(4, spanSubDyn1.size());
    ASSERT_TRUE(std::equal(span.begin() + 1, span.begin() + 5, spanSubDyn1.begin()));

    Span<int> spanSubDyn2 = span.subspan(1, 3);
    ASSERT_EQ(3, spanSubDyn2.size());
    ASSERT_TRUE(std::equal(span.begin() + 1, span.begin() + 4, spanSubDyn2.begin()));

    Span<int, 5> spanStatic(vec.data(), 3);
    Span<int> spanSubStatic1 = spanStatic.subspan(1);
    ASSERT_EQ(4, spanSubStatic1.size());
    ASSERT_TRUE(std::equal(spanStatic.begin() + 1, spanStatic.begin() + 5, spanSubStatic1.begin()));

    Span<int> spanSubStatic2 = spanStatic.subspan(1, 3);
    ASSERT_EQ(3, spanSubStatic2.size());
    ASSERT_TRUE(std::equal(spanStatic.begin() + 1, spanStatic.begin() + 4, spanSubStatic2.begin()));
}

} // namespace zserio
