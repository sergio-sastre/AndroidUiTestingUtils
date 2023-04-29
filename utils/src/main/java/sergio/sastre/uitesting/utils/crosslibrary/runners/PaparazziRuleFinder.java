package sergio.sastre.uitesting.utils.crosslibrary.runners;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import sergio.sastre.uitesting.utils.crosslibrary.config.ScreenshotConfig;
import sergio.sastre.uitesting.utils.crosslibrary.testrules.ScreenshotTestRule;
import sergio.sastre.uitesting.utils.crosslibrary.testrules.SharedScreenshotTestRule;

public class PaparazziRuleFinder {
    public static boolean usesPaparazzi(Class<?> c) {
        try {
            Class<?> screenshotTestRule =
                    Class.forName("sergio.sastre.uitesting.utils.crosslibrary.ScreenshotTestRule");
            for (Field f : c.getDeclaredFields()) {
                if (screenshotTestRule.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    return screenshotTestRuleUsesPaparazzi(f.getType());
                }
            }
        } catch (ClassNotFoundException e) {
            // no-op
        }
        return false;
    }

    private static boolean screenshotTestRuleUsesPaparazzi(Class<?> clazz) {
        try {
            Class<?> currentClass = clazz;
            Object obj =
                    Class.forName(currentClass.getName())
                            .getConstructor(ScreenshotConfig.class)
                            .newInstance(new ScreenshotConfig());

            if (obj instanceof SharedScreenshotTestRule) {
                ScreenshotTestRule testRule =
                        ((SharedScreenshotTestRule) obj).getJvmScreenshotTestRule(new ScreenshotConfig());
                currentClass = testRule.getClass();
            }

            Class<?> paparazzi = Class.forName("app.cash.paparazzi.Paparazzi");

            Field[] fields = currentClass.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(paparazzi)) {
                    return true;
                }
            }

            Method[] methods = currentClass.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getReturnType().isAssignableFrom(paparazzi)) {
                    return true;
                }
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
