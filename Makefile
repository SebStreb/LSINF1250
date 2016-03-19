all: launch clean

launch: main.class
	@echo "Launching main"
	@java main

main.class: main.java
	@echo "Compiling main"
	@javac main.java

clean:
	@echo "Removing class files"
	@rm main.class

.PHONY: clean
