/**
 * Could be better...too much redundancy
 * Utility stuff for shit....
 */
object Utils {


  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def checkInt(input: String): Boolean = {
    input match {
      case r"\d+" => true
      case _ => false
    }
  }

  def validateInput(input:String): Boolean = {
    input match {
      case r"^[AtSQq]{1}|^[pPdDcC]\d+|[rdpc]{1}|[rw]{1}" => true
      case _ => false
    }
  }

  def parseIfTerminate(input: String):Boolean = {
    input match {
      case r"^t{1}" => true
      case _ => false
    }
  }

  def doWeQuit(input:String): Boolean = {
    input match {
      case r"[Qq]{1}" => true
      case _  => false
    }
  }

  def extractDeviceInfo(input: String): (String,Int) = {
    val regex:scala.util.matching.Regex = """([PpDdCc]{1})(\d+)""".r
    input match {
      case regex(device,deviceNo) => (device,Integer.parseInt(deviceNo))
      case _ => ("",-1)
    }

  }

  def  populatePCB(pcb: PCB, printer:Boolean = false) {
    val fileName:String = readLine("Enter a file name: ")
    pcb.fileName = fileName

    pcb.readwrite = readLine("Is this a read or a write (r/w): ")
    while(!validateInput(pcb.readwrite)) {
      pcb.readwrite = readLine("Invalid input for read/write! You must only enter r or w! Try again: ")
    }
    if(pcb.readwrite == "w") {
      var fileSize =  readLine("How big is the file: ")
      while(!checkInt(fileSize)) {
         fileSize = readLine("Only enter integers for file size! Try again: ")
      }
      pcb.fileSize = fileSize.toInt

    }
    var memoryStartRegion = readLine("Enter a memory start region (must be an integer): ")
    while(!(checkInt(memoryStartRegion) && memoryStartRegion.toInt>0)) {
      memoryStartRegion = readLine("Must be an integer for memory start region! Try again: ")
    }
    pcb.memoryStartRegion = memoryStartRegion.toInt

    var cylinder = readLine("Enter a cylinder #: ")
    while (!(checkInt(cylinder) && cylinder.toInt >= 0)) {
      cylinder = readLine("Must be an integer for cylinder! Try again: ")
    }
    pcb.cylinder = cylinder.toInt
  }
}