#pragma once

#include <functional>
#include <string>
#include <vector>

#include "gl/glew.h"
#include "glfw/glfw3.h"

namespace prototype {
namespace daemon {
namespace renderer {

class GlWindow {
 public:
    using KeyCb = std::function<void(int)>;
    using MouseCb = std::function<void(int)>;
    using MousePosCb = std::function<void(double, double)>;

    GlWindow();
    bool create_window(uint32_t width, uint32_t height);
    // EXP: rgb
    void render_frame(const std::vector<uint8_t> &image);
    void close_window();

    void register_callbacks(KeyCb &&key, MouseCb &&mouse, MousePosCb &&mouse_pos) {
        key_ = std::move(key);
        mouse_ = std::move(mouse);
        mouse_pos_ = std::move(mouse_pos);
    }

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

    KeyCb key_;
    MouseCb mouse_;
    MousePosCb mouse_pos_;

    static int get_key_mapping(int glfw);

    static void key_cb(GLFWwindow *window, int key, int scancode, int actions, int mods) {
        GlWindow *w = reinterpret_cast<GlWindow *>(glfwGetWindowUserPointer(window));
        w->key_(get_key_mapping(key));
    }
    static void mouse_cb(GLFWwindow *window, int button, int action, int mods) {
        GlWindow *w = reinterpret_cast<GlWindow *>(glfwGetWindowUserPointer(window));
        w->mouse_(button + 1);
    }
    static void mouse_pos_cb(GLFWwindow *window, double x, double y) {
        GlWindow *w = reinterpret_cast<GlWindow *>(glfwGetWindowUserPointer(window));
        w->mouse_pos_(x / w->width_, y / w->height_);
    }
};

}  // namespace renderer
}  // namespace daemon
}  // namespace prototype
