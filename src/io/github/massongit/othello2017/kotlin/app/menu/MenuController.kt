package io.github.massongit.othello2017.kotlin.app.menu

import io.github.massongit.othello2017.kotlin.app.DisplayKind
import io.github.massongit.othello2017.kotlin.app.MainApplication
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import java.net.URL
import java.util.*


/**
 * メニューコントローラ
 * @author Masaya SUZUKI
 */
class MenuController : Initializable {
    /**
     * リソースバンドル
     */
    private lateinit var resources: ResourceBundle

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.resources = resources
    }

    /**
     * ゲームをリセットする
     */
    @FXML
    fun onReset() = MainApplication.translateDisplay(DisplayKind.START)

    /**
     * プログラムを終了する
     */
    @FXML
    fun onClose() = Platform.exit()

    /**
     * バージョン情報を表示する
     */
    @FXML
    private fun onAbout() = Alert(AlertType.INFORMATION, this.resources.getString("credit")).apply {
        title = resources.getString("about")
        headerText = "${resources.getString("title")} Ver.${MainApplication.properties.getProperty("version")}"
    }.show()
}
