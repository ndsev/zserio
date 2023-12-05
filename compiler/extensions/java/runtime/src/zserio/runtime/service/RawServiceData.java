package zserio.runtime.service;

import zserio.runtime.io.Writer;

/** ServiceData implementation based on raw data. */
public final class RawServiceData implements ServiceData<Writer>
{
    /**
     * Constructor from raw data.
     *
     * @param rawData Raw data to use.
     */
    public RawServiceData(byte[] rawData)
    {
        this.rawData = rawData;
    }

    @Override
    public byte[] getByteArray()
    {
        return rawData;
    }

    @Override
    public Writer getZserioObject()
    {
        return null;
    }

    private final byte[] rawData;
}
