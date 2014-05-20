
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

  def populatePCB(pcb: PCB, printer: Boolean = false, disk: Boolean = false, numCylinders: Int = 0, alpha: Float): PCB = {

    val timeSpent = promptForFloat("How much time was this process in the CPU in ms (float)")
    pcb.bursts += timeSpent
    pcb.cpuTime += timeSpent
    pcb.tau = recalculateTau(alpha = alpha, prevTau = pcb.tau, timeInCPU = timeSpent)
    pcb.tauLeft = 0
    pcb.burstCount += 1
    pcb.fileName = readLine("Enter a file name: ")

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
    val memoryStartRegion = promptForInt("Enter a memory start region (must be an integer): ")
    if (!validateLogicalAddress(pcb.base, memoryStartRegion, pcb.limit)) {
      println("Invalid memory start address!")
      return null
    } else {
      pcb.memoryStartRegion = memoryStartRegion
    }

    if (disk) {
      var cylinder = promptForInt("Enter a cylinder #: ")
      while (!checkRange(cylinder, 1, numCylinders)) {
        cylinder = promptForInt(s"Invalid Cylinder specified Valid range [1,$numCylinders]: ")
      }
      pcb.cylinder = cylinder
    }
    pcb
  }

  def validateInput(input: String): Boolean = {
    input.stripSuffix(" ")
    input.stripPrefix(" ")
    input match {
      case r"[AtSQq]{1}|[pPdDcC]\d+|[rdpcm]{1}|[rw]{1}" => true
      case _ => false
    }
  }

  def promptForFloat(msg: String = "", a: Float = 0.0f, b: Float = 0.0f, validateRange: Boolean = false): Float = {
    var ret = 0.0f
    var valid = false
    do {
      try {
        if (msg.length > 0) print(msg)
        ret = readFloat()
        if (validateRange) {
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

  def checkRange(input: Int, a: Int, b: Int): Boolean = input >= a && input <= b

  def validateLogicalAddress(base: Int, logical: Int, limit: Int) = logical >= base && logical < limit

  def logicalToPhysicalAddress(base: Int, logical: Int, limit: Int): Int = if (validateLogicalAddress(base, logical, limit)) base + logical else -1

  def promptForInt(msg: String = ""): Int = {
    var ret = 0
    var valid = false
    if (msg.length > 0) print(msg)
    do {
      try {
        ret = readInt()
        if (ret > 0) valid = true
      } catch {
        case _: NumberFormatException => valid = false
      }
      if (!valid) {
        println(s"\nNot valid! Must be an integer greater than 0 and less than ${Int.MaxValue}")
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
      case r"[PDCpdc]{1}\d+" => true
      case _ => false
    }
  }

  def snapshot(queue: Iterable[PCB]) = {
    var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
    data.append(ArrayBuffer("PID", "Cyl.", "file", "length", "cpuTime", "tau", "timeRem.", "avgBurst", "base", "limit", "physAddr."))
    for (pcb <- queue) {
      val averageBursts = if (pcb.burstCount > 0) pcb.bursts / pcb.burstCount else 0.0f
      data += ArrayBuffer(pcb.pid, pcb.cylinder, pcb.fileName, pcb.fileSize, pcb.cpuTime, pcb.tau, pcb.tauLeft, averageBursts, pcb.base, pcb.limit, pcb.memoryStartRegion)
    }
    println(Tabulator.format(data))
  }

  def snapshot(holes: Iterable[Block], mem: Iterable[PCB], os: OS) {
    var data = new ArrayBuffer[ArrayBuffer[Any]]()
    var data2 = new ArrayBuffer[ArrayBuffer[Any]]
    data += ArrayBuffer("Base", "Limit")
    data2 += ArrayBuffer("PID", "Base", "Limit")
    for (block <- holes) {
      val base = block.base
      val limit = block.limit
      data += ArrayBuffer(base, limit)
    }
    if (mem.size > 0) {
      for (block <- mem) {
        val base = block.base
        val limit = block.limit
        data2 += ArrayBuffer(block.pid, base, limit)
      }
    }
    println(s"Job Pool\n\n${os.jobpool.snapshot}\n\nHoles\n\n${ if(holes.size >0) Tabulator.format(data) else "No Holes."}\nMemory\n${if (!mem.isEmpty) Tabulator.format(data2) else "Empty"}")
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
    } mkString ("+", "+", "+")
  }
  //--------------------------------------------------------------------------------------------------------------------
}
