package io.github.massongit.othello2017.kotlin.app.play.ai.node

import io.github.massongit.othello2017.kotlin.app.play.Node
import io.github.massongit.othello2017.kotlin.app.play.stone.Move
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import java.math.BigInteger

/**
 * ゲーム木探索を行うためのノード
 * @param move 着手
 * @param depth ゲーム木における深さ (デフォルト値: 1)
 * @param parent 親ノード (デフォルト値: null)
 * @author Masaya SUZUKI
 */
abstract class AINode<T>(override var tern: StoneState, override val bitBoard: MutableMap<StoneState, BigInteger>, var depth: Int, val move: Move = Move(), val parent: T? = null) : Node<T>() {
    /**
     * ターンを実行したかどうか
     */
    var isExecuteTern: Boolean = false
        get() = (this.nodeList != null)

    /**
     * ノードタイプ
     */
    var nodeType: AINodeType = AINodeType.NORMAL

    /**
     * パスしたかどうか
     */
    internal var isPass = false

    /**
     * 評価値
     * (終局を迎えた場合、評価値 = ∞)
     */
    open var evaluationValue: Int? = null

    override val moveList: MutableList<Move> = mutableListOf(this.move)

    /**
     * ノードのリスト
     */
    internal open var nodeList: List<T>? = null

    /**
     * ターンを実行する
     * @return ターンを実行することで得られた着手のリスト
     */
    fun executeTern(): List<T> {
        if (this.nodeList == null) {
            this.nodeList = super.executeTern(this.moveList[0])
        }
        return this.nodeList!!
    }

    override fun endGame() {
        // 評価値を∞にする
        this.evaluationValue = Int.MAX_VALUE
    }

    override fun nextTernPerState(state: StoneState): List<T>? {
        if (state == this.tern) {
            this.isPass = true
        }
        return super.nextTernPerState(state)
    }
}
