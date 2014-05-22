class JobPool {

  import scala.collection.mutable.ArrayBuffer

  val queue = new ArrayBuffer[PCB]

  def empty: Boolean = queue.isEmpty

  def size: Int = queue.size

  def enqueue(pcb: PCB): PCB = {
    queue += pcb
    pcb
  }

  def dequeue: PCB = queue.remove(0)

  def processJobPool(os: OS) {
    if (queue.isEmpty) {
      return
    } else {
      for (job <- queue) {
        val _pcb = os.allocate(job.limit, job)
        if (_pcb.isDefined) {
          os.memory += job
          os.readyqueue.enqueue(job)
          queue -= job
          println(s"Process ${job.pid} has been moved from the job pool to the ready queue!")
        }
      }
    }
  }

  def snapshot: String = {
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