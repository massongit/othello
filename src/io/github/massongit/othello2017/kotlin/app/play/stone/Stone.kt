package io.github.massongit.othello2017.kotlin.app.play.stone

import javafx.scene.shape.Circle

/**
 * 石
 * @param styleClass スタイルシートのクラス
 * @author Masaya SUZUKI
 */
open class Stone(styleClass: String) : Circle(20.0) {
    init {
        this.styleClass.add(styleClass)
    }

    /**
     * @param stoneState 石の先手・後手
     */
    constructor(stoneState: StoneState) : this(stoneState.styleClass)
}
