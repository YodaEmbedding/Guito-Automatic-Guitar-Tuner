// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Receives input pitch (Hz) over Bluetooth.
//              Runs transfer function converting Hz -> degrees and feeds into PID controller.
//              Outputs to motor.
// License:     GPLv3


// #define USB_DEBUG
// #define TEST
#define ENABLE_BT
// #define ENABLE_PID
#define ENABLE_SERVO
// #define SERVO_WRITEUS


#include "Arduino.h"
#include "Notes.h"

// #ifdef ENABLE_SERVO
  #include <Servo.h>
  #include "Motor.h"
// #endif

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
const int AXLE_RADIUS = 5;
const int GEAR_RATIO = 1;
// PWR / w  ~=  255 / (360 * rps)
const double PWR_OVER_W = 255.0 / (360.0 * 0.25);

// Motor
const int PIN_SERVO = 9;

#ifdef SERVO_WRITEUS
const int SERVO_RADIUS = 200;
const int SERVO_MIDPOINT = 1500;
const int SERVO_MIN = SERVO_MIDPOINT - SERVO_RADIUS; // 1300;
const int SERVO_MAX = SERVO_MIDPOINT + SERVO_RADIUS; // 1700;
#else
// Note that actual output commands are modulated
// so that it's between [t-0.2ms, t+0.2ms] pulses + 20ms pulse
// (When using PWM, not PPM)
// This means that if write is using a 1/t relationship,
// radius is not completely symmetrical
// i.e., midpoint+10 may turn at a different speed than midpoint-10
const int SERVO_RADIUS = 18;
const int SERVO_MIDPOINT = 94;
const int SERVO_MIN = SERVO_MIDPOINT - SERVO_RADIUS; // 76;
const int SERVO_MAX = SERVO_MIDPOINT + SERVO_RADIUS; // 112;
#endif

bool stopped = false;
int power = 0;

int frequency = 0;
int distance = 0;

#ifdef ENABLE_PID
// PID variables
double pid_in = 0.0;
double pid_out = 0.0;
double pid_setpoint = 0.0;
PID pid(&pid_in, &pid_out, &pid_setpoint, 1.0, 0.2, 0.5, DIRECT);
#endif


void setup()
{
#ifdef ENABLE_BT
  // Turn on Bluetooth communication at 9600 baud rate
  Serial.begin(9600);
  meetAndroid.registerFunction(receivePitch, 'C');
  meetAndroid.registerFunction(receiveCommand, 'X');
  meetAndroid.registerFunction(receiveCommand, 'T');
  meetAndroid.registerFunction(receiveCommand, 'F');
  meetAndroid.registerFunction(receiveCommand, 'N');
  meetAndroid.registerFunction(receiveCommand, 'P');
#endif

#ifdef USB_DEBUG
  Serial.begin(9600);
#endif

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
#ifdef TEST
  // Generate fake values for frequency
  static int lastTime = millis();
  if((lastTime + 200) <= millis())
  {
    lastTime = millis();

    frequency += (goalFrequency - frequency) / 4;
  }
#else
  #ifdef ENABLE_BT
  // Receive pitch from android device
  meetAndroid.receive();
  #endif
#endif
  
#ifdef ENABLE_SERVO
  // Transform input frequency into PID controllable input
  // (Transfer function)
  distance = pitchToDistance(goalFrequency, frequency);
#endif

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
#endif

  if(!stopped)
  {
#ifdef ENABLE_PID
    // Compute dTheta/dt
    pid.Compute();

    // Convert pid_out to power
    power = angularVelocityToPower(pid_out);
#else
    if((frequency >= (goalFrequency - 1)) && (frequency <= (goalFrequency + 1)))
    {
      power = 0;
    }
    else
    {
      power = constrain((goalFrequency - frequency) * 10, -255, 255);
    }
#endif
    
#ifdef ENABLE_SERVO
    rotateMotor(power);
#endif

#ifdef ENABLE_BT
    static int lastSent = millis();
    if((lastSent + 500) <= millis())
    {
      lastSent = millis();

      meetAndroid.send(power);
    }
#endif
  }
  else
  {
#ifdef ENABLE_SERVO
    rotateMotor(0);
    // myservo.detach(); // Coast motors?
#endif
  }
}

