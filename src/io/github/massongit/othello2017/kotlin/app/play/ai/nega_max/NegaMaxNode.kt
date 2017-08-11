package io.github.massongit.othello2017.kotlin.app.play.ai.nega_max

import io.github.massongit.othello2017.kotlin.app.play.ai.AIStrength
import io.github.massongit.othello2017.kotlin.app.play.ai.node.AINode
import io.github.massongit.othello2017.kotlin.app.play.ai.node.AINodeType
import io.github.massongit.othello2017.kotlin.app.play.stone.Move
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import java.math.BigInteger

/**
 * NegaMax法によるゲーム木探索を行うためのノード
 * @param tern ターン
 * @param stoneBitBoard 石のビットボード
 * @param depth ゲーム木における深さ (デフォルト値: 1)
 * @param strength AIの強さ
 * @param move 着手 (デフォルト値: 空の着手)
 * @param parent 親ノード (デフォルト値: null)
 * @author Masaya SUZUKI
 */
open class NegaMaxNode(tern: StoneState, stoneBitBoard: MutableMap<StoneState, BigInteger>, depth: Int, private val strength: AIStrength, move: Move = Move(), parent: NegaMaxNode? = null) : AINode<NegaMaxNode>(tern, stoneBitBoard, depth, move, parent) {
    /**
     * 評価値
     * (終局を迎えた場合、評価値 = ∞)
     * (強いAIの場合、葉ノードの着手の数をa、葉ノードの子ノードの着手の数をb_1, b_2, ..., b_nとしたとき、評価値 = a + max(-b_1, -b_2, ..., -b_n))
     * (弱いAIの場合、葉ノードの着手の数をa、葉ノードの子ノードの着手の数をb_1, b_2, ..., b_nとしたとき、評価値 = a + min(-b_1, -b_2, ..., -b_n))
     */
    override var evaluationValue: Int? = null
        get() {
            if (field != null) {
                if (!this.isPass) { // パスしていないとき
                    if (this.nodeType == AINodeType.LEAF) { // 葉ノードのとき
                        return -field!! - this.moveList.size
                    } else { // 葉ノードでないとき
                        return -field!!
                    }
                } else if (this.nodeType == AINodeType.LEAF_PARENT) { // 葉ノードの親ノードでパスしたとき
                    return field!! + this.moveList.size
                }
            }
            return field
        }
        set(value) {
            if (value != null && (field == null || (this.strength == AIStrength.STRONG && field!! < value) || (this.strength == AIStrength.WEAK && value < field!!))) {
                field = value
            }
        }

    override fun changeTern(state: StoneState): List<NegaMaxNode> {
        if (this.isPass && this.nodeType == AINodeType.LEAF_CHILD) { // 葉ノードの子ノードでパスした場合、評価値を0にする
            this.evaluationValue = 0
        } else if ((this.isPass && this.nodeType == AINodeType.LEAF) || (!this.isPass && this.nodeType == AINodeType.LEAF_CHILD)) { // 葉ノードでパスしたか、葉ノードの子ノードでパスしていない場合、評価値を着手の数にする
            this.evaluationValue = this.moveList.size
        }

        return this.moveList.map { NegaMaxNode(state, this.bitBoard, this.depth + 1, this.strength, it, this) }
    }
}
