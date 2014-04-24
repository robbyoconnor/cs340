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
    if (queue.isEmpty) {
      println(s"No processes in Ready Queue")
    } else {
      println("Ready Queue")
      var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
      data += ArrayBuffer("PID", "Cyl.", "Mem", "file", "length", "cpu time", "tau", "time rem.", "avg burst time")
      for (pcb <- queue) {
        data += Utils.pcbToList(pcb)
      }
      println(Utils.Tabulator.format(data))
    }
  }
}