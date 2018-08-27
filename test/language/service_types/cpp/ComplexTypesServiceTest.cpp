#include <algorithm>
#include <memory>
#include <cmath>
#include <grpcpp/grpcpp.h>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "service_types/complex_types_service/ComplexTypesService.h"

namespace service_types
{
namespace complex_types_service
{

class Client
{
public:
    explicit Client(const std::shared_ptr<grpc::Channel>& channel)
    :   m_stub(ComplexTypesService::NewStub(channel))
    {}

    bool swapModels(const Request& request, Response& response)
    {
        grpc::ClientContext context;
        grpc::Status status = m_stub->swapModels(&context, request, &response);

        return status.ok();
    }

    uint32_t getLength(const Request& request)
    {
        grpc::ClientContext context;
        LengthResponse response;
        grpc::Status status = m_stub->getLength(&context, request, &response);

        if (!status.ok())
            return 0;

        return response.getLength();
    }

    std::unique_ptr<ComplexTypesService::Stub> m_stub;
};

namespace
{
    void convertRgbToCmyk(uint8_t r, uint8_t g, uint8_t b,
            double& c, double &m, double &y, double& k)
    {
        // see https://www.rapidtables.com/convert/color/rgb-to-cmyk.html
        const double rr = r / 255. * 100;
        const double gg = g / 255. * 100;
        const double bb = b / 255. * 100;

        k = 100. - std::max(std::max(rr, gg), bb);

        c = std::round((100. - rr - k) / (100. - k) * 100);
        m = std::round((100. - gg - k) / (100. - k) * 100);
        y = std::round((100. - bb - k) / (100. - k) * 100);
        k = std::round(k);
    }

    void convertCmykToRgb(uint8_t c, uint8_t m, uint8_t y, uint8_t k,
            uint8_t& r, uint8_t& g, uint8_t& b)
    {
        // see https://www.rapidtables.com/convert/color/cmyk-to-rgb.html
        r = std::round(255 * (1 - c / 100.) * (1 - k / 100.));
        g = std::round(255 * (1 - m / 100.) * (1 - k / 100.));
        b = std::round(255 * (1 - y / 100.) * (1 - k / 100.));
    }
}

class Service final : public ComplexTypesService::Service
{
public:
    ::grpc::Status swapModels(grpc::ServerContext*, const Request* request, Response* response)
    {
        const RequestData& requestData = request->getData();
        const auto& data = requestData.getData();

        response->setLength(data.size());

        if (requestData.getModel() == ColorModel::RGB)
            rgbToCmyk(data, *response);
        else
            cmykToRgb(data, *response);

        response->initializeChildren();
        return grpc::Status::OK;
    }

    ::grpc::Status getLength(grpc::ServerContext*, const Request* request, LengthResponse* response)
    {
        const RequestData& requestData = request->getData();
        response->setLength(requestData.getData().size());
        return grpc::Status::OK;
    }

private:
    void rgbToCmyk(const zserio::ObjectArray<ColorModelChoice>& data, Response& response)
    {
        response.getData().setCmykData(zserio::ObjectArray<CMYKModel>());
        auto& cmykData = response.getData().getCmykData();
        cmykData.resize(response.getLength());
        for (uint32_t i = 0; i < response.getLength(); ++i)
        {
            const RGBModel& rgb = data.at(i).getRgb();
            double c, m, y, k;
            convertRgbToCmyk(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), c, m, y, k);
            cmykData[i].setCyan(c);
            cmykData[i].setMagneta(m);
            cmykData[i].setYellow(y);
            cmykData[i].setKey(k);
        }
    }

    void cmykToRgb(const zserio::ObjectArray<ColorModelChoice>& data, Response& response)
    {
        response.getData().setRgbData(zserio::ObjectArray<RGBModel>());
        auto& rgbData = response.getData().getRgbData();
        rgbData.resize(response.getLength());
        for (uint32_t i = 0; i < response.getLength(); ++i)
        {
            const CMYKModel& cmyk = data.at(i).getCmyk();
            uint8_t r, g, b;
            convertCmykToRgb(cmyk.getCyan(), cmyk.getMagneta(), cmyk.getYellow(), cmyk.getKey(), r, g, b);
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
    :   server(buildServer()),
        client(server->InProcessChannel(grpc::ChannelArguments()))
    {
        for (size_t i = 0; i < 3; ++i)
        {
            double c, m, y, k;
            convertRgbToCmyk(rgbValues[i][0], rgbValues[i][1], rgbValues[i][2], c, m, y, k);
            cmykValues[i][0] = c;
            cmykValues[i][1] = m;
            cmykValues[i][2] = y;
            cmykValues[i][3] = k;
        }
    }

private:
    std::unique_ptr<grpc::Server> buildServer()
    {
        grpc::ServerBuilder serverBuilder;
        serverBuilder.RegisterService(&service);
        return serverBuilder.BuildAndStart();
    }

    Service service;
    std::unique_ptr<grpc::Server> server;

protected:
    Client client;

    // note that conversion is slightly inaccurate and therefore this values are carefully choosen
    // to provide consistent results for the test needs
    static constexpr uint8_t rgbValues[3][3] = { { 0 ,128, 255 }, { 222, 222, 0 }, { 65, 196, 31 } };
    uint8_t cmykValues[3][4];
};

constexpr uint8_t ComplexTypesServiceTest::rgbValues[3][3];

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

    ASSERT_EQ(length, client.getLength(request));

    Response response;
    ASSERT_TRUE(client.swapModels(request, response));
    ASSERT_EQ(length, response.getLength());

    const auto& cmykData = response.getData().getCmykData();
    for (size_t i = 0; i < cmykData.size(); ++i)
    {
        ASSERT_EQ(cmykValues[i % 3][0], cmykData.at(i).getCyan());
        ASSERT_EQ(cmykValues[i % 3][1], cmykData.at(i).getMagneta());
        ASSERT_EQ(cmykValues[i % 3][2], cmykData.at(i).getYellow());
        ASSERT_EQ(cmykValues[i % 3][3], cmykData.at(i).getKey());
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
        model.setMagneta(cmykValues[i % 3][1]);
        model.setYellow(cmykValues[i % 3][2]);
        model.setKey(cmykValues[i % 3][3]);
        data.at(i).setCmyk(model);
    }
    request.initializeChildren();

    ASSERT_EQ(length, client.getLength(request));

    Response response;
    ASSERT_TRUE(client.swapModels(request, response));
    ASSERT_EQ(length, response.getLength());

    const auto& rgbData = response.getData().getRgbData();
    for (size_t i = 0; i < rgbData.size(); ++i)
    {
        ASSERT_EQ(rgbValues[i % 3][0], rgbData.at(i).getRed());
        ASSERT_EQ(rgbValues[i % 3][1], rgbData.at(i).getGreen());
        ASSERT_EQ(rgbValues[i % 3][2], rgbData.at(i).getBlue());
    }
}

} // namespace complex_types_service
} // namespace service_types
