import io.circe.{Encoder, Json}

List(1, 2, 3)
List("a", "b", "c")

trait Figure
case class Circle(radius: Double) extends Figure
case class Rectangle(width: Double, height: Double) extends Figure

val fig1: Figure = Circle(1.0)
val fig2: Figure = Rectangle(1.0, 2.0)

object Concat {
  def concat(a: String, b: String) = a + b
  def concat(a: Int, b: Int) = a + b
}

Concat.concat("a", "b")
Concat.concat(1, 2)


implicit val intEncoder: Encoder[Int] = new Encoder[Int] {
  override def apply(a: Int) = Json.fromInt(a)
}

implicit val stringEncoder: Encoder[String] = new Encoder[String] {
  override def apply(a: String) = Json.fromString(a)
}

case class Dog(name: String, age: Int)

implicit val dogEncoder = new Encoder[Dog] {
  override def apply(a: Dog) = Json.obj(
    ("name", Json.fromString(a.name)),
    ("age", Json.fromInt(a.age))
  )
}

def toJson[T](a: T)(implicit encoder: Encoder[T]): Json = encoder(a)

case class Cat(name: String, age: Int)

//toJson(Cat("Garfield", 38))

trait JsonEncodable {
  def toJson: Json
}

def encodeToJson(a: JsonEncodable): Json = a.toJson

//encodeToJson(1)

// nameをjson化するためにTにはJsonEncodableを継承しているという上限境界が必要になる
case class GenericNameSB[T <: JsonEncodable](name: T, age: Int) extends JsonEncodable {
  def toJson: Json = Json.obj(
    "name" -> name.toJson,
    "age"  -> Json.fromInt(age)
  )
}

//GenericNameSB(1, 2)

case class GenericNameTC[T](name: T, age: Int)

implicit def genericNameTCEncode[T](implicit encoder: Encoder[T]): Encoder[GenericNameTC[T]] =
  new Encoder[GenericNameTC[T]] {
    override def apply(a: GenericNameTC[T]): Json = Json.obj(
      "name" -> encoder(a.name),
      "age"  -> Json.fromInt(a.age)
    )
  }

toJson(GenericNameTC("hoge", 1))

case class Name(value: String)

//toJson(GenericNameTC(Name("john"), 1))
