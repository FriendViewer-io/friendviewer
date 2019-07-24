#pragma once

#include <d3d11.h>
#include <dxgi.h>
#include <dxgi1_2.h>

#include <memory>
#include <vector>

namespace prototype {
namespace daemon {
namespace capture {

class DxgiCapture {
 public:
    DxgiCapture();
    bool init(uint32_t width, uint32_t height, uint32_t capture_timeout);

    bool get_frame(std::vector<uint8_t> &data_out);

 private:
    using D3D11Deleter = void (*)(IUnknown *);

    std::unique_ptr<ID3D11Device, D3D11Deleter> device_;
    std::unique_ptr<ID3D11DeviceContext, D3D11Deleter> device_context_;
    std::unique_ptr<IDXGIAdapter1, D3D11Deleter> adapter_;
    std::unique_ptr<IDXGIFactory1, D3D11Deleter> factory_;
    std::unique_ptr<IDXGIOutput1, D3D11Deleter> output1_;
    std::unique_ptr<IDXGIOutput, D3D11Deleter> output_;
    std::unique_ptr<IDXGIOutputDuplication, D3D11Deleter> duplicator_;
    std::unique_ptr<ID3D11Texture2D, D3D11Deleter> texture_;

    uint32_t width_, height_;
    uint32_t capture_timeout_;
    // BGRA format -> 32 bpp
    static constexpr uint32_t bytes_per_pixel_ = 4;
};

}  // namespace capture
}  // namespace daemon
}  // namespace prototype
