

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

	private static final int SCR_WIDTH = 800;
	private static final int SCR_HEIGHT = 600;
	private static long window;

	private static float cameraX = 0f, cameraY = 0f, cameraZ = 3f;
	private static float[] cameraFront = {0f, 0f, -1f};
	private static float[] cameraUp = {0f, 1f, 0f};

	private static float cubeX = 0f, cubeY = 0f, cubeZ = 0f;

	private static double lastX = SCR_WIDTH / 2.0;
	private static double lastY = SCR_HEIGHT / 2.0;
	private static float yaw = -90f;
	private static float pitch = 0f;
	private static boolean firstMouse = true;

	public static void main(String[] args) {
		init();

		// Vertex Shader
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

		// Fragment Shader
		String fragmentShaderSource = """
            #version 330 core
            out vec4 FragColor;

            void main() {
                FragColor = vec4(0.5, 0.7, 1.0, 1.0);
            }
        """;

		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);
		checkCompileErrors(vertexShader, "VERTEX");

		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);
		checkCompileErrors(fragmentShader, "FRAGMENT");

		int shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glLinkProgram(shaderProgram);
		checkLinkErrors(shaderProgram);

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);

		// Setup Cube
		Cube cube = new Cube();
		int vao = glGenVertexArrays();
		int vbo = glGenBuffers();
		int ebo = glGenBuffers();

		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, cube.getVertices(), GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, cube.getIndices(), GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glEnable(GL_DEPTH_TEST);

		while (!glfwWindowShouldClose(window)) {
			processInput(window);

			glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			glUseProgram(shaderProgram);

			float[] eye = {cameraX, cameraY, cameraZ};
			float[] center = {
					cameraX + cameraFront[0],
					cameraY + cameraFront[1],
					cameraZ + cameraFront[2]
			};

			float[] view = lookAt(eye, center, cameraUp);

			float aspect = (float) SCR_WIDTH / SCR_HEIGHT;
			float fov = (float) Math.toRadians(45);
			float zNear = 0.1f, zFar = 100.0f;
			float yScale = (float)(1 / Math.tan(fov / 2));
			float xScale = yScale / aspect;
			float length = zFar - zNear;

			float[] projection = {
					xScale, 0, 0, 0,
					0, yScale, 0, 0,
					0, 0, -(zFar + zNear) / length, -1,
					0, 0, -(2 * zNear * zFar) / length, 0
			};

			float[] model = {
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0,
					cubeX, cubeY, cubeZ, 1
			};

			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "model"), false, model);
			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "view"), false, view);
			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "projection"), false, projection);

			glBindVertexArray(vao);
			glDrawElements(GL_TRIANGLES, cube.getIndexCount(), GL_UNSIGNED_INT, 0);

			glfwSwapBuffers(window);
			glfwPollEvents();
		}

		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		glDeleteProgram(shaderProgram);

		glfwTerminate();
	}

	private static void processInput(long window) {
		float speed = 0.05f;

		// Calculate right vector (for movement in lateral direction)
		float[] right = cross(cameraFront, cameraUp);
		normalize(right);

		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			cameraX += cameraFront[0] * speed;
			cameraY += cameraFront[1] * speed;
			cameraZ += cameraFront[2] * speed;
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			cameraX -= cameraFront[0] * speed;
			cameraY -= cameraFront[1] * speed;
			cameraZ -= cameraFront[2] * speed;
		}
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
			cameraX -= right[0] * speed;
			cameraY -= right[1] * speed;
			cameraZ -= right[2] * speed;
		}
		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
			cameraX += right[0] * speed;
			cameraY += right[1] * speed;
			cameraZ += right[2] * speed;
		}

		// Spacebar moves the camera up
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			cameraY += speed;  // Move camera up
		}

		// Left Shift moves the camera down
		if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
			cameraY -= speed;  // Move camera down
		}

		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
			glfwSetWindowShouldClose(window, true);
	}


	private static void mouseCallback(long window, double xpos, double ypos) {
		if (firstMouse) {
			lastX = xpos;
			lastY = ypos;
			firstMouse = false;
		}

		double xoffset = xpos - lastX;
		double yoffset = lastY - ypos;
		lastX = xpos;
		lastY = ypos;

		float sensitivity = 0.1f;
		xoffset *= sensitivity;
		yoffset *= sensitivity;

		yaw += xoffset;
		pitch += yoffset;

		if (pitch > 89.0f) pitch = 89.0f;
		if (pitch < -89.0f) pitch = -89.0f;

		float radYaw = (float) Math.toRadians(yaw);
		float radPitch = (float) Math.toRadians(pitch);

		cameraFront[0] = (float) (Math.cos(radYaw) * Math.cos(radPitch));
		cameraFront[1] = (float) Math.sin(radPitch);
		cameraFront[2] = (float) (Math.sin(radYaw) * Math.cos(radPitch));
		normalize(cameraFront);
	}

	private static void init() {
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LWJGL Cube with Mouse", NULL, NULL);
		if (window == NULL) {
			glfwTerminate();
			throw new RuntimeException("Failed to create GLFW window");
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		GL.createCapabilities();
		glfwSetFramebufferSizeCallback(window, (w, width, height) -> glViewport(0, 0, width, height));
		glfwSetCursorPosCallback(window, Main::mouseCallback);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	private static void checkCompileErrors(int shader, String type) {
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
			System.err.println("ERROR::SHADER::" + type + "::COMPILATION_FAILED\n" + glGetShaderInfoLog(shader));
	}

	private static void checkLinkErrors(int program) {
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
			System.err.println("ERROR::PROGRAM::LINKING_FAILED\n" + glGetProgramInfoLog(program));
	}

	private static float[] cross(float[] a, float[] b) {
		return new float[]{
				a[1]*b[2] - a[2]*b[1],
				a[2]*b[0] - a[0]*b[2],
				a[0]*b[1] - a[1]*b[0]
		};
	}

	private static void normalize(float[] vec) {
		float len = (float) Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
		if (len != 0.0f) {
			vec[0] /= len;
			vec[1] /= len;
			vec[2] /= len;
		}
	}

	private static float dot(float[] a, float[] b) {
		return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
	}

	private static float[] lookAt(float[] eye, float[] center, float[] up) {
		float[] f = {center[0] - eye[0], center[1] - eye[1], center[2] - eye[2]};
		normalize(f);
		float[] s = cross(f, up);
		normalize(s);
		float[] u = cross(s, f);
		return new float[]{
				s[0], u[0], -f[0], 0,
				s[1], u[1], -f[1], 0,
				s[2], u[2], -f[2], 0,
				-dot(s, eye), -dot(u, eye), dot(f, eye), 1
		};
	}

	static class Cube {
		private final FloatBuffer vertices;
		private final IntBuffer indices;
		private final int indexCount;

		public Cube() {
			float[] verts = {
					-0.5f, -0.5f, -0.5f,
					0.5f, -0.5f, -0.5f,
					0.5f,  0.5f, -0.5f,
					-0.5f,  0.5f, -0.5f,
					-0.5f, -0.5f,  0.5f,
					0.5f, -0.5f,  0.5f,
					0.5f,  0.5f,  0.5f,
					-0.5f,  0.5f,  0.5f
			};

			int[] inds = {
					0, 1, 2, 2, 3, 0,
					4, 5, 6, 6, 7, 4,
					0, 1, 5, 5, 4, 0,
					2, 3, 7, 7, 6, 2,
					0, 3, 7, 7, 4, 0,
					1, 2, 6, 6, 5, 1
			};

			vertices = BufferUtils.createFloatBuffer(verts.length);
			vertices.put(verts).flip();

			indices = BufferUtils.createIntBuffer(inds.length);
			indices.put(inds).flip();

			indexCount = inds.length;
		}

		public FloatBuffer getVertices() {
			return vertices;
		}

		public IntBuffer getIndices() {
			return indices;
		}

		public int getIndexCount() {
			return indexCount;
		}
	}
}
