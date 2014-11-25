// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Receives input pitch (Hz) over Bluetooth.
//              Runs transfer function converting Hz -> degrees and feeds into PID controller.
//              Outputs to motor.
// License:     GPLv3


// #define TEST
#define ENABLE_BT
// #define ENABLE_PID
#define ENABLE_SERVO


#include "Arduino.h"
#include <Servo.h>
#include "Motor.h"
#include "Notes.h"


#ifdef ENABLE_PID
#include <PID_v1.h>
#endif


#ifdef ENABLE_BT
#include <MeetAndroid.h>
#include "Comm.h"
#endif


#ifdef ENABLE_BT
MeetAndroid meetAndroid;
#endif


// TORQUE = 38 oz-in torque @ 6 V
const int RADIUS = 30;
const int GEAR_RATIO = 1;
// PWR / w  ~=  255 / (360 * rps)
const double PWR_OVER_W = 255.0 / (360.0 * 0.25);


// Motor
/*
DC Motor
const int PIN_ENABLE = 11;
const int PIN_LEFT = 10;
const int PIN_RIGHT = 5;
*/
const int PIN_SERVO = 9;
const int SERVO_MIN = 75;
const int SERVO_MAX = 113;
const int SERVO_MIDPOINT = 94;
bool stopped = false;

int frequency = 0;
int goalFrequency = 440;
int distance = 0;
int power = 0;

#ifdef ENABLE_PID
// PID variables
double pid_in = 0.0;
double pid_out = 0.0;
double pid_setpoint = 0.0;
PID pid(&pid_in, &pid_out, &pid_setpoint, 1.0, 0.0, 0.0, DIRECT);
#endif


void setup()
{
#ifdef ENABLE_BT
  // Turn on Bluetooth communication at 9600 baud rate
  Serial.begin(9600);
  meetAndroid.registerFunction(receivePitch, 'A');
  meetAndroid.registerFunction(receiveCommand, 'C');
#endif


#ifdef USB_DEBUG
  Serial.begin(9600);
#endif

  // Construct tables
  InitializeTuningTable();

#ifdef ENABLE_PID
  // Set up PID controller
  pid.SetOutputLimits(-255.0, 255.0);
  pid.SetSampleTime(200);
  pid.SetMode(AUTOMATIC);
#endif

  // Turn on servo motor
#ifdef ENABLE_SERVO
  myservo.attach(PIN_SERVO);
#endif

  stopped = false;
}


void loop()
{
  // Receive pitch from android device
#ifdef TEST

  static int lastTime = millis();
  if(lastTime + 200 < millis())
  {
    lastTime = millis();

    frequency += (goalFrequency - frequency) / 4;
  }

#else
  #ifdef ENABLE_BT
  meetAndroid.receive();
  #endif
#endif
  

  // Transform input frequency into PID controllable input
  // (Transfer function)
  distance = pitchToDistance(goalFrequency, frequency);

#ifdef ENABLE_PID
  // pid_error = pid_setpoint - pid_in
  pid_setpoint = distanceToDegrees(distance);
  pid_in = 0;


  // power   ~= velocity
  // degrees ~= distance
  // Note that power control is virtually instantaneous.
  // So, integral term not needed...? PD control? 
  // 
  // out       = velocity
  // in/setp   = distance
  // 
  // Error term = pitchToDegrees(goal, current)
  // Should I record starting position, or let it vary depending on changing setpoint?
  // Probably latter? but why?? hmmm. Do some math to check.
#endif


  if(!stopped)
  {
#ifdef ENABLE_PID
    // Compute dTheta/dt
    pid.Compute();

    // Convert pid_out to power
    power = angularVelocityToPower(pid_out);
#else

    if(frequency >= goalFrequency - 1 && frequency <= goalFrequency + 1)
    {
      power = 0;
    }
    else
    {
      power = constrain((goalFrequency - frequency) * 10, -255, 255);
    }

#endif



    meetAndroid.send(power);
    
#ifdef ENABLE_SERVO
    rotateMotor(power);
#endif
  }
  else
  {
    rotateMotor(0);
    // myservo.detach(); // Coast motors
  }
}

