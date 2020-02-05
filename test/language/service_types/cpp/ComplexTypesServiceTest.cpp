#include <algorithm>
#include <memory>
#include <cmath>

#include "gtest/gtest.h"

#include "service_types/complex_types_service/ComplexTypesService.h"

namespace service_types
{
namespace complex_types_service
{

namespace
{
    void convertRgbToCmyk(uint8_t r, uint8_t g, uint8_t b,
            uint8_t& c, uint8_t &m, uint8_t &y, uint8_t& k)
    {
        // see https://www.rapidtables.com/convert/color/rgb-to-cmyk.html
        const double rr = r / 255. * 100;
        const double gg = g / 255. * 100;
        const double bb = b / 255. * 100;

        double kk = 100. - std::max(std::max(rr, gg), bb);

        c = static_cast<uint8_t>(std::round((100. - rr - kk) / (100. - kk) * 100));
        m = static_cast<uint8_t>(std::round((100. - gg - kk) / (100. - kk) * 100));
        y = static_cast<uint8_t>(std::round((100. - bb - kk) / (100. - kk) * 100));
        k = static_cast<uint8_t>(std::round(kk));
    }

    void convertCmykToRgb(uint8_t c, uint8_t m, uint8_t y, uint8_t k,
            uint8_t& r, uint8_t& g, uint8_t& b)
    {
        // see https://www.rapidtables.com/convert/color/cmyk-to-rgb.html
        r = static_cast<uint8_t>(std::round(255 * (1 - c / 100.) * (1 - k / 100.)));
        g = static_cast<uint8_t>(std::round(255 * (1 - m / 100.) * (1 - k / 100.)));
        b = static_cast<uint8_t>(std::round(255 * (1 - y / 100.) * (1 - k / 100.)));
    }
}

class ComplexTypesServiceImpl : public ComplexTypesService::Service
{
public:
    void swapModelsImpl(const Request& request, Response& response) override
    {
        const RequestData& requestData = request.getData();
        const auto& data = requestData.getData();

        response.setLength(static_cast<uint32_t>(data.size()));

        if (requestData.getModel() == ColorModel::RGB)
            rgbToCmyk(data, response);
        else
            cmykToRgb(data, response);

        response.initializeChildren();
    }

    void getLengthImpl(const Request& request, LengthResponse& response) override
    {
        const RequestData& requestData = request.getData();
        response.setLength(static_cast<uint32_t>(requestData.getData().size()));
    }

private:
    void rgbToCmyk(const std::vector<ColorModelChoice>& data, Response& response)
    {
        response.getData().setCmykData(std::vector<CMYKModel>());
        auto& cmykData = response.getData().getCmykData();
        cmykData.resize(response.getLength());
        for (uint32_t i = 0; i < response.getLength(); ++i)
        {
            const RGBModel& rgb = data.at(i).getRgb();
            uint8_t c, m, y, k;
            convertRgbToCmyk(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), c, m, y, k);
            cmykData[i].setCyan(c);
            cmykData[i].setMagenta(m);
            cmykData[i].setYellow(y);
            cmykData[i].setKey(k);
        }
    }

    void cmykToRgb(const std::vector<ColorModelChoice>& data, Response& response)
    {
        response.getData().setRgbData(std::vector<RGBModel>());
        auto& rgbData = response.getData().getRgbData();
        rgbData.resize(response.getLength());
        for (uint32_t i = 0; i < response.getLength(); ++i)
        {
            const CMYKModel& cmyk = data.at(i).getCmyk();
            uint8_t r, g, b;
            convertCmykToRgb(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getKey(), r, g, b);
            rgbData[i].setRed(r);
            rgbData[i].setGreen(g);
            rgbData[i].setBlue(b);
        }
    }
};

class ComplexTypesServiceTest : public ::testing::Test
{
public:
    ComplexTypesServiceTest()
    :   client(service)
    {
        for (size_t i = 0; i < 3; ++i)
        {
            uint8_t c, m, y, k;
            convertRgbToCmyk(rgbValues[i][0], rgbValues[i][1], rgbValues[i][2], c, m, y, k);
            cmykValues[i][0] = c;
            cmykValues[i][1] = m;
            cmykValues[i][2] = y;
            cmykValues[i][3] = k;
        }
    }

protected:
    ComplexTypesServiceImpl service;
    ComplexTypesService::Client client;

    // note that conversion is slightly inaccurate and therefore this values are carefully choosen
    // to provide consistent results for the test needs
    static constexpr uint8_t rgbValues[3][3] = { { 0 ,128, 255 }, { 222, 222, 0 }, { 65, 196, 31 } };
    uint8_t cmykValues[3][4];
};

constexpr uint8_t ComplexTypesServiceTest::rgbValues[3][3];

TEST_F(ComplexTypesServiceTest, serviceFullName)
{
    ASSERT_EQ(std::string("service_types.complex_types_service.ComplexTypesService"),
            ComplexTypesService::Service::serviceFullName());
}

TEST_F(ComplexTypesServiceTest, methodNames)
{
    ASSERT_EQ(std::string("swapModels"), ComplexTypesService::Service::methodNames()[0]);
    ASSERT_EQ(std::string("getLength"), ComplexTypesService::Service::methodNames()[1]);
}

TEST_F(ComplexTypesServiceTest, rgbToCmyk)
{
    Request request;
    request.setModel(ColorModel::RGB);
    RequestData& requestData = request.getData();
    static const size_t length = 10000;
    requestData.getOffsets().resize(length);
    auto& data = requestData.getData();
    data.resize(length);
    RGBModel model;
    for (size_t i = 0; i < data.size(); ++i)
    {
        model.setRed(rgbValues[i % 3][0]);
        model.setGreen(rgbValues[i % 3][1]);
        model.setBlue(rgbValues[i % 3][2]);
        data.at(i).setRgb(model);
    }
    request.initializeChildren();

    LengthResponse lengthResponse;
    client.getLengthMethod(request, lengthResponse);
    ASSERT_EQ(length, lengthResponse.getLength());

    Response response;
    client.swapModelsMethod(request, response);
    ASSERT_EQ(length, response.getLength());

    const auto& cmykData = response.getData().getCmykData();
    for (size_t i = 0; i < cmykData.size(); ++i)
    {
        const CMYKModel& cmyk = cmykData.at(i);
        ASSERT_EQ(cmykValues[i % 3][0], cmyk.getCyan());
        ASSERT_EQ(cmykValues[i % 3][1], cmyk.getMagenta());
        ASSERT_EQ(cmykValues[i % 3][2], cmyk.getYellow());
        ASSERT_EQ(cmykValues[i % 3][3], cmyk.getKey());
    }
}

TEST_F(ComplexTypesServiceTest, cmykToRgb)
{
    Request request;
    request.setModel(ColorModel::CMYK);
    RequestData& requestData = request.getData();
    static const size_t length = 10000;
    requestData.getOffsets().resize(length);
    auto& data = requestData.getData();
    data.resize(length);
    CMYKModel model;
    for (size_t i = 0; i < data.size(); ++i)
    {
        model.setCyan(cmykValues[i % 3][0]);
        model.setMagenta(cmykValues[i % 3][1]);
        model.setYellow(cmykValues[i % 3][2]);
        model.setKey(cmykValues[i % 3][3]);
        data.at(i).setCmyk(model);
    }
    request.initializeChildren();

    LengthResponse lengthResponse;
    client.getLengthMethod(request, lengthResponse);
    ASSERT_EQ(length, lengthResponse.getLength());

    Response response;
    client.swapModelsMethod(request, response);
    ASSERT_EQ(length, response.getLength());

    const auto& rgbData = response.getData().getRgbData();
    for (size_t i = 0; i < rgbData.size(); ++i)
    {
        const RGBModel& rgb = rgbData.at(i);
        ASSERT_EQ(rgbValues[i % 3][0], rgb.getRed());
        ASSERT_EQ(rgbValues[i % 3][1], rgb.getGreen());
        ASSERT_EQ(rgbValues[i % 3][2], rgb.getBlue());
    }
}

TEST_F(ComplexTypesServiceTest, invalidServiceMethod)
{
    std::vector<uint8_t> responseData;
    ASSERT_THROW(service.callMethod("nonexistentMethod", {}, responseData), zserio::ServiceException);
}

} // namespace complex_types_service
} // namespace service_types
