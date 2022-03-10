package sandbox


object step5 extends App {

  //  def add(items: List[Int]): Int =
  //    items.foldLeft(Monoid[Int].empty)(_ |+| _)

  import cats.kernel.Monoid
  import cats.syntax.semigroup._ // for |+|
  import cats.instances.int._ // for Monoid

  def add(items: List[Int]): Int = items.foldLeft(Monoid[Int].empty)(_ |+| _)

  println(add(List(1, 2, 3, 4, 5)))

  /**
   * Option
   */
  import cats.instances.option._ // for Monoid

  def add[A](items: List[A])(implicit monoid: Monoid[A]): A = items.foldLeft(monoid.empty)(_ |+| _)

  println(add(List(Some(1), Some(2), Some(3), Some(4), None, Some(5))))
  println(add(List[Option[Int]](Some(1), Some(2), Some(3), Some(4), Some(5))))

  /**
   * custom class
   */
  case class Order(totalCost: Double, quantity: Double)

  implicit val monoid: Monoid[Order] = new Monoid[Order] {
    def combine(o1: Order, o2: Order) = Order(o1.totalCost + o2.totalCost, o1.quantity + o2.quantity)
    def empty = Order(0, 0)
  }
  val o1= Order(20.0, 21.5)
  val o2= Order(40.0, 31.5)
  println(add(List(o1,o2)))

  /**
   * map
   */
  import cats.instances.map._ // for Monoid

  val map1 = Map("a" -> 1, "b" -> 2)
  val map2 = Map("b" -> 3, "d" -> 4)

  println( map1 |+| map2 )

  /**
   * tople
   */
  import cats.instances.tuple._  // for Monoid
  import cats.instances.string._ // for Monoid

  val tuple1 = ("hello", 123, "X")
  val tuple2 = (" world", 321, "Y")

  println(tuple1 |+| tuple2)

  /**
   * universal add for monoids
   */
  def addAll[A](values: List[A])(implicit monoid: Monoid[A]): A = values.foldRight(monoid.empty)(_ |+| _)

  println(addAll(List(1, 2, 3)))
  println(addAll(List(None, Some(1), Some(2))))
}
