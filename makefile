RESOURCES =

all: installDbg push run

installDbg: $(RESOURCES)
	./gradlew installDebug

push:
	adb install -r ./app/build/outputs/apk/debug/app-debug.apk

run:
	adb shell am start -n com.github.winterweird.jpractice/com.github.winterweird.jpractice.MainActivity

logcat:
	adb logcat *:S AndroidRuntime:E Test:D $(EXTRA_LOGCAT_TAGS)
