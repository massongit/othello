package io.github.massongit.othello2017.kotlin.app.play

import io.github.massongit.othello2017.kotlin.app.menu.MenuController
import io.github.massongit.othello2017.kotlin.app.play.information.InformationController
import io.github.massongit.othello2017.kotlin.app.play.stone.ChoiceIndicator
import io.github.massongit.othello2017.kotlin.app.play.stone.Stone
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import io.github.massongit.othello2017.kotlin.utils.*
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.layout.GridPane
import java.math.BigInteger
import java.net.URL
import java.util.*

/**
 * プレイ画面コントローラ
 * @author Masaya SUZUKI
 */
class PlayDisplayController : Initializable {
    /**
     * 盤面本体
     */
    @FXML
    private lateinit var board: GridPane

    /**
     * メニューコントローラ
     */
    @FXML
    private lateinit var menuController: MenuController

    /**
     * 情報ラベルコントローラ
     */
    @FXML
    private lateinit var informationController: InformationController

    /**
     * リソースバンドル
     */
    private lateinit var resources: ResourceBundle

    /**
     * 現在のターン
     */
    private var tern: StoneState = StoneState.PLAY_FIRST

    /**
     * 石のインスタンスボード
     */
    private val stoneInstanceBoard: MutableMap<Int, Stone> = mutableMapOf()

    /**
     * 選択肢インジケーターのインスタンスボード
     */
    private val choiceIndicatorInstanceBoard: MutableMap<Int, ChoiceIndicator> = mutableMapOf()

    /**
     * 石のビットボード
     */
    private val stoneBitBoard: MutableMap<StoneState, BigInteger> = mutableMapOf(
            StoneState.PLAY_FIRST to BigInteger.ZERO, // 先手
            StoneState.DRAW_FIRST to BigInteger.ZERO // 後手
    )

    /**
     * 盤面のマスの数
     */
    private val BOARD_SIZE: Int = 64

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.resources = resources

        // 初期位置に石を置く
        this.addStone(3, 4, StoneState.PLAY_FIRST)
        this.addStone(4, 3, StoneState.PLAY_FIRST)
        this.addStone(3, 3, StoneState.DRAW_FIRST)
        this.addStone(4, 4, StoneState.DRAW_FIRST)

        // 先手のターンを始める
        this.nextTern(this.tern)
    }

    /**
     * 石を追加する
     * @param columnIndex 列
     * @param rowIndex 行
     * @param stoneState 石の種類
     */
    private fun addStone(columnIndex: Int, rowIndex: Int, stoneState: StoneState) {
        // 既に置かれている石を削除
        this.board.children.remove(this.stoneInstanceBoard.remove(this.getStoneBoardIndex(columnIndex, rowIndex)))

        // 石
        val stone = Stone(stoneState)

        // 盤面本体に石を追加する
        this.board.add(stone, columnIndex, rowIndex)

        // インスタンスボードに石を追加する
        this.stoneInstanceBoard[this.getStoneBoardIndex(columnIndex, rowIndex)] = stone

        // ビットボードに石を置く
        this.stoneBitBoard[stoneState] = this.stoneBitBoard[stoneState]!! or this.getStoneBit(columnIndex, rowIndex)
    }

    /**
     * ターンを実行する
     * @param choiceIndicator 選択肢インジケーター
     */
    private fun executeTern(choiceIndicator: ChoiceIndicator) {
        // 石の列
        val stoneColumnIndex = GridPane.getColumnIndex(choiceIndicator)

        // 石の行
        val stoneRowIndex = GridPane.getRowIndex(choiceIndicator)

        // 石の座標を標準出力へ出力
        println("Put: ${this.tern.name} ($stoneColumnIndex, $stoneRowIndex)")

        // 反転処理前の自分のビットボード
        val prevMyBitBoard = this.stoneBitBoard[this.tern]!!

        // ビットボードに対して反転処理を行う
        this.stoneBitBoard[this.tern] = this.stoneBitBoard[this.tern]!! or this.getStoneBit(stoneColumnIndex, stoneRowIndex) or choiceIndicator.reversePattern
        this.stoneBitBoard[this.tern.inv()] = this.stoneBitBoard[this.tern.inv()]!! xor choiceIndicator.reversePattern

        // 反転処理前後での自分のビットボードの差分
        var myBitBoardDiff = prevMyBitBoard xor this.stoneBitBoard[this.tern]!!

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

        // 選択肢インジケーターを全削除
        for (i in 0 until this.BOARD_SIZE) {
            if (this.choiceIndicatorInstanceBoard.contains(i) && this.choiceIndicatorInstanceBoard[i]!!.styleClass.contains(ChoiceIndicator.STYLE_CLASS)) {
                this.board.children.remove(this.choiceIndicatorInstanceBoard.remove(i))
            }
        }

        // 次のターンへ移る
        this.nextTern(this.tern.inv())
    }

    /**
     * 次のターンへ移る
     * @param stoneState 次のターンの先手・後手
     */
    private fun nextTern(stoneState: StoneState) {
        // 自分のターン -> 相手のターンの順に次のターンへ移れるかどうかを見る
        // (nullになった場合は勝負が着いたものとみなす)
        for (state in listOf(stoneState, stoneState.inv(), null)) {
            if (state == null) { // 勝負が着いた場合、勝敗を表示し、ループを抜ける
                this.endGame()
                break
            } else { // まだ勝負が着いていないとき
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

                // 選択肢インジケーターのビットボード
                var choicePattern = BigInteger.ZERO

                // 石が置けそうな場所を探す
                for (transfer in transfers) {
                    choicePattern = choicePattern or transfer(this.stoneBitBoard[state.inv()]!!)
                }
                choicePattern = choicePattern and (this.stoneBitBoard[state]!! or this.stoneBitBoard[state.inv()]!!).inv()

                // 実際に石が置かれたかどうか
                var isPutStone = false

                // マスク
                var mask = BigInteger.ONE

                // 実際に石が置ける場所を探し、石を置いていく
                for (i in this.BOARD_SIZE - 1 downTo 0) {
                    if (choicePattern == BigInteger.ZERO) { // 石を置けそうな場所がない場合、ループを抜ける
                        break
                    } else {
                        // 現在見ている位置
                        val currentPosition = choicePattern and mask

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
                                    if (reverseStoneMask and this.stoneBitBoard[state.inv()]!! == BigInteger.ZERO) { // 反転させられない場所にきた場合、ループを抜ける
                                        break
                                    } else {
                                        reverseStones = reverseStones or reverseStoneMask
                                    }
                                }

                                // 石を反転させていき、最後に自分の石に到達した場合、反転する石が立ったビットボードへ結果をマージ
                                if (reverseStoneMask and this.stoneBitBoard[state]!! != BigInteger.ZERO) {
                                    reversePattern = reversePattern or reverseStones
                                }
                            }

                            if (reversePattern != BigInteger.ZERO) { // 実際に石が置ける場所に石を置く
                                // 列
                                val columnIndex = this.getStoneColumnIndex(i)

                                // 行
                                val rowIndex = this.getStoneRowIndex(i)

                                // 選択肢インジケーター
                                val choiceIndicator = ChoiceIndicator(reversePattern, EventHandler { this.executeTern(it.source as ChoiceIndicator) })

                                // 盤面本体に選択肢インジケーターを追加する
                                this.board.add(choiceIndicator, columnIndex, rowIndex)

                                // インスタンスボードに選択肢インジケーターを追加する
                                this.choiceIndicatorInstanceBoard[this.getStoneBoardIndex(columnIndex, rowIndex)] = choiceIndicator

                                isPutStone = true
                            }

                            choicePattern = choicePattern xor mask
                        }

                        mask = mask shl 1
                    }
                }

                // 実際に石が置かれた場合、ループを抜ける
                if (isPutStone) {
                    // 現在見ているターンと実際のターンが一致する場合、ターンを進める
                    if (state != this.tern) {
                        this.tern = this.tern.inv()
                        this.informationController.change(this.tern.styleClass)
                    }

                    break
                }
            }
        }
    }

    /**
     * ゲームを終了させる
     */
    private fun endGame() {
        // ダイアログ
        val victoryOrDefeatDialog = Alert(AlertType.INFORMATION).apply { headerText = null }

        // 勝敗を出力
        if (this.stoneBitBoard[StoneState.PLAY_FIRST]!!.bitCount() == this.stoneBitBoard[StoneState.DRAW_FIRST]!!.bitCount()) {
            victoryOrDefeatDialog.contentText = this.resources.getString("draw")
            println("Draw")
        } else {
            val winStone: StoneState

            if (this.stoneBitBoard[StoneState.PLAY_FIRST]!!.bitCount() < this.stoneBitBoard[StoneState.DRAW_FIRST]!!.bitCount()) {
                winStone = StoneState.DRAW_FIRST
            } else {
                winStone = StoneState.PLAY_FIRST
            }

            victoryOrDefeatDialog.contentText = this.resources.getString(winStone.winKey)
            println("Win: ${winStone.name}")
        }

        // ダイアログを表示
        victoryOrDefeatDialog.showAndWait()

        if (Alert(AlertType.CONFIRMATION, this.resources.getString("replay")).apply {
            headerText = null
            buttonTypes.setAll(ButtonType.YES, ButtonType.NO)
        }.showAndWait().get() == ButtonType.YES) {
            this.menuController.onReset()
        } else {
            this.menuController.onClose()
        }
    }

    /**
     * 石の座標をボード上のインデックスに変換する
     * @param columnIndex 列
     * @param rowIndex 行
     * @return ボード上のインデックス
     */
    private fun getStoneBoardIndex(columnIndex: Int, rowIndex: Int): Int = 8 * rowIndex + columnIndex

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
