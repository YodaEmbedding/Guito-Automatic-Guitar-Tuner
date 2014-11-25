// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Determine pitch (Hz) of note.
// License:     GPLv3


#ifndef __NOTES_H
#define __NOTES_H


#include "Arduino.h"


// Classes
class Tuning
{
public:
  int pitch;
  double tension;
};


// Musical constants
const int NOTE_A4 = 440;
const double TWELFTH_ROOT_OF_TWO = 1.0594630943593;

// Scales
const String chromatic_scale_sharp[12] = {
  "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
};

const String chromatic_scale_flat[12] = {
  "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"
};

// Tunings
const int TUNING_STANDARD = 0;
const int TUNING_DROPD = 1;
const int TUNING_EFLAT = 2;
const int TUNING_COUNT = 3;

extern int tuning_flag;
extern int currString;
extern int goalFrequency;

// Other constants
const int STRING_COUNT = 6;

// Tables
extern String tuning_notes[];
extern Tuning tuning_table[TUNING_COUNT][STRING_COUNT];


// Prototypes
inline int getFrequency(int i, int flag);
inline int getCurrFrequency();
void setFlag(int flag);
void setString(int i);
void nextString();

bool isValidNoteChar(char c);
int stepsFromA4(String s);
void InitializeTuningTable();


#endif
