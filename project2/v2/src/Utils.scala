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
      case r"^[AtSQq]{1}|^[pPdDcC]\d+|[rdpc]{1}" => true
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
    var regex:scala.util.matching.Regex = """([PpDdCc]{1})(\d+)""".r
    input match {
      case regex(device,deviceNo) => (device,Integer.parseInt(deviceNo))
      case _ => ("",-1)
    }

  }
}