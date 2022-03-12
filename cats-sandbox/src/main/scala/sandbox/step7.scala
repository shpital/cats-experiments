package sandbox



object step7 extends App {

  /**
   *   Напишите a Functor для следующего типа данных двоичного дерева.
   *   Убедитесь, что код работает должным образом для экземпляров Branch & Leaf:
   */

  sealed trait Tree[+A]
  final case class Branch[A](left: Tree[A], right: Tree[A])  extends Tree[A]
  final case class Leaf[A](value: A) extends Tree[A]

  import cats.Functor

  implicit val treeFunctor: Functor[Tree] = new Functor[Tree] {
    def map[A, B](value: Tree[A])(func: A => B): Tree[B] = {
      value match {
        case Leaf(a) => Leaf(func(a))
        case Branch(left, right) => Branch(map(left)(func), map(right)(func))
      }
    }
  }

  val func1 = (a: Int) => a + 1
  val p1 = Branch(Leaf(10),Leaf(20))
  println(Functor[Tree].map(p1)(func1))

  import cats.implicits.toFunctorOps
  //Branch(Leaf(10), Leaf(20)).map(_ * 2)
  object Tree {
    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)
    def leaf[A](value: A): Tree[A] = Leaf(value)
  }
  {
    Tree.leaf(100).map(_ * 2)
    Tree.branch(Tree.leaf(10), Tree.leaf(20)).map(_ * 2)
    println( Tree.branch(Tree.leaf(10), Tree.leaf(20)).map(_ * 2+11) )
  }
}
