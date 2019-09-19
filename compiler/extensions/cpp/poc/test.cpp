#include <iostream>

#include "Emotions.h"

int main()
{
    using bitmask_types::uint8_bitmask::Emotions;

    Emotions emotion;
    std::cout << "emotion: " << emotion.toString() << std::endl;

    emotion = Emotions::SAD | Emotions::CHEERY;
    std::cout << "emotion: " << emotion.toString() << std::endl;

    const Emotions emotion2(Emotions::ALIVE | Emotions::DEAD);
    emotion = emotion | emotion2;
    std::cout << "emotion: " << emotion.toString() << std::endl;

    emotion = emotion & ~Emotions::DEAD;
    std::cout << "emotion: " << emotion.toString() << std::endl;

    uint8_t i = (uint8_t)emotion;

    return 0;
}
