package io.github.massongit.othello2017.kotlin.app.play.ai.node

/**
 * ゲーム木探索を行うためのノードの種類
 * @author Masaya SUZUKI
 */
enum class AINodeType {
    /**
     * 通常ノード
     */
    NORMAL,

    /**
     * 葉ノードの親ノード
     */
    LEAF_PARENT,

    /**
     * 葉ノード
     */
    LEAF,

    /**
     * 葉ノードの子ノード
     */
    LEAF_CHILD
}
