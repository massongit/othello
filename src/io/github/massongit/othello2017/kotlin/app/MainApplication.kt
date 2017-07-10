package io.github.massongit.othello2017.kotlin.app

import io.github.massongit.othello2017.kotlin.utils.XMLResourceBundleControl
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import java.io.FileInputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Paths
import java.util.*

/**
 * メインアプリケーション
 * @author seraphy, Masaya SUZUKI
 */
class MainApplication : Application() {
    companion object {
        /**
         * ステージ
         */
        lateinit var stage: Stage

        /**
         * プロパティ
         */
        val properties: Properties = Properties().apply { loadFromXML(FileInputStream(Paths.get(MainApplication::class.java.protectionDomain.codeSource.location.toURI()).resolve("settings.xml").toString())) }

        /**
         * リソースバンドル
         */
        private val RESOURCES: ResourceBundle = ResourceBundle.getBundle("resources", XMLResourceBundleControl())

        /**
         * 画面を遷移させる
         * @param display 遷移先の画面
         */
        fun translateDisplay(display: DisplayKind) {
            stage.scene = Scene(FXMLLoader(MainApplication::class.java.getResource(Paths.get(MainApplication::class.qualifiedName?.replace(".", "/")).parent.relativize(display.fxmlPath).toString()), RESOURCES).load())
            display.init()
            stage.apply {
                // ステージの表示
                show()

                // リサイズできないようにする
                maxWidth = width
                minWidth = width
                maxHeight = height
                minHeight = height
            }
        }
    }

    override fun start(primaryStage: Stage) {
        try {
            stage = primaryStage.apply { title = RESOURCES.getString("title") }
            translateDisplay(DisplayKind.START)
        } catch (ex: Exception) {
            // スタックトレースを標準エラー出力へ出力する
            ex.printStackTrace()

            // エラーダイアログ
            val errorDialog = Alert(AlertType.ERROR, ex.message).apply {
                title = RESOURCES.getString("error-dialog-title")
                headerText = ex::class.simpleName
            }

            // スタックトレース
            val stackTrace = StringWriter()
            PrintWriter(stackTrace).use {
                ex.printStackTrace(it)
            }

            // テキストエリア
            val textArea = TextArea(stackTrace.toString()).apply {
                isEditable = false
                isWrapText = true
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
            }
            GridPane.setVgrow(textArea, Priority.ALWAYS)
            GridPane.setHgrow(textArea, Priority.ALWAYS)

            // 詳細コンテンツをセット
            errorDialog.dialogPane.expandableContent = GridPane().apply {
                maxWidth = Double.MAX_VALUE
                add(Label(RESOURCES.getString("error-dialog-label")), 0, 0)
                add(textArea, 0, 1)
            }

            // エラーダイアログを表示
            errorDialog.showAndWait()
        }
    }
}
