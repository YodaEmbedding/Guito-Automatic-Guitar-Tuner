#include <MeetAndroid.h>


class PID
{
  double p;
  double i;
  double d;
  
  PID()
  {
    p = i = d = 0.0;
  }
  
  void step(double kp, double ki, double kd)
  {
    
  }
};


MeetAndroid meetAndroid;
PID pid_terms;
int frequency = 0;
int distance = 0;
const int RADIUS = 10;
const int GEAR_RATIO = 100 / 10;


void setup()
{
  Serial.begin(9600);
  meetAndroid.registerFunction(receivePitch, 'A');
}

void loop()
{
  meetAndroid.receive();
  distance = pitchToDistance(frequency);
  getPID(pid_terms, 0, 0, 0);
}

// Called whenever a value is received over the Bluetooth connection
void receivePitch(byte flag, byte numOfValues)
{
  frequency = meetAndroid.getInt();
}

// Formula to convert pitch to distance needed to travel (in milimeters)
// Destination frequency, current frequency
int pitchToDistance(int freq_dest, int freq_curr)
{
  // totally incorrect
  return((freq_dest - freq_curr) / 12);
}

// Formula to convert distance of guitar string to tighten into motor degrees
int distanceToDegrees(int dist)
{
  // totally incorrect
  return(dist * RADIUS / GEAR_RATIO);
}

int transferFunc()
{
  
}

void getPID(PID &terms, double Kp, double Ki, double Kd)
{
  
}

void rotateMotor()
{
  
}

