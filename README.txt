Welcome to the Guito Automatic Guitar Tuner, a first year Engineering project by Simon Fraser University's ENSC100W Group Kappa of Fall 2014!

This guide is meant to help you set up this project up for yourself, and warn you of a few caveats you may encounter.



HOW DO I SET THIS UP?
=====================

You will need the following hardware:
	A Bluetooth slave module
		i.e. HC-05, HC-06, etc
	900-00008 Continuous Rotation Servo Motor
		Will require calibration using the calibration pin.
		Alternatively, simply change the SERVO_MIDPOINT constant in "\GuitoArduino\GuitoArduino.ino" and set it to a value which results in the motor being completely still.
		You may use the program "\GuitoArduino\ServoMotorCalibration.ino" to assist in calibration.
	Arduino Uno R3
		Or any Arduino board with Rx/Tx, PWM outputs, 5Vout, and GND.
	Android device
		Android OS 4.0.3 or higher
		Tested on Samsung S4 Mini

Install the APKs from "\GuitoApp\app\bin\" onto your android device.
	Guito.apk
	Amarino_2_v0_55.apk
	Make sure your phone has installing APKs "Unknown sources" enabled.
	Copy over the APKs to your phone and install them using the file manager.

Compile and upload "\GuitoArduino\GuitoArduino.ino" onto your Arduino.
	Copy the libraries in "\GuitoArduino\libraries\" into your Arduino library folder (usually located in "C:\Program Files (x86)\Arduino\libraries").
	You may need to change the baudrate to correspond with your Bluetooth device. See your manufacturer's documentation. (Common baudrates include multiples of 9600: 9600, 19200, 38400, 76800, 153600.)
	NOTE: Make sure your Rx, Tx, and PWM cables are completely disconnected before uploading! You may not be able to upload if these are still plugged into the Arduino.

Build a circuit as described in "\Diagrams\Circuit.jpg".

Build a tuning head attachment as shown in "\Diagrams\Construction_TuningHead<1/2>.jpg".



HOW DO I RUN THIS?
==================

Open the Amarino app.
	Locate your Bluetooth slave module. For the HC-05, name/pass are usually:
		Default baud rate: 9600 Baud 8N1
		Name: HC-05 or linvor
		Pass: 1234
	Connect to your Bluetooth module.

Open the Guito app.
	NOTE: Make sure your microphone device is not in use or the app will not be able to read your microphone!

If your screen turns off before you exit the Guito app, you will need to manually kill both the Guito app and the Amarino app.

Due to the current buggy nature of the Guito app, it doesn't properly release resources onPause() / onStop(). You may need to kill the Guito app manually or your battery life will drain even when the app is minimized in the background.



HOW DO I USE THIS?
==================

1) Select your desired tuning (i.e. "Standard Tuning").
2) Pluck the string with the lowest note (E2).
3) When the signal turns green, move the tuning head to the next string.
4) Press "Next String".
5) Repeat from step 2.

If your tuner goes out of control for some reason, you can press "Stop" to freeze the motor.


