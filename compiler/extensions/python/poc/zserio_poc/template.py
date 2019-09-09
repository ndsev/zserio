import zserio

class TemplateArgString():
    def __init__(self):
        self.value = None

    @classmethod
    def fromFields(cls, value):
        instance = cls()
        instance.value = value

        return instance

    @classmethod
    def fromReader(cls, reader):
        instance = cls()
        instance.read(reader)

        return instance

    def __eq__(self, other):
        if isinstance(other, TemplateArgString):
            return self.value == other.value

        return False

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED
        result = zserio.hashcode.calcHashCode(result, hash(self.value))

        return result

    def bitSizeOf(self, _bitPosition):
        return zserio.bitSizeOfString(self.value)

    def initializeOffsets(self, bitPosition):
        return bitPosition + zserio.bitSizeOfString(self.value)

    def read(self, reader):
        self.value = reader.readString()

    def write(self, writer, *, callInitializeOffsets=True):
        del callInitializeOffsets

        writer.writeString(self.value)

class TemplateArgUInt32():
    def __init__(self):
        self.value = None

    @classmethod
    def fromFields(cls, value):
        instance = cls()
        instance.value = value

        return instance

    @classmethod
    def fromReader(cls, reader):
        instance = cls()
        instance.read(reader)

        return instance

    def __eq__(self, other):
        if isinstance(other, TemplateArgUInt32):
            return self.value == other.value

        return False

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED
        result = zserio.hashcode.calcHashCode(result, hash(self.value))

        return result

    def bitSizeOf(self, _bitPosition):
        return 32

    def initializeOffsets(self, bitPosition):
        return bitPosition + 32

    def read(self, reader):
        self.value = reader.readBits(32)

    def write(self, writer, *, callInitializeOffsets=True):
        del callInitializeOffsets

        writer.writeBits(self.value, 32)
