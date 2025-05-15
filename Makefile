

# Java settings
JAVAC = javac
JAVA = java

# Project paths
SRC_DIR = src
OUT_DIR = out/production/3d
LIB_DIR = libs

# All .jar files in libs/
JARS := $(wildcard $(LIB_DIR)/*.jar)
CLASSPATH = $(subst $(space),;,$(JARS))

# For native LWJGL libraries
JAVA_OPTS = -Djava.library.path=$(LIB_DIR)

# Main class (update if different)
MAIN_CLASS = Main

# Space character
space := $(empty) $(empty)

.PHONY: all run clean

# Default target: compile
all: $(OUT_DIR) compile

$(OUT_DIR):
	mkdir -p $(OUT_DIR)

compile:
	$(JAVAC) -cp "$(CLASSPATH)" -d $(OUT_DIR) $(SRC_DIR)/*.java

run:
	$(JAVA) -cp "$(CLASSPATH);$(OUT_DIR)" $(JAVA_OPTS) $(MAIN_CLASS)

clean:
	del /s /q $(OUT_DIR)\*.class 2>nul || true
