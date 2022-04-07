import io.circe.Json

trait JsonEncodable {
  def toJson: Json
}

def encodeToJson(a: JsonEncodable): Json = a.toJson

encodeToJson(1)
