import com.sun.jna.Native

trait Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
  def Beep(dwFreq: Int, dwDuration: Int): Unit
}

object Kernel32 {
  private lazy val instance: Kernel32 =
    Native.load("kernel32", classOf[Kernel32])

  def apply(): Kernel32 = instance

  def main(args: Array[String]): Unit = {
    /*val c: List[Int] = Main.frequency.toList.sortBy(_._1).map(_._2).map(Math.round(_).toInt).filter(e => e >= 131 && e <= 3951)

    val notes = List(1, 3, 5, 6, 8, 10, 12)

    for (note <- notes ++ notes.map(_ + 13))
      instance.Beep(c(note), 500)*/


    instance.Beep(37, 1000)
  }
}
