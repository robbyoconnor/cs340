import scala.collection.mutable.ArrayBuffer

trait Device {
  var queue = new ArrayBuffer[PCB]
}


class Disk(num: Int, cylinders: Int) extends Device {
  val name: String = s"disk $name"
}

class Printer(num: Int) extends Device {
  var name: String = s"printer $name"
}

class CDRW(num: Int) extends Device {
  var name: String = s"cdrw $num"
}
