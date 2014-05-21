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
   

  def snapshot:String ={
    if (queue.isEmpty)
      "Job Pool is empty"      
    else {
      var data = new ArrayBuffer[ArrayBuffer[Any]]()
      data += ArrayBuffer("PID", "Limit")
      for (job <- queue) {
        data += ArrayBuffer(job.pid, job.limit)
      }
      Utils.Tabulator.format(data)
    }
  }
}