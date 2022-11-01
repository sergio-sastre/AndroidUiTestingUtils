package sergio.sastre.uitesting.myapplication

import org.junit.Test
import sergio.sastre.uitesting.utils.common.LocaleUtil

class LocaleUtilTests {

    @Test
    fun testLocaleWithAllVariants(){
        val localeString = "zh-Latn-TW-pinyin"
        val locale = LocaleUtil.localeFromString(localeString)
        assert(locale.language == "zh")
        assert(locale.script == "Latn")
        assert(locale.country == "TW")
        assert(locale.variant == "pinyin")
    }

    @Test
    fun testSerbianLatin(){
        val localeString = "sr-Latn-RS"
        val locale = LocaleUtil.localeFromString(localeString)
        assert(locale.language == "sr")
        assert(locale.script == "Latn")
        assert(locale.country == "RS")
    }

    @Test
    fun testSerbianCyrillic(){
        val localeString = "sr-Cyrl-RS"
        val locale = LocaleUtil.localeFromString(localeString)
        assert(locale.language == "sr")
        assert(locale.script == "Cyrl")
        assert(locale.country == "RS")
    }

    @Test
    fun testSerbianRegion(){
        val localeString = "sr-RS"
        val locale = LocaleUtil.localeFromString(localeString)
        assert(locale.language == "sr")
        assert(locale.country == "RS")
    }

    @Test
    fun testISOLocale(){
        val localeString = "en_US"
        val locale = LocaleUtil.localeFromString(localeString)
        assert(locale.language == "en")
        assert(locale.country == "US")
    }

    @Test
    fun testPseudolocale(){
        val localeString = "en_XA"
        val locale = LocaleUtil.localeFromString(localeString)
        assert(locale.language == "en")
        assert(locale.country == "XA")
    }
}
