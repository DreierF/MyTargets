#!/bin/bash

# Configuration
IMAGE="system-images;android-28;google_apis;x86"
AVD_IMAGES=( "Pixel_API_28" "Nexus_7_API_28" "Pixel_C_API_28" )
DEVICE_TYPES=( "phone" "sevenInch" "tenInch" )
DEVICE_DEFINITIONS=( "pixel" "Nexus 7 2013" "pixel_c" )
RESOLUTIONS=( "1080x1920" "1200x1920" "2560x1600" )

# Install prerequisites
sudo gem install fastlane
sdkmanager "emulator" "system-images;android-28;google_apis;x86"

# Compile app
./gradlew assembleScreengrabDebug assembleScreengrabDebugAndroidTest

# Disable emulator authentication
touch "~/.emulator_console_auth_token"
echo "" > "~/.emulator_console_auth_token"
export DYLD_LIBRARY_PATH="$ANDROID_HOME/emulator/lib64:$ANDROID_HOME/emulator/lib64/qt/lib:$DYLD_LIBRARY_PATH"
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin

echo Killing all emulators first!
{ echo "kill"; echo "exit"; sleep 1; } | telnet localhost 5554

for i in "${!AVD_IMAGES[@]}"
do
	# Create Emulator
	echo no | avdmanager create avd -force --name ${AVD_IMAGES[$i]} --package $IMAGE --device ${DEVICE_DEFINITIONS[$i]}

	# Start emulator
	emulator -netdelay none -netspeed full -skin ${RESOLUTIONS[$i]} -avd ${AVD_IMAGES[$i]} &
	echo Emulator started, waiting for boot
	# wait until adb is connected to device, so that we can issue adb shell commands
	adb wait-for-device

	# Wait until boot is completed (see http://ncona.com/2014/01/detect-when-android-emulator-is-ready/ )
	output=""
	while [[ ${output:0:7} != "stopped" ]]; do
		output=`adb shell getprop init.svc.bootanim`
		sleep 1
		echo waiting...
	done

	sleep 1
	# Unlock lockscreen
	adb shell input keyevent 82

	# Disable system animations
	adb shell settings put global window_animation_scale 0
	adb shell settings put global transition_animation_scale 0
	adb shell settings put global animator_duration_scale 0

	# Hide all system notification icons
	adb shell service call notification 1

	# Display battery as fully charged
	{ echo "power capacity 100"; echo "power ac off"; echo "exit"; sleep 1; } | telnet localhost 5554

	echo Device online, initiating screengrabs

	# Now let the screengrab script run
	fastlane screengrab --device_type ${DEVICE_TYPES[$i]}

	echo Done, killing this emulator now!
	{ echo "kill"; echo "exit"; sleep 1; } | telnet localhost 5554
done

cp -r build/screengrab/ app/src/main/play/
rm -rf build/screengrab/
