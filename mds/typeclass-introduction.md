---
theme: seriph 
class: 'text-center' 
highlighter: shiki
---

# Typeclass Introduction

---

# 型クラスの目的

- クラスの継承とかと同じ様に多相性を実現すること
- つまりDogとかCatとかをAnimalとしてみなせるかみたいな
- ただし実現できる多相性の種類がクラスの継承とかと異なる

---

# 言葉としての型クラス

- そもそも「型クラス=Typeclass」という名前は何なのか
- 最初に聞いたときの思ひで
    - 「型」と「クラス」って同じちゃうん？（クソ雑魚Rubyプログラマ並の感想）
- なんで同じ言葉並べてるんや？
    - 「型」と「クラス」は型クラスの文脈では明確に違う意味を持つ
- 型: いわゆるデータ型、Int、String、Dogとか
    - クラス: 型を分類するもの
    - つまり型クラスとはデータ型を何らかの特徴に基づいて分類し、その分類に基づいて多態性を実現する機能

---

# Scalaにおける型クラスの実装

- Scalaではtraitを使用して型クラスを実現する
- 題材としてcirceのEncoderを考える

```scala
// 説明に必要ない部分は一旦省いた
trait Encoder[A] extends Serializable {
  self =>
  /**
   * Convert a value to JSON.
   */
  def apply(a: A): Json
}
```

- ここで定義した Encoder trait自体を型クラスと呼ぶ
- この型クラスが言いたいのは何らかの型 AがあってそれがcirceのJsonにできるということ
- つまり型 Aになにか新しい操作を定義しているような感じ

--- 

- 実際に Encoder型クラスを使って具体的な型にencode能力を与えるためには各々型への実装を用意する

```scala

val intEncoder = new Encoder[Int] {
  override def apply(a: Int) = Json.fromInt(a)
}

val stringEncoder = new Encoder[String] {
  override def apply(a: String) = Json.fromString(a)
}

```

- この様に特定の型に対しての型クラスの実装を「型クラスのインスタンス」と呼ぶ
- 型クラスが型を分類するためのものだったのに対して、特定の型に対して型クラスが実装されたので抽象度が下がっているためこう呼べる

--- 

実際に型クラスを通じて獲得した新たな操作を使用するためには型クラスのインスタンスを使えばいい

```scala
intEncoder(1)
// val res0: io.circe.Json = 1

stringEncoder("foo")
// val res1: io.circe.Json = "foo"
```

--- 

# 多相性の整理

- そもそも多相性とは違うものを同じものとみなしてプログラムを構造化してプログラミングコストを下げるためのテクニック
- 多相性とは言っても種類がある

---

## パラメトリック多相

- いわゆるジェネリクスだと思っておけばいい

--- 

## サブタイピング

- Javaでいうクラスの継承
- tsみたいなstructural subtypingだと構造として同じかどうか（多分これはサブタイピングとみなしていい）
- オブジェクト指向が言ってる多相性はこれのこと

---

## アドホック多相

- 処理対象のデータ型によって処理内容を変更する多相性のこと
- 関数のオーバーロードとかこれ
- 型クラスが実現する多相性もこれ

--- 

# 型クラスを使用したアドホック多相

- 型クラスとそのインスタンス、多相性について整理できたので実際にアドホック多相を実現する

---

## インスタンスの定義

```scala
import io.circe.{Encoder, Json}

implicit val intEncoder = new Encoder[Int] {
  override def apply(a: Int) = Json.fromInt(a)
}

implicit val stringEncoder = new Encoder[String] {
  override def apply(a: String) = Json.fromString(a)
}

case class Dog(name: String, age: Int)

implicit val dogEncoder = new Encoder[Dog] {
  override def apply(a: Dog) = Json.obj(
    ("name", Json.fromString(a.name)),
    ("age", Json.fromInt(a.age))
  )
}
```

---

## アドホック多相な関数の作成

```scala
def toJson[T](a: T)(implicit encoder: Encoder[T]): Json = encoder(a)

// 処理対象のデータ型によって型クラスのインスタンスが解決されて異なる処理が実行されている
toJson(1)
// val res0: io.circe.Json = 1

toJson("hello")
// val res1: io.circe.Json = "hello"

toJson(Dog("john", 3))
// val res2: io.circe.Json =
// {
//   "name" : "john",
//   "age" : 3
// }
```

- この様に toJson という関数が処理対象のデータ型に対して多相的に振る舞う

---

# 型クラスとサブタイピング

- 型クラスでアドホック多相ができることはわかったのだがこれがなぜ必要なのかがわからない
- クラス継承を用いたサブタイピングでも同じようなことができそうな気がする
- 型クラスが解決するサブタイピングの課題をいくつか紹介する

---

## 前提としてのサブタイピングを用いた仮実装

```scala
// 仮にこういう抽象traitが用意されていて
trait JsonEncodable {
  def toJson: Json
}

case class EncodableDog(name: String, age: Int) extends JsonEncodable {
  override def toJson: Json = Json.obj(
    ("name", Json.fromString(name)),
    ("age", Json.fromInt(age))
  )

}

case class Person(name: String) extends JsonEncodable {
  override def toJson: Json = Json.obj(
    ("name", Json.fromString(name))
  )
}

def encodeToJson(a: JsonEncodable): Json = a.toJson

encodeToJson(EncodableDog("john", 3))
encodeToJson(Person("taro"))
```

---

## ライブラリコードに対しての抽象化
- 前述の例だとうまい具合にサブタイピングで同じようなことができているような気がする
- しかしユーザコードではないライブラリコードに対して抽象化をかまそうとするとうまく行かない
- なぜなら継承はデータ型の定義時点でしか行うことができないから
- 例えば Int に対して JsonEncodable継承はできないので Int を encodeToJson関数に突っ込むのは不可能
  - ラッパークラスを作成したりして回避することはできるけど。。。

# 参考
