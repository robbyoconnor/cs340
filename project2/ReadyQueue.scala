import scala.collection.mutable.PriorityQueue

class ReadyQueue {

  import scala.collection.mutable.ArrayBuffer

  var queue = new PriorityQueue[PCB]()(new PCBRQOrdering)


  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue(): PCB = {
    val pcb = queue.dequeue()
    pcb
  }

  def snapshot() = {
    println("Ready Queue")
    var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
    data += ArrayBuffer("PID", "Cylinder", "Memstart", "filename", "file length", "total CPU time", "burst est", "time rem.", "avg burst time")
    for (pcb <- queue) {
      data += Utils.pcbToList(pcb)
    }
    println(Utils.Tabulator.format(data))
  }
}