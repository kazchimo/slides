---
theme: bricks
highlighter: shiki
layout: cover
---

# Implicit特論

---
layout: section
---

# Implicitの分類

---

- Scalaではimplicitという名前がついた機能がいくつかある 
  - Implicit Conversion
  - Implicit Class
  - Implicit Parameter
- まずはこの3つの基本的機能について説明する

---
layout: head-two-cols
---

::head::

## Implicit Conversion

::left::

```scala
implicit def plus1IntToString(i: Int): String =
  (i + 1).toString

val two: String = 1
two // => "2"
```

::right::

- Implicit Conversionは型を暗黙的に変換するための機能
- 左の例だとIntが自動的にStringに変換されている
- 変換ロジックは自由に実装できるので例では`1 => "2"`に変換されている

<br/>

> NOTE: Implicit Conversionは乱用するとコードの見通しが悪くなるので積極的には使用しないほうがいい


---
layout: head-two-cols
---

::head::

## Implicit Class

::left::

```scala
implicit class RichInt(i: Int) {
  def doubled: Int = i * 2
}

2.doubled // => 4
```

::right::

- 既存のクラスにメソッドを生やす機能
- 例の様にIntを二倍するようなメソッドを追加できる
- `enrich my library`パターンとか`pimp my library`パターンとかと呼ばれたりする

---
layout: head-two-cols
---

::head::

## Implicit Parameter

::left::

```scala
import scala.concurrent._

def asyncDouble(f: Future[Int])(
        implicit ec: ExecutionContext
): Future[Int] = f.map(_ * 2)

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.Duration

implicit val ec: ExecutionContext = global

Await.result(asyncDouble(Future(2)), Duration.Inf) 
// => 4
```

::right::

- Implicit Parameterは引数に指定した型を暗黙的に渡すための機能
- 例では`ExecutionContext`が自動で渡されている

---
layout: head-two-cols
---

::head::

## Implicit Parameterと`implicitly`関数

::left::

```scala
implicit val ec: ExecutionContext = global

implicitly[ExecutionContext] 
// => 上で定義したecが返る
```

<br/>

```scala
// scala.Predef.scala
def implicitly[T](implicit e: T): T = e
```

::right::
- Implicit Parameterに関連してscalaには`implicitly`関数が定義されている
- これは型`T`を明示的に渡してやるとimplicitな値を探してきてくれる
- 実装としてはかんたんでimplicitな引数を一つ定義した関数がそのままその引数を返しているだけ
- よく出てくるので一応紹介

---
layout: section
---


# Implicit Parameterのスコープ

--- 

## Implicit Parameterにはスコープがある


---
# 参考
- https://www.scala-lang.org/files/archive/spec/2.13/07-implicits.html
