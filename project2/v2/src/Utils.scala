
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

  def isInterrupt(input:String): Boolean = {
    val regex: scala.util.matching.Regex = """[PDC]{1}\d+""".r
    input match {
      case regex => true
      case _ => false
    }
  }

  def populatePCB(pcb: PCB, printer: Boolean = false, disk: Boolean = false, numCylinders: Int = 0 ): PCB = {
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
      while(checkRange(cylinder,0,numCylinders)) {
        print(s"Invalid Cylinder specified Valid range [0,$numCylinders]: ")
        cylinder = promptForInt()
      }
      pcb.cylinder = cylinder
    }
    pcb
  }

  def validateInput(input: String): Boolean = {
    input match {
      case r"^[AtSQq]{1}|^[pPdDcC]\d+|[rdpc]{1}|[rw]{1}" => true
      case _ => false
    }
  }

  def generateDisks(noOfDisk: Int): ArrayBuffer[Disk] = {
    val disks = new ArrayBuffer[Disk](noOfDisk)
    for (i <- 0 to noOfDisk-1) {
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

  def checkRange(input:Float, a:Float,b:Float): Boolean = {
    input>=a && input<=b
  }

  def checkRange(input:Int, a:Int, b:Int):Boolean = {
    input>=a && input <=b
  }

  def promptForFloat(a:Float, b:Float): Float = {
    var ret = 0.0f
    var valid = false
    do {
      try {
        ret = readFloat()
        if (checkRange(ret,0.0f,1.0f)) {
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


  def promptForInt(): Int = {
    var ret = 0
    var valid = false
    do {
      try {
        ret = readInt()
        if (ret>0) {
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





  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
}