#include "zserio/BuiltInOperators.h"

#include "gtest/gtest.h"

namespace zserio
{

namespace builtin
{

namespace
{

class DummyBitmask
{
public:
    typedef uint8_t underlying_type;

    enum class Values : underlying_type
    {
        READ = 1U,
        WRITE = 2U,
        CREATE = 1U | 2U
    };

    constexpr DummyBitmask(Values value) noexcept :
            m_value(static_cast<underlying_type>(value))
    {}

    constexpr explicit DummyBitmask(underlying_type value) noexcept :
            m_value(value)
    {}

    constexpr underlying_type getValue() const
    {
        return m_value;
    }

private:
    underlying_type m_value;
};

inline bool operator==(const DummyBitmask& lhs, const DummyBitmask& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

inline DummyBitmask operator|(DummyBitmask::Values lhs, DummyBitmask::Values rhs)
{
    return DummyBitmask(static_cast<DummyBitmask::underlying_type>(lhs) |
            static_cast<DummyBitmask::underlying_type>(rhs));
}


inline DummyBitmask operator&(DummyBitmask::Values lhs, DummyBitmask::Values rhs)
{
    return DummyBitmask(static_cast<DummyBitmask::underlying_type>(lhs) &
            static_cast<DummyBitmask::underlying_type>(rhs));
}

inline DummyBitmask operator&(const DummyBitmask& lhs, const DummyBitmask& rhs)
{
    return DummyBitmask(lhs.getValue() & rhs.getValue());
}

} // namespace

TEST(BuiltInOperatorsTest, isSet)
{
    // randomly mix bitmask instances with DummyBitmask::Values enum values to check that all variants work
    ASSERT_TRUE(isSet(DummyBitmask(DummyBitmask::Values::READ), DummyBitmask::Values::READ));
    ASSERT_TRUE(isSet(DummyBitmask::Values::CREATE, DummyBitmask::Values::READ));
    ASSERT_TRUE(isSet(DummyBitmask::Values::CREATE, DummyBitmask(DummyBitmask::Values::WRITE)));
    ASSERT_TRUE(isSet(DummyBitmask::Values::CREATE, DummyBitmask::Values::CREATE));
    ASSERT_TRUE(isSet(DummyBitmask::Values::CREATE, DummyBitmask::Values::READ | DummyBitmask::Values::WRITE));
    ASSERT_FALSE(isSet(DummyBitmask(DummyBitmask::Values::READ), DummyBitmask(DummyBitmask::Values::WRITE)));
    ASSERT_FALSE(isSet(DummyBitmask::Values::READ, DummyBitmask::Values::CREATE));
}

TEST(BuiltInOperatorsTest, numBits)
{
    EXPECT_EQ(0, numBits(0));
    EXPECT_EQ(1, numBits(1));
    EXPECT_EQ(1, numBits(2));
    EXPECT_EQ(2, numBits(3));
    EXPECT_EQ(2, numBits(4));
    EXPECT_EQ(3, numBits(5));
    EXPECT_EQ(3, numBits(6));
    EXPECT_EQ(3, numBits(7));
    EXPECT_EQ(3, numBits(8));
    EXPECT_EQ(4, numBits(16));
    EXPECT_EQ(5, numBits(32));
    EXPECT_EQ(6, numBits(64));
    EXPECT_EQ(7, numBits(128));
    EXPECT_EQ(8, numBits(256));
    EXPECT_EQ(9, numBits(512));
    EXPECT_EQ(10, numBits(1024));
    EXPECT_EQ(11, numBits(2048));
    EXPECT_EQ(12, numBits(4096));
    EXPECT_EQ(13, numBits(8192));
    EXPECT_EQ(14, numBits(16384));
    EXPECT_EQ(15, numBits(32768));
    EXPECT_EQ(16, numBits(65536));
    EXPECT_EQ(24, numBits(UINT64_C(1) << 24U));
    EXPECT_EQ(25, numBits((UINT64_C(1) << 24U) + 1));
    EXPECT_EQ(32, numBits(UINT64_C(1) << 32U));
    EXPECT_EQ(33, numBits((UINT64_C(1) << 32U) + 1));
    EXPECT_EQ(63, numBits(UINT64_C(1) << 63U));
    EXPECT_EQ(64, numBits((UINT64_C(1) << 63U) + 1));
}

} // namespace builtin

} // namespace zserio
