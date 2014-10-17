class Device(var deviceName: String = "") {

  import scala.collection.mutable

  var name = deviceName
  var queue = new mutable.Queue[PCB]

  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue(): PCB = {
    var pcb = queue.dequeue()
    pcb
  }

  def snapshot(name: String) = {
    if (queue.isEmpty) {
      println(s"No processes in $name")
    } else {
      println(s"\n$name\n")
      Utils.snapshot(queue)
    }
  }
}

class Disk(num: Int, numCylinders: Int) extends Device {

  import scala.collection.mutable.ArrayBuffer

  name = s"disk $num"
  var q1 = new ArrayBuffer[PCB]
  var q2 = new ArrayBuffer[PCB]
  var head: Int = 0
  var scanningQ1 = false
  var scanningQ2 = false
  var switchQ = false // true = pq2; false = p1
  var cylinders = numCylinders


  override def enqueue(pcb: PCB): PCB = {
    selectActiveQ()
    if (!scanningQ1)
      q1.append(pcb)
    else if (!scanningQ2)
      q2.append(pcb)
    pcb

  }

  override def dequeue(): PCB = {
    fscan()
  }

  def size(): Int = {
    selectActiveQ()
    if (!switchQ)
      q1.size
    else
      q2.size
  }

  def selectActiveQ() = {
    if (q1.isEmpty && !switchQ) {
      scanningQ1 = false
      switchQ = true
    } else if (q2.isEmpty && switchQ) {
      scanningQ2 = false
      switchQ = false
    }
    if (!switchQ) {
      scanningQ1 = true
    } else if (switchQ) {
      scanningQ2 = true

    }
  }

  def fscan(): PCB = {
    var found: Boolean = false
    var foundPCB: PCB = null
    var isReverse: Boolean = false
    selectActiveQ()
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
              }
            }
          } else {
            for (pcb <- q1.sorted(new PCBDiskOrdering)) {
              if (pcb.cylinder == head) {
                found = true
                foundPCB = pcb
                q1 = q1.filterNot(pcb => pcb.cylinder == head)
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
              }
            }
          } else {
            for (pcb <- q1.sorted(new PCBDiskOrdering)) {
              if (pcb.cylinder == head) {
                found = true
                foundPCB = pcb
                q1 = q1.filterNot(pcb => pcb.cylinder == head)
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
    println(s"\n$name\n")
    if (q1.isEmpty) {
      println(s"No processes in $name Queue 1")
    } else {
      println("FSCAN Queue 1")
      Utils.snapshot(q1)
    }
    if(q2.isEmpty) {
      println(s"No processes in $name Queue 2")
    } else {
      println("FSCAN Queue 2")
      Utils.snapshot(q2)
    }
  }
}


class Printer(num: Int) extends Device {
  name = s"printer $num"
}

class CDRW(num: Int) extends Device {
  name = s"cdrw $num"
}

