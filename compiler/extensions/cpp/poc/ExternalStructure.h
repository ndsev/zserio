#ifndef EXTERNAL_STRUCTURE_H
#define EXTERNAL_STRUCTURE_H

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
#include <zserio/Types.h>

class ExternalStructure
{
public:
    ExternalStructure() noexcept;

    explicit ExternalStructure(
            uint8_t numberB_) :
            m_numberB_(numberB_)
    {
    }

    explicit ExternalStructure(::zserio::BitStreamReader& in);

    ~ExternalStructure() = default;

    ExternalStructure(const ExternalStructure&) = default;
    ExternalStructure& operator=(const ExternalStructure&) = default;

    ExternalStructure(ExternalStructure&&) = default;
    ExternalStructure& operator=(ExternalStructure&&) = default;

    uint8_t getNumberB() const;
    void setNumberB(uint8_t numberB_);

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t initializeOffsets(size_t bitPosition);

    bool operator==(const ExternalStructure& other) const;
    int hashCode() const;

    void read(::zserio::BitStreamReader& in);
    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction preWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS);

private:
    uint8_t readNumberB(::zserio::BitStreamReader& in);

    uint8_t m_numberB_;
};

#endif // EXTERNAL_STRUCTURE_H
