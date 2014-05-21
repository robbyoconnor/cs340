class OS {

  import scala.collection.mutable.ArrayBuffer

  var printers = new ArrayBuffer[Printer]
  var cdrws = new ArrayBuffer[CDRW]
  var disks = new ArrayBuffer[Disk]
  var readyqueue = new ReadyQueue
  var jobpool = new JobPool
  var memory = new ArrayBuffer[PCB]
  var holes = new ArrayBuffer[Block]

  var alpha: Float = 0.0f

  var initialTau: Float = 0

  var totalCPUTime: Float = 0.0f

  var completedProcesses = 0

  var totalMemory: Int = 0
  var totalFreeMemory: Int = 0

  def sysgen {
    println("Welcome to sysgen! I'll be your guide...let's set up the system!")
    var printerCnt = Utils.promptForInt("How many printers: ")
    var disk_cnt = Utils.promptForInt("How many disks: ")
    var cdrw_cnt = Utils.promptForInt("How many CDRWs: ")
    for (i <- 0 to printerCnt - 1) printers += new Printer(i + 1)
    for (i <- 0 to cdrw_cnt - 1) cdrws += new CDRW(i + 1)
    disks = Utils.generateDisks(disk_cnt)
    initialTau = Utils.promptForFloat("Enter initial burst estimate (tau) (greater than 0): ")
    alpha = Utils.promptForFloat(msg = "Enter history parameter (alpha) (float between 0 and 1): ", a = 0.0f, b = 1.0f, validateRange = true)
    totalMemory = Utils.promptForInt("Enter the total amount of memory (in words as an integer greater than 0): ")
    holes += new Block(1, totalMemory)
  }

  def run {
    help
    var done = false
    do {
      jobpool.processJobPool(this)
      var userInput: String = readLine("[A,S,t,p#,d#,c#,P#,D#,C#]:")
      while (!Utils.validateInput(userInput)) {
        println("Invalid input")
        userInput = readLine("[A,S,t,p#,d#,c#,P#,D#,C#]:")

      }
      totalFreeMemory = holes.map(_.limit).sum
      if (userInput == "A") {
        interruptRQ
        var size = Utils.promptForInt(s"How big is this process (in words, greater than 0) [1-${holes.map(_.limit).sum} words:")
        if (size > totalMemory) {
          println(s"process size $size words has been rejected since it exceeds $totalMemory words")
        } else {
          var pcb = new PCB
          pcb.tau = initialTau
          pcb.tauLeft = initialTau
          pcb.limit = size
          if (size < totalMemory && size > holes.map(_.limit).sum) {
            jobpool.queue += pcb
            println(s"Process ${pcb.pid} is in the job pool awaiting free space.")
          } else {
            if (doWeCompact(size)) {
              compactMemory
            }
            var _pcb = allocate(pcb.limit, pcb)            
            if (_pcb.isDefined) {
              memory += _pcb.get
              readyqueue.enqueue(_pcb.get)
              totalFreeMemory -= size
              println(s"Process ${_pcb.get.pid} has been allocated ${_pcb.get.limit} words and is in the ready queue!")
            }
          }
        }

      } else if (userInput == "S") {
        var selection = readLine("[r,d,p,c,m]: ")
        while (!Utils.validatesnapshot(selection)) {
          selection = readLine("Not valid. [r,d,p,c,m]")
        }

        if (selection == "r") {
          snapshot(readyQ = true)
        } else if (selection == "d") {
          snapshot(diskQ = true)
        } else if (selection == "p") {
          snapshot(printerQ = true)
        } else if (selection == "c") {
          snapshot(cdrwQ = true)
        } else if (selection == "m") {
          snapshot(mem = true)
        }
      } else if (Utils.checkInterruptSyscall(userInput)) {
        if (userInput.head.isUpper) {
          interrupt(userInput)
        } else if (userInput.head.isLower) {
          syscall(userInput)
        } else {
          println("If you are reading this...fail me!")
        }
      } else if (userInput == "t") {
        if (readyqueue.queue.isEmpty) {
          println("Nothing is in the CPU!")
        } else {
          var pcb = readyqueue.dequeue
          var timeSpent = Utils.promptForFloat("How long was this process in the CPU for: ", 1.0f, pcb.tauLeft)
          totalCPUTime += timeSpent
          pcb.cpuTime += timeSpent
          pcb.burstCount += 1
          completedProcesses += 1
          freeMemory(pcb.base, pcb.limit, pcb)
          println(s"Freed ${pcb.limit} words of memory");
          println("Terminating process...")
          println(s"Average burst time: ${pcb.bursts / pcb.burstCount}")
          println(s"Total process CPU time ${pcb.cpuTime}")
          println(s"Total System per completed process: ${totalCPUTime / completedProcesses}")
          println(s"Terminated process ${pcb.pid}")
        }
      } else if (userInput == "Q" || userInput == "q") {
        println("This always happens...was it me??!! I love you anyways...")
        done = true
      }

    } while (!done)

  }

  def help {
    println(s"Welcome to the OS...Here's how it works: ")
    println(s"'A' will create a new process (exclude quotes).")
    println(s"Lower case letters are system calls. e.g., p1")
    println(s"Upper case letters are interrupts.e.g., 'P1' for printer 1.")
    println(s"System calls can be made to access the following devices:")
    println(s"Printers, CDRW drives, and Disk drives")
    println(s"To kill a process, type 't'")
    println(s"To quit me, type 'q' or 'Q'")
    println(s"With the exception of 'A' and 't' all comamnds are two characters long")
    println(s"and may not contain spaces.")

  }

  def snapshot(printerQ: Boolean = false, diskQ: Boolean = false, cdrwQ: Boolean = false, readyQ: Boolean = false, mem: Boolean = false) {

    if (diskQ) {
      var num: Int = 1
      for (disk <- disks) {
        disk.snapshot(s"disk $num")
        num = num + 1
      }
    } else if (cdrwQ) {
      var num: Int = 1
      for (cdrw <- cdrws) {
        cdrw.snapshot(s"cdrw $num")
        num = num + 1
      }
    } else if (printerQ) {
      var num: Int = 1
      for (printer <- printers) {
        printer.snapshot(s"printer $num")
        num = num + 1
      }
    } else if (mem) {
      println(s"Free Memory: $totalFreeMemory  / $totalMemory")
      Utils.snapshot(holes, memory, this)
    } else if (readyQ) {
      readyqueue.snapshot
    }
  }

  def syscall(userInput: String = "") {
    var tuple: (String, Int) = Utils.extractDeviceInfo(userInput)
    var deviceNo: Int = tuple._2 - 1
    if (tuple._1 == "c") {
      if (tuple._2 > cdrws.size) {
        println(deviceNo > cdrws.size)
        println(s"There is no CDRW ${tuple._2} Valid value: 0-${cdrws.size}.")
      } else if (readyqueue.queue.size == 0) {
        println("Nothing is in the CPU!")
      } else {
        movePCB(cdrws(tuple._2 - 1), cdrwQ = true, fromRQ = true)
      }
    } else if (tuple._1 == "d") {
      if (tuple._2 > disks.size) {
        println(s"There is no disk ${tuple._2} Valid value: 0-${disks.size}.")
      } else if (readyqueue.queue.size == 0) {
        println("Nothing is in the CPU!")
      } else {
        movePCB(disks(tuple._2 - 1), diskQ = true, fromRQ = true)
      }

    } else if (tuple._1 == "p") {
      if (tuple._2 > printers.size) {
        println(s"There is no printer ${tuple._2} Valid value: 0-${printers.size}.")
      } else if (readyqueue.queue.size == 0) {
        println("Nothing is in the CPU!")
      } else {
        movePCB(printers(tuple._2 - 1), printerQ = true, fromRQ = true)
      }
    }
  }

  def interrupt(userInput: String = "") = {
    val tuple: (String, Int) = Utils.extractDeviceInfo(userInput)
    val deviceNo: Int = tuple._2
    if (tuple._1 == "C") {
      if (tuple._2 > cdrws.size) {
        println(s"There is no CDRW ${tuple._2} Valid value: 1-${cdrws.size}.")
      } else if (cdrws(tuple._2 - 1).queue.size == 0) {
        println(s"There is nothing in CDRW $deviceNo")
      } else {
        interruptRQ
        movePCB(cdrws(tuple._2 - 1), fromRQ = false)
      }
    } else if (tuple._1 == "D") {
      if (tuple._2 > disks.size) {
        println(s"There is no disk ${tuple._2} Valid value: 1-${disks.size}.")
      } else if (disks(tuple._2 - 1).size() == 0) {
        print(s"There is nothing in disk $deviceNo")
      } else {
        interruptRQ
        movePCB(disks(tuple._2 - 1), fromRQ = false)
      }
    } else if (tuple._1 == "P") {
      if (tuple._2 > printers.size) {
        println(s"There is no printer ${tuple._2} Valid value: 1-${printers.size}.")
      } else if (printers(tuple._2 - 1).queue.size == 0) {
        println(s"There is nothing in printer $deviceNo")
      } else {
        interruptRQ
        movePCB(printers(tuple._2 - 1), fromRQ = false)
      }
    }
  }

  def interruptRQ {
    if (readyqueue.queue.size > 0) {
      val oldPCB: PCB = readyqueue.queue.head
      var cpuTime = Utils.promptForFloat("How long was this process in the CPU: ")
      oldPCB.tauLeft -= cpuTime
      oldPCB.bursts += cpuTime
      oldPCB.burstCount += 1
      totalCPUTime += cpuTime
    }
  }

  def movePCB(device: Device, fromRQ: Boolean = true, cdrwQ: Boolean = false,
    printerQ: Boolean = false, diskQ: Boolean = false) {
    if (fromRQ) {
      var pcb = readyqueue.queue.head
      if (printerQ) {
        pcb = Utils.populatePCB(pcb, printer = true, alpha = alpha)
      } else if (diskQ) {
        pcb = Utils.populatePCB(pcb, disk = true, alpha = alpha, numCylinders = device.asInstanceOf[Disk].cylinders)
      } else if (cdrwQ) {
        pcb = Utils.populatePCB(pcb, alpha = alpha)
      }
      if (pcb == null) {
        return
      } else {
        readyqueue.dequeue
      }
      device.enqueue(pcb)
      println(s"Process ${pcb.pid} has been sent to disk ${device.name}")
    } else {
      var pcb: PCB = device.dequeue()
      readyqueue.enqueue(pcb)
      println(s"Process ${pcb.pid} is finished in ${device.name} and " +
        s"has been moved back to the ready queue.")
    }
  }
  def largestFit(size: Int): Int = {
    var largestfit: Int = -1
    if(holes.isEmpty) 
      return largestfit
    for (i <- 0 to holes.size - 1 if holes.size>0) {    
      if (holes(i).limit >= size && holes.map(_.limit).sum > 0) largestfit = i      
    }    
    return largestfit
  }

  def doWeCompact(size: Int) = (holes.size > 1 && size <= holes.map(_.limit).sum)

  def allocate(size: Int, pcb: PCB): Option[PCB] = {
    var fit = largestFit(size)    
    if (fit < 0) {     
      return None
    }
    var block = holes(fit)
    if (block.limit == size) {
      holes.remove(fit)
      pcb.base = block.base
    } else if (block.limit > size) {
      pcb.base = block.base
      if (block.limit - size > 0) {
        block.base = pcb.base + pcb.limit
        block.limit -= size
      } else {
        holes -= block
      }
    }
    return Some(pcb)
  }

  def compactMemory {
    var pcb: Option[PCB] = None
    if (!memory.isEmpty)
      memory(0).base = 1
    for (i <- 0 until memory.length - 1 if i > 0) {
      memory(i + 1).base = memory(i).base + memory(i).limit
    }
    if (memory.isEmpty) {
      holes.clear()
      holes += new Block(1, totalMemory)
    } else if (holes.length > 1 && memory.map(_.limit).sum <= totalMemory) {
      holes.clear()
      holes += new Block(memory(memory.length - 1).base + memory(memory.length - 1).limit, totalFreeMemory)
    }
  }
     
  def freeMemory(base: Int, limit: Int, pcb: PCB) {
    for (process <- memory)
      if (process == pcb) memory -= process
    holes += new Block(base, limit)
    memory -= pcb
    totalFreeMemory = holes.map(_.limit).sum
  }
}

object RunThis extends App {
  val os = new OS
  os.sysgen
  os.run
}