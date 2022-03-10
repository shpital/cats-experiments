package sandbox

object step3 extends App {

  import cats.Show

  // Импорт экземпляров по умолчанию
  import cats.instances.int._    // for Show
  import cats.instances.string._ // for Show


  val showInt = Show.apply[Int]
  val showString = Show.apply[String]

  // Импорт синтаксиса интерфейса
  import cats.syntax.show._ // for show

  val shownInt = 123.show
  val shownString = "abc".show

  println(12.show)
  println("eee".show)

  // Импорт всех вещей!
//  import cats._  // импортирует все классы типов кошек за один раз;
//  import cats.implicits._ // импортирует все экземпляры класса стандартного типа и весь синтаксис за один раз.

}
