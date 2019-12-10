
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
        if self._value & Emotions.Values.SAD._value == Emotions.Values.SAD._value:
            result += "SAD" if not result else " | SAD"
        if self._value & Emotions.Values.CHEERY._value == Emotions.Values.CHEERY._value:
            result += "CHEERY" if not result else " | CHEERY"
        if self._value & Emotions.Values.UNHAPPY._value == Emotions.Values.UNHAPPY._value:
            result += "UNHAPPY" if not result else " | UNHAPPY"
        if self._value & Emotions.Values.HAPPY._value == Emotions.Values.HAPPY._value:
            result += "HAPPY" if not result else " | HAPPY"
        if self._value & Emotions.Values.SANE._value == Emotions.Values.SANE._value:
            result += "SANE" if not result else " | SANE"
        if self._value & Emotions.Values.MAD._value == Emotions.Values.MAD._value:
            result += "MAD" if not result else " | MAD"
        if self._value & Emotions.Values.ALIVE._value == Emotions.Values.ALIVE._value:
            result += "ALIVE" if not result else " | ALIVE"
        if self._value & Emotions.Values.DEAD._value == Emotions.Values.DEAD._value:
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

    class Values():
        pass

Emotions.Values.SAD = Emotions._fromValue(0x01)
Emotions.Values.CHEERY = Emotions._fromValue(0x02)
Emotions.Values.UNHAPPY = Emotions._fromValue(0x04)
Emotions.Values.HAPPY = Emotions._fromValue(0x08)
Emotions.Values.SANE = Emotions._fromValue(0x10)
Emotions.Values.MAD = Emotions._fromValue(0x20)
Emotions.Values.ALIVE = Emotions._fromValue(0x40)
Emotions.Values.DEAD = Emotions._fromValue(0x80)
