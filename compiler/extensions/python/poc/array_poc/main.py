import array_py.api as array_py
import zserio
import zserio_poc

# test how UInt32 array could be generated
class TestArray(array_py.Array):
    def __init__(self):
        super().__init__(zserio_poc.TemplateArgUInt32)

    @classmethod
    def fromFields(cls, values):
        instance = cls()
        instance.setValues(values)
        return instance

    @classmethod
    def fromReader(cls, reader):
        instance = cls()
        instance.read(reader)
        return instance

if __name__ == "__main__":
    sa = array_py.StringArray(zserio_poc.TemplateArgString)
    values = [zserio_poc.TemplateArgString.fromFields("test"), zserio_poc.TemplateArgString.fromFields("test2")]
    sa.setValues(values)

    writer = zserio.BitStreamWriter()
    sa.write(writer)
    reader = zserio.BitStreamReader(writer.getByteArray())

    rsa = array_py.StringArray(zserio_poc.TemplateArgString)
    rsa.read(reader)

    for v in rsa.getValues():
        print(v.value)

    da = array_py.DataArray(array_py.Data)
    values = [array_py.Data.fromFields(20), array_py.Data.fromFields(30)]
    da.setValues(values)

    writer = zserio.BitStreamWriter()
    da.write(writer)
    reader = zserio.BitStreamReader(writer.getByteArray())

    rda = array_py.DataArray(array_py.Data)
    rda.read(reader)

    for v in rda.getValues():
        print(v.getValue())

    ta = TestArray()
    ta.setValues([zserio_poc.TemplateArgUInt32.fromFields(42)])

    writer = zserio.BitStreamWriter()
    ta.write(writer)
    reader = zserio.BitStreamReader(writer.getByteArray())

    rta = TestArray.fromReader(reader)

    for v in rta.getValues():
        print(v.value)
