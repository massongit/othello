package io.github.massongit.othello2017.kotlin.app.play.information

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import java.net.URL
import java.util.*

/**
 * 情報ラベルコントローラ
 * @author Masaya SUZUKI
 */
class InformationController : Initializable {
    /**
     * 情報ラベル
     */
    @FXML
    private lateinit var information: Label

    /**
     * リソースバンドル
     */
    private lateinit var resources: ResourceBundle

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.resources = resources
    }

    /**
     * 表示する情報を変更する
     * @param key リソースバンドルのkey
     */
    fun change(key: String) {
        this.information.text = this.resources.getString(key)
    }
}
