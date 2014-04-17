object PID {
  var pid = -1
}
class PCB {
  var memoryStartRegion: Int = 0

  var readwrite: String = ""

  var fileName: String = ""

  var fileSize: Int = 0

  var cylinder: Int = 0

  var tau: Int = 0

  var pid: Int = PID.pid + 1

  PID.pid += 1
  pid = PID.pid

}

