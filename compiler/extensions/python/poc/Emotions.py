
class Emotions(object):
    def __init__(self):
        self._value = 0

    @classmethod
    def _fromValue(cls, value):
        instance = cls()
        instance._value = value
        return instance

    def __str__(self):
        result = ""
        if self._value & Emotions.SAD._value == Emotions.SAD._value:
            result += "SAD" if not result else " | SAD"
        if self._value & Emotions.CHEERY._value == Emotions.CHEERY._value:
            result += "CHEERY" if not result else " | CHEERY"
        if self._value & Emotions.UNHAPPY._value == Emotions.UNHAPPY._value:
            result += "UNHAPPY" if not result else " | UNHAPPY"
        if self._value & Emotions.HAPPY._value == Emotions.HAPPY._value:
            result += "HAPPY" if not result else " | HAPPY"
        if self._value & Emotions.SANE._value == Emotions.SANE._value:
            result += "SANE" if not result else " | SANE"
        if self._value & Emotions.MAD._value == Emotions.MAD._value:
            result += "MAD" if not result else " | MAD"
        if self._value & Emotions.ALIVE._value == Emotions.ALIVE._value:
            result += "ALIVE" if not result else " | ALIVE"
        if self._value & Emotions.DEAD._value == Emotions.DEAD._value:
            result += "DEAD" if not result else " | DEAD"
        if not result:
            result = "NONE"
        return result

    def __invert__(self):
        return Emotions._fromValue(~self._value & ((1 << Emotions._NUM_BITS) - 1))

    def __or__(self, other):
        return Emotions._fromValue(self._value | other._value)

    def __and__(self, other):
        return Emotions._fromValue(self._value & other._value)

    def __xor__(self, other):
        return Emotions._fromValue(self._value ^ other._value)


    _NUM_BITS=8

Emotions.SAD = Emotions._fromValue(0x01)
Emotions.CHEERY = Emotions._fromValue(0x02)
Emotions.UNHAPPY = Emotions._fromValue(0x04)
Emotions.HAPPY = Emotions._fromValue(0x08)
Emotions.SANE = Emotions._fromValue(0x10)
Emotions.MAD = Emotions._fromValue(0x20)
Emotions.ALIVE = Emotions._fromValue(0x40)
Emotions.DEAD = Emotions._fromValue(0x80)
