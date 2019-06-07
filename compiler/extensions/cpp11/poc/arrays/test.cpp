#include <iostream>

#include "Arrays.h"

/**
 * A test class that holds a 31-bit unsigned integer.
 */
class DummyObject
{
public:
    DummyObject() : m_value(0) {}
    DummyObject(uint32_t value) : m_value(value) {}
    explicit DummyObject(zserio::BitStreamReader& in) { read(in); }

    void initialize(uint32_t value)
    {
        m_value = value;
    }

    size_t bitSizeOf(size_t = 0) const
    {
        return sizeof(uint32_t) * 8 - 1 /* to make an unaligned type */;
    }

    size_t initializeOffsets(size_t bitPosition) const
    {
        return bitPosition + bitSizeOf(bitPosition);
    }

    int hashCode() const { return zserio::calcHashCode(zserio::HASH_SEED, m_value); }

    bool operator==(const DummyObject& other) const
    {
        return m_value == other.m_value;
    }

    DummyObject operator+(const DummyObject& other) const
    {
        return DummyObject(m_value + other.m_value);
    }

    DummyObject& operator+=(const DummyObject& other)
    {
        m_value += other.m_value;
        return *this;
    }

    uint32_t getValue() const
    {
        return m_value;
    }

    void write(zserio::BitStreamWriter& out, zserio::PreWriteAction)
    {
        out.writeBits(m_value, static_cast<uint8_t>(bitSizeOf()));
    }

    void read(zserio::BitStreamReader& in)
    {
        m_value = in.readBits(static_cast<uint8_t>(bitSizeOf()));
    }

private:
    uint32_t    m_value;
};

int main()
{
    using namespace zserio::arrays;

    // bit_field_array_traits
    std::vector<int8_t> bitArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    std::cout << "sum(bitArray): " << static_cast<int>(sum(bitArray)) << std::endl;
    std::cout << "hashCode(bitArray): " << hashCode(bitArray) << std::endl;
    std::cout << "bitSizeOf(bitArray): " << bitSizeOf<detail::bit_field_array_traits<5, int8_t>>(bitArray) << std::endl;
    std::cout << "initializeOffsets(bitArray): " << initializeOffsets<detail::bit_field_array_traits<5, int8_t>>(bitArray, 1) << std::endl << std::endl;

    // std_integer_array_traits
    std::vector<int8_t> intArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    std::cout << "sum(intArray): " << static_cast<int>(sum(intArray)) << std::endl;
    std::cout << "hashCode(intArray): " << hashCode(intArray) << std::endl;
    std::cout << "bitSizeOf(intArray): " << bitSizeOf<detail::std_int_array_traits<int8_t>>(intArray) << std::endl;
    std::cout << "initializeOffsets(intArray): " << initializeOffsets<detail::std_int_array_traits<int8_t>>(intArray, 1) << std::endl << std::endl;

    // object_array_traits
    std::vector<DummyObject> objectArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    std::cout << "sum(objectArray): " << sum(objectArray).getValue() << std::endl;
    std::cout << "hashCode(objectArray): " << hashCode(objectArray) << std::endl;
    std::cout << "bitSizeOf(objectArray): " << bitSizeOf<detail::object_array_traits<DummyObject>>(objectArray) << std::endl;
    std::cout << "initializeOffsets(objectArray): " << initializeOffsets<detail::object_array_traits<DummyObject>>(objectArray, 1) << std::endl << std::endl;

    return 0;
}
