object PID {
  var pid = -1
}
class PCB {
  var memory_start_region: Int = 0

  var readwrite: String = ""

  var file_name: String = ""

  var file_size: Int = 0

  var cylinder: Int = 0

  var pid: Int = PID.pid + 1

  PID.pid += 1
  pid = PID.pid

  def populatePCB() = {
    memory_start_region = readLine();
    check

  }
}
