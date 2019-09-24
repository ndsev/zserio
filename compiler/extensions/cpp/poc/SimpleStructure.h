#ifndef SIMPLE_STRUCTURE_H
#define SIMPLE_STRUCTURE_H

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
#include <zserio/BitStream.h>
#include <zserio/Types.h>

class SimpleStructure
{
public:
    SimpleStructure() noexcept;

    template <typename ZSERIO_T_externalStructure,
            typename ::std::enable_if<!::std::is_same<typename ::std::decay<ZSERIO_T_externalStructure>::type, SimpleStructure>::value,
                    int>::type = 0>
    explicit SimpleStructure(
            uint8_t numberA_,
            ZSERIO_T_externalStructure&& externalStructure_,
            uint8_t numberC_) :
            m_numberA_(numberA_),
            m_externalStructure_(::std::forward<ZSERIO_T_externalStructure>(externalStructure_)),
            m_numberC_(numberC_)
    {
    }

    explicit SimpleStructure(::zserio::BitStreamReader& in);

    ~SimpleStructure() = default;

    SimpleStructure(const SimpleStructure&) = default;
    SimpleStructure& operator=(const SimpleStructure&) = default;

    SimpleStructure(SimpleStructure&&) = default;
    SimpleStructure& operator=(SimpleStructure&&) = default;

    uint8_t getNumberA() const;
    void setNumberA(uint8_t numberA_);

    ::zserio::BitStream& getExternalStructure();
    const ::zserio::BitStream& getExternalStructure() const;
    void setExternalStructure(const ::zserio::BitStream& externalStructure_);
    void setExternalStructure(::zserio::BitStream&& externalStructure_);

    uint8_t getNumberB() const;
    void setNumberB(uint8_t externalStructure_);

    uint8_t getNumberC() const;
    void setNumberC(uint8_t numberC_);

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t initializeOffsets(size_t bitPosition);

    bool operator==(const SimpleStructure& other) const;
    int hashCode() const;

    void read(::zserio::BitStreamReader& in);
    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction preWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS);

private:
    uint8_t readNumberA(::zserio::BitStreamReader& in);
    void readExternalStructure(::zserio::BitStreamReader& in);
    uint8_t readNumberC(::zserio::BitStreamReader& in);

    uint8_t m_numberA_;
    ::zserio::BitStream m_externalStructure_;
    uint8_t m_numberC_;
};

#endif // SIMPLE_STRUCTURE_H
