import io.circe.{Encoder, Json}

def toJson[T](a: T)(implicit encoder: Encoder[T]): Json = encoder(a)

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

toJson(GenericNameTC(Name("john"), 1))
