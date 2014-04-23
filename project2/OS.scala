class OS {

  import scala.collection.mutable.ArrayBuffer

  var printers = new ArrayBuffer[Printer]
  var cdrws = new ArrayBuffer[CDRW]
  var disks = new ArrayBuffer[Disk]
  var readyqueue = new ReadyQueue

  var alpha: Float = 0.0f

  var initialTau: Int = 0

  def sysgen() = {
    println("Welcome to sysgen! I'll be your guide...let's set up the system!")
    print("How many printers: ")
    var printerCnt = Utils.promptForInt()

    for (i <- 0 to printerCnt - 1) printers += new Printer(i + 1)

    print("How many disks: ")
    var disk_cnt = Utils.promptForInt()

    disks = Utils.generateDisks(disk_cnt)

    print("How many CDRWs: ")
    var cdrw_cnt = Utils.promptForInt()

    for (i <- 0 to cdrw_cnt - 1) cdrws += new CDRW(i + 1)

    print("Enter initial burst estimate (tau) (integer greater than 0): ")
    initialTau = Utils.promptForInt()

    print("Enter history parameter (alpha) (float between 0 and 1): ")
    alpha = Utils.promptForFloat(a = 0.0f, b = 1.0f)

  }

  def run() = {
    help()
    var done = false
    do {
      var userInput: String = readLine("[A,S,t,p#,d#,c#,P#,D#,C#]:")
      while (!Utils.validateInput(userInput)) {
        println("Invalid input")
        userInput = readLine("[A,S,t,p#,d#,c#,P#,D#,C#]:")
      }

      if (userInput == "A") {
        var pcb = new PCB
        pcb.tau = initialTau
        pcb.timeLeftInCPU = initialTau
        readyqueue.enqueue(pcb)
        println(s"Added process ${pcb.pid}")

      } else if (userInput == "S") {
        var selection = readLine("[r,d,p,c]: ")
        while (!Utils.validateInput(selection)) {
          selection = readLine("Not valid. [r,d,p,c]")
        }
        if (selection == "r") {
          snapshot(readyQ = true)
        } else if (selection == "d") {
          snapshot(diskQ = true)
        } else if (selection == "p") {
          snapshot(printerQ = true)
        } else if (selection == "c") {
          snapshot(cdrwQ = true)
        } else {
          print("If we get here...fail me.")
        }

      } else if (Utils.checkInterruptSyscall(userInput)) {
        if (userInput.head.isUpper) {
          interrupt(userInput)
        } else if (userInput.head.isLower) {
          var pcb = readyqueue.queue.head
          syscall(userInput)
        } else {
          println("If you are reading this...fail me!")
        }
      } else if (userInput == "t") {
        val pcb = readyqueue.dequeue()
        import scala.collection.mutable.ArrayBuffer
        var data: ArrayBuffer[ArrayBuffer[Any]] = new ArrayBuffer[ArrayBuffer[Any]]()
        println("Terminating process...")
        data += ArrayBuffer("total cpu time", "average CPU burst")
        data += Utils.pcbToList(pcb, terminating = true)

        println(Utils.Tabulator.format(data))
        println(s"Terminated process ${pcb.pid}")
      } else if (userInput == "Q" || userInput == "q") {
        println("This always happens...was it me??!! I love you anyst ways...")
        done = true
      }

    } while (!done)

  }

  def help() = {
    println(s"Welcome to the OS...Here's how it works: ")
    println(s"'A' will create a new process (exclude quotes).")
    println(s"Lower case letters are system calls. e.g., p1")
    println(s"Upper case letters are interrupts.e.g., 'P1' for printer 1.")
    println(s"System calls can be made to access the following devices:")
    println(s"Printers, CDRW drives, and Disk drives")
    println(s"To kill a process, type 't'")
    println(s"To quit me, type 'q' or 'Q'")
    println(s"With the exception of 'A' and 't'")
  }

  def snapshot(printerQ: Boolean = false, diskQ: Boolean = false, cdrwQ: Boolean = false, readyQ: Boolean = false) = {

    if (diskQ) {
      for (disk <- disks) {
        var num: Int = 1
        disk.snapshot(s"printer $num")
        num = num + 1
      }
    } else if (cdrwQ) {
      for (cdrw <- cdrws) {
        var num: Int = 1
        cdrw.snapshot(s"printer $num")
        num = num + 1
      }
    } else if (printerQ) {
      for (printer <- printers) {
        var num: Int = 1
        printer.snapshot(s"printer $num")
        num = num + 1
      }

    } else if (readyQ) {
      readyqueue.snapshot()
    }
  }


  def syscall(userInput: String = "") = {
    var tuple: (String, Int) = Utils.extractDeviceInfo(userInput)
    var deviceNo: Int = tuple._2
    if (tuple._1 == "c") {
      if (tuple._2 > cdrws.size) {
        println(s"There is no CDRW ${tuple._2} Valid value: 0-${cdrws.size}.")
      } else if (readyqueue.queue.size == 0) {
        print("Nothing is in the CPU!")
      } else {
        var pcb: PCB = readyqueue.dequeue()
        pcb = Utils.populatePCB(pcb, alpha = alpha)
        cdrws(tuple._2).enqueue(pcb)
        println(s"Process ${pcb.pid} has been sent to cdrw $deviceNo")
      }
    } else if (tuple._1 == "d") {
      if (tuple._2 > disks.size) {
        println(s"There is no disk ${tuple._2} Valid value: 0-${disks.size}.")
      } else if (readyqueue.queue.size == 0) {
        print("Nothing is in the CPU!")
      } else {
        var pcb: PCB = readyqueue.dequeue()
        pcb = Utils.populatePCB(pcb, disk = true, alpha = alpha)
        disks(tuple._2).enqueue(pcb)
        println(s"Process ${pcb.pid} has been sent to disk $deviceNo")
      }

    } else if (tuple._1 == "p") {
      if (tuple._2 > printers.size) {
        println(s"There is no printer ${tuple._2} Valid value: 0-${printers.size}.")
      } else if (readyqueue.queue.size == 0) {
        print("Nothing is in the CPU!")
      } else {
        var pcb: PCB = readyqueue.dequeue()
        pcb = Utils.populatePCB(pcb, printer = true, alpha = alpha)
        printers(tuple._2).enqueue(pcb)
        println(s"Process ${pcb.pid} has been sent to printer $deviceNo")
      }
    }
  }

  def interrupt(userInput: String = "") = {
    val tuple: (String, Int) = Utils.extractDeviceInfo(userInput)
    val deviceNo: Int = tuple._2
    if (tuple._1 == "C") {
      if (tuple._2 > cdrws.size) {
        println(s"There is no CDRW ${tuple._2} Valid value: 1-${cdrws.size}.")
      } else if (cdrws(tuple._2).queue.size == 0) {
        print(s"There is nothing in CDRW $deviceNo")
      } else {
        var pcb: PCB = cdrws(deviceNo).dequeue()
        readyqueue.enqueue(pcb)
        println(s"Process ${pcb.pid} is finished in CDRW $deviceNo and " +
          s"has been moved back to the ready queue.")
      }
    } else if (tuple._1 == "D") {
      if (tuple._2 > disks.size) {
        println(s"There is no disk ${tuple._2} Valid value: 1-${disks.size}.")
      } else if (disks(tuple._2).queue.size == 0) {
        print(s"There is nothing in disk $deviceNo")
      } else {
        var pcb: PCB = disks(deviceNo).dequeue()
        readyqueue.enqueue(pcb)
        println(s"Process ${pcb.pid} is finished in disk $deviceNo and " +
          s"has been moved back to the ready queue.")
      }
    } else if (tuple._1 == "P") {
      if (tuple._2 > printers.size) {
        println(s"There is no printer ${tuple._2} Valid value: 1-${printers.size}.")
      } else if (cdrws(tuple._2).queue.size == 0) {
        print(s"There is nothing in CDRW $deviceNo")
      } else {
        var pcb: PCB = printers(deviceNo).dequeue()
        readyqueue.enqueue(pcb)
        println(s"Process ${pcb.pid} is finished in printer $deviceNo and " +
          s"has been moved back to the ready queue.")
      }
    }
  }


}

object RunThis extends App {
  var os = new OS
  os.sysgen()
  os.run()
}