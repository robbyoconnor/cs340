
import scala.collection.mutable.ArrayBuffer

class PCB {
  PID.pid += 1
  var pid = PID.pid

  var memoryStartRegion: Int = 0

  var readwrite: String = ""

  var fileName: String = ""

  var fileSize: Float = 0.0f

  var cylinder: Int = 0

  var tau: Float = 0.0f

  var cpuTime: Float = 0.0f

  var tauLeft: Float = 0.0f

  var bursts: Float = 0.0f

  var burstCount: Int  = 0
}

class PCBRQOrdering extends Ordering[PCB] {
  override def compare(x: PCB, y: PCB): Int = x.tauLeft compare y.tauLeft
}

class PCBDiskOrdering extends Ordering[PCB] {
  override def compare(x: PCB, y: PCB): Int = x.cylinder compare y.cylinder
}

/**
 *
 * Singleton to hold a PID...
 */
object PID {
  var pid = -1
}