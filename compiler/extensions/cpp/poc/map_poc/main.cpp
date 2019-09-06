#include <iostream>
#include "map/ConcreteMap.h"

using namespace ::map;

int main()
{
    std::vector<Element<ConcreteType, ConcreteValue>> valueList;
    valueList.push_back(Element<ConcreteType, ConcreteValue>{ConcreteValue{std::string{"test"}}});

    ConcreteMap concreteMap{ConcreteType::STRING, valueList};
    concreteMap.initializeChildren();

    std::cout << ::zserio::enumToString(concreteMap.getValueList()[0].getValue().getType()) << std::endl;
    std::cout << concreteMap.getValueList()[0].getValue().getValueString() << std::endl;

    return 0;
}
