package sandbox



object step3a extends App {

  /**
   * Определение пользовательских экземпляров
   */

  import cats.Show
  import cats.implicits._
  import java.util.Date

  object handMade {
    // ручное создание
    implicit val dateShow: Show[Date] = new Show[Date] {
      override def show(date: Date): String = s"${date.getTime} ms since the epoch."
    }
  }

  /**
   * Существует два метода построения сопутствующего объекта Show, которые мы можем использовать
   * для определения экземпляров для наших собственных типов:
   *  def fromToString[A]: Show[A]
   *  def show[A](f: A => String): Show[A]
   */
  implicit val dateShow: Show[Date] = Show.show(date => s"${date.getTime} ms since the epoch.")

  println( new Date().show )

  final case class Cat(name: String, age: Int, color: String)
  implicit val catShow: Show[Cat] = Show.show[Cat] { cat =>
    val name  = cat.name.show
    val age   = cat.age.show
    val color = cat.color.show
    s"$name is a $age year-old $color cat."
  }
  println( Cat("Barsik", 2, "ginger").show )
}
