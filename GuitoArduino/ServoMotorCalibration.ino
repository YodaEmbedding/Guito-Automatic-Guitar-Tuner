// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Assists in motor calibration.
// License:     GPLv3


#include "Arduino.h"
#include <Servo.h>
#include "Motor.h"


// Motor
const int PIN_SERVO = 9;

const int SERVO_RADIUS = 18;
const int SERVO_MIDPOINT = 94;
const int SERVO_MIN = SERVO_MIDPOINT - SERVO_RADIUS; // 76;
const int SERVO_MAX = SERVO_MIDPOINT + SERVO_RADIUS; // 112;


void setup()
{
  // Turn on servo motor
  myservo.attach(PIN_SERVO);

  // Rotate at "midpoint" speed.
  // 
  // If your motor calibration pin is correctly set,
  // the motor should be completely still.
  // 
  // Alternatively, you may experimentally change the value of
  // SERVO_MIDPOINT until the motor stops turning.
  rotateMotor(SERVO_MIDPOINT);
}


void loop()
{

}
