
/*
* Name: Sai
* Date: Today
* Des: Basically for shader and how they are going to be processed 
*
*/

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {
    

    // shader id
    private int programID;


    // creating the actualyl shader
    public void create(String vertexShaderSource, String fragmentShaderSource) {

        
      // compulign the vertex and fragment shader
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
        
        // creating the prgram id
        programID = glCreateProgram();
        glAttachShader(programID, vertexShader);
        glAttachShader(programID, fragmentShader);

        // linking to make it work 
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader link error: " + glGetProgramInfoLog(programID));
        
        // deleating cuz now we doing need them
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }
    
    // how they are going to be compiles
    private int compileShader(int type, String src) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(shader));
        return shader;
    }

    
    // helper functions
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


