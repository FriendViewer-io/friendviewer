#include "prototype/daemon/capture/dxgi_capture.hh"

#include <d3d11.h>
#include <dxgi.h>
#include <dxgi1_2.h>

#include <memory>
#include <vector>

namespace prototype {
namespace daemon {
namespace capture {

namespace {
void com_deleter(IUnknown *com_obj) {
    if (com_obj) {
        com_obj->Release();
    }
}
const D3D_FEATURE_LEVEL feature_levels[] = {
    D3D_FEATURE_LEVEL_11_0,
    D3D_FEATURE_LEVEL_10_1,
    D3D_FEATURE_LEVEL_10_0,
    D3D_FEATURE_LEVEL_9_3,
};
}  // namespace

DxgiCapture::DxgiCapture()
    : device_(nullptr, nullptr),
      device_context_(nullptr, nullptr),
      adapter_(nullptr, nullptr),
      factory_(nullptr, nullptr),
      output1_(nullptr, nullptr),
      output_(nullptr, nullptr),
      duplicator_(nullptr, nullptr),
      texture_(nullptr, nullptr),
      width_(0),
      height_(0) {}

bool DxgiCapture::init(uint32_t width, uint32_t height, uint32_t capture_timeout) {
    capture_timeout_ = capture_timeout;
    width_ = width;
    height_ = height;
    CoInitialize(NULL);
    D3D_FEATURE_LEVEL level_used = D3D_FEATURE_LEVEL_9_3;
    IID factory_iid = __uuidof(IDXGIFactory1);

    IDXGIFactory1 *factory;
    auto hresult = CreateDXGIFactory1(factory_iid, reinterpret_cast<void **>(&factory));
    if (FAILED(hresult)) {
        return false;
    }
    factory_ = std::unique_ptr<IDXGIFactory1, D3D11Deleter>(factory, com_deleter);

    IDXGIAdapter1 *adapter;
    hresult = factory_->EnumAdapters1(0, &adapter);
    if (FAILED(hresult)) {
        return false;
    }
    adapter_ = std::unique_ptr<IDXGIAdapter1, D3D11Deleter>(adapter, com_deleter);

    ID3D11Device *device;
    ID3D11DeviceContext *device_context;
    hresult = D3D11CreateDevice(adapter_.get(), D3D_DRIVER_TYPE_UNKNOWN, nullptr,
                                D3D11_CREATE_DEVICE_BGRA_SUPPORT | D3D11_CREATE_DEVICE_DEBUG,
                                feature_levels, sizeof(feature_levels) / sizeof(D3D_FEATURE_LEVEL),
                                D3D11_SDK_VERSION, &device, &level_used, &device_context);
    if (FAILED(hresult)) {
        return false;
    }
    device_ = std::unique_ptr<ID3D11Device, D3D11Deleter>(device, com_deleter);
    device_context_ =
        std::unique_ptr<ID3D11DeviceContext, D3D11Deleter>(device_context, com_deleter);

    IDXGIOutput *output;
    for (int i = 0; adapter_->EnumOutputs(i, &output) == DXGI_ERROR_NOT_FOUND; i++)
        ;
    output_ = std::unique_ptr<IDXGIOutput, D3D11Deleter>(output, com_deleter);

    IDXGIOutput1 *output1;
    hresult = output_->QueryInterface(__uuidof(IDXGIOutput1), reinterpret_cast<void **>(&output1));
    if (FAILED(hresult)) {
        return false;
    }
    output1_ = std::unique_ptr<IDXGIOutput1, D3D11Deleter>(output1, com_deleter);

    IDXGIOutputDuplication *duplicator;
    hresult = output1_->DuplicateOutput(device_.get(), &duplicator);
    if (FAILED(hresult)) {
        return false;
    }
    duplicator_ = std::unique_ptr<IDXGIOutputDuplication, D3D11Deleter>(duplicator, com_deleter);

    D3D11_TEXTURE2D_DESC desc = {};
    desc.Width = width;
    desc.Height = height;
    desc.MipLevels = desc.ArraySize = 1;
    desc.Format = DXGI_FORMAT_B8G8R8A8_UNORM;
    desc.SampleDesc.Count = 1;
    desc.Usage = D3D11_USAGE_STAGING;
    desc.BindFlags = 0;
    desc.CPUAccessFlags = D3D11_CPU_ACCESS_READ;
    desc.MiscFlags = 0;

    ID3D11Texture2D *texture;
    hresult = device_->CreateTexture2D(&desc, nullptr, &texture);
    if (FAILED(hresult)) {
        return false;
    }
    texture_ = std::unique_ptr<ID3D11Texture2D, D3D11Deleter>(texture, com_deleter);

    return true;
}

bool DxgiCapture::get_frame(std::vector<uint8_t> &data_out) {
    if (data_out.size() != width_ * height_ * bytes_per_pixel_) {
        data_out.resize(width_ * height_ * bytes_per_pixel_);
    }

    D3D11_MAPPED_SUBRESOURCE mapped_resource = {};
    DXGI_OUTDUPL_FRAME_INFO info = {};
    std::unique_ptr<IDXGIResource, D3D11Deleter> resource(nullptr, nullptr);
    std::unique_ptr<ID3D11Texture2D, D3D11Deleter> texture(nullptr, nullptr);

    duplicator_->ReleaseFrame();

    IDXGIResource *resource_temp;
    auto hresult = duplicator_->AcquireNextFrame(capture_timeout_, &info, &resource_temp);
    if (FAILED(hresult)) {
        return false;
    }
    resource = std::unique_ptr<IDXGIResource, D3D11Deleter>(resource_temp, com_deleter);

    ID3D11Texture2D *texture_temp;
    hresult = resource->QueryInterface(__uuidof(ID3D11Texture2D),
                                       reinterpret_cast<void **>(&texture_temp));
    if (FAILED(hresult)) {
        return false;
    }
    texture = std::unique_ptr<ID3D11Texture2D, D3D11Deleter>(texture_temp, com_deleter);

    device_context_->CopyResource(texture_.get(), texture.get());

    hresult = device_context_->Map(texture_.get(), 0, D3D11_MAP_READ, 0, &mapped_resource);
    if (FAILED(hresult)) {
        return false;
    }
    memcpy(reinterpret_cast<void *>(&data_out[0]), mapped_resource.pData, data_out.size());
    device_context_->Unmap(texture_.get(), 0);

    return true;
}

}  // namespace capture
}  // namespace daemon
}  // namespace prototype
