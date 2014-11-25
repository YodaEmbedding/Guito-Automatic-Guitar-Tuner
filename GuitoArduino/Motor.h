// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Motor control.
// License:     GPLv3


#ifndef __MOTOR_H
#define __MOTOR_H


extern const int RADIUS;
extern const int GEAR_RATIO;
extern const double PWR_OVER_W;

extern const int PIN_ENABLE;
extern const int PIN_LEFT;
extern const int PIN_RIGHT;



int pitchToDistance(int freq_goal, int freq_curr);
int distanceToDegrees(int dist);
int angularVelocityToPower(double w);
void rotateMotor(int pwr);


#endif
