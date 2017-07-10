package io.github.massongit.othello2017.kotlin.app

import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import java.nio.file.Path
import java.nio.file.Paths

/**
 * 画面の種類
 * @author Masaya SUZUKI
 */
enum class DisplayKind {
    /**
     * スタート画面
     */
    START {
        override val fxmlPath: Path = super.fxmlPath.resolve("start").resolve("StartDisplay.fxml")

        override fun init() {
            // スペースキーが押された場合、プレイ画面に遷移するように設定
            MainApplication.stage.scene.onKeyPressed = EventHandler {
                if (it.code == KeyCode.SPACE) {
                    MainApplication.translateDisplay(PLAY)
                }
            }
        }
    },

    /**
     * プレイ画面
     */
    PLAY {
        override val fxmlPath: Path = super.fxmlPath.resolve("play").resolve("PlayDisplay.fxml")
    };

    /**
     * 画面のFXMLのパス
     */
    open val fxmlPath: Path = Paths.get(DisplayKind::class.qualifiedName?.replace(".", "/")).parent

    /**
     * 画面の初期化処理
     */
    open fun init() {

    }
}
