all: launch clean

launch: main.class
	@echo "Launching main"
	@java main Bott.csv Coleman.csv

main.class: main.java
	@echo "Compiling main"
	@javac -cp Jama-1.0.3.jar main.java

clean:
	@echo "Removing class files"
	@rm main.class

.PHONY: clean
