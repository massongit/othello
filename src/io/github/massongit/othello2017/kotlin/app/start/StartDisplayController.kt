package io.github.massongit.othello2017.kotlin.app.start

import io.github.massongit.othello2017.kotlin.app.DisplayType
import io.github.massongit.othello2017.kotlin.app.MainApplication
import io.github.massongit.othello2017.kotlin.app.play.ai.AIStrength
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import java.net.URL
import java.util.*

/**
 * スタート画面コントローラ
 * @author Masaya SUZUKI
 */
class StartDisplayController : Initializable {
    /**
     * AIの強さ
     */
    @FXML
    private lateinit var aiStrength: ToggleGroup

    /**
     * 強いAI
     */
    @FXML
    private lateinit var strongAI: ToggleButton

    /**
     * 弱いAI
     */
    @FXML
    private lateinit var weakAI: ToggleButton

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.strongAI.userData = AIStrength.STRONG
        this.weakAI.userData = AIStrength.WEAK
    }

    /**
     * スタートボタンをクリックしたときのイベント
     */
    @FXML
    private fun onClick() {
        // AIの強さをセットする
        MainApplication.aiStrength = this.aiStrength.selectedToggle.userData as AIStrength

        // プレイ画面へ遷移する
        MainApplication.translateDisplay(DisplayType.PLAY)
    }
}
