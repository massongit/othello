package io.github.massongit.othello2017.kotlin.app.play

import io.github.massongit.othello2017.kotlin.app.play.stone.Move
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import io.github.massongit.othello2017.kotlin.utils.*
import javafx.scene.layout.GridPane
import java.math.BigInteger

/**
 * ノード
 * ( https://ja.wikipedia.org/wiki/%E3%82%AA%E3%82%BB%E3%83%AD%E3%81%AB%E3%81%8A%E3%81%91%E3%82%8B%E3%83%93%E3%83%83%E3%83%88%E3%83%9C%E3%83%BC%E3%83%89 を元に作成)
 * @author Masaya SUZUKI
 */
abstract class Node<out T> {
    /**
     * 現在のターン
     */
    internal open var tern: StoneState = StoneState.PLAY_FIRST

    /**
     * 着手のリスト
     */
    internal open val moveList: MutableList<Move> = mutableListOf()

    /**
     * ビットボード
     */
    internal open val bitBoard: MutableMap<StoneState, BigInteger> = mutableMapOf(
            StoneState.PLAY_FIRST to BigInteger.ZERO, // 先手
            StoneState.DRAW_FIRST to BigInteger.ZERO // 後手
    )

    /**
     * 盤面のマスの数
     */
    private val BOARD_SIZE: Int = 64

    /**
     * 石を追加する
     * @param columnIndex 列
     * @param rowIndex 行
     * @param stoneState 石の種類
     */
    internal open fun addStone(columnIndex: Int, rowIndex: Int, stoneState: StoneState) {
        this.bitBoard[stoneState] = this.bitBoard[stoneState]!! or this.getStoneBit(columnIndex, rowIndex)
    }

    /**
     * 次のターンへ移る
     * @param stoneState 次のターンの先手・後手
     * @return 次のターンのノードのリスト
     */
    internal open fun nextTern(stoneState: StoneState): List<T> {
        // 自分のターン -> 相手のターンの順に次のターンへ移れるかどうかを見る
        for (state in listOf(stoneState, stoneState.inv())) {
            val result = this.nextTernPerState(state)
            if (result != null) {
                return result
            }
        }

        // どのターンにも移れない場合、ゲームを終了する
        this.endGame()

        return listOf()
    }

    /**
     * 次のターンへ移る
     * @param state 石
     * @return 次のターンのノードのリスト
     */
    internal open fun nextTernPerState(state: StoneState): List<T>? {
        // ビットボードを動かすラムダ式のリスト
        val transfers: List<(BigInteger) -> BigInteger> = listOf<(BigInteger) -> BigInteger>(
                { (it shl 1) and BigInteger("fefefefefefefefe", 16) }, // 左方向へ1マス
                { (it ushr 1) and BigInteger("7f7f7f7f7f7f7f7f", 16) }, // 右方向へ1マス
                { (it shl 7) and BigInteger("7f7f7f7f7f7f7f00", 16) }, // 右上方向へ1マス
                { (it ushr 7) and BigInteger("fefefefefefefe", 16) }, // 左下方向へ1マス
                { (it shl 8) and BigInteger("ffffffffffffff00", 16) }, // 上方向へ1マス
                { (it ushr 8) and BigInteger("ffffffffffffff", 16) }, // 下方向へ1マス
                { (it shl 9) and BigInteger("fefefefefefefe00", 16) }, // 左上方向へ1マス
                { (it ushr 9) and BigInteger("7f7f7f7f7f7f7f", 16) } // 右下方向へ1マス
        )

        // 着手のビットボード
        var movePattern = BigInteger.ZERO

        // 石が置けそうな場所を探す
        for (transfer in transfers) {
            movePattern = movePattern or transfer(this.bitBoard[state.inv()]!!)
        }
        movePattern = movePattern and (this.bitBoard[state]!! or this.bitBoard[state.inv()]!!).inv()

        // 実際に石が置ける場所が見つかったかどうか
        var isFindMove = false

        // マスク
        var mask = BigInteger.ONE

        // 実際に石が置ける場所を探し、石を置いていく
        for (i in this.BOARD_SIZE - 1 downTo 0) {
            if (movePattern == BigInteger.ZERO) { // 石を置けそうな場所がない場合、ループを抜ける
                break
            } else {
                // 現在見ている位置
                val currentPosition = movePattern and mask

                // 現在見ている位置が石を置ける場所の候補に入っているとき
                if (currentPosition != BigInteger.ZERO) {
                    // 反転する石が立ったビットボード
                    var reversePattern = BigInteger.ZERO

                    // 各方向について、反転する石のビットを立てる
                    for (transfer in transfers) {
                        // 現在見ている方向で反転する石が立ったビットボード
                        var reverseStones = BigInteger.ZERO

                        // マスク
                        var reverseStoneMask = currentPosition

                        // 現在見ている方向へ石を反転させていく
                        while (true) {
                            reverseStoneMask = transfer(reverseStoneMask)
                            if (reverseStoneMask and this.bitBoard[state.inv()]!! == BigInteger.ZERO) { // 反転させられない場所にきた場合、ループを抜ける
                                break
                            } else {
                                reverseStones = reverseStones or reverseStoneMask
                            }
                        }

                        // 石を反転させていき、最後に自分の石に到達した場合、反転する石が立ったビットボードへ結果をマージ
                        if (reverseStoneMask and this.bitBoard[state]!! != BigInteger.ZERO) {
                            reversePattern = reversePattern or reverseStones
                        }
                    }

                    if (reversePattern != BigInteger.ZERO) { // 実際に石が置ける場所に石を置く
                        // 着手を追加する
                        this.addMove(Move(reversePattern), this.getStoneColumnIndex(i), this.getStoneRowIndex(i))

                        isFindMove = true
                    }

                    movePattern = movePattern xor mask
                }

                mask = mask shl 1
            }
        }

        if (isFindMove) { // 実際に石が置けるとき
            return this.changeTern(state)
        } else { // 石が置けないとき
            return null
        }
    }

    /**
     * 着手を追加する
     * @param move 着手
     * @param columnIndex 列
     * @param rowIndex 行
     */
    internal open fun addMove(move: Move, columnIndex: Int, rowIndex: Int) {
        // 着手の座標をセット
        GridPane.setColumnIndex(move, columnIndex)
        GridPane.setRowIndex(move, rowIndex)

        // リストに着手を追加する
        this.moveList.add(move)
    }

    /**
     * ターンを進める
     * @param state 石
     * @return 次のターンのノードのリスト
     */
    internal open fun changeTern(state: StoneState): List<T> = listOf()

    /**
     * ターンを実行する
     * @param move 着手
     * @return ターンを実行することで得られたノードのリスト
     */
    internal fun executeTern(move: Move): List<T> = this.executeTern(move, GridPane.getColumnIndex(move), GridPane.getRowIndex(move))

    /**
     * ターンを実行する
     * @param move 着手
     * @param columnIndex 列
     * @param rowIndex 行
     * @return ターンを実行することで得られたノードのリスト
     */
    internal open fun executeTern(move: Move, columnIndex: Int, rowIndex: Int): List<T> {
        // 反転処理前の自分のビットボード
        val prevMyBitBoard = this.bitBoard[this.tern]!!

        // ビットボードに対して反転処理を行う
        this.bitBoard[this.tern] = this.bitBoard[this.tern]!! or this.getStoneBit(columnIndex, rowIndex) or move.reversePattern
        this.bitBoard[this.tern.inv()] = this.bitBoard[this.tern.inv()]!! xor move.reversePattern

        // 反転処理前後での自分のビットボードの差分
        var myBitBoardDiff = prevMyBitBoard xor this.bitBoard[this.tern]!!

        // インスタンスボードに対して反転処理を行う
        for (i in this.BOARD_SIZE - 1 downTo 0) {
            if (myBitBoardDiff == BigInteger.ZERO) {
                break
            } else {
                if (myBitBoardDiff and BigInteger.ONE == BigInteger.ONE) {
                    this.addStone(this.getStoneColumnIndex(i), this.getStoneRowIndex(i), this.tern)
                }
                myBitBoardDiff = myBitBoardDiff ushr 1
            }
        }

        // 次のターンへ移る
        return this.nextTern(this.tern.inv())
    }

    /**
     * ゲームを終了する
     */
    internal abstract fun endGame()

    /**
     * 石の座標をボード上のインデックスに変換する
     * @param columnIndex 列
     * @param rowIndex 行
     * @return ボード上のインデックス
     */
    internal fun getStoneBoardIndex(columnIndex: Int, rowIndex: Int): Int = 8 * rowIndex + columnIndex

    /**
     * ボード上のインデックスから列を取得する
     * @param index ボード上のインデックス
     * @return 列
     */
    private fun getStoneColumnIndex(index: Int): Int = index % 8

    /**
     * ボード上のインデックスから行を取得する
     * @param index ボード上のインデックス
     * @return 行
     */
    private fun getStoneRowIndex(index: Int): Int = index / 8

    /**
     * 石の座標を表すビットのみが立ったビットボードを取得する
     * @param columnIndex 列
     * @param rowIndex 行
     * @return 石の座標を表すビットのみが立ったビットボード
     */
    private fun getStoneBit(columnIndex: Int, rowIndex: Int): BigInteger = BigInteger.ONE shl (this.BOARD_SIZE - this.getStoneBoardIndex(columnIndex, rowIndex) - 1)
}
