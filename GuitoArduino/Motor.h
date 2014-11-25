// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Motor control.
// License:     GPLv3


#ifndef __MOTOR_H
#define __MOTOR_H


#include "Arduino.h"
#include <Servo.h>


const int DEG_OVER_RAD = 180.0 / PI;

extern const int RADIUS;
extern const int AXLE_RADIUS;
extern const int GEAR_RATIO;
extern const double PWR_OVER_W;

extern const int PIN_SERVO;
extern const int PIN_ENABLE;
extern const int PIN_LEFT;
extern const int PIN_RIGHT;

extern const int SERVO_MIN;
extern const int SERVO_MAX;
extern const int SERVO_MIDPOINT;


extern Servo myservo;


int pitchToDistance(int freq_goal, int freq_curr);
int distanceToDegrees(int dist);
int angularVelocityToPower(double w);
void rotateMotor(int pwr);


#endif
