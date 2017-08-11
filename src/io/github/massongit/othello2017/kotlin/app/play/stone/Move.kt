package io.github.massongit.othello2017.kotlin.app.play.stone

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import java.math.BigInteger

/**
 * 着手
 * @param reversePattern 石を置いた際に反転する石が立ったビットボード
 * @author Masaya SUZUKI
 */
class Move(val reversePattern: BigInteger = BigInteger.ZERO) : Stone(STYLE_CLASS) {
    companion object {
        /**
         * 着手のスタイルクラス
         */
        val STYLE_CLASS: String = "move"
    }

    /**
     * ユーザ向けの仕様に変更する
     * @param handler クリック時のイベントハンドラー
     */
    fun toUser(handler: EventHandler<in MouseEvent>) {
        this.styleClass.add("move-for-user")
        this.onMouseClicked = handler
    }
}
