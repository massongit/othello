package io.github.massongit.othello2017.kotlin.utils

import java.io.IOException
import java.util.*

/**
 * XMLプロパティ形式のリソースバンドルを読み込み可能にするコントローラ
 * @author seraphy, Masaya SUZUKI
 */
class XMLResourceBundleControl : ResourceBundle.Control() {
    /**
     * 拡張子
     */
    private val extension = "xml"

    override fun getFormats(baseName: String): List<String> = listOf(this.extension)

    @Throws(IllegalAccessException::class, InstantiationException::class, IOException::class)
    override fun newBundle(baseName: String, locale: Locale, format: String, loader: ClassLoader, reload: Boolean): ResourceBundle? {
        if (this.extension == format) {

            // ロケールと結合したリソース名を求める
            val plainBundleName = this.toBundleName(baseName, locale)

            // XMLプロパティを重ねてロードする.
            val props = Properties()
            var isOfLoadedProps = false
            for (bundleName in listOf<String>(plainBundleName, listOf<String>(plainBundleName, System.getProperty("os.name").toLowerCase(Locale.ENGLISH).replace(" ", "")).joinToString("_"))) {
                // 対応するフォーマットと結合したリソース名を求める
                val url = loader.getResource(this.toResourceName(bundleName, format))

                // XMLプロパティを上書きロードする.
                if (url != null) {
                    props.loadFromXML(url.openStream())
                    isOfLoadedProps = true
                }
            }

            // 少なくとも、どちらかのXMLプロパティが読み込めた場合、XMLプロパティをリソースバンドルに接続する.
            if (isOfLoadedProps) {
                return object : ResourceBundle() {
                    override fun handleGetObject(key: String): Any = props.getProperty(key)

                    override fun getKeys(): Enumeration<String> = Collections.enumeration(props.stringPropertyNames())
                }
            }
        }

        // ロードできなかった場合、nullを返す
        return null
    }
}
