
import scala.collection.mutable.ArrayBuffer

class PCB {
  PID.pid += 1
  pid = PID.pid

  var memoryStartRegion: Int = 0

  var readwrite: String = ""

  var fileName: String = ""

  var fileSize: Int = 0

  var cylinder: Int = 0

  var tau: Float = 0

  var pid: Int = PID.pid + 1

  var timeSpentInCPU: Int = 0

  var bursts = new ArrayBuffer[Int]

}

/**
 *
 * Singleton to hold a PID...
 */
object PID {
  var pid = -1
}

class PCBOrdering extends Ordering[PCB] {
  override def compare(x: PCB, y: PCB): Int = y.tau compare x.tau
}
