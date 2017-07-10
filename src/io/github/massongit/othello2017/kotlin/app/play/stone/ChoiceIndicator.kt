package io.github.massongit.othello2017.kotlin.app.play.stone

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import java.math.BigInteger

/**
 * 選択肢インジケーター
 * @param reversePattern インジケーターが示す場所に石を置いた際に反転する石が立ったビットボード
 * @param handler クリック時のイベントハンドラー
 * @author Masaya SUZUKI
 */
class ChoiceIndicator(val reversePattern: BigInteger, handler: EventHandler<in MouseEvent>) : Stone(STYLE_CLASS) {
    companion object {
        /**
         * 選択肢インジケーターのスタイルクラス
         */
        val STYLE_CLASS: String = "choice-indicator"
    }

    init {
        this.onMouseClicked = handler
    }
}
