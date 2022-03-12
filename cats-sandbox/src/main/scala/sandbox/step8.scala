package sandbox

/**
 * contramap & imap
 */
object step8 extends App {

  trait Printable[A] { self =>
    def format(value: A): String
    def contramap[B](func: B => A): Printable[B] = new Printable[B] {
      def format(value: B): String = self.format(func(value))
    }
  }

  def format[A](value: A)(implicit p: Printable[A]): String =
    p.format(value)

  implicit val stringPrintable: Printable[String] = new Printable[String] {
    def format(value: String): String = s"'${value}'"
  }

  implicit val booleanPrintable: Printable[Boolean] = new Printable[Boolean] {
    def format(value: Boolean): String = if (value) "yes" else "no"
  }

  implicit val intPrintable: Printable[Int] = new Printable[Int] {
    def format(value: Int): String = value.toString
  }

  println( format("hello") )
  println( format(false) )

  final case class Box[A](value: A)
//  явное определение
//  implicit def boxPrintable[A](implicit p: Printable[A]): Printable[Box[A]] = new Printable[Box[A]] {
//    def format(box: Box[A]): String = p.format(box.value)
//  }
  //используем комбинатор
  implicit def boxPrintable[A](implicit p: Printable[A]): Printable[Box[A]] =  p.contramap[Box[A]](_.value)

  println(format(Box("hello world")))
  println(format(Box(true)))
  println(format(Box(123)))

  /**
   *  imap
   */
  trait Codec[A] { self =>
    def encode(value: A): String
    def decode(value: String): A
    def imap[B](dec: A => B, enc: B => A): Codec[B] = new Codec[B] {
      def encode(value: B): String = self.encode(enc(value))
      def decode(value: String): B = dec(self.decode(value))
    }
  }

  def encode[A](value: A)(implicit c: Codec[A]): String = c.encode(value)
  def decode[A](value: String)(implicit c: Codec[A]): A = c.decode(value)

  implicit val stringCodec: Codec[String] = new Codec[String] {
    def encode(value: String): String = value
    def decode(value: String): String = value
  }
  implicit val intCodec: Codec[Int] = stringCodec.imap(_.toInt, _.toString)
  implicit val booleanCodec: Codec[Boolean] = stringCodec.imap(_.toBoolean, _.toString)

  /**
   * Обратите внимание, что decode метод нашего Codec класса type не учитывает сбои.
   * Если мы хотим смоделировать более сложные отношения, мы можем выйти за рамки
   * функторов и посмотреть на линзы и оптику. https://www.optics.dev/Monocle/
   */

  implicit val doubleCodec: Codec[Double] = stringCodec.imap[Double](_.toDouble, _.toString)
  implicit def boxCodec[A](implicit c: Codec[A]): Codec[Box[A]] = c.imap[Box[A]](Box(_), _.value)

  encode(123.4)  // res11: String = "123.4"
  decode[Double]("123.4")  // res12: Double = 123.4

  encode(Box(123.4))  // res13: String = "123.4"
  decode[Box[Double]]("123.4")  // res14: Box[Double] = Box(123.4)

  println(encode(Box(22.5)))
  println(decode[Box[Double]]("123.4"))

  /**
   * trait Contravariant[F[_]] {
   *   def contramap[A, B](fa: F[A])(f: B => A): F[B]
   * }
   *
   * trait Invariant[F[_]] {
   *   def imap[A, B](fa: F[A])(f: A => B)(g: B => A): F[B]
   * }
   */
  import cats.Contravariant
  import cats.Show
  import cats.instances.string._

  val showString = Show[String]

  val showSymbol = Contravariant[Show].contramap(showString)((sym: Symbol) => s"'${sym.name}")

  val r0 = showSymbol.show(Symbol("dave"))
  println(r0)

  /**
   * syntax
   */

  import cats.syntax.contravariant._ // for contramap

  val r1 = showString.contramap[Symbol](sym => s"'${sym.name}").show(Symbol("dave"))
  println(r1)

  /**
   * imap
   */
/**
 *  Cats не предоставляет a MonoidдляSymbol, но он предоставляет a Monoidдля аналогичного типа:
 *  String. Мы можем написать нашу новую полугруппу с emptyпомощью метода ,
 *  который полагается на пустойString, и combineметода, который работает следующим образом:
 *
 *  примите два Symbols параметра в качестве параметров;
 *    преобразовать SymbolsвStrings;
 *    объедините Strings использование Monoid[String];
 *    преобразуйте результат обратно в a Symbol.
 *  Мы можем реализовать combine использование imap,
 *  передавая функции типа String => Symbol и Symbol => String в качестве параметров.
 *  Вот код, написанный с использованием метода imap расширения,
 *  предоставленного cats.syntax.invariant
 */
  import cats.Monoid
  import cats.instances.string._ // for Monoid
  import cats.syntax.invariant._ // for imap
  import cats.syntax.semigroup._ // for |+|

  implicit val symbolMonoid: Monoid[Symbol] =
    Monoid[String].imap(Symbol.apply)(_.name)

  Monoid[Symbol].empty
  // res3: Symbol = '

  val r2 = Symbol("a") |+| Symbol("few") |+| Symbol("words")
  // res4: Symbol = 'afewwords
  println(r2)

  /**
   *  Aside: Partial Unification
   *  todo:
   */
  import cats.Functor
  import cats.instances.function._ // for Functor
  import cats.syntax.functor._     // for map

  val func1 = (x: Int)    => x.toDouble
  val func2 = (y: Double) => y * 2

  val func3 = func1.map(func2)
  // func3: Int => Double = scala.Function1$$Lambda$6493/63932183@157213ca
}
