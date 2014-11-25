// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Bluetooth communicaion code.
// License:     GPLv3


// #include <MeetAndriod.h>
#include "Arduino.h"
#include "Comm.h"


void rotateMotor(int);


// Called whenever a command is received over the Bluetooth connection
void receiveCommand(byte flag, byte numOfValues)
{
  static char command[64] = {'\0'};
  meetAndroid.getString(command);

  switch(command[0])
  {
  case 0:
    // null
    break;

  case 'X':
    // Emergency stop
    // Cut power to system (coast motors)
    rotateMotor(0);
    break;

  case 'S':
    // Standard tuning
    goalFrequency = 440;
    stopped = false;
    break;

  case 'N':
    // Tune next string
    break;

  case 'D':
    // Drop D
    break;

  case 'E':
    // Eb
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


