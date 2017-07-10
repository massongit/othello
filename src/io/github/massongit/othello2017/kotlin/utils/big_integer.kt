package io.github.massongit.othello2017.kotlin.utils

import java.math.BigInteger

/**
 * BigIntegerのビット演算子をKotlinのビット演算子と同様に扱えるよう拡張 (中置記法対応)
 * @author Masaya SUZUKI
 */

/**
 * 論理積をとる
 * @param v BigIntegerとの論理積をとる値
 * @return {@code this and v}
 */
infix fun BigInteger.and(v: BigInteger): BigInteger = this.and(v)

/**
 * 論理和をとる
 * @param v BigIntegerとの論理和をとる値
 * @return {@code this or v}
 */
infix fun BigInteger.or(v: BigInteger): BigInteger = this.or(v)

/**
 * 排他的論理和をとる
 * @param v BigIntegerとの排他的論理和をとる値
 * @return {@code this xor v}
 */
infix fun BigInteger.xor(v: BigInteger): BigInteger = this.xor(v)

/**
 * 左シフトを行う
 * {@code n} が負の値になる場合には右シフトを行う
 * (<tt>floor(this * 2<sup>n</sup>)</tt>を算出することと同等)
 * @param  n 左シフトを行うビット数
 * @return {@code this shl n}
 * @see ushr
 */
infix fun BigInteger.shl(n: Int): BigInteger = this.shiftLeft(n)

/**
 * 右シフトを行う
 * 符号拡張が行われ、{@code n} が負の値になる場合には左シフトを行う
 * (<tt>floor(this / 2<sup>n</sup>)</tt>を算出することと同等)
 * @param  n 右シフトを行うビット数
 * @return {@code this ushr n}
 * @see shl
 */
infix fun BigInteger.ushr(n: Int): BigInteger = this.shiftRight(n)

/**
 * 否定を行う
 * @return {@code this.inv()}
 */
fun BigInteger.inv(): BigInteger = this.not()
