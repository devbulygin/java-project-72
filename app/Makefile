clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew install

clean-install:
	./gradlew clean installDist


lint:
	./gradlew checkstyleMain checkstyleTest
.PHONY: build

report:
	./gradlew jacocoTestReport

start:
	APP_ENV=development ./gradlew run