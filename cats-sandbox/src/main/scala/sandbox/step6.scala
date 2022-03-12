package sandbox

object step6 extends App {

  /**
   * Functors
   * Function1
   */
  import cats.instances.function._ // for Functor
  import cats.syntax.functor._ // for map

  val func1: Int => Double = (x: Int) => x.toDouble

  val func2: Double => Double = (y: Double) => y * 2

  val y1 = (func1 map func2) (1)     // composition using map
  val y2 = (func1 andThen func2) (1) // composition using andThen
  val y3 = func2(func1(1))                // composition written out by hand

  println(y1)
  println(y2)
  println(y3)

  val func = ((x: Int) => x.toDouble).
      map(x => x + 1).
      map(x => x * 2).
      map(x => s"${x}!")

  println( func(123) )

  /**
   *   trait Functor[F[_]] {
   *      def map[A, B](fa: F[A])(f: A => B): F[B]
   *   }
   *   law:
   *   fa.map(a => a) == fa
   *   fa.map(g(f(_))) == fa.map(f).map(g)
   */

  import cats.Functor
  import cats.instances.list._   // for Functor
  import cats.instances.option._ // for Functor

  val list1 = List(1, 2, 3)
  val list2 = Functor[List].map(list1)(_ * 2)

  val option1 = Option(123)
  val option2 = Functor[Option].map(option1)(_.toString)

  /**
   * Functor предоставляет вызываемый метод lift, который преобразует функцию типа A => B в функцию,
   * которая работает над функтором и имеет тип F[A] => F[B]
   */
  {
    val func = (x: Int) => x + 1
    // func: Int => Int = <function1>
    val liftedFunc = Functor[Option].lift(func)
    // liftedFunc: Option[Int] => Option[Int] = cats.Functor$$Lambda$11546/665425203@13439aca
    println( liftedFunc(Option(1)) ) // res1: Option[Int] = Some(2)

    /**
     * Этот as метод-это другой метод, который вы, скорее всего, используете.
     * Он заменяет значение внутри Functor на заданное значение.
     *  def as[A, B](fa: F[A], b: B): F[B] = map(fa)(_ => b)
     */
    Functor[List].as(list1, "As")
    // res2: List[String] = List("As", "As", "As")
  }

  /**
   * Основной метод, предусмотренный синтаксисом для Functor, -  map.
   *
   * Сначала давайте посмотрим на отображение функций.
   * Function1Тип Scala не имеет map метода (andThen вместо этого), поэтому конфликтов имен нет:
   */
  {
    import cats.instances.function._ // for Functor
    import cats.syntax.functor._ // for map

    val func1 = (a: Int) => a + 1
    val func2 = (a: Int) => a * 2
    val func3 = (a: Int) => s"${a}!"
    val func4 = func1.map(func2).map(func3)

    println( func4(123) ) // 248!
  }

  /**
   * implicit class FunctorOps[F[_], A](src: F[A]) {
   *    def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
   *        functor.map(src)(func)
   * }
   *
   * Компилятор может использовать этот метод расширения для вставки map метода везде, где нет встроенного map:
   * foo.map(value => value + 1)
   *
   * Предполагая foo, что у него нет встроенного map метода, компилятор обнаруживает потенциальную
   * ошибку и обертывает выражение в FunctorOps, чтобы исправить код:
   *
   * new FunctorOps(foo).map(value => value + 1)
   *
   * Код будет компилироваться только в том случае, если у нас есть Functor F в области видимости.
   */
  {
    def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] =  start.map(n => n + 1 * 2)

    import cats.instances.option._ // for Functor
    import cats.instances.list._ // for Functor

    doMath(Option(20))
    // res4: Option[Int] = Some(22)
    doMath(List(1, 2, 3))
    // res5: List[Int] = List(3, 4, 5)

    /**
     * as Метод также доступен в виде синтаксиса.
     */
    List(1, 2, 3).as("As")
    // res7: List[String] = List("As", "As", "As")
  }

  /**
   * Экземпляры пользовательских типов
    implicit val optionFunctor: Functor[Option] = new Functor[Option] {
        def map[A, B](value: Option[A])(func: A => B): Option[B] =
          value.map(func)
    }
  */
  /**
   *  Мы не можем добавить дополнительные параметры в functor.map,
   *  поэтому мы должны учитывать зависимость при создании экземпляра(ExecutionContext):
   *
   *  import scala.concurrent.{Future, ExecutionContext}
   *
   *  implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] =  new Functor[Future] {
   *      def map[A, B](value: Future[A])(func: A => B): Future[B] =
   *        value.map(func)
   *    }
   */

}
