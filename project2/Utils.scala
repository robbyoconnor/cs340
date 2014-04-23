
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
      case regex => true
    }
    false
  }

  def populatePCB(pcb: PCB, printer: Boolean = false, disk: Boolean = false, numCylinders: Int = 0, alpha: Float): PCB = {
    print("How much time was this process in the CPU in ms (float)")
    val timeSpent = promptForFloat()
    pcb.timeSpentInCPU += timeSpent
    pcb.bursts += pcb.tau

    pcb.timeLeftInCPU -= (timeSpent + recalculateTau(pcb.tau, alpha, timeSpent))

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
      print("How big is the file: ")
      pcb.fileSize = promptForInt()
    }
    print("Enter a memory start region (must be an integer): ")
    pcb.memoryStartRegion = promptForInt()

    if (disk) {
      print("Enter a cylinder #: ")
      var cylinder = promptForInt()
      while (checkRange(cylinder, 0, numCylinders)) {
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
      case r"^[AtSQq]{1}|^[pPdDcC]\d+|[rdpc]{1}|[rw]{1}" => true
      case _ => false
    }
  }

  def checkRange(input: Int, a: Int, b: Int): Boolean = {
    input >= a && input <= b
  }

  def promptForFloat(msg: String = "", a: Float = 0.0f, b: Float = 0.0f): Float = {
    var ret = 0.0f
    var valid = false
    do {
      try {
        ret = readFloat()
        if (checkRange(ret, 0.0f, 1.0f)) {
          valid = true
        }
        if (!valid) {
          print(s"Not valid -- must be float between $a and $b: Try again: ")
        }
      } catch {
        case ex: NumberFormatException => print(s"Not valid -- must be float between $a and $b: Try again: ")
      }
    } while (!valid)
    ret
  }

  def checkRange(input: Float, a: Float, b: Float): Boolean = {
    input >= a && input <= b
  }

  def promptForInt(msg: String = ""): Int = {
    var ret = 0
    var valid = false
    do {
      try {
        print(msg)
        ret = readInt()
        if (ret > 0) {
          valid = true
        }
        if (!valid) {
          print("Not valid -- must be a positive integer -- Try again: ")
        }
      } catch {
        case ex: NumberFormatException => print("Not valid -- must be a positive integer -- Try again: ")
      }
    } while (!valid)
    ret
  }

  def recalculateTau(prevTau: Float, alpha: Float, timeInCPU: Float): Float = {
    val tau = alpha * prevTau + (1 - alpha) * timeInCPU
  }

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

  def pcbToList(pcb: PCB, terminating: Boolean = false): ArrayBuffer[Any] = {
    val row = new ArrayBuffer[Any]()
    if (terminating) {
      row += pcb.timeSpentInCPU += (pcb.bursts.sum / pcb.bursts.size)
    } else {
      row += pcb.pid.toString() += pcb.cylinder += pcb.memoryStartRegion.toString() += pcb.fileName += pcb.fileSize.toString() += pcb.timeSpentInCPU.toString() += pcb.tau.toString() += pcb.timeLeftInCPU.toString() += ((pcb.bursts.sum / pcb.bursts.size.toFloat)).toString()
    }
  }
    :

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

}

