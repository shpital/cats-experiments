package sandbox

object step4 extends App {

  /**
   * Eq
   *
   * trait Eq[A] {
   *   def eqv(a: A, b: A): Boolean
   *   // other concrete methods based on eqv...
   * }
   *    === сравнивает два объекта на равенство;
   *    =!= сравнивает два объекта на предмет неравенства.
   */
  import cats.Eq
  import cats.instances.int._ // for Eq[Int]

  val eqInt = Eq[Int]
  println(eqInt.eqv(123, 123))
  println(eqInt.eqv(123, 234))

  import cats.syntax.eq._ // for === and =!=

  println(123 === 123)
  println(123 === 234)

  import cats.instances.option._ // for Eq[Option]

  println((Some(1): Option[Int]) === (None: Option[Int]))
  println((Some(1): Option[Int]) === (Some(1): Option[Int]))
  //apply
  println(Option(123) === Option(123))
  println(Option(1) === Option.empty[Int])

  import cats.syntax.option._ // for some and none

  println(123.some === none[Int])
  println(123.some =!= 125.some)

  /**
   * custom types
   * Мы можем определить наши собственные экземпляры Eqс помощью Eq.instanceметода,
   * который принимает функцию типа (A, A) => Booleanи возвращаетEq[A]:
   */

  import java.util.Date
  import cats.instances.long._ // for Eq

  implicit val dateEq: Eq[Date] = Eq.instance[Date] { (date1, date2) =>
    date1.getTime === date2.getTime
  }

  val x = new Date(10000)
  val y = new Date(10001)

  println("Eq.instance")
  println(x === x)
  println(x === y)

  /**
   * Equality, Liberty, and Felinity
   */

  final case class Cat(name: String, age: Int, color: String)

  val cat1 = Cat("Garfield",   38, "orange and black")
  val cat2 = Cat("Heathcliff", 33, "orange and black")

  val optionCat1 = Option(cat1)
  val optionCat2 = Option.empty[Cat]

  import cats.instances.string._

  implicit val catEqual: Eq[Cat] = Eq.instance[Cat] { (cat1, cat2) =>
    (cat1.name === cat2.name) && (cat1.age === cat2.age) && (cat1.color === cat2.color)
  }

  println(" * Cats *")
  println(cat1 === cat2)
  println(cat1 =!= cat2)

  import cats.instances.option._ // for Eq

  println(" * Option[Cats] *")
  println(optionCat1 === optionCat2)
  println(optionCat1 =!= optionCat2)

}
