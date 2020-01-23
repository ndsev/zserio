#include <iostream>

#include "Emotions.h"

int main()
{
    using bitmask_types::uint8_bitmask::Emotions;

    Emotions emotion;
    std::cout << emotion.toString() << std::endl;
    std::cout << (~emotion).toString() << std::endl;

    std::cout << Emotions::Values::SAD.toString() << std::endl;
    std::cout << Emotions::Values::CHEERY.toString() << std::endl;
    std::cout << Emotions::Values::UNHAPPY.toString() << std::endl;
    std::cout << Emotions::Values::HAPPY.toString() << std::endl;
    std::cout << Emotions::Values::SANE.toString() << std::endl;
    std::cout << Emotions::Values::MAD.toString() << std::endl;
    std::cout << Emotions::Values::ALIVE.toString() << std::endl;
    std::cout << Emotions::Values::DEAD.toString() << std::endl;

    emotion = Emotions::Values::SAD | Emotions::Values::CHEERY;
    std::cout << emotion.toString() << std::endl;

    const Emotions emotion2(Emotions::Values::ALIVE | Emotions::Values::DEAD);
    emotion = emotion | emotion2;
    std::cout << emotion.toString() << std::endl;

    emotion &= ~Emotions::Values::DEAD;
    std::cout << emotion.toString() << std::endl;

    return 0;
}
