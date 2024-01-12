#include <algorithm>
#include <cmath>
#include <memory>

#include "gtest/gtest.h"
#include "service_types/complex_types_service/ComplexTypesService.h"
#include "test_utils/LocalServiceClient.h"
#include "zserio/RebindAlloc.h"

using namespace zserio::literals;

namespace service_types
{
namespace complex_types_service
{

using allocator_type = ComplexTypesService::Client::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using LocalServiceClient = test_utils::LocalServiceClient<allocator_type>;

namespace
{

void convertRgbToCmyk(
        uint8_t colR, uint8_t colG, uint8_t colB, uint8_t& colC, uint8_t& colM, uint8_t& colY, uint8_t& colK)
{
    // see https://www.rapidtables.com/convert/color/rgb-to-cmyk.html
    const double colRR = colR / 255. * 100;
    const double colGG = colG / 255. * 100;
    const double colBB = colB / 255. * 100;

    double colKK = 100. - std::max(std::max(colRR, colGG), colBB);

    colC = static_cast<uint8_t>(std::round((100. - colRR - colKK) / (100. - colKK) * 100));
    colM = static_cast<uint8_t>(std::round((100. - colGG - colKK) / (100. - colKK) * 100));
    colY = static_cast<uint8_t>(std::round((100. - colBB - colKK) / (100. - colKK) * 100));
    colK = static_cast<uint8_t>(std::round(colKK));
}

void convertCmykToRgb(
        uint8_t colC, uint8_t colM, uint8_t colY, uint8_t colK, uint8_t& colR, uint8_t& colG, uint8_t& colB)
{
    // see https://www.rapidtables.com/convert/color/cmyk-to-rgb.html
    colR = static_cast<uint8_t>(std::round(255 * (1 - colC / 100.) * (1 - colK / 100.)));
    colG = static_cast<uint8_t>(std::round(255 * (1 - colM / 100.) * (1 - colK / 100.)));
    colB = static_cast<uint8_t>(std::round(255 * (1 - colY / 100.) * (1 - colK / 100.)));
}

} // namespace

class ComplexTypesServiceImpl : public ComplexTypesService::Service
{
public:
    Response swapModelsImpl(const Request& request, void*) override
    {
        const RequestData& requestData = request.getData();
        const auto& data = requestData.getData();

        Response response{get_allocator_ref()};
        response.setLength(static_cast<uint32_t>(data.size()));

        if (requestData.getModel() == ColorModel::RGB)
            rgbToCmyk(data, response);
        else
            cmykToRgb(data, response);

        response.initializeChildren();
        return response;
    }

    LengthResponse getLengthImpl(const Request& request, void*) override
    {
        const RequestData& requestData = request.getData();
        return LengthResponse(static_cast<uint32_t>(requestData.getData().size()), get_allocator_ref());
    }

private:
    void rgbToCmyk(const vector_type<ColorModelChoice>& data, Response& response)
    {
        response.getData().setCmykData(vector_type<CMYKModel>());
        auto& cmykData = response.getData().getCmykData();
        cmykData.resize(response.getLength());
        for (uint32_t i = 0; i < response.getLength(); ++i)
        {
            const RGBModel& rgb = data.at(i).getRgb();
            uint8_t colC = 0;
            uint8_t colM = 0;
            uint8_t colY = 0;
            uint8_t colK = 0;
            convertRgbToCmyk(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), colC, colM, colY, colK);
            cmykData[i].setCyan(colC);
            cmykData[i].setMagenta(colM);
            cmykData[i].setYellow(colY);
            cmykData[i].setKey(colK);
        }
    }

    void cmykToRgb(const vector_type<ColorModelChoice>& data, Response& response)
    {
        response.getData().setRgbData(vector_type<RGBModel>());
        auto& rgbData = response.getData().getRgbData();
        rgbData.resize(response.getLength());
        for (uint32_t i = 0; i < response.getLength(); ++i)
        {
            const CMYKModel& cmyk = data.at(i).getCmyk();
            uint8_t colR = 0;
            uint8_t colG = 0;
            uint8_t colB = 0;
            convertCmykToRgb(
                    cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getKey(), colR, colG, colB);
            rgbData[i].setRed(colR);
            rgbData[i].setGreen(colG);
            rgbData[i].setBlue(colB);
        }
    }
};

class ComplexTypesServiceTest : public ::testing::Test
{
public:
    ComplexTypesServiceTest() :
            localServiceClient(service),
            client(localServiceClient),
            cmykValues()
    {
        for (size_t i = 0; i < 3; ++i)
        {
            uint8_t colC = 0;
            uint8_t colM = 0;
            uint8_t colY = 0;
            uint8_t colK = 0;
            convertRgbToCmyk(rgbValues[i][0], rgbValues[i][1], rgbValues[i][2], colC, colM, colY, colK);
            cmykValues[i][0] = colC;
            cmykValues[i][1] = colM;
            cmykValues[i][2] = colY;
            cmykValues[i][3] = colK;
        }
    }

protected:
    ComplexTypesServiceImpl service;
    LocalServiceClient localServiceClient;
    ComplexTypesService::Client client;

    // note that conversion is slightly inaccurate and therefore this values are carefully chosen
    // to provide consistent results for the test needs
    static constexpr std::array<std::array<uint8_t, 3>, 3> rgbValues = {
            {{0, 128, 255}, {222, 222, 0}, {65, 196, 31}}};
    std::array<std::array<uint8_t, 4>, 3> cmykValues;
};

constexpr std::array<std::array<uint8_t, 3>, 3> ComplexTypesServiceTest::rgbValues;

TEST_F(ComplexTypesServiceTest, serviceFullName)
{
    ASSERT_EQ("service_types.complex_types_service.ComplexTypesService"_sv,
            ComplexTypesService::Service::serviceFullName());
}

TEST_F(ComplexTypesServiceTest, methodNames)
{
    ASSERT_EQ("swapModels"_sv, ComplexTypesService::Service::methodNames()[0]);
    ASSERT_EQ("getLength"_sv, ComplexTypesService::Service::methodNames()[1]);
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
    request.initializeOffsets();

    LengthResponse lengthResponse = client.getLengthMethod(request);
    ASSERT_EQ(length, lengthResponse.getLength());

    Response response = client.swapModelsMethod(request);
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
    request.initializeOffsets();

    LengthResponse lengthResponse = client.getLengthMethod(request);
    ASSERT_EQ(length, lengthResponse.getLength());

    Response response = client.swapModelsMethod(request);
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
    ASSERT_THROW(service.callMethod("nonexistentMethod"_sv, {}, nullptr), zserio::ServiceException);
}

} // namespace complex_types_service
} // namespace service_types
