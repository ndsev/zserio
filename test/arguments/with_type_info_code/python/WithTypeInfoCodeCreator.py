import zserio

def createWithTypeInfoCode(api, *, createOptionals = True):
    simpleStruct = _createSimpleStruct(api)
    testEnum = api.TestEnum.TWO
    ts32 = _createTS32(api)
    withTypeInfoCode = api.WithTypeInfoCode(
        simpleStruct,
        _createComplexStruct(api, createOptionals),
        _createParameterizedStruct(api, simpleStruct),
        _createRecursiveStruct(api),
        _createRecursiveUnion(api),
        _createRecursiveChoice(api, True, False),
        testEnum,
        _createSimpleChoice(api, testEnum),
        ts32,
        _createTemplatedParameterizedStruct_TS32(api, ts32),
        _createExternData(),
        [_createExternData(), _createExternData()],
        [1, 4, 6, 4, 6, 1])

    return withTypeInfoCode

def _createSimpleStruct(api):
    simpleStruct = api.SimpleStruct()
    simpleStruct.field_offset = 0
    simpleStruct.field_float32 = 4.0

    return simpleStruct

def _createComplexStruct(api, createOptionals):
    simpleStruct = _createSimpleStruct(api)
    anotherSimpleStruct = _createSimpleStruct(api)
    complexStruct = api.ComplexStruct(
        simpleStruct,
        anotherSimpleStruct,
        _createSimpleStruct(api) if createOptionals else None,
        [3, 0xABCD2, 0xABCD3, 0xABCD4, 0xABCD5],
        list(range(3, 0, -1)),
        [_createParameterizedStruct(api, simpleStruct),
         _createParameterizedStruct(api, anotherSimpleStruct)] if createOptionals else None,
        8,
        list(range(1, 65536, 2)),
        api.TestEnum.ITEM_THREE if createOptionals else None,
        api.TestBitmask.Values.RED if createOptionals else None,
        _createOptionalExternData() if createOptionals else None,
        [api.TestEnum.TWO, api.TestEnum.ITEM_THREE],
        [api.TestBitmask.Values.GREEN, api.TestBitmask.Values.GREEN, api.TestBitmask.Values.GREEN,
         api.TestBitmask.Values.GREEN, api.TestBitmask.Values.GREEN])

    return complexStruct

def _createParameterizedStruct(api, simpleStruct):
    parameterizedStruct = api.ParameterizedStruct(
        simpleStruct,
        list(range(simpleStruct.field_u32)))

    return parameterizedStruct

def _createRecursiveStruct(api):
    recursiveStruct = api.RecursiveStruct(
        0xDEAD1,
        api.RecursiveStruct(0xDEAD2, None, []),
        [api.RecursiveStruct(0xDEAD3, None, []), api.RecursiveStruct(0xDEAD4, None, []) ])

    return recursiveStruct

def _createRecursiveUnion(api):
    recursiveUnion = api.RecursiveUnion()
    recursiveUnion.recursive = [api.RecursiveUnion(field_u32_ = 0xDEAD)]

    return recursiveUnion

def _createRecursiveChoice(api, param1, param2):
    recursiveChoice = api.RecursiveChoice(param1, param2)
    if param1:
        recursiveChoice.recursive = [_createRecursiveChoice(api, param2, False)]
    else:
        recursiveChoice.field_u32 = 0xDEAD

    return recursiveChoice

def _createSimpleUnion(api):
    simpleUnion = api.SimpleUnion()
    simpleUnion.test_bitmask = api.TestBitmask.Values.GREEN

    return simpleUnion

def _createSimpleChoice(api, testEnum):
    simpleChoice = api.SimpleChoice(testEnum)
    if testEnum == api.TestEnum.TWO:
        simpleChoice.field_two = _createSimpleUnion(api)
    else:
        simpleChoice.field_default = "text"

    return simpleChoice

def _createTS32(api):
    ts32 = api.TS32(
        0xDEAD
        )

    return ts32

def _createTemplatedParameterizedStruct_TS32(api, ts32):
    templatedParameterizedStruct_TS32 = api.TemplatedParameterizedStruct_TS32(
        ts32,
        list(range(ts32.field, 0, -1)))

    return templatedParameterizedStruct_TS32

def _createExternData():
    return zserio.BitBuffer(bytes([0xCA, 0xFE]), 15)

def _createOptionalExternData():
    return zserio.BitBuffer(bytes([0xCB, 0xF0]), 12)
