# AutoVolume Android SDK Library

As of now, there exists no library for setting an auto-volume option like auto-brightness in Android. We propose a library which allows any android app developer to set the application volume based on the ambient noise to the device.
This library uses the AudioRecord library from android and uses STA/LTA algorithm on the volume measured. The algorithm is implemented on a decibel scale like in this [article](https://www.quicklogic.com/wp-content/uploads/2018/12/QL-Auto-Brightness-How-to-Improve-Android-OS-in-Handheld-Devices-White-Paper.pdf).

## To use Library

Steps:
1. Clone the Repository:

```
git clone https://github.com/madhavpcm/AutoVolume
```
2. Include the Library Module in Your Project:

    Copy the AutoVolume folder from the Project and add to your application

3. Add to settings.gradle of your app
```
include(":AutoVolume")
```
   
4. Add the Dependency in Your build.gradle:

In your app's build.gradle file, add a dependency on the library module:

```
implementation(project(":AutoVolume"))
```
5. Sync Your Project:

   Sync your Gradle project to include the new module.

## Add in app code
1. Add in your activity
```kotlin
class YourAppActivity : AnyExistingActivies(), AmbientSoundSensor.OnNoiseDetectedListener
```
2. Initialise Ambient Sound Sensor
```
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // yourViewModel.isAutoVolumeEnabled() should be a MutableStateFlow<Boolean> 
        // indicating UI to enable/disable ambient sound listening
        ambientSoundSensor = AmbientSoundSensor(this, this, yourViewModel.isAutoVolumeEnabled())

        setContent {
            MainScreen(viewModel = yourViewModel)
        }
    }
```
3. Override onNoiseDetected
```
override fun onNoiseDetected(staLtaValue: Double) {
        yourViewModel.updateNoiseLevel(staLtaValue) // your UI updates
}
```
- some_callback would be a class which has an onNoiseDetected() callback implemented
- toggle could be used enable or disable the auto volume

## Timeline
- Initial template code for Kotlin library based project with Demo App created.
- Ambient Sound sensing intial algorithm developed.
- Adding basic UI to interact with library.
- Fine tuning algorithm for real world scenario

## Challenges Faced:
- Figuring out the values for implementing STA LTA based algorithm for Ambient Sound Sensing.
- Fine tuning for real world noise scenarios.
- Both of us haven't worked on Kotlin before :)

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
