package io.github.massongit.othello2017.kotlin.app

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * 画面の種類
 * @author Masaya SUZUKI
 */
enum class DisplayType {
    /**
     * スタート画面
     */
    START {
        override val fxmlPath: Path = super.fxmlPath.resolve("start").resolve("StartDisplay.fxml")
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
    open val fxmlPath: Path = Paths.get(DisplayType::class.qualifiedName?.replace(".", File.separator)).parent
}
