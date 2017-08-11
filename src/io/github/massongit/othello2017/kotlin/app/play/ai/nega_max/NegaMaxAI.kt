package io.github.massongit.othello2017.kotlin.app.play.ai.nega_max

import io.github.massongit.othello2017.kotlin.app.play.ai.AI
import io.github.massongit.othello2017.kotlin.app.play.ai.AIStrength
import io.github.massongit.othello2017.kotlin.app.play.ai.node.AINodeType
import io.github.massongit.othello2017.kotlin.app.play.stone.Move
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import java.math.BigInteger
import java.util.*

/**
 * NegaMax法によるAI
 * @param strength AIの強さ
 * @author Masaya SUZUKI
 */
class NegaMaxAI(private val strength: AIStrength) : AI {
    /**
     * 探索を行う最大の深さ
     */
    private val MAX_DEPTH: Int = 10

    override fun execute(tern: StoneState, stoneBitBoard: MutableMap<StoneState, BigInteger>, moves: List<Move>): Move {
        // 枝刈りの対象となるノードの親ノード
        // (枝刈りの手法: 強いAIの場合はβカット / 弱いAIの場合はαカット)
        var cutParentNode: NegaMaxNode? = null

        // ルートノード
        val rootNode = NegaMaxNode(tern, stoneBitBoard, 0, this.strength)
        rootNode.nodeList = moves.map { NegaMaxNode(tern, stoneBitBoard, 1, this.strength, it, rootNode) }

        // スタック
        val nodeStack = ArrayDeque<NegaMaxNode>(rootNode.nodeList)

        // 深さ優先探索を行う
        while (nodeStack.isNotEmpty()) {
            // 現在見ているノード
            val currentNode = nodeStack.peek()

            // ターンを実行したかどうか
            val isExecuteTern = currentNode.isExecuteTern

            if (cutParentNode == null && !isExecuteTern) { // 現在見ているノードがβカットによる枝刈りの対象になっていないとき
                when (currentNode.depth) {
                    this.MAX_DEPTH - 1 -> currentNode.nodeType = AINodeType.LEAF_PARENT
                    this.MAX_DEPTH -> currentNode.nodeType = AINodeType.LEAF
                    this.MAX_DEPTH + 1 -> currentNode.nodeType = AINodeType.LEAF_CHILD
                }
            }

            // 現在見ているノードが以下のいずれかを満たす場合、そのノードをスタックからpopする
            // * βカットによる枝刈りの対象になっている
            // * 既に子ノードを展開済み
            // * 葉ノードの子ノード
            if (cutParentNode != null || isExecuteTern || currentNode.nodeType == AINodeType.LEAF_CHILD) {
                nodeStack.pop()
            }

            if (cutParentNode == null) { // 現在見ているノードがβカットによる枝刈りの対象になっていないとき
                if (!isExecuteTern) { // 現在見ているノードがまだ子ノードを展開していないとき
                    // 子ノードのリスト
                    val childNodes = currentNode.executeTern()

                    // 現在見ているノードが葉ノードの子ノードでない場合、子ノードをスタックにpushする
                    if (currentNode.nodeType != AINodeType.LEAF_CHILD) {
                        for (node in childNodes) {
                            nodeStack.push(node)
                        }
                    }
                }

                if (isExecuteTern || currentNode.nodeType == AINodeType.LEAF_CHILD) {
                    // 親ノードの評価値を更新する
                    currentNode.parent?.evaluationValue = currentNode.evaluationValue

                    // 親ノードの親ノードの評価値<現在見ているノードの評価値が成り立つ場合、枝刈りを行う
                    if (currentNode.parent != null && currentNode.parent.parent != null && currentNode.parent.parent.evaluationValue != null && currentNode.evaluationValue != null && ((this.strength == AIStrength.STRONG && currentNode.parent.parent.evaluationValue!! < currentNode.evaluationValue!!) || (this.strength == AIStrength.WEAK && currentNode.evaluationValue!! < currentNode.parent.parent.evaluationValue!!))) {
                        cutParentNode = currentNode.parent
                    }
                }

            } else if (cutParentNode == currentNode) { // 現在見ているノードが枝刈りの対象となるノードの親ノードのとき
                cutParentNode = null
            }
        }

        // 評価値が最大になっている着手を返す
        return rootNode.nodeList!!.maxBy { it.evaluationValue!! }!!.move
    }
}
