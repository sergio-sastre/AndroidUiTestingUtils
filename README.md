[![](https://jitpack.io/v/sergio-sastre/AndroidUiTestingUtils.svg)](https://jitpack.io/#sergio-sastre/AndroidUiTestingUtils)</br>
<a href="https://androidweekly.net/issues/issue-508">
<img src="https://androidweekly.net/issues/issue-508/badge">
</a>


# <p align="center">Android UI testing utils</p>

<p align="center">
<img width="130" src="https://user-images.githubusercontent.com/6097181/172724660-778176b0-a6b0-4aad-b6b4-7115ad4fc7f3.png">
</p>

A set of *TestRules*, *ActivityScenarios* and utils to facilitate UI & screenshot testing under
certain configurations, independent of the UI testing libraries you are using.
<br clear="left"/>
</br></br>
For screenshot testing, it supports:
 - **Jetpack Compose**
 - **android Views** (e.g. custom Views, ViewHolders, etc.)
 - **Activities**
 - **Fragments**
 - [**Robolectric**](https://sergio-sastre.gitbook.io/androiduitestingutils/setup/robolectric-setup)
 - [**Cross-library** & **Shared screenshot testing**](https://sergio-sastre.gitbook.io/androiduitestingutils/setup/cross-library-setup) i.e. same test running either on device or on JVM.
</br>

This library enables you to easily change the following configurations in your UI tests:

1. Locale (also [Pseudolocales](https://developer.android.com/guide/topics/resources/pseudolocales#:~:text=4%20or%20earlier%3A-,On%20the%20device%2C%20open%20the%20Settings%20app%20and%20tap%20Languages,language%20(see%20figure%203)) **en_XA** & **ar_XB**)
    1. App Locale (i.e. per-app language preference)
    2. System Locale
2. Font size
3. Orientation
4. Custom themes
5. Dark mode / Day-Night mode
6. Display size

Wondering why verifying our design under these configurations is important? I've got you covered:

🎨 [Design a pixel perfect Android app](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)
</br></br>

## Documentation
Check out [this library's documentation page](https://sergio-sastre.gitbook.io/androiduitestingutils/) to see how to use it, including code and ready-to-run examples  

## Sponsors

Thanks to [Screenshotbot](https://screenshotbot.io) for their support!
[<img align="left" width="100" src="https://user-images.githubusercontent.com/6097181/192350235-b3b5dc63-e7e7-48da-bdb6-851a130aaf8d.png">](https://screenshotbot.io)

By using Screenshotbot instead of the in-build record/verify modes provided by most screenshot
libraries, you'll give your colleages a better developer experience, since they will not be required
to manually record screenshots after every run, instead getting notifications on their Pull
Requests.
<br clear="left"/>

</br></br>
<a href="https://www.flaticon.com/free-icons/ninja" title="ninja icons">Android UI testing utils
logo modified from one by Freepik - Flaticon</a>

