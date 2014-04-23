trait Device {

  import scala.collection.mutable

  var queue = new mutable.Queue[PCB]

  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue(): PCB = {
    val pcb = queue.dequeue()
    pcb
  }

  def snapshot(name: String) = {
    println(s"$name\n")
    import scala.collection.mutable.ArrayBuffer
    var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
    data += ArrayBuffer("PID", "Cylinder", "Memstart", "filename", "file length", "total CPU time", "burst est", "time rem.", "avg burst time")
    for (pcb <- queue) {
      data += Utils.pcbToList(pcb)
    }
    println(Utils.Tabulator.format(data))
  }
}

class Disk(num: Int, cylinders: Int) extends Device {

  import scala.collection.mutable

  val pq1 = new mutable.PriorityQueue[PCB]()(new PCBDiskOrdering)
  val pq2 = new mutable.PriorityQueue[PCB]()(new PCBDiskOrdering)
  val name = s"disk $num"

  def enqueue(pcb: PCB, switchQ: Boolean): PCB = {
    if (switchQ)
      pq2 += pcb
    else
      pq1 += pcb
    pcb
  }

  def dequeue(switchQ: Boolean): PCB = {
    var pcb = null
    if (switchQ) {
      return pq2.dequeue()
    } else {
      return pq1.dequeue()
    }
  }

  override def snapshot() = {
    println(s"$name\n")
    import scala.collection.mutable.ArrayBuffer
    var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
    println("Queue 1")
    data += ArrayBuffer("PID", "Cylinder", "Memstart", "filename", "file length", "total CPU time", "burst est", "time rem.", "avg burst time")
    for (pcb <- queue) {
      data += Utils.pcbToList(pcb)
    }
    println(Utils.Tabulator.format(data))

    println("Queue 2")
    data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
    data += ArrayBuffer("PID", "Cylinder", "Memstart", "filename", "file length", "total CPU time", "burst est", "time rem.", "avg burst time")
    for (pcb <- queue) {
      data += Utils.pcbToList(pcb)
    }
    println(Utils.Tabulator.format(data))
  }
}


class Printer(num: Int) extends Device {
  val name = s"printer $num"
}

class CDRW(num: Int) extends Device {
  val name = s"cdrw $num"
}
