class JobPool {

  import scala.collection.mutable.ArrayBuffer

  val queue = new ArrayBuffer[PCB]()


  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue(): PCB = {
    val pcb = queue.remove(0)
    pcb
  }

  def snapshot() {
    if(queue.isEmpty)
      println("No processes in Job Queue!")
     else
      Utils.snapshot(queue)
  }
}