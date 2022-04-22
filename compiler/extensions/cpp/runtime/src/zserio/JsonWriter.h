#ifndef ZSERIO_JSON_WRITER_H_INC
#define ZSERIO_JSON_WRITER_H_INC

#include <ostream>
#include "zserio/IWalkObserver.h"
#include "zserio/OptionalHolder.h"

namespace zserio
{

/**
 * Walker observer which dumps zserio objects to JSON format.
 */
class JsonWriter : public IWalkObserver
{
public:
    static constexpr const char* DEFAULT_ITEM_SEPARATOR = ", ";
    static constexpr const char* DEFAULT_ITEM_SEPARATOR_WITH_INDENT = ",";
    static constexpr const char* DEFAULT_KEY_SEPARATOR = ": ";

    explicit JsonWriter(std::ostream& out);
    JsonWriter(std::ostream& out, uint8_t indent);
    JsonWriter(std::ostream& out, const std::string& indent);

    void setItemSeparator(const std::string& itemSeparator);
    void setKeySeparator(const std::string& keySeparator);

    virtual void beginRoot(const IReflectablePtr& compound) override;
    virtual void endRoot(const IReflectablePtr& compound) override;

    virtual void beginArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;
    virtual void endArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;

    virtual void beginCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual void endCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual void visitValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    JsonWriter(std::ostream& out, InplaceOptionalHolder<std::string>&& optionalIndent);

    void beginItem();
    void endItem();
    void beginObject();
    void endObject();
    void beginArray();
    void endArray();

    void writeIndent();
    void writeKey(StringView key);
    void writeValue(const IReflectablePtr& value);
    void writeBitBuffer(const BitBuffer& bitBuffer);

    std::ostream& m_out;
    InplaceOptionalHolder<std::string> m_indent;
    std::string m_itemSeparator;
    std::string m_keySeparator;

    bool m_isFirst = true;
    size_t m_level = 0;
};

} // namespace zserio

#endif // ZSERIO_JSON_WRITER_H_INC
