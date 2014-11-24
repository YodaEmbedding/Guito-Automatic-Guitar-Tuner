// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Receives input pitch (Hz) over Bluetooth.
//              Runs transfer function converting Hz -> degrees and feeds into PID controller.
//              Outputs to motor.


#include <MeetAndroid.h>
#include <PID_v1.h>


MeetAndroid meetAndroid;

const int RADIUS = 10;
const int GEAR_RATIO = 100 / 10;

// Motor pins
const int PIN_ENABLE = 11;
const int PIN_LEFT = 10;
const int PIN_RIGHT = 5;
bool stopped = false;

int frequency = 0;
int goalFrequency = 440;
int distance = 0;

// PID variables
double pid_in = 0.0;
double pid_out = 0.0;
double pid_setpoint = 0.0;
PID pid(&pid_in, &pid_out, &pid_setpoint, 1.0, 0.0, 0.0, DIRECT);


void setup()
{
  // Turn on Bluetooth communication at 9600 baud rate
  Serial.begin(9600);
  meetAndroid.registerFunction(receivePitch, 'A');
  meetAndroid.registerFunction(receiveCommand, 'C');
  
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

  // Totally incorrect variable settings yo, wtf is dis. Fix it!!!!1
  pid_setpoint = distanceToDegrees(distance);
  pid_in = pid_setpoint - distanceToDegrees(distance);

  // change in angle?
  // power   ~= velocity
  // degrees ~= distance
  // Note that power control is virtually instantaneous.
  // So, integral term not needed...? PD control? 
  //
  // pid_in/out == power?
  // pid_setpoint == degrees?
  // Error term = pitchToDegrees(goal, current)
  // Should I record starting position, or let it vary depending on changing setpoint?
  // Probably latter? but why?? hmmm. Do some math to check.
  // 

  
  if(!stopped)
  {
    // Compute how much to turn the motor, and output.
    pid.Compute();
    rotateMotor(pid_out);
  }
  else
  {
    rotateMotor(0); // Coast motors
  }
}


// Called whenever a command is received over the Bluetooth connection
void receiveCommand(byte flag, byte numOfValues)
{
  string command = meetAndroid.getString();

  switch(string[0])
  {
  case 0:
    // null
    break;

  case 'X':
    // Emergency stop
    // Cut power to system (coast motors)
    break;

  case 'S':
    // Standard tuning
    break;

  case 'N':
    // Tune next string
    break;

  default:
    break;
  }
}


// Called whenever a value is received over the Bluetooth connection
void receivePitch(byte flag, byte numOfValues)
{
  frequency = meetAndroid.getInt();
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


// Spin motor at given power
void rotateMotor(int power)
{
  // Eh. Get code from some library?
  // Also, remember DC motor has min/max ~127, 255
  // So need to implement directional PID control
  
  if(power == 0)
  {
    digitalWrite(PIN_ENABLE, LOW);
    analogWrite(PIN_LEFT, 0);
    analogWrite(PIN_RIGHT, 0);
  }
  else if(power > 0)
  {
    digitalWrite(PIN_ENABLE, HIGH);
    analogWrite(PIN_LEFT, power);
    analogWrite(PIN_RIGHT, 0);
  }
  else
  {
    // rotate in opposite direction
    digitalWrite(PIN_ENABLE, HIGH);
    analogWrite(PIN_LEFT, 0);
    analogWrite(PIN_RIGHT, -power);
  }
}

