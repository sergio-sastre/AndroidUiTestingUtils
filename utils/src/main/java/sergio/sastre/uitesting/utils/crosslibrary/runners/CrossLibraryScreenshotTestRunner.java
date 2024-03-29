/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sergio.sastre.uitesting.utils.crosslibrary.runners;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * A JUnit4 runner for Cross-Library screenshot tests.
 *
 * When running on the JVM, it will work out of the box in 99% of the use cases.
 * There could be some issues though, if you have Paparazzi dependency/plugin in your module,
 * but want to use Roborazzi for screenshot tests.
 *
 * In such cases, you can enforce this runner to use the correct underlying runner for the
 * screenshot testing library in use (e.g. Roborazzi needs a RobolectricTestRunner, but Paparazzi doesn't)
 * by setting the system property "screenshotLibrary" to "paparazzi or "roborazzi" respectively.
 *
 * You can do this either programmatically in your test code:
 *
 *    System.setProperty("screenshotLibrary","roborazzi")
 *
 * or in gradle:
 *
 *   testOptions {
 *      unitTests.all {
 *         systemProperty "screenshotLibrary", "roborazzi"
 *      }
 *   }
 *
 *
 * <p>This runner offers several features on top of the standard JUnit4 runner,
 *
 * <ul>
 *   <li>Supports running on Robolectric. This implementation will delegate to RobolectricTestRunner
 *       if test is running in Robolectric enviroment. A custom runner can be provided by specifying
 *       the full class name in a 'android.junit.runner' system property.
 *   <li>Supports a per-test timeout - specfied via a 'timeout_msec' {@link
 *       androidx.test.runner} argument.
 *   <li>Supports running tests on the application's UI Thread, for tests annotated with {@link
 *       androidx.test.annotation}.
 * </ul>
 *
 * <p>Usage {@code @RunWith(AndroidJUnit4.class)}
 */
public final class CrossLibraryScreenshotTestRunner extends Runner implements Filterable, Sortable {

    private static final String TAG = "CrossLibraryScreenshotTest";

    private final Runner delegate;

    public CrossLibraryScreenshotTestRunner(Class<?> klass) throws InitializationError {
        delegate = loadRunner(klass);
    }

    private static String getRunnerClassName() {
        String runnerClassName = System.getProperty("android.junit.runner", null);
        if (runnerClassName != null) {
            return runnerClassName;
        }
        if (!System.getProperty("java.runtime.name").toLowerCase().contains("android")) {
            String screenshotLibrary = System.getProperty("screenshotLibrary");
            if (screenshotLibrary != null) {
                if (screenshotLibrary.equals("paparazzi")) {
                    return "org.junit.runners.BlockJUnit4ClassRunner";
                } else if (screenshotLibrary.equals("roborazzi")) {
                    return "org.robolectric.RobolectricTestRunner";
                }
            }
            if (hasClass("app.cash.paparazzi.Paparazzi")) {
                return "org.junit.runners.BlockJUnit4ClassRunner";
            }
            if (hasClass("org.robolectric.RobolectricTestRunner")) {
                return "org.robolectric.RobolectricTestRunner";
            }
        }
        return "androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner";
    }

    private static boolean hasClass(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static Runner loadRunner(Class<?> testClass) throws InitializationError {
        String runnerClassName = getRunnerClassName();
        return loadRunner(testClass, runnerClassName);
    }

    @SuppressWarnings("unchecked")
    private static Runner loadRunner(Class<?> testClass, String runnerClassName)
            throws InitializationError {

        Class<? extends Runner> runnerClass = null;
        try {
            runnerClass = (Class<? extends Runner>) Class.forName(runnerClassName);
        } catch (ClassNotFoundException e) {
            throwInitializationError(
                    String.format(
                            "Delegate runner %s for AndroidJUnit4 could not be found.\n", runnerClassName),
                    e);
        }

        Constructor<? extends Runner> constructor = null;
        try {
            constructor = runnerClass.getConstructor(Class.class);
        } catch (NoSuchMethodException e) {
            throwInitializationError(
                    String.format(
                            "Delegate runner %s for AndroidJUnit4 requires a public constructor that takes a"
                                    + " Class<?>.\n",
                            runnerClassName),
                    e);
        }

        try {
            return constructor.newInstance(testClass);
        } catch (IllegalAccessException e) {
            throwInitializationError(
                    String.format("Illegal constructor access for test runner %s\n", runnerClassName), e);
        } catch (InstantiationException e) {
            throwInitializationError(
                    String.format("Failed to instantiate test runner %s\n", runnerClassName), e);
        } catch (InvocationTargetException e) {
            String details = getInitializationErrorDetails(e, testClass);
            throwInitializationError(
                    String.format("Failed to instantiate test runner %s\n%s\n", runnerClass, details), e);
        }
        throw new IllegalStateException("Should never reach here");
    }

    private static void throwInitializationError(String details, Throwable cause)
            throws InitializationError {
        throw new InitializationError(new RuntimeException(details, cause));
    }

    private static String getInitializationErrorDetails(Throwable throwable, Class<?>
            testClass) {
        StringBuilder innerCause = new StringBuilder();
        final Throwable cause = throwable.getCause();

        if (cause == null) {
            return "";
        }

        final Class<? extends Throwable> causeClass = cause.getClass();
        if (causeClass == InitializationError.class) {
            final InitializationError initializationError = (InitializationError) cause;
            final List<Throwable> testClassProblemList = initializationError.getCauses();
            innerCause.append(
                    String.format(
                            "Test class %s is malformed. (%s problems):\n",
                            testClass, testClassProblemList.size()));
            for (Throwable testClassProblem : testClassProblemList) {
                innerCause.append(testClassProblem).append("\n");
            }
        }
        return innerCause.toString();
    }

    @Override
    public Description getDescription() {
        return delegate.getDescription();
    }

    @Override
    public void run(RunNotifier runNotifier) {
        delegate.run(runNotifier);
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        ((Filterable) delegate).filter(filter);
    }

    @Override
    public void sort(Sorter sorter) {
        ((Sortable) delegate).sort(sorter);
    }
}
