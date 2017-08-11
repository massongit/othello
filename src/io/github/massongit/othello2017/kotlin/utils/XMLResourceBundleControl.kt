package io.github.massongit.othello2017.kotlin.utils

import java.util.*

/**
 * XML形式のリソースバンドルを読み込み可能にするコントローラ
 * ( https://github.com/seraphy/JavaFXSimpleApp/blob/master/src/jp/seraphyware/utils/XMLResourceBundleControl.java を元に作成)
 * @author seraphy, Masaya SUZUKI
 */
class XMLResourceBundleControl : ResourceBundle.Control() {
    /**
     * 拡張子
     */
    private val extension = "xml"

    override fun getFormats(baseName: String): List<String> = listOf(this.extension)

    override fun newBundle(baseName: String, locale: Locale, format: String, loader: ClassLoader, reload: Boolean): ResourceBundle? {
        // 拡張子が設定されているものと一致するとき
        if (this.extension == format) {
            // プロパティをロードしたかどうか
            var isLoadProperties = false

            // プロパティ
            val properties = Properties()

            // ロケールと結合したリソース名を求める
            val plainBundleName = this.toBundleName(baseName, locale)

            // プロパティをロードする
            // (実行しているOSのプロパティがあればそちらを優先的にロードする)
            for (bundleName in listOf<String>(plainBundleName, listOf<String>(plainBundleName, System.getProperty("os.name").toLowerCase(Locale.ENGLISH).replace(" ", "")).joinToString("_"))) {
                // 対応するフォーマットと結合したリソース名を求める
                val url = loader.getResource(this.toResourceName(bundleName, format))

                // プロパティを上書きロードする
                if (url != null) {
                    properties.loadFromXML(url.openStream())
                    isLoadProperties = true
                }
            }

            // 何らかのプロパティが読み込めた場合、プロパティをリソースバンドルに接続する
            if (isLoadProperties) {
                return object : ResourceBundle() {
                    override fun handleGetObject(key: String): Any = properties.getProperty(key)

                    override fun getKeys(): Enumeration<String> = Collections.enumeration(properties.stringPropertyNames())
                }
            }
        }

        // ロードできなかった場合、nullを返す
        return null
    }
}
