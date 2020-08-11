KERNEL_PATH := $(HOME)/.local/share/jupyter/kernels/camel

build:
	mvn clean package

deploy:
	mkdir -p $(KERNEL_PATH)
	cp target/icamel-0.3-SNAPSHOT.jar $(KERNEL_PATH)/
