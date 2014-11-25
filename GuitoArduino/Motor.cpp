// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Motor control.
// License:     GPLv3


#include "Arduino.h"
#include "Motor.h"


Servo myservo;


// Determine how much string needs to be "removed" (in milimeters)
// based on goal frequency and current frequency
int pitchToDistance(int freq_goal, int freq_curr)
{
  // totally incorrect
  return((freq_goal - freq_curr) / 12);
}


// Formula to convert distance of guitar string to tighten into motor degrees
int distanceToDegrees(int dist)
{
  // totally incorrect
  return(dist * RADIUS * 180.0 / PI);
}


// Formula to convert angularVelocity in degrees to power output
int angularVelocityToPower(double w)
{
  return w * PWR_OVER_W;
  // w * k * (255 - 0) / 100;
}


// Spin motor at given power [-255, 255]
void rotateMotor(int pwr)
{
  int val = constrain(pwr, -255, 255);
  val = map(pwr, -255, 255, SERVO_MIN, SERVO_MAX);

#ifdef SERVO_WRITEUS
  if(pwr == 0)
  {
    myservo.writeMicroseconds(SERVO_MIDPOINT);
  }
  else
  {
    myservo.writeMicroseconds(val);
  }
#else
  if(pwr == 0)
  {
    myservo.write(SERVO_MIDPOINT);
  }
  else
  {
    myservo.write(val);
  }
#endif
}
