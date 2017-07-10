package io.github.massongit.othello2017.kotlin.app.play.stone

/**
 * 石の状態
 * (先手・後手を表す際にも使用)
 * @param styleClass 石に対応するスタイルシートのクラス
 * @param winKey 勝利した際の文章のkey
 * @author Masaya SUZUKI
 */
enum class StoneState(val styleClass: String, val winKey: String) {
    /**
     * 先手の石
     */
    PLAY_FIRST("play-first-tern", "play-first-win") {
        override fun inv(): StoneState = DRAW_FIRST
    },

    /**
     * 後手の石
     */
    DRAW_FIRST("draw-first-tern", "draw-first-win") {
        override fun inv(): StoneState = PLAY_FIRST
    };

    /**
     * 先手・後手を反転させる
     */
    abstract fun inv(): StoneState
}
