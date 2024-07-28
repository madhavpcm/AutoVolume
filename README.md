# AutoVolume Android SDK Library

As of now, there exists no library for setting an auto-volume option like auto-brightness in Android. We propose a library which allows any android app developer to set the application volume based on the ambient noise to the device.
This library uses the AudioRecord library from android and uses STA/LTA algorithm on the volume measured. The algorithm is implemented on a decibel scale like in this [article](https://www.quicklogic.com/wp-content/uploads/2018/12/QL-Auto-Brightness-How-to-Improve-Android-OS-in-Handheld-Devices-White-Paper.pdf).

## Dev experience
```kotlin
        ambientSoundSensor = AmbientSoundSensor(<app_context>, <some_callback>, <toggle>)
```
- some_callback would be a class which has an onNoiseDetected() callback implemented
- toggle could be used enable or disable the auto volume

## Improvement
As the sensitivity and properties of mic's across devices vary, the library potential to vary setters for

- `minRMS`: minimum volume level threshold
- `maxRMS`: maximum volume level threshold
- `volumeAdjustmentFactor() (based on stalta)`: STALTA (of the queue of RMS values) value calculated can be used scale the RMS value
- `queueSize`: The size of queue K, holding K previous RMS values

By default the sampling rate is 44100Hz, so in one second there can be 44100 RMS Values.


## Why should I leave my mic on?
ANC devices having mics, which are almost always on :D, most people find it totally OK to use such devices in their day to day life. For these devices, this library will be surely of use, which can set the volume level media playback based on ambient noise.
With scientific testing and development, ensuring audio playback at a nominal level which is not hazardous to the human ear can be ensured.

At the moment we did not find any way to directly access the ANC mic from Android SDK, and are currently using the normal mic with AudioRecorder (android sdk).
This library can also be better implemented in the android-ndk for better performance.

## Demo link:

https://www.loom.com/share/bc48930e2af944eab81a7d5f9c51ded8?sid=424993b5-2843-4fa7-af37-2bc1bbdc7205
