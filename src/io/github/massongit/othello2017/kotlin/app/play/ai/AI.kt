package io.github.massongit.othello2017.kotlin.app.play.ai

import io.github.massongit.othello2017.kotlin.app.play.stone.Move
import io.github.massongit.othello2017.kotlin.app.play.stone.StoneState
import java.math.BigInteger

/**
 * AI
 * @author Masaya SUZUKI
 */
interface AI {
    /**
     * AIによる処理を実行する
     * @param tern ターン
     * @param stoneBitBoard 石のビットボード
     * @param moves 着手のリスト
     * @return AIが選択した着手
     */
    fun execute(tern: StoneState, stoneBitBoard: MutableMap<StoneState, BigInteger>, moves: List<Move>): Move
}
