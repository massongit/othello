package io.github.massongit.othello2017.kotlin.app.play

import io.github.massongit.othello2017.kotlin.app.MainApplication
import io.github.massongit.othello2017.kotlin.app.menu.MenuController
import io.github.massongit.othello2017.kotlin.app.play.ai.AI
import io.github.massongit.othello2017.kotlin.app.play.ai.nega_max.NegaMaxAI
import io.github.massongit.othello2017.kotlin.app.play.information.InformationController
import io.github.massongit.othello2017.kotlin.app.play.stone.Move
import io.github.massongit.othello2017.kotlin.app.play.stone.Stone
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.layout.GridPane
import javafx.util.Duration
import java.net.URL
import java.util.*

/**
 * プレイ画面コントローラ
 * @author Masaya SUZUKI
 */
class PlayDisplayController : Initializable, Node<PlayDisplayController>() {
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
     * AIが担当するターン
     */
    private lateinit var aiTern: StoneState

    /**
     * AI
     */
    private val ai: AI = NegaMaxAI(MainApplication.aiStrength)

    /**
     * 乱数生成器
     */
    private val random: Random = Random()

    /**
     * インスタンスボード
     */
    private val instanceBoard: MutableMap<Int, Stone> = mutableMapOf()

    /**
     * AIが思考している際の盤面のスタイルクラス
     */
    private val BOARD_FOR_AI_STYLE_CLASS: String = "board-for-ai"

    /**
     * 出力用の列表記の対応表
     */
    private val COLUMN_INDEX_CORRESPONDENCE_TABLE = listOf("a", "b", "c", "d", "e", "f", "g", "h")

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.resources = resources

        // 初期位置に石を置く
        this.addStone(3, 4, StoneState.PLAY_FIRST)
        this.addStone(4, 3, StoneState.PLAY_FIRST)
        this.addStone(3, 3, StoneState.DRAW_FIRST)
        this.addStone(4, 4, StoneState.DRAW_FIRST)

        // AIのターンを決める
        if (this.random.nextBoolean()) {
            this.aiTern = StoneState.PLAY_FIRST
        } else {
            this.aiTern = StoneState.DRAW_FIRST
        }

        // プレイヤーが先手か後手かを表示する
        Alert(AlertType.INFORMATION, this.resources.getString(this.aiTern.inv().decideTernKey)).apply { headerText = null }.showAndWait()

        // 先手のターンを始める
        this.nextTern(this.tern)
    }

    override fun addStone(columnIndex: Int, rowIndex: Int, stoneState: StoneState) {
        // インデックス
        val index = this.getStoneBoardIndex(columnIndex, rowIndex)

        // 既に置かれている石を削除
        this.removeStoneFromBoard(this.instanceBoard.remove(index))

        // 石
        val stone = Stone(stoneState)

        // 盤面本体に石を追加する
        this.board.add(stone, columnIndex, rowIndex)

        // インスタンスボードに石を追加する
        this.instanceBoard[index] = stone

        super.addStone(columnIndex, rowIndex, stoneState)
    }

    override fun nextTern(stoneState: StoneState): List<PlayDisplayController> {
        // 着手を全て削除する
        for (i in 0 until this.moveList.size) {
            this.removeStoneFromBoard(this.moveList.removeAt(0))
        }

        return super.nextTern(stoneState)
    }

    override fun endGame() = Platform.runLater {
        // ダイアログ
        val dialog = Alert(AlertType.INFORMATION).apply { headerText = null }

        // 勝敗を出力
        if (this.bitBoard[StoneState.PLAY_FIRST]!!.bitCount() == this.bitBoard[StoneState.DRAW_FIRST]!!.bitCount()) {
            dialog.contentText = this.resources.getString("draw")
            println("Draw")
        } else {
            val winStone: StoneState

            if (this.bitBoard[StoneState.PLAY_FIRST]!!.bitCount() < this.bitBoard[StoneState.DRAW_FIRST]!!.bitCount()) {
                winStone = StoneState.DRAW_FIRST
            } else {
                winStone = StoneState.PLAY_FIRST
            }

            dialog.contentText = this.resources.getString(winStone.winKey)
            println("Win: ${winStone.name}")
        }

        // ダイアログを表示
        dialog.showAndWait()

        if (Alert(AlertType.CONFIRMATION, this.resources.getString("replay")).apply {
            headerText = null
            buttonTypes.setAll(ButtonType.YES, ButtonType.NO)
        }.showAndWait().get() == ButtonType.YES) { // ゲームをもう一度プレイする場合
            this.menuController.onReset()
        } else { // ゲームを終了する場合
            this.menuController.onClose()
        }
    }

    override fun addMove(move: Move, columnIndex: Int, rowIndex: Int) {
        // 盤面本体に着手を追加する
        this.board.children.add(move)

        super.addMove(move, columnIndex, rowIndex)
    }

    override fun changeTern(state: StoneState): List<PlayDisplayController> {
        // 現在見ているターンと実際のターンが一致しない場合、ターンを進める
        if (state != this.tern) {
            this.tern = this.tern.inv()
            this.informationController.change(this.tern.styleClass)
        }

        if (this.tern == aiTern) { // AIのターンのとき
            // 盤面のスタイルクラスをAIが思考している際のものに変更する
            this.board.styleClass.add(this.BOARD_FOR_AI_STYLE_CLASS)

            // AIのターンを実行する
            // (思考時間 = 実際の思考時間 + 300〜1199ms)
            Timeline(KeyFrame(Duration.millis(300.0 + this.random.nextInt(900)), EventHandler {
                // 盤面のスタイルクラスを通常のものに戻す
                this.board.styleClass.remove(this.BOARD_FOR_AI_STYLE_CLASS)

                // AIのターンをを実行する
                this.executeTern(this.ai.execute(this.tern, this.bitBoard.toMutableMap(), this.moveList.toList()))
            })).play()
        } else { // ユーザのターンの場合、着手をユーザ仕様に変更する
            for (move in this.moveList) {
                move.toUser(EventHandler { this.executeTern(it.source as Move) })
            }
        }

        return super.changeTern(state)
    }

    override fun executeTern(move: Move, columnIndex: Int, rowIndex: Int): List<PlayDisplayController> {
        // 石の座標を標準出力へ出力
        println("Put: ${this.COLUMN_INDEX_CORRESPONDENCE_TABLE[columnIndex]}${rowIndex + 1} (${this.tern.name})")

        return super.executeTern(move, columnIndex, rowIndex)
    }

    /**
     * 盤面本体から石を削除する
     * @param stone 石
     */
    private fun removeStoneFromBoard(stone: Stone?) = this.board.children.remove(stone)
}
