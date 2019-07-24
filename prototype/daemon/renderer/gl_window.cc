#include "prototype/daemon/renderer/gl_window.hh"

#include <string>

#include "gl/glew.h"
#include "glfw/glfw3.h"

namespace prototype {
namespace daemon {
namespace renderer {

namespace {
constexpr char fragment_shader[] =
    "#version 330 core\n"
    "out vec3 color;\n"
    "in vec2 tex_coord;\n"
    "uniform sampler2D video_frame;\n"
    "void main() {\n"
    "   color = texture2D(video_frame, tex_coord).xyz;\n"
    "}";
constexpr char vertex_shader[] =
    "#version 330 core\n"
    "layout(location = 0) in vec2 vertex_pos;\n"
    "layout(location = 1) in vec2 tex_pos;\n"
    "out vec2 tex_coord;\n"
    "void main() {\n"
    "   gl_Position = vec4(vertex_pos, 0, 1);\n"
    "   tex_coord = tex_pos;\n"
    "}";

constexpr GLfloat vertex_buffer_data[] = {1, 1, 1, 1, 1, -1, 1, 0, -1, -1, 0, 0, -1, 1, 0, 1};
constexpr int index_buffer_data[] = {0, 1, 3, 1, 2, 3};
}  // namespace

GlWindow::GlWindow() {}

GLuint GlWindow::load_shader(const std::string &vertex_src, const std::string &fragment_src) {
    GLuint vertex_id = glCreateShader(GL_VERTEX_SHADER);
    GLuint fragment_id = glCreateShader(GL_FRAGMENT_SHADER);

    GLint result = GL_FALSE;
    int log_len = 0;

    const char *vertex_src_ptr = vertex_src.c_str();
    glShaderSource(vertex_id, 1, &vertex_src_ptr, nullptr);
    glCompileShader(vertex_id);

    glGetShaderiv(vertex_id, GL_COMPILE_STATUS, &result);

    const char *fragment_src_ptr = fragment_src.c_str();
    glShaderSource(fragment_id, 1, &fragment_src_ptr, nullptr);
    glCompileShader(fragment_id);

    glGetShaderiv(fragment_id, GL_COMPILE_STATUS, &result);

    glGetShaderiv(fragment_id, GL_INFO_LOG_LENGTH, &log_len);
    if (log_len > 0) {
        std::string message;
        message.resize(log_len + 1);
        glGetShaderInfoLog(fragment_id, log_len, nullptr, &message[0]);
    }

    GLuint program_id = glCreateProgram();
    glAttachShader(program_id, vertex_id);
    glAttachShader(program_id, fragment_id);
    glLinkProgram(program_id);

    glGetShaderiv(vertex_id, GL_LINK_STATUS, &result);

    glDetachShader(program_id, vertex_id);
    glDetachShader(program_id, fragment_id);

    glDeleteShader(vertex_id);
    glDeleteShader(fragment_id);

    return program_id;
}

bool GlWindow::create_window(uint32_t width, uint32_t height) {
    width_ = width;
    height_ = height;
    glewExperimental = true;
    if (!glfwInit()) {
        return false;
    }
    glfwWindowHint(GLFW_SAMPLES, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window_ = glfwCreateWindow(width, height, "FriendViewer", nullptr, nullptr);
    if (window_ == nullptr) {
        glfwTerminate();
        return false;
    }
    glfwMakeContextCurrent(window_);
    glfwSetWindowUserPointer(window_, this);
    glewExperimental = true;
    if (glewInit() != GLEW_OK) {
        return false;
    }
    glfwSetInputMode(window_, GLFW_STICKY_KEYS, GL_TRUE);

    glfwSetKeyCallback(window_, key_cb);
    glfwSetMouseButtonCallback(window_, mouse_cb);
    glfwSetCursorPosCallback(window_, mouse_pos_cb);

    shader_id_ = load_shader(vertex_shader, fragment_shader);

    glGenVertexArrays(1, &vertex_array_id_);
    glBindVertexArray(vertex_array_id_);

    glGenBuffers(1, &vertex_buffer_id_);
    glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer_id_);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertex_buffer_data), vertex_buffer_data, GL_STATIC_DRAW);

    glGenBuffers(1, &index_buffer_id_);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index_buffer_id_);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(index_buffer_data), index_buffer_data,
                 GL_STATIC_DRAW);

    glGenTextures(1, &streaming_tex_id_);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, streaming_tex_id_);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    std::vector<uint8_t> init(width * height * 3);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, init.data());
    glBindTexture(GL_TEXTURE_2D, 0);

    glGenBuffers(1, &pbo_id_);
    glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbo_id_);
    glBufferData(GL_PIXEL_UNPACK_BUFFER, width * height * 3, 0, GL_STREAM_DRAW);
}

void GlWindow::render_frame(const std::vector<uint8_t> &image) {
    glBindTexture(GL_TEXTURE_2D, streaming_tex_id_);
    glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbo_id_);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width_, height_, GL_RGB, GL_UNSIGNED_BYTE, 0);
    glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbo_id_);
    glBufferData(GL_PIXEL_UNPACK_BUFFER, width_ * height_ * 3, 0, GL_STREAM_DRAW);
    GLubyte *ptr = reinterpret_cast<GLubyte *>(glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_WRITE_ONLY));
    if (ptr) {
        std::memcpy(ptr, image.data(), image.size());
        glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
    }
    glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(shader_id_);

    glActiveTexture(GL_TEXTURE0);
    tex_uniform_ = glGetUniformLocation(shader_id_, "video_frame");
    glUniform1i(tex_uniform_, 0);
    glBindTexture(GL_TEXTURE_2D, streaming_tex_id_);

    glBindVertexArray(vertex_array_id_);

    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer_id_);
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 16, nullptr);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 16, (void *)8);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index_buffer_id_);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, nullptr);
    glBindTexture(GL_TEXTURE_2D, 0);

    glDisableVertexAttribArray(1);
    glDisableVertexAttribArray(0);

    glfwSwapBuffers(window_);
    glfwPollEvents();
}

void GlWindow::close_window() {
    glDeleteBuffers(1, &vertex_buffer_id_);
    glDeleteBuffers(1, &index_buffer_id_);
    glDeleteBuffers(1, &pbo_id_);
    glDeleteProgram(shader_id_);
    glDeleteTextures(1, &streaming_tex_id_);
    glDeleteVertexArrays(1, &vertex_array_id_);
    glfwDestroyWindow(window_);
    glfwTerminate();
}

int GlWindow::get_key_mapping(int glfw) {
    switch (glfw) {
        case GLFW_KEY_APOSTROPHE:
            return 0xDE;
        case GLFW_KEY_COMMA:
            return 0xBC;
        case GLFW_KEY_MINUS:
            return 0xBD;
        case GLFW_KEY_PERIOD:
            return 0xBE;
        case GLFW_KEY_SLASH:
            return 0xBF;
        case GLFW_KEY_SEMICOLON:
            return 0xBA;
        case GLFW_KEY_EQUAL:
            return 0xBB;
        case GLFW_KEY_LEFT_BRACKET:
            return 0xDB;
        case GLFW_KEY_BACKSLASH:
            return 0xE2;
        case GLFW_KEY_RIGHT_BRACKET:
            return 0xDD;
        case GLFW_KEY_GRAVE_ACCENT:
            return 0xC0;
        case GLFW_KEY_ESCAPE:
            return 0x1B;
        case GLFW_KEY_ENTER:
            return 0x0D;
        case GLFW_KEY_TAB:
            return 0x09;
        case GLFW_KEY_BACKSPACE:
            return 0x08;
        case GLFW_KEY_INSERT:
            return 0x2D;
        case GLFW_KEY_DELETE:
            return 0x2E;
        case GLFW_KEY_RIGHT:
            return 0x27;
        case GLFW_KEY_LEFT:
            return 0x25;
        case GLFW_KEY_DOWN:
            return 0x28;
        case GLFW_KEY_UP:
            return 0x26;
        case GLFW_KEY_PAGE_UP:
            return 0x21;
        case GLFW_KEY_PAGE_DOWN:
            return 0x22;
        case GLFW_KEY_HOME:
            return 0x24;
        case GLFW_KEY_END:
            return 0x23;
        case GLFW_KEY_CAPS_LOCK:
            return 0x14;
        case GLFW_KEY_SCROLL_LOCK:
        case GLFW_KEY_NUM_LOCK:
        case GLFW_KEY_PRINT_SCREEN:
        case GLFW_KEY_PAUSE:
        case GLFW_KEY_F1:
        case GLFW_KEY_F2:
        case GLFW_KEY_F3:
        case GLFW_KEY_F4:
        case GLFW_KEY_F5:
        case GLFW_KEY_F6:
        case GLFW_KEY_F7:
        case GLFW_KEY_F8:
        case GLFW_KEY_F9:
        case GLFW_KEY_F10:
        case GLFW_KEY_F11:
        case GLFW_KEY_F12:
        case GLFW_KEY_F13:
        case GLFW_KEY_F14:
        case GLFW_KEY_F15:
        case GLFW_KEY_F16:
        case GLFW_KEY_F17:
        case GLFW_KEY_F18:
        case GLFW_KEY_F19:
        case GLFW_KEY_F20:
        case GLFW_KEY_F21:
        case GLFW_KEY_F22:
        case GLFW_KEY_F23:
        case GLFW_KEY_F24:
        case GLFW_KEY_F25:
        case GLFW_KEY_KP_0:
        case GLFW_KEY_KP_1:
        case GLFW_KEY_KP_2:
        case GLFW_KEY_KP_3:
        case GLFW_KEY_KP_4:
        case GLFW_KEY_KP_5:
        case GLFW_KEY_KP_6:
        case GLFW_KEY_KP_7:
        case GLFW_KEY_KP_8:
        case GLFW_KEY_KP_9:
        case GLFW_KEY_KP_DECIMAL:
        case GLFW_KEY_KP_DIVIDE:
        case GLFW_KEY_KP_MULTIPLY:
        case GLFW_KEY_KP_SUBTRACT:
        case GLFW_KEY_KP_ADD:
        case GLFW_KEY_KP_ENTER:
        case GLFW_KEY_KP_EQUAL:
        case GLFW_KEY_LEFT_SHIFT:
        case GLFW_KEY_LEFT_CONTROL:
        case GLFW_KEY_LEFT_ALT:
        case GLFW_KEY_LEFT_SUPER:
        case GLFW_KEY_RIGHT_SHIFT:
        case GLFW_KEY_RIGHT_CONTROL:
        case GLFW_KEY_RIGHT_ALT:
        case GLFW_KEY_RIGHT_SUPER:
        case GLFW_KEY_MENU:
            return 0;
        default:
            return glfw;
    }
}

}  // namespace renderer
}  // namespace daemon
}  // namespace prototype
