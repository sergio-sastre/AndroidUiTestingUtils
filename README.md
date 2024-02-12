# Overview

[![](https://jitpack.io/v/sergio-sastre/AndroidUiTestingUtils.svg)](https://jitpack.io/#sergio-sastre/AndroidUiTestingUtils)\
[![](https://androidweekly.net/issues/issue-508/badge)](https://androidweekly.net/issues/issue-508)

<div align="center" data-full-width="true">

<img src="https://user-images.githubusercontent.com/6097181/172724660-778176b0-a6b0-4aad-b6b4-7115ad4fc7f3.png" alt="" width="130">

</div>

A set of _TestRules_, _ActivityScenarios_ and utils to facilitate UI & screenshot testing under certain configurations, independent of the UI testing libraries you are using.\
\
For screenshot testing, it supports:

* **Jetpack Compose**
* **Android Views** (e.g. custom Views, ViewHolders, etc.)
* **Activities**
* **Fragments**
* [**Robolectric**](setup/robolectric-setup.md)
* [**Cross-library** & **Shared screenshot testing**](setup/cross-library-setup.md) i.e. same test running either on device or on JVM.

This library enables you to easily change the following configurations in your UI tests:

1. Locale (also [Pseudolocales](https://developer.android.com/guide/topics/resources/pseudolocales) **en\_XA** & **ar\_XB**)
   1. App Locale (i.e. per-app language preference)
   2. System Locale
2. Font size
3. Orientation
4. Custom themes
5. Dark mode / Day-Night mode
6. Display size

Wondering why verifying our design under these configurations is important? I've got you covered:

🎨 [Design a pixel perfect Android app](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)

## Sponsors

<figure><img src=".gitbook/assets/192350235-b3b5dc63-e7e7-48da-bdb6-851a130aaf8d.png" alt="" width="100"><figcaption></figcaption></figure>

Thanks to [Screenshotbot](https://screenshotbot.io) for their support!&#x20;

By using Screenshotbot instead of the in-build record/verify modes provided by most screenshot libraries, you'll give your colleagues a better developer experience, since they will not be required to manually record screenshots after every run, instead getting notifications on their Pull Requests.

## Reading on screenshot testing

* [An introduction to snapshot testing on Android in 2021 📸](https://sergiosastre.hashnode.dev/an-introduction-to-snapshot-testing-on-android-in-2021)
* [The secrets of effectively snapshot testing on Android 🔓](https://sergiosastre.hashnode.dev/the-secrets-of-effectively-snapshot-testing-on-android)
* [UI tests vs. snapshot tests on Android: which one should I write? 🤔](https://sergiosastre.hashnode.dev/ui-tests-vs-snapshot-tests-on-android-which-one-should-i-write)
* [Design a pixel perfect Android app 🎨](https://sergiosastre.hashnode.dev/design-a-pixel-perfect-android-app-with-screenshot-testing)

## Code attributions

This library has been possible due to the work others have done previously. Most TestRules are based on code written by others:

* SystemLocaleTestRule -> [Screengrab](https://github.com/fastlane/fastlane/tree/master/screengrab/screengrab-lib/src/main/java/tools.fastlane.screengrab/locale)
* FontSizeTestRule -> [Novoda/espresso-support](https://github.com/novoda/espresso-support/tree/master/core/src/main/java/com/novoda/espresso)
* UiModeTestRule -> [AdevintaSpain/Barista](https://github.com/AdevintaSpain/Barista)
* Orientation change for activities -> [Shopify/android-testify](https://github.com/Shopify/android-testify/)
* MeasureViewHelpers -> a copy of ViewHelpers from Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android)

Moreover, to enable cross-library screenshot tests this library uses the following screenshot testing libraries under the hood:

* [Paparazzi](https://github.com/cashapp/paparazzi)
* [Shot](https://github.com/pedrovgs/Shot)
* [Dropshots](https://github.com/dropbox/dropshots)
* [Roborazzi](https://github.com/takahirom/roborazzi)
* [Android-testify](https://github.com/Shopify/android-testify/)

## Contributing

1. Create an issue in this repo
2. Fork the repo [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground)
3. In that repo, add an example and test where the bug is reproducible/ and showcasing the new feature.
4. Once pushed, add a link to the PR in the issue created in this repo and add @sergio-sastre as a reviewer.
5. Once reviewed and approved, create an issue in this repo.
6. Fork this repo and add the approved code from the other repo to this one (no example or test needed). Add @sergio-sastre as a reviewer.
7. Once approved, I will merge the code in both repos, and you will be added as a contributor to [Android UI testing utils](https://github.com/sergio-sastre/AndroidUiTestingUtils) as well as [Android screenshot testing playground](https://github.com/sergio-sastre/Android-screenshot-testing-playground).

I'll try to make the process easier in the future if I see many issues/feature requests incoming.

\
\
[Android UI testing utils logo modified from one by Freepik - Flaticon](https://www.flaticon.com/free-icons/ninja)
