package sandbox


/**
 * = Абстрактное синтаксическое дерево (AST) =
 *
 * JsonWriter это наш класс типа в этом примере, с Json и его подтипами,
 * предоставляющими вспомогательный код.
 *
 * Когда мы перейдем к реализации экземпляров JsonWriter,
 * параметром type A будет конкретный тип данных.
 */

object step1 extends App {

  /**
   * Type Class
   */

  // Define a very simple JSON Abstract Syntax Tree (AST)
  sealed trait Json
  final case class JsObject(get: Map[String, Json]) extends Json
  final case class JsString(get: String) extends Json
  final case class JsNumber(get: Double) extends Json
  final case object JsNull extends Json

  // The "serialize to JSON" behaviour is encoded in this trait
  trait JsonWriter[A] {
    def write(value: A): Json
  }

  /**
   * Type Class Instances
   */

  final case class Person(name: String, email: String)

  object JsonWriterInstances {
    // implicit values:

    implicit val stringWriter: JsonWriter[String] = new JsonWriter[String] {
      def write(value: String): Json = JsString(value)
    }

    implicit val personWriter: JsonWriter[Person] = new JsonWriter[Person] {
      def write(value: Person): Json = JsObject(Map(
        "name" -> JsString(value.name),
        "email" -> JsString(value.email)
      ))
    }
    // etc...
  }

  /**
   * Type Class Use
   */

  //Interface Objects
  object Json {
    def toJson[A](value: A)(implicit w: JsonWriter[A]): Json = w.write(value)
  }

  import JsonWriterInstances._

  Json.toJson(Person("Dave", "dave@example.com"))
  // res1: Json = JsObject(
  //   Map("name" -> JsString("Dave"), "email" -> JsString("dave@example.com"))
  // )

  /**
   * Interface Syntax
   */

  object JsonSyntax {
    implicit class JsonWriterOps[A](value: A) {
      def toJson(implicit w: JsonWriter[A]): Json = w.write(value)
    }
  }

  import JsonWriterInstances._
  import JsonSyntax._

  Person("Dave", "dave@example.com").toJson
  // res3: Json = JsObject(
  //   Map("name" -> JsString("Dave"), "email" -> JsString("dave@example.com"))
  // )

  /**
   * The implicitly Method
   */
  import JsonWriterInstances._

  implicitly[JsonWriter[String]]
  // res5: JsonWriter[String] = repl.Session$App0$JsonWriterInstances$$anon$1@76f60d45
}
