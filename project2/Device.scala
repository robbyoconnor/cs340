
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
    if (queue.isEmpty) {
      println(s"No processes in $name")
    } else {
      println(s"\n$name\n")
      import scala.collection.mutable.ArrayBuffer
      var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
      data += ArrayBuffer("PID", "Cyl.", "Mem", "file", "length", "cpu time", "tau", "time rem.", "avg burst time")
      for (pcb <- queue) {
        data += Utils.pcbToList(pcb)
      }
      println(Utils.Tabulator.format(data))
    }
  }
}

class Disk(num: Int, cylinders: Int) extends Device {

  import scala.collection.mutable

  var q1 = new mutable.ArrayBuffer[PCB]
  var q2 = new mutable.ArrayBuffer[PCB]
  val name = s"disk $num"
  var head: Int = 0
  var scanningQ1 = false
  var scanningQ2 = false
  var switchQ = false // true = pq2; false = p1


  override def enqueue(pcb: PCB): PCB = {
    if (!scanningQ1) q1.append(pcb)
    else if (!scanningQ2) q2.append(pcb)
    pcb
  }

  override def dequeue(): PCB = {
    selectActiveQ().remove(0)
  }

  def selectActiveQ(): mutable.ArrayBuffer[PCB] = {
    var ret: mutable.ArrayBuffer[PCB] = q1 // initialize to pq1
    if (q1.isEmpty && !switchQ) {
      ret = q1
      scanningQ1 = false
      switchQ = true
    } else if (q2.isEmpty && switchQ) {
      ret = q2
      scanningQ2 = false
      switchQ = false
    }
    if (!switchQ) {
      ret = q1
      scanningQ1 = true
    } else if (switchQ) {
      ret = q2
      scanningQ2 = true
    }
    ret = ret.sorted(new PCBDiskOrdering)



    ret.to
  }

  def size(): Int = selectActiveQ().size


  def fscan(): PCB = {
    var found: Boolean = false
    var foundPCB: PCB = null
    var isReverse: Boolean = false
    while (!found) {
      if (!isReverse) {
        if (head > cylinders) {
          isReverse = true
          head = cylinders
        }

        if (!isReverse) {
          if (switchQ) {
            for (pcb <- q2.sorted(new PCBDiskOrdering)) {
              if (pcb.cylinder == head) {
                found = true
                foundPCB = pcb
                q2 = q2.filterNot(pcb => pcb.cylinder == head)
                q2(0) = foundPCB
              }
            }
          } else {
            for (pcb <- q1.sorted(new PCBDiskOrdering)) {
              if (pcb.cylinder == head) {
                found = true
                foundPCB = pcb
                q1 = q1.filterNot(pcb => pcb.cylinder == head)
                q1(0) = foundPCB
              }
            }
          }
          head += 1
        } else {
          if (head < 0) {
            isReverse = false
            head = 0
          }

          if (switchQ) {
            for (pcb <- q2.sorted(new PCBDiskOrdering)) {
              if (pcb.cylinder == head) {
                found = true
                foundPCB = pcb
                q2 = q2.filterNot(pcb => pcb.cylinder == head)
                q2(0) = foundPCB
              }
            }
          } else {
            for (pcb <- q1.sorted(new PCBDiskOrdering)) {
              if (pcb.cylinder == head) {
                found = true
                foundPCB = pcb
                q1 = q1.filterNot(pcb => pcb.cylinder == head)
                q1(0) = foundPCB
              }
            }
          }
          head -= 1
        }
      }
    }
    foundPCB
  }


  override def snapshot(name: String) = {
    if (queue.isEmpty) {
      println(s"No processes in $name")
    } else {
      println(s"\n$name\n")
      import scala.collection.mutable.ArrayBuffer
      var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]
      println("Queue 1")
      data += ArrayBuffer("PID", "Cyl.", "Mem", "file", "length", "cpu time", "tau", "time rem.", "avg burst time")
      for (pcb <- queue) {
        data += Utils.pcbToList(pcb)
      }
      println(Utils.Tabulator.format(data))

      println("Queue 2")
      var data2: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]
      data += ArrayBuffer("PID", "Cyl.", "Mem", "file", "length", "cpu time", "tau", "time rem.", "avg burst time")
      for (pcb <- queue) {
        data2 += Utils.pcbToList(pcb)
      }
      println(Utils.Tabulator.format(data2))
    }
  }
}


class Printer(num: Int) extends Device {
  val name = s"printer $num"
}

class CDRW(num: Int) extends Device {
  val name = s"cdrw $num"
}
