// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Determine pitch (Hz) of note.
// License:     GPLv3


#include "Arduino.h"
#include "Notes.h"


Tuning currTuning[STRING_COUNT];

int concertPitch = 440;
int currString = 0;
int goalFrequency = 440;


// Get frequency from table
inline int getFrequency(int i)
{
  if((i >= STRING_COUNT) || (i < 0))
    return(-1);
  
  return(currTuning[i].pitch);
}


// Get current string frequency
inline int getCurrFrequency()
{
  return(getFrequency(currString));
}


// Set current string index
void setString(int i)
{
  currString = i;
  goalFrequency = getCurrFrequency();
}


// Move to next string
void nextString()
{
  ++currString;
  goalFrequency = getCurrFrequency();
}


// Checks if valid character
// (a-z)(A-Z)(0-9)(#)
bool isValidNoteChar(char c)
{
  if(
    ((c >= 'a') && (c <= 'z')) ||
    ((c >= 'A') && (c <= 'Z')) ||
    ((c >= '0') && (c <= '9')) ||
    (c == '#')
  )
  {
    return true;
  }

  return false;
}


// Accepts note string and returns steps from A4
int stepsFromA4(String s)
{
#ifdef NOTES_FROM_TABLE
#else
  int i = 0;
  int n = 0;
  int octave = 4;
  String letter = "A";

  // Check for empty string
  if(s.length() == 0)
  {
    return 0;
  }

  // Get letter and convert to uppercase
  letter[0] = s[i++];
  letter.toUpperCase();

  // Check if sharp, flat, or natural
  // Find note in scale
  // Add index to n
  if(s[i] == '#')
  {
    letter = letter + s[i++];

    for(int j = 0; j < 12; ++j)
    {
      if(letter.equals(chromatic_scale_sharp[j]))
      {
        n = j;
        break;
      }
    }
  }
  else
  {
    letter = letter + s[i++];

    for(int j = 0; j < 12; ++j)
    {
      if(letter.equals(chromatic_scale_flat[j]))
      {
        n = j;
        break;
      }
    }
  }

  n -= 9; // Center at index of "A" (9)

  octave = s.substring(i).toInt();

  n += (octave - 4) * 12;
#endif


  return 0;
}


// Converts s into t
// Accepts s of format:
// <Concert Pitch>, <Notes>
// Example:
// s = "440, E2 A2 D3 G3 B3 E4"
void parseNoteString(Tuning t[], String s) 
{
#ifdef NOTES_FROM_TABLE
#else
  int j = 0; // position in string
  int k = 0; // guitar string #

  int len = s.length();

  String note = "";

  // Get A4
  for(; j < len; ++j)
  {
    if(s[j] == ',')
    {
      ++j;
      break;
    }

    note = note + s[j];
  }

  concertPitch = note.toInt();

  // Parse string
  for(; j < len;)
  {
    // Skip invalid characters like , and space
    if(!isValidNoteChar(s[j]))
    {
      ++j;
      continue;
    }

    // Get note
    for(note = ""; isValidNoteChar(s[j]); ++j)
    {
      note = note + s[j];
    }

    t[k].pitch = concertPitch * pow(TWELFTH_ROOT_OF_TWO, stepsFromA4(note));
    ++k;

    // T (Tension) = (UW x (2 x L x F)2) / 386.4
    // t[k].tension = ????? WTFF?????;
  }
#endif
}


// Converts s into t
// Accepts s of format:
// <Concert Pitch>, <Notes>
// Example:
// s = "440, 1 2 3 4 5 6"
void parsePitchString(Tuning t[], String s)
{
  int j = 0; // position in string
  int k = 0; // guitar string #

  int len = s.length();

  String note = "";

  // Get concert pitch
  for(; j < len; ++j)
  {
    if(s[j] == ',')
    {
      ++j;
      break;
    }

    note = note + s[j];
  }
  
  concertPitch = note.toInt();

  // Get tuning pitches
  for(; j < len;)
  {
    // Skip invalid characters like , and space
    if(!isValidNoteChar(s[j]))
    {
      ++j;
      continue;
    }

    // Get note
    for(note = ""; isValidNoteChar(s[j]); ++j)
    {
      note = note + s[j];
    }

    t[k].pitch = note.toInt();
    ++k;

    // T (Tension) = (UW x (2 x L x F)2) / 386.4
    // t[k].tension = ????? WTFF?????;
  }
}

