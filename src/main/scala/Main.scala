import javax.sound.midi.{MidiSystem, ShortMessage}

object Main {

  case class Note(key: Int, velocity: Int) {
    val freq: Double = frequency(key)
    val freqInt: Int = Math.round(freq).toInt

    def isAudible: Boolean = freqInt >= 37 && freqInt <= 32_767
  }

  def main(args: Array[String]): Unit = {
    val sequence = MidiSystem.getSequence(getClass.getClassLoader.getResourceAsStream("Jurassic_Park_Theme.mid"))

    println(sequence.getTracks.length + " Tracks")

    val keys = sequence.getTracks.zipWithIndex.filter(e => e._2 == 2 || e._2 == 3).map(_._1).toList
      .flatMap(e => (0 until e.size()).map(e.get).toList)
      .map(e => e.getTick -> e.getMessage)
      .foldLeft(List.empty[(Long, List[Note])]) {
        case (track, (tick, shortMessage: ShortMessage)) =>
          val command = shortMessage.getCommand
          val key = shortMessage.getData1
          val velocity = shortMessage.getData2

          val (notes, trackTail) = track.headOption match {
            case Some((`tick`, notes)) => (notes, track.tail)
            case Some((_, notes)) => (notes, track)
            case None => (List.empty, track)
          }

          (command, velocity) match {
            case (ShortMessage.NOTE_OFF, _) | (ShortMessage.NOTE_ON, 0) =>
              (tick, notes.filterNot(_.key == key)) +: trackTail

            case (ShortMessage.NOTE_ON, velocity) =>
              (tick, Note(key, velocity) +: notes.filterNot(_.key == key)) +: trackTail

            case _ => track
          }

        case (notes, _) => notes
      }
      .reverse
      .map(e => (e._1, e._2.filter(_.isAudible)))

    val highestMergedNotes: List[(Long, Option[Note])] = keys.map {
      case (tick, notes) => (tick, notes.maxByOption(_.key))
    }.foldLeft(List.empty[(Long, Option[Note])]) {
      case (track, (tick, noteOption)) =>
        if (track.headOption.exists(_._2 == noteOption))
          track
        else
          (tick, noteOption) +: track
    }.reverse

    val ticksToMillis: Double = (sequence.getTickLength.toDouble / sequence.getMicrosecondLength) * 1000

    val noteLengths = highestMergedNotes.foldLeft[((Long, Option[Note]), List[(Option[Note], Int)])](((0, None), List.empty)) {
      case (((lastTick, lastNoteOption), track), (tick, noteOption)) =>
        val tickDelta = tick - lastTick
        val duration = (tickDelta / ticksToMillis).toInt
        ((tick, noteOption), (lastNoteOption, duration) +: track)
    }._2.reverse
      .dropWhile(_._1.isEmpty)

    val commands =
      noteLengths.map {
        case (None, duration) =>
          s"${if (duration >= 2000) "\n" else ""}:delay ${duration}ms"

        case (Some(note), duration) =>
          s":beep frequency=${note.freqInt} length=${duration}ms"
      }
        .mkString("; ")

    println(commands)

    noteLengths.foreach {
      case (None, duration) =>
        Thread.sleep(if (duration > 2000) 2000 else duration)

      case (Some(note), duration) =>
        Kernel32().Beep(note.freqInt, duration)
    }
  }

  val frequency: Map[Int, Double] = Map[Int, Double](
    0 -> 8.1757989156,
    1 -> 8.6619572180,
    2 -> 9.1770239974,
    3 -> 9.7227182413,
    4 -> 10.3008611535,
    5 -> 10.9133822323,
    6 -> 11.5623257097,
    7 -> 12.2498573744,
    8 -> 12.9782717994,
    9 -> 13.7500000000,
    10 -> 14.5676175474,
    11 -> 15.4338531643,
    12 -> 16.3515978313,
    13 -> 17.3239144361,
    14 -> 18.3540479948,
    15 -> 19.4454364826,
    16 -> 20.6017223071,
    17 -> 21.8267644646,
    18 -> 23.1246514195,
    19 -> 24.4997147489,
    20 -> 25.9565435987,
    21 -> 27.5000000000,
    22 -> 29.1352350949,
    23 -> 30.8677063285,
    24 -> 32.7031956626,
    25 -> 34.6478288721,
    26 -> 36.7080959897,
    27 -> 38.8908729653,
    28 -> 41.2034446141,
    29 -> 43.6535289291,
    30 -> 46.2493028390,
    31 -> 48.9994294977,
    32 -> 51.9130871975,
    33 -> 55.0000000000,
    34 -> 58.2704701898,
    35 -> 61.7354126570,
    36 -> 65.4063913251,
    37 -> 69.2956577442,
    38 -> 73.4161919794,
    39 -> 77.7817459305,
    40 -> 82.4068892282,
    41 -> 87.3070578583,
    42 -> 92.4986056779,
    43 -> 97.9988589954,
    44 -> 103.8261743950,
    45 -> 110.0000000000,
    46 -> 116.5409403795,
    47 -> 123.4708253140,
    48 -> 130.8127826503,
    49 -> 138.5913154884,
    50 -> 146.8323839587,
    51 -> 155.5634918610,
    52 -> 164.8137784564,
    53 -> 174.6141157165,
    54 -> 184.9972113558,
    55 -> 195.9977179909,
    56 -> 207.6523487900,
    57 -> 220.0000000000,
    58 -> 233.0818807590,
    59 -> 246.9416506281,
    60 -> 261.6255653006,
    61 -> 277.1826309769,
    62 -> 293.6647679174,
    63 -> 311.1269837221,
    64 -> 329.6275569129,
    65 -> 349.2282314330,
    66 -> 369.9944227116,
    67 -> 391.9954359817,
    68 -> 415.3046975799,
    69 -> 440.0000000000,
    70 -> 466.1637615181,
    71 -> 493.8833012561,
    72 -> 523.2511306012,
    73 -> 554.3652619537,
    74 -> 587.3295358348,
    75 -> 622.2539674442,
    76 -> 659.2551138257,
    77 -> 698.4564628660,
    78 -> 739.9888454233,
    79 -> 783.9908719635,
    80 -> 830.6093951599,
    81 -> 880.0000000000,
    82 -> 932.3275230362,
    83 -> 987.7666025122,
    84 -> 1046.5022612024,
    85 -> 1108.7305239075,
    86 -> 1174.6590716696,
    87 -> 1244.5079348883,
    88 -> 1318.5102276515,
    89 -> 1396.9129257320,
    90 -> 1479.9776908465,
    91 -> 1567.9817439270,
    92 -> 1661.2187903198,
    93 -> 1760.0000000000,
    94 -> 1864.6550460724,
    95 -> 1975.5332050245,
    96 -> 2093.0045224048,
    97 -> 2217.4610478150,
    98 -> 2349.3181433393,
    99 -> 2489.0158697766,
    100 -> 2637.0204553030,
    101 -> 2793.8258514640,
    102 -> 2959.9553816931,
    103 -> 3135.9634878540,
    104 -> 3322.4375806396,
    105 -> 3520.0000000000,
    106 -> 3729.3100921447,
    107 -> 3951.0664100490,
    108 -> 4186.0090448096,
    109 -> 4434.9220956300,
    110 -> 4698.6362866785,
    111 -> 4978.0317395533,
    112 -> 5274.0409106059,
    113 -> 5587.6517029281,
    114 -> 5919.9107633862,
    115 -> 6271.9269757080,
    116 -> 6644.8751612791,
    117 -> 7040.0000000000,
    118 -> 7458.6201842894,
    119 -> 7902.1328200980,
    120 -> 8372.0180896192,
    121 -> 8869.8441912599,
    122 -> 9397.2725733570,
    123 -> 9956.0634791066,
    124 -> 10548.0818212118,
    125 -> 11175.3034058561,
    126 -> 11839.8215267723,
    127 -> 12543.8539514160
  )
}
