#include "zserio/AlignedStorage.h"
#include "zserio/Types.h"

#include "gtest/gtest.h"

namespace zserio
{

class AlignedStorageTest : public ::testing::Test
{
protected:
    template <typename T>
    struct UIntObject
    {
        T        uintValue;
        uint8_t  uint8Value;
        uint64_t uint64Value;
    };

    template <typename T>
    void UIntObjectTest()
    {
        struct Memory
        {
            char dummy;
            typename AlignedStorage<UIntObject<T> >::type storage;
        };

        Memory* memory = new Memory();
        UIntObject<T>* object = new (&memory->storage) UIntObject<T>();
        object->uintValue = static_cast<T>(-1);
        object->uint8Value = static_cast<uint8_t>(-1);
        object->uint64Value = static_cast<uint64_t>(-1);
        delete(memory);
    }
};

TEST_F(AlignedStorageTest, UInt8Object)
{
    UIntObjectTest<uint8_t>();
}

TEST_F(AlignedStorageTest, UInt16Object)
{
    UIntObjectTest<uint16_t>();
}

TEST_F(AlignedStorageTest, UInt32Object)
{
    UIntObjectTest<uint32_t>();
}

TEST_F(AlignedStorageTest, UInt64Object)
{
    UIntObjectTest<uint64_t>();
}

} // namespace zserio
