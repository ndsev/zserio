"""
Test utilities.
"""

import typing
import zserio


import pprint

def _writeReadTestSerialize(
    clazz: typing.Type[typing.Any],
    data
):
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

def writeReadTest(
    clazz: typing.Type[typing.Any],
    data
):
    _writeReadTestSerialize(clazz, data)