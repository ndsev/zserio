"""
Zserio Python runtime library

.. data:: VERSION_STRING

   Zserio Python runtime library version string.
"""

from zserio.bitreader import BitStreamReader
from zserio.bitwriter import BitStreamWriter
from zserio.exception import PythonRuntimeException

import zserio.array
import zserio.builtin
import zserio.float
import zserio.hashcode
import zserio.limits

VERSION_STRING = "1.1.0"
