package service_types.complex_types_service;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import zserio.runtime.array.ObjectArray;
import zserio.runtime.array.UnsignedIntArray;
import zserio.runtime.service.ServiceException;

public class ComplexTypesServiceTest
{
    @BeforeClass
    public static void init()
    {
        cmykValues = new short[3][4];

        Cmyk cmyk = new Cmyk();
        for (int i = 0; i < 3; ++i)
        {
            convertRgbToCmyk(rgbValues[i][0], rgbValues[i][1], rgbValues[i][2], cmyk);
            cmykValues[i][0] = cmyk.c;
            cmykValues[i][1] = cmyk.m;
            cmykValues[i][2] = cmyk.y;
            cmykValues[i][3] = cmyk.k;
        }
    }

    @Test
    public void serviceFullName()
    {
        assertEquals("service_types.complex_types_service.ComplexTypesService",
                ComplexTypesService.ComplexTypesServiceService.serviceFullName());
    }

    @Test
    public void methodNames()
    {
        assertEquals("swapModels", ComplexTypesService.ComplexTypesServiceService.methodNames()[0]);
        assertEquals("getLength", ComplexTypesService.ComplexTypesServiceService.methodNames()[1]);
    }

    @Test
    public void rgbToCmyk()
    {
        final int length = 10000;

        final UnsignedIntArray offsets = new UnsignedIntArray(length);
        final ObjectArray<ColorModelChoice> data = new ObjectArray<ColorModelChoice>(length);

        for (int i = 0; i < length; ++i)
        {
            ColorModelChoice choice = new ColorModelChoice(ColorModel.RGB);
            choice.setRgb(new RGBModel(rgbValues[i % 3][0], rgbValues[i % 3][1], rgbValues[i % 3][2]));
            data.setElementAt(choice, i);
        }

        final RequestData requestData = new RequestData(ColorModel.RGB, offsets, data);
        final Request request = new Request(ColorModel.RGB, requestData);

        assertEquals(length, client.getLengthMethod(request).getLength());

        final Response response = client.swapModelsMethod(request);
        assertEquals(length, response.getLength());

        final ObjectArray<CMYKModel> cmykData = response.getData().getCmykData();
        for (int i = 0; i < length; ++i)
        {
            CMYKModel cmyk = cmykData.elementAt(i);
            assertEquals(cmykValues[i % 3][0], cmyk.getCyan());
            assertEquals(cmykValues[i % 3][1], cmyk.getMagenta());
            assertEquals(cmykValues[i % 3][2], cmyk.getYellow());
            assertEquals(cmykValues[i % 3][3], cmyk.getKey());
        }
    }

    @Test
    public void cmykToRgb()
    {
        final int length = 10000;

        final UnsignedIntArray offsets = new UnsignedIntArray(length);
        final ObjectArray<ColorModelChoice> data = new ObjectArray<ColorModelChoice>(length);

        for (int i = 0; i < length; ++i)
        {
            final ColorModelChoice choice = new ColorModelChoice(ColorModel.CMYK);
            choice.setCmyk(new CMYKModel(cmykValues[i % 3][0], cmykValues[i % 3][1], cmykValues[i % 3][2],
                    cmykValues[i % 3][3]));
            data.setElementAt(choice, i);
        }

        final RequestData requestData = new RequestData(ColorModel.CMYK, offsets, data);
        final Request request = new Request(ColorModel.CMYK, requestData);

        assertEquals(length, client.getLengthMethod(request).getLength());

        final Response response = client.swapModelsMethod(request);
        assertEquals(length, response.getLength());

        final ObjectArray<RGBModel> rgbData = response.getData().getRgbData();
        for (int i = 0; i < length; ++i)
        {
            final RGBModel rgb = rgbData.elementAt(i);
            assertEquals(rgbValues[i % 3][0], rgb.getRed());
            assertEquals(rgbValues[i % 3][1], rgb.getGreen());
            assertEquals(rgbValues[i % 3][2], rgb.getBlue());
        }
    }

    @Test(expected=ServiceException.class)
    public void invalidServiceMethod()
    {
        service.callMethod("nonexistentMethod", null, null);
    }

    private static void convertRgbToCmyk(int r, int g, int b, Cmyk cmyk)
    {
        // see https://www.rapidtables.com/convert/color/rgb-to-cmyk.html
        final double rr = r / 255. * 100;
        final double gg = g / 255. * 100;
        final double bb = b / 255. * 100;

        final double k = 100. - Math.max(Math.max(rr, gg), bb);

        cmyk.c = (short)Math.round((100. - rr - k) / (100. - k) * 100);
        cmyk.m = (short)Math.round((100. - gg - k) / (100. - k) * 100);
        cmyk.y = (short)Math.round((100. - bb - k) / (100. - k) * 100);
        cmyk.k = (short)Math.round(k);
    }

    private static void convertCmykToRgb(short c, short m, short y, short k, Rgb rgb)
    {
        // see https://www.rapidtables.com/convert/color/cmyk-to-rgb.html
        rgb.r = (short)Math.round(255 * (1 - c / 100.) * (1 - k / 100.));
        rgb.g = (short)Math.round(255 * (1 - m / 100.) * (1 - k / 100.));
        rgb.b = (short)Math.round(255 * (1 - y / 100.) * (1 - k / 100.));
    }

    private static class Cmyk
    {
        public short c;
        public short m;
        public short y;
        public short k;
    }

    private static class Rgb
    {
        public short r;
        public short g;
        public short b;
    }

    private static class Service extends ComplexTypesService.ComplexTypesServiceService
    {
        @Override
        public Response swapModelsImpl(Request request, Object context)
        {
            RequestData requestData = request.getData();
            ObjectArray<ColorModelChoice> data = requestData.getData();

            Response response = new Response(data.length(), new ResponseData(data.length()));

            if (requestData.getModel() == ColorModel.RGB)
                rgbToCmyk(data, response);
            else
                cmykToRgb(data, response);

            return response;
        }

        @Override
        public LengthResponse getLengthImpl(Request request, Object context)
        {
            RequestData requestData = request.getData();
            LengthResponse lengthResponse = new LengthResponse(requestData.getData().length());

            return lengthResponse;
        }

        private static void rgbToCmyk(ObjectArray<ColorModelChoice> data, Response response)
        {
            ObjectArray<CMYKModel> cmykData = new ObjectArray<CMYKModel>(data.length());
            response.getData().setCmykData(cmykData);
            Cmyk cmyk = new Cmyk();
            for (int i = 0; i < data.length(); ++i)
            {
                RGBModel rgbModel = data.elementAt(i).getRgb();
                convertRgbToCmyk(rgbModel.getRed(), rgbModel.getGreen(), rgbModel.getBlue(), cmyk);
                CMYKModel cmykModel = new CMYKModel(cmyk.c, cmyk.m, cmyk.y, cmyk.k);
                cmykData.setElementAt(cmykModel, i);
            }
        }

        private static void cmykToRgb(ObjectArray<ColorModelChoice> data, Response response)
        {
            ObjectArray<RGBModel> rgbData = new ObjectArray<RGBModel>(data.length());
            response.getData().setRgbData(rgbData);
            Rgb rgb = new Rgb();
            for (int i = 0; i < data.length(); ++i)
            {
                CMYKModel cmykModel = data.elementAt(i).getCmyk();
                convertCmykToRgb(cmykModel.getCyan(), cmykModel.getMagenta(), cmykModel.getYellow(),
                        cmykModel.getKey(), rgb);
                RGBModel rgbModel = new RGBModel(rgb.r, rgb.g, rgb.b);
                rgbData.setElementAt(rgbModel, i);
            }
        }
    }

    private static final Service service = new Service();
    private static final ComplexTypesService.ComplexTypesServiceClient client =
            new ComplexTypesService.ComplexTypesServiceClient(service);

    // note that conversion is slightly inaccurate and therefore this values are carefully chosen
    // to provide consistent results for the test needs
    private static final short rgbValues[][] = { { 0 ,128, 255 }, { 222, 222, 0 }, { 65, 196, 31 } };
    private static short cmykValues[][];
}
