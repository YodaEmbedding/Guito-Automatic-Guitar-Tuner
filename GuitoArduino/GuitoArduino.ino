// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Receives input pitch (Hz) over Bluetooth.
//              Runs transfer function converting Hz -> degrees and feeds into PID controller.
//              Outputs to motor.
// License:     GPLv3


#include <MeetAndroid.h>
#include <PID_v1.h>
#include "Notes.h"
#include "Comm.h"


MeetAndroid meetAndroid;

const int RADIUS = 10;
const int GEAR_RATIO = 100 / 10;
const double PWR_OVER_W = 255.0 / (180.0 / RADIUS);

// Motor pins
const int PIN_ENABLE = 11;
const int PIN_LEFT = 10;
const int PIN_RIGHT = 5;
bool stopped = false;

int frequency = 0;
int goalFrequency = 440;
int distance = 0;
int power = 0;

// PID variables
double pid_in = 0.0;
double pid_out = 0.0;
double pid_setpoint = 0.0;
PID pid(&pid_in, &pid_out, &pid_setpoint, 1.0, 0.0, 0.0, DIRECT);


void receiveCommand(byte flag, byte numOfValues);
void receivePitch(byte flag, byte numOfValues);


void setup()
{
  // Turn on Bluetooth communication at 9600 baud rate
  Serial.begin(9600);
  meetAndroid.registerFunction(receivePitch, 'A');
  meetAndroid.registerFunction(receiveCommand, 'C');
  
  // Construct tables
  InitializeTuningTable();

  // Set up PID controller
  pid.SetOutputLimits(-255.0, 255.0);
  pid.SetSampleTime(200);
  pid.SetMode(AUTOMATIC);

  // Set motor pins to OUTPUT
  pinMode(PIN_ENABLE, OUTPUT);
  pinMode(PIN_LEFT, OUTPUT);
  pinMode(PIN_RIGHT, OUTPUT);

  stopped = false;
}


void loop()
{
  // Receive pitch from android device
  meetAndroid.receive();
  
  // Transform input frequency into PID controllable input
  // (Transfer function)
  distance = pitchToDistance(goalFrequency, frequency);

  // pid_error = pid_setpoint - pid_in
  pid_setpoint = distanceToDegrees(distance);
  pid_in = 0;



  // change in angle?
  // power   ~= velocity
  // degrees ~= distance
  // Note that power control is virtually instantaneous.
  // So, integral term not needed...? PD control? 
  // 
  // out       = velocity
  // in/setp   = distance
  // YEAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
  // too easy yo
  // 
  // Error term = pitchToDegrees(goal, current)
  // Should I record starting position, or let it vary depending on changing setpoint?
  // Probably latter? but why?? hmmm. Do some math to check.
  // 

  
  if(!stopped)
  {
    // Compute dTheta/dt
    pid.Compute();

    // Convert pid_out to power
    power = angularVelocityToPower(pid_out);

    rotateMotor(power);
  }
  else
  {
    rotateMotor(0); // Coast motors
  }
}


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






