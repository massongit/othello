#オセロ [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

##バージョン
1.0

##作者
Masaya SUZUKI <suzukimasaya428@gmail.com>

##開発言語
Kotlin

##AI
* 強いAI  
ユーザがどんなに強くても勝ちに行くことを目指したAI。  
Negamax法とβカットを用い、10手先まで読む。  
葉ノードにおける着手の数をx、葉ノードの子ノードにおける着手の数をそれぞれy_1, y_2, ..., y_nとしたとき、評価関数は次のようになる。
```
x+max(-y_1, -y_2, ..., -y_n)
```


* 弱いAI  
ユーザがどんなに弱くても負けに行くことを目指したAI。  
強いAIをベースに次のような変更を加えた。
  * 最大値を取る箇所で最小値を取る
  * βカットの代わりにαカットを用いる
