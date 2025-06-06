"""
Test utilities.
"""

import zserio
from typing import Type, Any
import pprint


def writeReadTest(clazz: Type[Any], data: Any):
    bitSize = data.bitsizeof()
    bitBuffer = zserio.serialize(data)
    assert bitSize == bitBuffer.bitsize

    readData = zserio.deserialize(clazz, bitBuffer)

    if readData != data:
        pp = pprint.PrettyPrinter(indent=4)
        print("readData.size=", readData.bitsizeof(), "data.size=", bitSize)
        print("readData=")
        pp.pprint(vars(readData))
        print("data=")
        pp.pprint(vars(data))

    assert readData == data


def hashTest(value: Any, hashValue: int, equalValue: Any, diffValue: Any = None, diffHashValue: int = 0):
    assert hashValue == hash(value)
    assert hashValue == hash(equalValue)
    if diffValue is not None:
        assert hashValue != diffHashValue
        assert diffHashValue == hash(diffValue)


def comparisonOperatorsTest(value: Any, equalValue: Any):
    assert value == equalValue
