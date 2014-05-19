
class PCB {
  PID.pid += 1
  var pid = PID.pid

  var memoryStartRegion: Int = 0

  var base: Int = 0

  var limit: Int = 0

  var readwrite: String = ""

  var fileName: String = ""

  var fileSize: Float = 0.0f

  var cylinder: Int = 0

  var tau: Float = 0.0f

  var cpuTime: Float = 0.0f

  var tauLeft: Float = 0.0f

  var bursts: Float = 0.0f

  var burstCount: Int = 0
}

class PCBRQOrdering extends Ordering[PCB] {
  override def compare(x: PCB, y: PCB): Int = y.tauLeft compare x.tauLeft
}

class PCBDiskOrdering extends Ordering[PCB] {

  override def compare(x: PCB, y: PCB): Int = y.cylinder compare x.cylinder

}

class PCBMemOrdering extends Ordering[PCB] {

  override def compare(x: PCB, y: PCB): Int = y.base compare x.base

}

/*
 *
 * Singleton to hold a PID...
 */
object PID {
  var pid = -1
}

class Block(_base: Int, _limit: Int) {
  var base: Int = _base
  var limit: Int = _limit
  var size: Int = limit - base
}