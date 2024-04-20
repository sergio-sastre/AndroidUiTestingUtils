# Cross-Library setup

This library provides support for running the very same screenshot tests for your **Composables** or **Android Views** across different libraries, without rewriting them!

I wrote about why you might need it and how _AndroidUiTestingUtils_ supports this in these 2 blog posts:

* 🌎 [A World Beyond Libraries: Cross-Library screenshot tests on Android](https://sergiosastre.hashnode.dev/a-world-beyond-libraries-cross-library-screenshot-tests-on-android)
* 📚 [Write Once, Test Everywhere: Cross-Library Screenshot Testing with AndroidUiTestingUtils 2.0.0](https://sergiosastre.hashnode.dev/write-once-test-everywhere-cross-library-screenshot-testing-with-androiduitestingutils)

Currently, it provides out-of-the-box support for the following screenshot testing libraries:

* [Paparazzi](https://github.com/cashapp/paparazzi)&#x20;
* [Shot](https://github.com/pedrovgs/Shot)
* [Dropshots](https://github.com/dropbox/dropshots)
* [Roborazzi](https://github.com/takahirom/roborazzi)
* [Android-Testify](https://github.com/ndtp/android-testify)

{% hint style="info" %}
Out-of-the-box support for Facebook [screenshot-tests-for-android](https://github.com/facebook/screenshot-tests-for-android) and QuickBird Studios [snappy](https://github.com/QuickBirdEng/kotlin-snapshot-testing) is on the roadmap.
{% endhint %}

You can also add support for your own solution / another library in 2 steps by&#x20;

1. Implementing `ScreenshotTestRuleForComposable` or `ScreenshotTestRuleForView` interfaces and&#x20;
2. Using that implementation in `ScreenshotLibraryTestRuleForComposable` or `ScreenshotLibraryTestRuleForView` respectively, as we'll see below.

## **Basic configuration**

This section covers the basics: how to configure cross-library screenshot tests that will run with one library, the one of your choice.

The main benefit is, that **to switch to another library you won't need to rewrite any of these tests**!

{% hint style="info" %}
To choose the library the test run with dynamically and/or for shared screenshot tests (i.e. running either on-device or on JVM), continue reading [the next section](cross-library-setup.md#shared-tests) after this one.
{% endhint %}

You can achieve this in 4 easy steps:

1. Firstly, configure the screenshot testing library you want your tests to support, as if you'd write them with that specific library. Visit its respective Github page for more info.\
   \
   It's recommended to use the AndroidUiTestingUtils version that corresponds to the screenshot library version you're using:

| AndroidUiTestingUtils | Roborazzi     | Paparazzi | Dropshots | Shot        | Android-Testify |
| --------------------- | ------------- | --------- | --------- | ----------- | --------------- |
| _**2.3.2**_           | _**1.12.0**_  | 1.3.3     | 0.4.1     | 6.1.0       | 2.0.0           |
| 2.3.1                 | 1.11.0        | 1.3.3     | 0.4.1     | 6.1.0       | 2.0.0           |
| 2.3.0                 | 1.10.1        | 1.3.3     | 0.4.1     | _**6.1.0**_ | 2.0.0           |
| 2.2.x                 | 1.8.0-alpha-6 | 1.3.2     | 0.4.1     | 6.0.0       | 2.0.0           |
| 2.1.0                 | 1.8.0-alpha-6 | 1.3.1     | 0.4.1     | 6.0.0       | 2.0.0           |
| 2.0.1                 | 1.7.0-rc-1    | 1.3.1     | 0.4.1     | 6.0.0       | -               |
| 2.0.0                 | 1.5.0-rc-1    | 1.3.1     | 0.4.1     | 6.0.0       | -               |

{% hint style="info" %}
AndroidUiTestingUtils 2.3.1+ uses Robolectric 4.12.1 under the hood, which adds support for Native Graphics on Windows, and therefore Roborazzi!
{% endhint %}

{% hint style="warning" %}
If having troubles with Paparazzi, beware of the [release notes of 1.3.2](https://github.com/cashapp/paparazzi/releases/tag/1.3.2)
{% endhint %}

2. After that, include the following dependencies in the `build.gradle` of the module that will include your cross-library screenshot tests.

```groovy
dependencies {
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:utils:<version>'

    // NOTE: From here down, add only those for the libraries you're planning to use

    // For Shot support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:shot:<version>'

    // For Dropshots support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:dropshots:<version>'
    
    // For Android-Testify support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:android-testify:<version>'

    // For Paparazzi support:
    //   2.2.0 -> AGP 8.1.1+
    // < 2.2.0 -> AGP 8.0.0+
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:mapper-paparazzi:<version>'
    testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:paparazzi:<version>'

    // For Roborazzi support
    debugImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:mapper-roborazzi:<version>'
    testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:robolectric:<version>'
    testImplementation 'com.github.sergio-sastre.AndroidUiTestingUtils:roborazzi:<version>'
}
```

3. Create the corresponding `ScreenshotLibraryTestRuleForComposable` or `ScreenshotLibraryTestRuleForView`, for instance:

```kotlin
class MyLibraryScreenshotTestRule(
    override val config: ScreenshotConfigForComposable,
) : ScreenshotLibraryTestRuleForComposable(config) {

    override fun getScreenshotLibraryTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRule {
        // The TestRule that uses the desired library. Could also be 
        // roborazziScreenshotTestRule, dropshotsScreenshotTestRule...
        // or even your own rule
        return paparazziScreenshotTestRule
    }
}
```

Executing your screenshot tests with another library will just require that you change the _ScreenshotLibraryTestRule_ accordingly!

4. Finally, write your tests with that `MyLibraryScreenshotTestRule`. \
   Put them under the corresponding folder, i.e `unitTest` (e.g. Roborazzi & Paparazzi) or `androidTest`(e.g. Dropshots, Shot, Android-Testify). \
   For an example, see [this section](../usage/cross-library-usage.md).

Want to try it out? Check out these executable examples:

* [Cross-library screenshot test example](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/src/sharedTest/java/com/example/road/to/effective/snapshot/testing/lazycolumnscreen/crosslibrary/CoffeeDrinkAppBarComposableTest.kt)
* [Parameterized Cross-library screenshot test example](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/src/sharedTest/java/com/example/road/to/effective/snapshot/testing/lazycolumnscreen/crosslibrary/parameterized/CoffeeDrinkListComposableParameterizedTest.kt)

### Roborazzi

If using Roborazzi or a Robolectric based library, enable _robolectric native graphics_ through Gradle as well.\
\
Optionally, you can also enable hardware native graphics via Gradle to render shadows and elevation!

```groovy
android {
    testOptions {
        ...
        unitTests {
            ...
            all {
                systemProperty 'robolectric.graphicsMode', 'NATIVE'
                
                // Enable hardware rendering to display shadows and elevation. 
                // Still experimental. Supported only on API 31+
                systemProperty 'robolectric.screenshot.hwrdr.native', 'true'
            }
        }
    }
}
```

{% hint style="danger" %}
Hardware native Graphics requires AndroidUiTestingUtils 2.3.1+, which use Robolectric 4.12.1 under the hood.
{% endhint %}

### **Android-Testify**

If using Android-Testify, you also need to define the annotation it uses to identify screenshot tests in the Android-Testify gradle plugin as follows

```groovy
testify {
    ...
    screenshotAnnotation 'sergio.sastre.uitesting.utils.crosslibrary.annotations.CrossLibraryScreenshot'
}
```

{% hint style="info" %}
Annotate your Cross-Library screenshot tests with it to run them with Android-Testify
{% endhint %}

## **Shared tests**

These are tests that can run either on the JVM or on a device/emulator. For that, you have to share resources between Unit Tests and Android Tests.

The easiest way is to add this in the `build.gradle` of the module where you'll write shared tests and then write your screenshot tests under `src/sharedTest`,

```groovy
android {
    ...
    sourceSets {
        test {
            java.srcDir 'src/sharedTest/java'
        }
        androidTest {
            java.srcDir 'src/sharedTest/java'
        }
    }
}
```

{% hint style="warning" %}
Android Studio might show errors if `sharedTests` are defined in an application module. Consider creating a separate library module for testing the UI of your application module.
{% endhint %}

Now follow steps 1. & 2. as in the [Basic configuration](cross-library-setup.md#basic-configuration) section for each library. After that:

3. Create the corresponding `SharedScreenshotLibraryTestRuleForComposable` or `SharedScreenshotLibraryTestRuleForView`, for instance:

```kotlin
class CrossLibraryScreenshotTestRule(
    override val config: ScreenshotConfigForComposable,
) : SharedScreenshotLibraryTestRuleForComposable(config) {

    override fun getJvmScreenshotTestRule(config: ScreenshotConfigForComposable): ScreenshotTestRule {
        return paparazziScreenshotTestRule // or roborazziScreenshotTestRule, or your own rule
    }

    override fun getInstrumentedScreenshotTestRule(config: ScreenshotConfigForComosable): ScreenshotTestRule {
        return dropshotsScreenshotTestRule // or shotScreenshotTestRule, androidTestifyScreenshotTestRule, or your own rule
    }
}
```

4. Finally, write your tests with the `CrossLibraryScreenshotTestRule`. For an example, see [this section](../usage/cross-library-usage.md).\
   Put them under the `sharedTest` folder we've just defined.

{% hint style="info" %}
This is likely the most common use case. There are Ready-To-Run examples available

* [Shot + Roborazzi](https://github.com/sergio-sastre/Android-screenshot-testing-playground/tree/master/dialogs/shot%2Broborazzi)
* [Android-Testify + Paparazzi](https://github.com/sergio-sastre/Android-screenshot-testing-playground/tree/master/lazycolumnscreen/android-testify+paparazzi)
* [Dropshots + Roborazzi](https://github.com/sergio-sastre/Android-screenshot-testing-playground/tree/master/recyclerviewscreen/dropshots%2Broborazzi)
{% endhint %}

### Pick the library dynamically

If you want to use&#x20;

* Many On-device libraries (e.g. either **Shot**, **Dropshots** or **Android-Testify**)&#x20;

and/or

* Many JVM libraries (e.g.. either **Paparazzi** or **Roborazzi**).

you need to dynamically pick the library your screenshot tests run with.&#x20;

For that you'll need some extra configuration, for instance, a custom Gradle property that you can pass via command line e.g.

* &#x20;`-PscreenshotLibrary=shot`

Check these links for advice on how to configure the Gradle file and the `SharedScreenshotTestRule` to get it working:

* [build.gradle](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/build.gradle)
* [CrossLibraryScreenshotTestRule.kt](https://github.com/sergio-sastre/Android-screenshot-testing-playground/blob/master/lazycolumnscreen/crosslibrary/src/sharedTest/java/com/example/road/to/effective/snapshot/testing/lazycolumnscreen/crosslibrary/utils/CrossLibraryScreenshotTestRule.kt)
