#!/bin/bash
#sudo gem install fastlane
#sudo gem install screengrab

#echo no | android create avd -f -n "Nexus_5_API_23_x86" -t android-23 --abi armeabi-v7a -s 1080x1920
#echo no | android create avd -f -n "Nexus_7_API_23" -t android-23 --abi armeabi-v7a -s 1200x1920
#echo no | android create avd -f -n "Nexus_10_API_23" -t android-23 --abi armeabi-v7a -s 2560x1600

# Disable emulator authentication
touch '~/.emulator_console_auth_token'
echo '' > '~/.emulator_console_auth_token'

# Names of AVD images for phone, 7-inch, 10-inch devices
avd_images=( "Nexus_5_API_23_x86" "Nexus_7_API_23" "Nexus_10_API_23" )
device_types=( "phone" "sevenInch" "tenInch" )

echo Killing all emulators first!
{ echo "kill"; echo "exit"; sleep 1; } | telnet localhost 5554

for i in "${!avd_images[@]}"
do
  # start emulator
  emulator -netdelay none -netspeed full -avd ${avd_images[$i]} &
  echo Emulator started, waiting for boot
  # wait until adb is connected to device, so that we can issue adb shell commands
  adb wait-for-device
  
  # wait until boot is completed (see http://ncona.com/2014/01/detect-when-android-emulator-is-ready/ )
  output=''
  while [[ ${output:0:7} != 'stopped' ]]; do
    output=`adb shell getprop init.svc.bootanim`
    sleep 1
    echo ...waiting
  done
  
  sleep 1 
  # unlock lockscreen
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

  # now let the screengrab script run
  screengrab --device_type ${device_types[$i]}
  
  echo Done, killing this emulator now!
  { echo "kill"; echo "exit"; sleep 1; } | telnet localhost 5554
done

cp -r build/screengrab/ app/src/main/play/
rm -rf build/screengrab/