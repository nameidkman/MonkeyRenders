import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {

    private int programID;

    public void create() {
        String vertexShaderSource = """
			#version 330 core
			layout (location = 0) in vec3 aPos;
			uniform mat4 model;
			uniform mat4 view;
			uniform mat4 projection;
			void main() {
				gl_Position = projection * view * model * vec4(aPos, 1.0);
			}
		""";

        String fragmentShaderSource = """
			#version 330 core
			out vec4 FragColor;
			void main() {
				FragColor = vec4(0.5, 0.7, 1.0, 1.0);
			}
		""";

        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        programID = glCreateProgram();
        glAttachShader(programID, vertexShader);
        glAttachShader(programID, fragmentShader);
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader link error: " + glGetProgramInfoLog(programID));

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int compileShader(int type, String src) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(shader));
        return shader;
    }

    public void use() {
        glUseProgram(programID);
    }

    public int getID() {
        return programID;
    }

    public void cleanup() {
        glDeleteProgram(programID);
    }
}
