#pragma once

#include <string>
#include <vector>

#include "gl/glew.h"
#include "glfw/glfw3.h"

namespace prototype {
namespace daemon {
namespace renderer {

class GlWindow {
 public:
    GlWindow();
    bool create_window(uint32_t width, uint32_t height);
    // EXP: rgb
    void render_frame(const std::vector<uint8_t> &image);
    void close_window();

 private:
    GLuint load_shader(const std::string &vertex_src, const std::string &fragment_src);

    uint32_t width_, height_;

    GLFWwindow *window_;
    GLuint shader_id_;
    GLuint vertex_array_id_;
    GLuint vertex_buffer_id_;
    GLuint index_buffer_id_;
    GLuint pbo_id_;
    GLuint streaming_tex_id_;
    GLuint tex_uniform_;
};

}  // namespace renderer
}  // namespace daemon
}  // namespace prototype
