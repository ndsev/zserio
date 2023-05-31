#include <string>
#include <array>

#include "gtest/gtest.h"

#include "zserio/StringView.h"

namespace zserio
{

TEST(StringViewTest, ptrConstructor)
{
    const char* str = "everything";
    StringView sv(str);
    ASSERT_EQ(str, sv.data());
    ASSERT_EQ(10, sv.size());
}

TEST(StringViewTest, ptrCountConstructor)
{
    const char* str = "everything";
    StringView sv(str, 9);
    ASSERT_EQ(str, sv.data());
    ASSERT_EQ(9, sv.size());
}

TEST(StringViewTest, stringConstructor)
{
    std::string str = "everything";
    StringView sv(str);
    ASSERT_EQ(str.data(), sv.data());
    ASSERT_EQ(str.size(), sv.size());
}

TEST(StringViewTest, beginEnd)
{
    std::string str = "everything";
    StringView sv(str);

    auto itStr = str.begin();
    for (auto it = sv.begin(); it != sv.end(); ++it, ++itStr)
    {
        ASSERT_NE(str.end(), itStr);
        ASSERT_EQ(*itStr, *it);
    }

    itStr = str.begin();
    for (auto it = sv.cbegin(); it != sv.cend(); ++it, ++itStr)
    {
        ASSERT_NE(str.end(), itStr);
        ASSERT_EQ(*itStr, *it);
    }
}

TEST(StringViewTest, rBeginRend)
{
    std::string str = "everything";
    StringView sv(str);

    auto itStr = str.rbegin();
    for (auto it = sv.rbegin(); it != sv.rend(); ++it, ++itStr)
    {
        ASSERT_NE(str.rend(), itStr);
        ASSERT_EQ(*itStr, *it);
    }

    itStr = str.rbegin();
    for (auto it = sv.crbegin(); it != sv.crend(); ++it, ++itStr)
    {
        ASSERT_NE(str.rend(), itStr);
        ASSERT_EQ(*itStr, *it);
    }
}

TEST(StringViewTest, arrayAccess)
{
    const char* str = "everything";
    StringView sv(str);

    for (size_t i = 0; i < sv.size(); ++i)
    {
        ASSERT_EQ(str[i], sv[i]);
    }
}

TEST(StringViewTest, at)
{
    const char* str = "everything";
    StringView sv(str);

    for (size_t i = 0; i < sv.size(); ++i)
    {
        ASSERT_EQ(str[i], sv.at(i));
    }

    ASSERT_THROW(sv.at(11), CppRuntimeException);
}

TEST(StringViewTest, front)
{
    const char* str = "nothing";
    StringView sv(str);
    ASSERT_EQ('n', sv.front());
}

TEST(StringViewTest, back)
{
    const char* str = "beam";
    StringView sv(str);
    ASSERT_EQ('m', sv.back());
}

TEST(StringViewTest, data)
{
    const char* str = "Everything";
    StringView sv(str);
    ASSERT_EQ(str, sv.data());
}

TEST(StringViewTest, size)
{
    const char* str = "Everything";
    StringView sv(str);
    ASSERT_EQ(10, sv.size());
}

TEST(StringViewTest, length)
{
    const char* str = "Everything";
    StringView sv(str);
    ASSERT_EQ(10, sv.length());
}

TEST(StringViewTest, maxSize)
{
    const char* str = "Everything";
    StringView sv(str);
    ASSERT_LE(1U << 16U, sv.max_size());
}

TEST(StringViewTest, empty)
{
    const char* str = "Everything";
    StringView sv(str);
    StringView svEmpty;
    ASSERT_FALSE(sv.empty());
    ASSERT_TRUE(svEmpty.empty());
}

TEST(StringViewTest, removePrefix)
{
    const char* str = "Everything";
    StringView sv(str);

    sv.remove_prefix(2);
    ASSERT_EQ(8, sv.size());
    ASSERT_EQ(str + 2, sv.data());

    sv.remove_prefix(8);
    ASSERT_TRUE(sv.empty());
}

TEST(StringViewTest, removeSuffix)
{
    const char* str = "Everything";
    StringView sv(str);

    sv.remove_suffix(2);
    ASSERT_EQ(8, sv.size());
    ASSERT_EQ(str, sv.data());

    sv.remove_suffix(8);
    ASSERT_TRUE(sv.empty());
}

TEST(StringViewTest, swap)
{
    const char* str1 = "everything";
    const char* str2 = "another string";
    StringView sv1(str1);
    StringView sv2(str2);
    sv1.swap(sv2);
    ASSERT_EQ(14, sv1.size());
    ASSERT_EQ(10, sv2.size());
    ASSERT_EQ(str2, sv1.data());
    ASSERT_EQ(str1, sv2.data());
}

TEST(StringViewTest, copy)
{
    const char* str = "everything";
    StringView sv(str);
    std::array<char, 6> buffer = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
    sv.copy(buffer.data(), buffer.size() - 1, 1);
    ASSERT_EQ('v', buffer[0]);
    ASSERT_EQ('e', buffer[1]);
    ASSERT_EQ('r', buffer[2]);
    ASSERT_EQ('y', buffer[3]);
    ASSERT_EQ('t', buffer[4]);
    ASSERT_EQ(0x55, buffer[5]);
    ASSERT_THROW(sv.copy(buffer.data(), buffer.size() - 1, 11), CppRuntimeException);
}

TEST(StringViewTest, substr)
{
    const char* str = "everything";
    StringView sv(str);
    StringView subView = sv.substr(2, 5);
    ASSERT_EQ(5, subView.size());
    ASSERT_EQ(str + 2, subView.data());

    ASSERT_THROW(sv.substr(11), CppRuntimeException);
}

TEST(StringViewTest, compareStringView)
{
    StringView aaa("aaa");
    StringView bbb("bbb");
    StringView aaaa("aaaa");
    StringView aa("aa");

    ASSERT_LT(0, bbb.compare(aaa));
    ASSERT_GT(0, aaa.compare(bbb));
    ASSERT_EQ(0, aaa.compare(aaa));
    ASSERT_LT(0, aaa.compare(aa));
    ASSERT_GT(0, aaa.compare(aaaa));
}

TEST(StringViewTest, compareStringViewSub)
{
    StringView aaa("aaa");
    StringView saaas("saaas");
    StringView bbb("bbb");
    StringView sbbbs("sbbbs");
    StringView aaaa("aaaa");
    StringView aa("aa");

    ASSERT_LT(0, sbbbs.compare(1, 3, aaa));
    ASSERT_GT(0, saaas.compare(1, 3, bbb));
    ASSERT_EQ(0, saaas.compare(1, 3, aaa));
    ASSERT_LT(0, saaas.compare(1, 3, aa));
    ASSERT_GT(0, saaas.compare(1, 3, aaaa));
}

TEST(StringViewTest, compareStringViewSubSub)
{
    StringView aaa("aaa");
    StringView saaas("saaas");
    StringView bbb("bbb");
    StringView sbbbs("sbbbs");
    StringView saaaas("saaaas");

    ASSERT_LT(0, sbbbs.compare(1, 3, saaas, 1, 3));
    ASSERT_GT(0, saaas.compare(1, 3, sbbbs, 1, 3));
    ASSERT_EQ(0, saaas.compare(1, 3, saaas, 1, 3));
    ASSERT_LT(0, saaas.compare(1, 3, saaas, 1, 2));
    ASSERT_GT(0, saaas.compare(1, 3, saaaas, 1, 4));
}

TEST(StringViewTest, compareChar)
{
    StringView aaa("aaa");
    StringView bbb("bbb");

    ASSERT_LT(0, bbb.compare("aaa"));
    ASSERT_GT(0, aaa.compare("bbb"));
    ASSERT_EQ(0, aaa.compare("aaa"));
    ASSERT_LT(0, aaa.compare("aa"));
    ASSERT_GT(0, aaa.compare("aaaa"));
}

TEST(StringViewTest, compareCharSub)
{
    StringView saaas("saaas");
    StringView sbbbs("sbbbs");

    ASSERT_LT(0, sbbbs.compare(1, 3, "aaa"));
    ASSERT_GT(0, saaas.compare(1, 3, "bbb"));
    ASSERT_EQ(0, saaas.compare(1, 3, "aaa"));
    ASSERT_LT(0, saaas.compare(1, 3, "aa"));
    ASSERT_GT(0, saaas.compare(1, 3, "aaaa"));
}

TEST(StringViewTest, compareCharSubSub)
{
    StringView saaas("saaas");
    StringView sbbbs("sbbbs");

    ASSERT_LT(0, sbbbs.compare(1, 3, "aaas", 3));
    ASSERT_GT(0, saaas.compare(1, 3, "bbbs", 3));
    ASSERT_EQ(0, saaas.compare(1, 3, "aaas", 3));
    ASSERT_LT(0, saaas.compare(1, 3, "aaas", 2));
    ASSERT_GT(0, saaas.compare(1, 3, "aaaas", 4));
}

TEST(StringViewTest, find)
{
    StringView str("karkarbanatek");

    StringView search1("karbanatekkarbanatek");
    ASSERT_EQ(StringView::npos, str.find(search1, 0));
    ASSERT_EQ(StringView::npos, str.find(search1, 9999));

    StringView search2("");
    ASSERT_EQ(0, str.find(search2, 0));

    StringView search3("kar");
    ASSERT_EQ(0, str.find(search3, 0));
    ASSERT_EQ(3, str.find(search3, 1));
    ASSERT_EQ(3, str.find(search3, 3));
    ASSERT_EQ(StringView::npos, str.find(search3, 4));

    StringView search4("ban");
    ASSERT_EQ(6, str.find(search4, 0));
    ASSERT_EQ(6, str.find(search4, 6));
    ASSERT_EQ(StringView::npos, str.find(search4, 7));

    StringView search5("tek");
    ASSERT_EQ(10, str.find(search5, 0));
    ASSERT_EQ(10, str.find(search5, 10));
    ASSERT_EQ(StringView::npos, str.find(search5, 11));

    StringView search6("tekk");
    ASSERT_EQ(StringView::npos, str.find(search6, 0));
    ASSERT_EQ(StringView::npos, str.find(search6, 10));
    ASSERT_EQ(StringView::npos, str.find(search6, 11));

    StringView search7("ana");
    ASSERT_EQ(7, str.find(search7, 0));
    ASSERT_EQ(7, str.find(search7, 7));
    ASSERT_EQ(StringView::npos, str.find(search7, 8));
}

TEST(StringViewTest, findChar)
{
    StringView str("karkarbanatek");

    StringView search2("");
    ASSERT_EQ(0, str.find(search2, 0));

    ASSERT_EQ(0, str.find('k', 0));
    ASSERT_EQ(3, str.find('k', 1));
    ASSERT_EQ(12, str.find('k', 5));
    ASSERT_EQ(StringView::npos, str.find('m', 0));
}

TEST(StringViewTest, findCharStrLen)
{
    StringView str("karkarbanatek");

    ASSERT_EQ(StringView::npos, str.find("karbanatekkarbanatek", 0, 21));
    ASSERT_EQ(StringView::npos, str.find("karbanatekkarbanatek", 9999, 21));

    ASSERT_EQ(0, str.find("", 0, 0));

    ASSERT_EQ(0, str.find("kar", 0, 3));
    ASSERT_EQ(3, str.find("kar", 1, 3));
    ASSERT_EQ(3, str.find("kar", 3, 3));
    ASSERT_EQ(StringView::npos, str.find("kar", 4, 3));

    ASSERT_EQ(6, str.find("ban", 0, 3));
    ASSERT_EQ(6, str.find("ban", 6, 3));
    ASSERT_EQ(StringView::npos, str.find("ban", 7, 3));

    ASSERT_EQ(10, str.find("tek", 0, 3));
    ASSERT_EQ(10, str.find("tek", 10, 3));
    ASSERT_EQ(StringView::npos, str.find("tek", 11, 3));

    ASSERT_EQ(StringView::npos, str.find("tekk", 0, 4));
    ASSERT_EQ(StringView::npos, str.find("tekk", 10, 4));
    ASSERT_EQ(StringView::npos, str.find("tekk", 11, 4));
}

TEST(StringViewTest, findCharStr)
{
    StringView str("karkarbanatek");

    ASSERT_EQ(StringView::npos, str.find("karbanatekkarbanatek", 0));
    ASSERT_EQ(StringView::npos, str.find("karbanatekkarbanatek", 9999));

    ASSERT_EQ(0, str.find("", 0));

    ASSERT_EQ(0, str.find("kar", 0));
    ASSERT_EQ(3, str.find("kar", 1));
    ASSERT_EQ(3, str.find("kar", 3));
    ASSERT_EQ(StringView::npos, str.find("kar", 4));

    ASSERT_EQ(6, str.find("ban", 0));
    ASSERT_EQ(6, str.find("ban", 6));
    ASSERT_EQ(StringView::npos, str.find("ban", 7));

    ASSERT_EQ(10, str.find("tek", 0));
    ASSERT_EQ(10, str.find("tek", 10));
    ASSERT_EQ(StringView::npos, str.find("tek", 11));

    ASSERT_EQ(StringView::npos, str.find("tekk", 0));
    ASSERT_EQ(StringView::npos, str.find("tekk", 10));
    ASSERT_EQ(StringView::npos, str.find("tekk", 11));
}

TEST(StringViewTest, rfind)
{
    StringView str("karkarbanatek");

    StringView search1("karbanatekkarbanatek");
    ASSERT_EQ(StringView::npos, str.rfind(search1, 0));
    ASSERT_EQ(StringView::npos, str.rfind(search1, 9999));

    StringView search2("");
    ASSERT_EQ(12, str.rfind(search2, 12));

    StringView search3("kar");
    ASSERT_EQ(3, str.rfind(search3, 12));
    ASSERT_EQ(3, str.rfind(search3, 3));
    ASSERT_EQ(0, str.rfind(search3, 2));
    ASSERT_EQ(0, str.rfind(search3, 0));

    StringView search4("ban");
    ASSERT_EQ(6, str.rfind(search4, 12));
    ASSERT_EQ(6, str.rfind(search4, 6));
    ASSERT_EQ(StringView::npos, str.rfind(search4, 5));

    StringView search5("tek");
    ASSERT_EQ(10, str.rfind(search5, 12));
    ASSERT_EQ(10, str.rfind(search5, 10));
    ASSERT_EQ(StringView::npos, str.rfind(search5, 9));

    StringView search6("tekk");
    ASSERT_EQ(StringView::npos, str.rfind(search6, 12));
    ASSERT_EQ(StringView::npos, str.rfind(search6, 10));
    ASSERT_EQ(StringView::npos, str.rfind(search6, 6));
}

TEST(StringViewTest, rFindChar)
{
    StringView str("karkarbanatek");

    StringView search2("");
    ASSERT_EQ(10, str.rfind(search2, 10));

    ASSERT_EQ(12, str.rfind('k', 12));
    ASSERT_EQ(3, str.rfind('k', 11));
    ASSERT_EQ(0, str.rfind('k', 2));

    ASSERT_EQ(StringView::npos, str.rfind('m', 12));
}

TEST(StringViewTest, rFindCharStr)
{
    StringView str("karkarbanatek");

    ASSERT_EQ(StringView::npos, str.rfind("karbanatekkarbanatek", 0));
    ASSERT_EQ(StringView::npos, str.rfind("karbanatekkarbanatek", 9999));

    ASSERT_EQ(12, str.rfind("", 12));

    ASSERT_EQ(3, str.rfind("kar", 12));
    ASSERT_EQ(3, str.rfind("kar", 3));
    ASSERT_EQ(0, str.rfind("kar", 2));
    ASSERT_EQ(0, str.rfind("kar", 0));

    ASSERT_EQ(6, str.rfind("ban", 12));
    ASSERT_EQ(6, str.rfind("ban", 6));
    ASSERT_EQ(StringView::npos, str.rfind("ban", 5));

    ASSERT_EQ(10, str.rfind("tek", 12));
    ASSERT_EQ(10, str.rfind("tek", 10));
    ASSERT_EQ(StringView::npos, str.rfind("tek", 9));

    ASSERT_EQ(StringView::npos, str.rfind("tekk", 12));
    ASSERT_EQ(StringView::npos, str.rfind("tekk", 10));
    ASSERT_EQ(StringView::npos, str.rfind("tekk", 6));
}

TEST(StringViewTest, rFindCharStrLen)
{
    StringView str("karkarbanatek");

    ASSERT_EQ(StringView::npos, str.rfind("karbanatekkarbanatek", 0, 20));
    ASSERT_EQ(StringView::npos, str.rfind("karbanatekkarbanatek", 9999, 20));

    ASSERT_EQ(12, str.rfind("", 12, 0));

    ASSERT_EQ(3, str.rfind("kar", 12, 3));
    ASSERT_EQ(3, str.rfind("kar", 3, 3));
    ASSERT_EQ(0, str.rfind("kar", 2, 3));
    ASSERT_EQ(0, str.rfind("kar", 0, 3));

    ASSERT_EQ(6, str.rfind("ban", 12, 3));
    ASSERT_EQ(6, str.rfind("ban", 6, 3));
    ASSERT_EQ(StringView::npos, str.rfind("ban", 5, 3));

    ASSERT_EQ(10, str.rfind("tek", 12, 3));
    ASSERT_EQ(10, str.rfind("tek", 10, 3));
    ASSERT_EQ(StringView::npos, str.rfind("tek", 9, 3));

    ASSERT_EQ(StringView::npos, str.rfind("tekk", 12, 4));
    ASSERT_EQ(StringView::npos, str.rfind("tekk", 10, 4));
    ASSERT_EQ(StringView::npos, str.rfind("tekk", 6, 4));
}

TEST(StringViewTest, findFirstOf)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_of(StringView(""), 0));
    ASSERT_EQ(StringView::npos, str.find_first_of(StringView("pqs"), 0));
    ASSERT_EQ(StringView::npos, str.find_first_of(StringView("kbt"), 11));

    ASSERT_EQ(0, str.find_first_of(StringView("kbt"), 0));
    ASSERT_EQ(3, str.find_first_of(StringView("kbt"), 1));
    ASSERT_EQ(3, str.find_first_of(StringView("kbt"), 3));
    ASSERT_EQ(7, str.find_first_of(StringView("kbt"), 4));
    ASSERT_EQ(7, str.find_first_of(StringView("kbt"), 7));
    ASSERT_EQ(9, str.find_first_of(StringView("kbt"), 8));
    ASSERT_EQ(9, str.find_first_of(StringView("kbt"), 9));
    ASSERT_EQ(StringView::npos, str.find_first_of(StringView("kbt"), 10));
}

TEST(StringViewTest, findFirstOfChar)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_of('s', 0));
    ASSERT_EQ(StringView::npos, str.find_first_of('k', 11));

    ASSERT_EQ(0, str.find_first_of('k', 0));
    ASSERT_EQ(3, str.find_first_of('b', 0));
    ASSERT_EQ(3, str.find_first_of('b', 3));
    ASSERT_EQ(StringView::npos, str.find_first_of('b', 4));
    ASSERT_EQ(StringView::npos, str.find_first_of('k', 10));
}

TEST(StringViewTest, findFirstOfCharStr)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_of("", 0));
    ASSERT_EQ(StringView::npos, str.find_first_of("pqs", 0));
    ASSERT_EQ(StringView::npos, str.find_first_of("kbt", 11));

    ASSERT_EQ(0, str.find_first_of("kbt", 0));
    ASSERT_EQ(3, str.find_first_of("kbt", 1));
    ASSERT_EQ(3, str.find_first_of("kbt", 3));
    ASSERT_EQ(7, str.find_first_of("kbt", 4));
    ASSERT_EQ(7, str.find_first_of("kbt", 7));
    ASSERT_EQ(9, str.find_first_of("kbt", 8));
    ASSERT_EQ(9, str.find_first_of("kbt", 9));
    ASSERT_EQ(StringView::npos, str.find_first_of("kbt", 10));
}

TEST(StringViewTest, findFirstOfCharStrLen)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_of("", 0, 0));
    ASSERT_EQ(StringView::npos, str.find_first_of("pqs", 0, 3));
    ASSERT_EQ(StringView::npos, str.find_first_of("kbt", 11, 3));

    ASSERT_EQ(0, str.find_first_of("kbt", 0, 3));
    ASSERT_EQ(3, str.find_first_of("kbt", 1, 3));
    ASSERT_EQ(3, str.find_first_of("kbt", 3, 3));
    ASSERT_EQ(7, str.find_first_of("kbt", 4, 3));
    ASSERT_EQ(7, str.find_first_of("kbt", 7, 3));
    ASSERT_EQ(9, str.find_first_of("kbt", 8, 3));
    ASSERT_EQ(9, str.find_first_of("kbt", 9, 3));
    ASSERT_EQ(StringView::npos, str.find_first_of("kbt", 10, 3));
}

TEST(StringViewTest, findLastOf)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_of(StringView(""), 10));
    ASSERT_EQ(StringView::npos, str.find_last_of(StringView("pqs"), 10));

    ASSERT_EQ(9, str.find_last_of(StringView("kbt"), 10));
    ASSERT_EQ(9, str.find_last_of(StringView("kbt"), 9));
    ASSERT_EQ(3, str.find_last_of(StringView("kbt"), 4));
    ASSERT_EQ(3, str.find_last_of(StringView("kbt"), 3));
    ASSERT_EQ(0, str.find_last_of(StringView("kbt"), 2));
    ASSERT_EQ(0, str.find_last_of(StringView("kbt"), 1));
    ASSERT_EQ(0, str.find_last_of(StringView("kbt"), 0));

    StringView empty("");
    ASSERT_EQ(StringView::npos, empty.find_last_of(StringView("text"), 0));
    ASSERT_EQ(StringView::npos, empty.find_last_of(StringView(""), 0));
}

TEST(StringViewTest, findLastOfChar)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_of('s', 10));

    ASSERT_EQ(8, str.find_last_of('e', 10));
    ASSERT_EQ(8, str.find_last_of('e', 9));
    ASSERT_EQ(8, str.find_last_of('e', 8));
    ASSERT_EQ(StringView::npos, str.find_last_of('e', 7));
}

TEST(StringViewTest, findLastOfCharStrLen)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_of("", 10, 0));
    ASSERT_EQ(StringView::npos, str.find_last_of("pqs", 10, 3));

    ASSERT_EQ(9, str.find_last_of("kbt", 10, 3));
    ASSERT_EQ(9, str.find_last_of("kbt", 9, 3));
    ASSERT_EQ(3, str.find_last_of("kbt", 4, 3));
    ASSERT_EQ(3, str.find_last_of("kbt", 3, 3));
    ASSERT_EQ(0, str.find_last_of("kbt", 2, 3));
    ASSERT_EQ(0, str.find_last_of("kbt", 1, 3));
    ASSERT_EQ(0, str.find_last_of("kbt", 0, 3));
}

TEST(StringViewTest, findLastOfCharStr)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_of("", 10));
    ASSERT_EQ(StringView::npos, str.find_last_of("pqs", 10));

    ASSERT_EQ(9, str.find_last_of("kbt", 10));
    ASSERT_EQ(9, str.find_last_of("kbt", 9));
    ASSERT_EQ(3, str.find_last_of("kbt", 4));
    ASSERT_EQ(3, str.find_last_of("kbt", 3));
    ASSERT_EQ(0, str.find_last_of("kbt", 2));
    ASSERT_EQ(0, str.find_last_of("kbt", 1));
    ASSERT_EQ(0, str.find_last_of("kbt", 0));
}

TEST(StringViewTest, findFirstNotOf)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_not_of(StringView(""), 0));
    ASSERT_EQ(StringView::npos, str.find_first_not_of(StringView("karbntex"), 0));
    ASSERT_EQ(StringView::npos, str.find_first_not_of(StringView("pqs"), 11));

    ASSERT_EQ(0, str.find_first_not_of(StringView("arb"), 0));
    ASSERT_EQ(5, str.find_first_not_of(StringView("arb"), 1));
    ASSERT_EQ(5, str.find_first_not_of(StringView("arb"), 5));
    ASSERT_EQ(7, str.find_first_not_of(StringView("arb"), 6));
    ASSERT_EQ(7, str.find_first_not_of(StringView("arb"), 7));
    ASSERT_EQ(StringView::npos, str.find_first_not_of(StringView("tekx"), 8));
}

TEST(StringViewTest, findFirstNotOfChar)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_not_of('m', 11));
    ASSERT_EQ(0, str.find_first_not_of('m', 0));
    ASSERT_EQ(0, str.find_first_not_of('a', 0));
    ASSERT_EQ(2, str.find_first_not_of('a', 1));
    ASSERT_EQ(7, str.find_first_not_of('a', 7));
}

TEST(StringViewTest, findFirstNotOfCharStrLen)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_not_of("", 0, 0));
    ASSERT_EQ(StringView::npos, str.find_first_not_of("karbntex", 0, 8));
    ASSERT_EQ(StringView::npos, str.find_first_not_of("pqs", 11, 3));

    ASSERT_EQ(0, str.find_first_not_of("arb", 0, 3));
    ASSERT_EQ(5, str.find_first_not_of("arb", 1, 3));
    ASSERT_EQ(5, str.find_first_not_of("arb", 5, 3));
    ASSERT_EQ(7, str.find_first_not_of("arb", 6, 3));
    ASSERT_EQ(7, str.find_first_not_of("arb", 7, 3));
    ASSERT_EQ(StringView::npos, str.find_first_not_of("tekx", 8, 4));
}

TEST(StringViewTest, findFirstNotOfCharStr)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_first_not_of("", 0));
    ASSERT_EQ(StringView::npos, str.find_first_not_of("karbntex", 0));
    ASSERT_EQ(StringView::npos, str.find_first_not_of("pqs", 11));

    ASSERT_EQ(0, str.find_first_not_of("arb", 0));
    ASSERT_EQ(5, str.find_first_not_of("arb", 1));
    ASSERT_EQ(5, str.find_first_not_of("arb", 5));
    ASSERT_EQ(7, str.find_first_not_of("arb", 6));
    ASSERT_EQ(7, str.find_first_not_of("arb", 7));
    ASSERT_EQ(StringView::npos, str.find_first_not_of("tekx", 8));
}

TEST(StringViewTest, findLastNotOf)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_not_of(StringView(""), 10));
    ASSERT_EQ(StringView::npos, str.find_last_not_of(StringView("karbntex"), 10));

    ASSERT_EQ(10, str.find_last_not_of(StringView("arb"), 10));
    ASSERT_EQ(9, str.find_last_not_of(StringView("arb"), 9));
    ASSERT_EQ(5, str.find_last_not_of(StringView("arb"), 6));
    ASSERT_EQ(5, str.find_last_not_of(StringView("arb"), 5));
    ASSERT_EQ(0, str.find_last_not_of(StringView("arb"), 4));
    ASSERT_EQ(0, str.find_last_not_of(StringView("arb"), 0));

    StringView empty("");
    ASSERT_EQ(StringView::npos, empty.find_last_not_of(StringView("text"), 0));
    ASSERT_EQ(StringView::npos, empty.find_last_not_of(StringView(""), 0));
}

TEST(StringViewTest, findLastNotOfChar)
{
    StringView str("karbanatekx");

    ASSERT_EQ(10, str.find_last_not_of('m', 10));
    ASSERT_EQ(8, str.find_last_not_of('k', 9));
    ASSERT_EQ(7, str.find_last_not_of('e', 8));
    ASSERT_EQ(3, str.find_last_not_of('e', 3));
    ASSERT_EQ(2, str.find_last_not_of('b', 3));
    ASSERT_EQ(StringView::npos, str.find_last_not_of('k', 0));
}

TEST(StringViewTest, findLastNotOfCharStrLen)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_not_of("", 10, 0));
    ASSERT_EQ(StringView::npos, str.find_last_not_of("karbntex", 10, 8));

    ASSERT_EQ(10, str.find_last_not_of("arb", 10, 3));
    ASSERT_EQ(9, str.find_last_not_of("arb", 9, 3));
    ASSERT_EQ(5, str.find_last_not_of("arb", 6, 3));
    ASSERT_EQ(5, str.find_last_not_of("arb", 5, 3));
    ASSERT_EQ(0, str.find_last_not_of("arb", 4, 3));
    ASSERT_EQ(0, str.find_last_not_of("arb", 0, 3));
}

TEST(StringViewTest, findLastNotOfCharStr)
{
    StringView str("karbanatekx");

    ASSERT_EQ(StringView::npos, str.find_last_not_of("", 10));
    ASSERT_EQ(StringView::npos, str.find_last_not_of("karbntex", 10));

    ASSERT_EQ(10, str.find_last_not_of("arb", 10));
    ASSERT_EQ(9, str.find_last_not_of("arb", 9));
    ASSERT_EQ(5, str.find_last_not_of("arb", 6));
    ASSERT_EQ(5, str.find_last_not_of("arb", 5));
    ASSERT_EQ(0, str.find_last_not_of("arb", 4));
    ASSERT_EQ(0, str.find_last_not_of("arb", 0));
}

TEST(StringViewTest, comparisonOperator)
{
    StringView str1("text");
    StringView str2("text");
    StringView str3("texts");
    ASSERT_TRUE(str1 == str2);
    ASSERT_TRUE(str1 != str3);
    ASSERT_TRUE(str1 < str3);
    ASSERT_TRUE(str1 <= str3);
    ASSERT_TRUE(str3 > str2);
    ASSERT_TRUE(str3 >= str2);
}

TEST(StringViewTest, makeStringView)
{
    StringView str1 = makeStringView("text");
    char textWithoutTermZero[] = {'t', 'e', 'x', 't'};
    StringView str2 = makeStringView(textWithoutTermZero);
    ASSERT_EQ(str1, str2);

    StringView empty = makeStringView("");
    ASSERT_EQ(""_sv, empty);
}

TEST(StringViewTest, stringViewToString)
{
    StringView str("text");
    const std::string convertedStr = stringViewToString(str);
    ASSERT_EQ("text", convertedStr);
}

TEST(StringViewTest, appendStringViewToString)
{
    StringView str("text");
    std::string convertedStr("another ");
    convertedStr += str;
    ASSERT_EQ("another text", convertedStr);
}

TEST(StringViewTest, toString)
{
    StringView str("text");
    const std::string convertedStr = toString(str);
    ASSERT_EQ("text", convertedStr);
}

TEST(StringViewTest, cppRuntimeExceptionOperator)
{
    CppRuntimeException exception = CppRuntimeException() << "test"_sv;
    ASSERT_STREQ("test", exception.what());
}

TEST(StringViewTest, literal)
{
    StringView str1("text");
    StringView str2 = "text"_sv;
    ASSERT_EQ(str2, str1);
}

} // namespace zserio
