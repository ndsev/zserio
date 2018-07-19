#ifndef ZSERIO_IBLOB_INSPECTOR_VALUE_LISTENER_H_INC
#define ZSERIO_IBLOB_INSPECTOR_VALUE_LISTENER_H_INC

#include <string>

#include "../Types.h"

namespace zserio
{

class IBlobInspectorValueListener
{
public:
    virtual ~IBlobInspectorValueListener()
    {}

    virtual void onValue(bool value) = 0;
    virtual void onValue(int64_t value) = 0;
    virtual void onValue(uint64_t value) = 0;
    virtual void onValue(float value) = 0;
    virtual void onValue(const std::string& value) = 0;
    virtual void onValue(int64_t enumValue, const std::string& enumSymbol) = 0;
    virtual void onValue(uint64_t enumValue, const std::string& enumSymbol) = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_IBLOB_INSPECTOR_VALUE_LISTENER_H_INC
