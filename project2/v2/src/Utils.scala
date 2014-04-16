
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
}
