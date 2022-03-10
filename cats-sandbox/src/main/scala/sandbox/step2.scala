package sandbox

object step2 extends App {

  trait Printable[A] {
    def format(value: A): String
  }

  object PrintableInstances {
    implicit val stringPrintable = new Printable[String] {
      override def format(value: String): String = value
    }
    implicit val intPrintable = new Printable[Int] {
      override def format(value: Int): String = value.toString
    }
  }

  object Printable {
    def format[A](value:A)(implicit p:Printable[A]) :String = p.format(value)
    def print[A](value:A)(implicit p:Printable[A]) :Unit = println(format(value))
  }

  final case class Cat(name: String, age: Int, color: String)

  import PrintableInstances._

  implicit val catPrintable = new Printable[Cat] {
    override def format(value: Cat): String = {
      val name = Printable.format(cat.name)
      val age = Printable.format(cat.age)
      val color = Printable.format(cat.color)
      s"$name is a $age year-old $color cat."
    }
  }

  val cat = Cat("Murzik", 2, "ginger")
  Printable.print(cat)

  object PrintableSyntax {
    implicit class PrintableOps[A](value: A) {
      def format(implicit p: Printable[A]): String = p.format(value)
      def print(implicit p: Printable[A]): Unit = println(format(p))
    }
  }

  import PrintableSyntax._
  cat.print

}
