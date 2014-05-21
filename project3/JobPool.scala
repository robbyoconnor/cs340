class JobPool {

  import scala.collection.mutable.ArrayBuffer

  val queue = new ArrayBuffer[PCB]
  
  def empty:Boolean = queue.isEmpty
  
  def size: Int = queue.size
  
  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue: PCB = queue.remove(0)
   
}