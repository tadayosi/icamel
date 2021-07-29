KERNEL_PATH := $(shell jupyter --data-dir)/kernels/camel

build:
	mvn clean package

deploy:
	mkdir -p $(KERNEL_PATH)
	cp target/icamel-0.5-SNAPSHOT-runner.jar $(KERNEL_PATH)/
	./bin/deploy-kernel-json.sh

test-run:
	java -jar target/icamel-0.5-SNAPSHOT-runner.jar
