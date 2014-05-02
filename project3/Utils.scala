
/**
 * Could be better...too much redundancy
 * Utility stuff for shit....
 */
object Utils {

  import scala.collection.mutable.ArrayBuffer


  def parseIfTerminate(input: String): Boolean = {
    input match {
      case r"^t{1}" => true
      case _ => false
    }
  }

  def doWeQuit(input: String): Boolean = input match {
    case r"[Qq]{1}" => true
    case _ => false
  }

  def extractDeviceInfo(input: String): (String, Int) = {
    val regex: scala.util.matching.Regex = """([PpDdCc]{1})(\d+)""".r
    input match {
      case regex(device, deviceNo) => (device, deviceNo.toInt)
      case _ => ("", -1) // This is a just in case -- we'll never hit this.
    }

  }

  def isInterrupt(input: String): Boolean = {
    val regex: scala.util.matching.Regex = """[PDC]{1}\d+""".r
    input match {
      case regex(in) => true
    }
    false
  }

  def populatePCB(pcb: PCB, printer: Boolean = false, disk: Boolean = false, numCylinders: Int = 0, alpha: Float): PCB = {

    val timeSpent = promptForFloat("How much time was this process in the CPU in ms (float)")
    pcb.bursts += timeSpent
    pcb.cpuTime += timeSpent
    pcb.tau = recalculateTau(alpha = alpha, prevTau = pcb.tau, timeInCPU = timeSpent)
    pcb.tauLeft = 0
    pcb.burstCount += 1

    val fileName: String = readLine("Enter a file name: ")
    pcb.fileName = fileName

    if (!printer) {
      pcb.readwrite = readLine("Is this a read or a write (r/w): ")
      while (!validateInput(pcb.readwrite)) {
        pcb.readwrite = readLine("Invalid input for read/write! You must only enter r or w! Try again: ")
      }
    } else {
      pcb.readwrite = "w"
    }
    if (pcb.readwrite == "w") {
      pcb.fileSize = promptForFloat("How big is the file: ")
    }
    print("Enter a memory start region (must be an integer): ")
    pcb.memoryStartRegion = promptForInt()

    if (disk) {
      print("Enter a cylinder #: ")
      var cylinder = promptForInt()
      while (!checkRange(cylinder, 1, numCylinders)) {
        print(s"Invalid Cylinder specified Valid range [0,$numCylinders]: ")
        cylinder = promptForInt()
      }
      pcb.cylinder = cylinder
    }
    pcb
  }


  def validateInput(input: String): Boolean = {
    input.stripSuffix(" ")
    input.stripPrefix(" ")
    input match {
      case r"[AtSQq]{1}|[pPdDcC]\d+|[rdpc]{1}|[rw]{1}" => true
      case _ => false
    }
  }

  def promptForFloat(msg: String = "", a: Float = 0.0f, b: Float = 0.0f,validateRange: Boolean = false): Float = {
    var ret = 0.0f
    var valid = false
    do {
      try {
        if (msg.length > 0) print(msg)
        ret = readFloat()
        if(validateRange) {
          if (checkRange(ret, a, b)) {
            valid = true
          } else {
            valid = false
          }
        } else {
          valid = true
        }

      } catch {
        case ex: NumberFormatException => println(s"Not valid -- must be float between $a and $b: Try again: ")
      }
      if (!valid) {
        println(s"Not valid -- must be float between $a and $b: Try again: ")
      }
    } while (!valid)
    ret
  }


  def checkRange(input: Float, a: Float, b: Float): Boolean = input >= a && input <= b



  def promptForInt(msg: String = ""): Int = {
    var ret = 0
    var valid = false
    if (msg.length > 0) print(msg)
    do {
      try {
        ret = readInt()
        if(ret >0) valid = true
      } catch {
        case _: NumberFormatException => valid = false
      }
      if (!valid) {
        println("\nNot valid! Must be greater than 0")
      }
    } while (!valid)
    ret
  }
  def recalculateTau(prevTau: Float, alpha: Float, timeInCPU: Float): Float = alpha * prevTau + (1 - alpha) * timeInCPU

  def generateDisks(noOfDisk: Int): ArrayBuffer[Disk] = {
    val disks = new ArrayBuffer[Disk](noOfDisk)
    for (i <- 0 to noOfDisk - 1) {
      var cylinders = readLine(s"How many cylinders for disk ${i + 1}: ")
      while (!checkInt(cylinders)) {
        cylinders = readLine("Enter an integer please: ")
      }
      disks += new Disk(i + 1, cylinders.toInt)
    }
    disks
  }

  def checkInt(input: String): Boolean = {
    input match {
      case r"\d+" => true
      case _ => false
    }
  }

  def checkInterruptSyscall(input: String = "") = {
    input match {
      case r"[PDCpdc]{1}\d" => true
      case _ => false
    }
  }

  def snapshot(queue:Iterable[PCB]) = {
      var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
      data.append(ArrayBuffer("PID", "Cyl.", "Mem", "file", "length", "cpu time", "tau", "time rem.", "avg burst time"))
      for (pcb <- queue) {
        val averageBursts = if(pcb.burstCount > 0) pcb.bursts / pcb.burstCount else 0.0f
        data += ArrayBuffer(pcb.pid, pcb.cylinder, pcb.memoryStartRegion, pcb.fileName, pcb.fileSize, pcb.cpuTime, pcb.tau, pcb.tauLeft,averageBursts)
      }
      println(Tabulator.format(data))
  }

  // taken from somewhere -- I don't remember where -- point is I didn't write it but I don't know where I got it.
  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  // taken in whole from http://stackoverflow.com/a/7542476/1508101
  object Tabulator {
    def format(table: Seq[Seq[Any]]) = table match {
      case ArrayBuffer() => ""
      case _ =>
        val sizes = for (row <- table) yield (for (cell <- row) yield if (cell == null) 0 else cell.toString.length)
        val colSizes = for (col <- sizes.transpose) yield col.max
        val rows = for (row <- table) yield formatRow(row, colSizes)
        formatRows(rowSeparator(colSizes), rows)
    }

    def formatRows(rowSeparator: String, rows: Seq[String]): String = (
      rowSeparator ::
        rows.head ::
        rowSeparator ::
        rows.tail.toList :::
        rowSeparator ::
        List()).mkString("\n")

    def formatRow(row: Seq[Any], colSizes: Seq[Int]) = {
      val cells = (for ((item, size) <- row.zip(colSizes)) yield if (size == 0) "" else ("%" + size + "s").format(item))
      cells.mkString("|", "|", "|")
    }

    def rowSeparator(colSizes: Seq[Int]) = colSizes map {
      "-" * _
    } mkString("+", "+", "+")
  }
  //--------------------------------------------------------------------------------------------------------------------

}

