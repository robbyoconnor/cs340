var global_pid : Int = -1
class PCB(var memory_start_region: Int = 0, var readwrite: String = "",
          var file_name: String = "", var file_size: Int = 0,
          var cylinder: Int = 0, val pid:Int = global_pid+1 ) {
  global_pid = global_pid + 1

  def populatePCB(pcb: PCB, printer: Boolean = false) = {

  }
}


