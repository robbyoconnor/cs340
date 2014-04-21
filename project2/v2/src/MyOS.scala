
import scala.collection.mutable.PriorityQueue

class ReadyQueue {
                                                                             e
  var queue = new PriorityQueue[PCB]()(new PCBOrdering)


  def enqueue(pcb:PCB):PCB = {
    queue += pcb
    pcb
  }

  def dequeue(): PCB = {
    val pcb = queue.dequeue()
    pcb
  }
}