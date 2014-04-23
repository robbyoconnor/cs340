
import scala.collection.mutable.ArrayBuffer

class PCB {
  PID.pid += 1
  var pid = PID.pid

  var memoryStartRegion: Int = 0

  var readwrite: String = ""

  var fileName: String = ""

  var fileSize: Int = 0

  var cylinder: Int = 0

  var tau: Float = 0.0f

  var timeSpentInCPU: Float = 0.0f

  var timeLeftInCPU: Float = 0.0f

  var bursts: ArrayBuffer[Float] = new ArrayBuffer[Float]
}

class PCBRQOrdering extends Ordering[PCB] {
  override def compare(x: PCB, y: PCB): Int = y.tau compare x.tau
}

class PCBDiskOrdering extends Ordering[PCB] {
  override def compare(x: PCB, y: PCB): Int = y.cylinder compare x.cylinder
}

/**
 *
 * Singleton to hold a PID...
 */
object PID {
  var pid = -1
}