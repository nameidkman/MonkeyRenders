import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;


// doing static import so that we dont have to write the thing like GL20, GL30 every single time 
// this is just for the heck of it and making my life a bit easier
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class firstThing {


    private static final int windowW = 800;
    private static final int windowH = 600;


    // need for the vertex shader 
    private static final String vertexShaderSource = "#version 330 core\n"
            + "layout (location = 0) in vec3 aPos;\n"
            + "void main()\n"
            + "{\n"
            + "   gl_Position = vec4(aPos, 1.0);\n"
            + "}\0";
    
    // need for the fragment Shader  
    private static final String fragmentShaderSource = "#version 330 core\n"
            + "out vec4 FragColor;\n"
            + "void main()\n"
            + "{\n"
            + "   FragColor = vec4(1.0, 0.5, 0.2, 1.0);\n"
            + "}\n\0";

    public static void main(String[] args) {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // setting up the hint for window 
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        

        // creating the window
        long window = glfwCreateWindow(windowW, windowH, "LearnOpenGL", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }
        
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();
        
        // actually creating the buffer 
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> {
            glViewport(0, 0, width, height);
        });
        // creating the vertix shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        // basically what we are doing here is that we have the vertex shader 
        // which we jsut initialize and then we have the vertexShaderSource whcih we are using 
        // throguh the declaring it at the start
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");
        
        // creating the fragmentShader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        // same thing as above 
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");
        
        // this is the linker whihc is the like the briger between the two shader
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkLinkErrors(shaderProgram);
        

        // deleteing the two shader becuase we dont need them anymore
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        float vertices[] = {
                0.5f,  0.5f, 0.0f,  // top right
                0.5f, -0.5f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f,  // bottom left
                -0.5f,  0.5f, 0.0f   // top left
        };
        int[] indices = {  // note that we start from 0!
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };
        

        // making the two shader
        // so its like making the first thing which is the vertex buffer shader
        // what that does is that it has the main thing for the vertex like position 
        // color texture and other important things 
        // then there is vao which is there for being like a container for the vbo
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        while (!glfwWindowShouldClose(window)) {
            processInput(window);
            
            // changing the color
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            
            // telling what is the shader whihc you need to use
            glUseProgram(shaderProgram);
            glBindVertexArray(vao);
            // tell that i am drawing a triangle
            glDrawElements(GL_TRIANGLES, 6,GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        // deleating all of the things
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(shaderProgram);

        glfwTerminate();
    }


    // checking if there are some error
    private static void checkCompileErrors(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shader);
            System.err.println("ERROR::SHADER::" + type + "::COMPILATION_FAILED\n" + infoLog);
        }
    }
    // checking if there are anny error with the linker
    private static void checkLinkErrors(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(program);
            System.err.println("ERROR::PROGRAM::LINKING_FAILED\n" + infoLog);
        }
    }
    
    // this is basically how you take input for the thing
    // so yea if you are going to do somethign with wasd then you are going to use this
    private static void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }
}
