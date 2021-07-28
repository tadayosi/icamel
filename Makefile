KERNEL_PATH := $(shell jupyter --data-dir)/kernels/camel

build:
	mvn clean package

deploy:
	mkdir -p $(KERNEL_PATH)
	cp target/icamel-0.4-SNAPSHOT.jar $(KERNEL_PATH)/
	./bin/deploy-kernel-json.sh
