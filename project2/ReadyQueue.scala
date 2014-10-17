class ReadyQueue {

  import scala.collection.mutable.PriorityQueue

  val queue = new PriorityQueue[PCB]()(new PCBRQOrdering)


  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue(): PCB = {
    val pcb = queue.dequeue()
    pcb
  }

  def snapshot() {
    if(queue.isEmpty)
      println("No processes in Ready Queue!")
     else
      Utils.snapshot(queue)
  }
}