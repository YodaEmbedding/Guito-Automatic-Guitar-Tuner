// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Motor control.
// License:     GPLv3


#include "Arduino.h"
#include "Motor.h"


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


// Spin motor at given power
void rotateMotor(int pwr)
{
  // Eh. Get code from some library?
  // Also, remember DC motor has min/max ~127, 255
  // So need to implement directional PID control
  
  if(pwr == 0)
  {
    digitalWrite(PIN_ENABLE, LOW);
    analogWrite(PIN_LEFT, 0);
    analogWrite(PIN_RIGHT, 0);
  }
  else if(pwr > 0)
  {
    digitalWrite(PIN_ENABLE, HIGH);
    analogWrite(PIN_LEFT, pwr);
    analogWrite(PIN_RIGHT, 0);
  }
  else
  {
    // rotate in opposite direction
    digitalWrite(PIN_ENABLE, HIGH);
    analogWrite(PIN_LEFT, 0);
    analogWrite(PIN_RIGHT, -pwr);
  }
}
